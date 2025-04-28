package com.xxu.growguide.auth

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.xxu.growguide.data.database.AppDatabase
import com.xxu.growguide.data.entity.UserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.Date

/**
 * Purpose: Manages authentication operations and user state
 */
class AuthManager private constructor(private val context: Context) {
    // Firebase Auth instance
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    // Database instance
    private val database: AppDatabase by lazy { AppDatabase.getInstance(context) }
    private val userDao by lazy { database.userDao() }

    // Observable user state
    val currentUser = mutableStateOf<FirebaseUser?>(null)
    val isLoggedIn = mutableStateOf(false)
    val authError = mutableStateOf<String?>(null)
    val isLoading = mutableStateOf(false)
    val isAnonymous = mutableStateOf(false)

    init {
        // Set initial state
        currentUser.value = auth.currentUser
        isLoggedIn.value = auth.currentUser != null
        isAnonymous.value = auth.currentUser?.isAnonymous == true

        Log.i("Plant-AuthManager", "currentUser: ${currentUser.value}")

        // Listen for auth state changes
        auth.addAuthStateListener { firebaseAuth ->
            currentUser.value = firebaseAuth.currentUser
            isLoggedIn.value = firebaseAuth.currentUser != null
            isAnonymous.value = firebaseAuth.currentUser?.isAnonymous == true
        }
    }

    /**
     * Purpose: Sign in with email and password
     *
     * @param email User's email address
     * @param password User's password
     * @return Boolean indicating whether sign-in was successful (true) or failed (false)
     */
    suspend fun signIn(email: String, password: String): Boolean {
        return try {
            isLoading.value = true
            authError.value = null
            val result = auth.signInWithEmailAndPassword(email, password).await()
            result.user?.let { user ->
                syncUserToLocalDb(user)
            }
            true
        } catch (e: Exception) {
            authError.value = e.message
            false
        } finally {
            isLoading.value = false
        }
    }

    /**
     * Purpose: Sign in anonymously
     *
     * @return Boolean indicating whether anonymous sign-in was successful
     */
    suspend fun signInAnonymously(): Boolean {
        return try {
            isLoading.value = true
            authError.value = null
            val result = auth.signInAnonymously().await()
            result.user?.let { user ->
                // Create anonymous user in local database with generated display name
                val displayName = "Guest_${user.uid.takeLast(5)}"
                val newUser = UserEntity(
                    userId = user.uid,
                    email = "",
                    displayName = displayName,
                    profileImageUrl = "",
                    experienceLevel = "Beginner",
                    created = Date().time,
                    lastActive = Date().time
                )
                insertUserToDb(newUser)

                // Update the display name in Firebase
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName)
                    .build()
                user.updateProfile(profileUpdates).await()

                isAnonymous.value = true
            }
            true
        } catch (e: Exception) {
            authError.value = e.message
            false
        } finally {
            isLoading.value = false
        }
    }

    /**
     * Purpose: Convert anonymous account to permanent account
     *
     * @param email User's email address
     * @param password User's password
     * @param displayName User's display name
     * @return Boolean indicating whether conversion was successful
     */
    suspend fun convertAnonymousUser(email: String, password: String, displayName: String): Boolean {
        return try {
            val user = auth.currentUser ?: return false

            if (!user.isAnonymous) {
                authError.value = "This account is not anonymous"
                return false
            }

            isLoading.value = true
            authError.value = null

            // Link anonymous account with email and password
            val credential = EmailAuthProvider.getCredential(email, password)
            user.linkWithCredential(credential).await()

            // Update profile
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .build()
            user.updateProfile(profileUpdates).await()

            // Update in local database
            val currentDbUser = withContext(Dispatchers.IO) {
                userDao.getUser(user.uid)
            } ?: return false

            val updatedUser = currentDbUser.copy(
                email = email,
                displayName = displayName,
                lastActive = Date().time
            )

            withContext(Dispatchers.IO) {
                userDao.updateUser(updatedUser)
            }

            isAnonymous.value = false
            true
        } catch (e: Exception) {
            authError.value = e.message
            false
        } finally {
            isLoading.value = false
        }
    }

    /**
     * Purpose: Sign up new user with email and password
     *
     * @param email User's email address
     * @param password User's password
     * @param displayName User's display name
     * @return Boolean indicating whether sign-up was successful (true) or failed (false)
     */
    suspend fun signUp(email: String, password: String, displayName: String): Boolean {
        return try {
            isLoading.value = true
            authError.value = null
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            result.user?.let { user ->
                // Update the display name in Firebase
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName)
                    .build()
                user.updateProfile(profileUpdates).await()

                // Create user in local database
                val newUser = UserEntity(
                    userId = user.uid,
                    email = email,
                    displayName = displayName,
                    profileImageUrl = "",
                    experienceLevel = "Beginner",
                    created = Date().time,
                    lastActive = Date().time
                )
                insertUserToDb(newUser)
            }
            true
        } catch (e: Exception) {
            authError.value = e.message
            false
        } finally {
            isLoading.value = false
        }
    }

    /**
     * Purpose: Sign out current user
     */
    fun signOut() {
        auth.signOut()
    }

    /**
     * Purpose: Get the current user from local database
     *
     * @return UserEntity of the current user or null if not found
     */
    suspend fun getCurrentUserFromDb(): UserEntity? {
        return currentUser.value?.uid?.let { uid ->
            authError.value = null
            try {
                withContext(Dispatchers.IO) {
                    userDao.getUser(uid)
                }
            } catch (e: Exception) {
                authError.value = e.message
                null
            }
        }
    }

    /**
     * Purpose: Sync Firebase user to local database
     *
     * @param firebaseUser The Firebase user to sync with local database
     */
    private suspend fun syncUserToLocalDb(firebaseUser: FirebaseUser) {
        // Check if user exists in database first
        // If not, create a new user entity
        val existingUser = getCurrentUserFromDb()

        if (existingUser == null) {
            // Create new user in database
            val newUser = UserEntity(
                userId = firebaseUser.uid,
                email = firebaseUser.email ?: "",
                displayName = firebaseUser.displayName ?: firebaseUser.email?.substringBefore('@') ?: "",
                profileImageUrl = firebaseUser.photoUrl?.toString() ?: "",
                experienceLevel = "Beginner",
                created = Date().time,
                lastActive = Date().time
            )
            insertUserToDb(newUser)
        } else {
            // Update last active time
        }
    }

    /**
     * Purpose: Insert user to database
     *
     * @param user The user entity to be inserted into the database
     */
    private suspend fun insertUserToDb(user: UserEntity) {
        withContext(Dispatchers.IO) {
            userDao.insertUser(user)
            Log.i("Plant-AuthManager", "Successfully insert a user into database.")
        }
    }

    companion object {
        @Volatile private var instance: AuthManager? = null

        /**
         * Purpose: Get the singleton instance of AuthManager
         *
         * @param context Application context used to initialize the database
         * @return The AuthManager instance
         */
        fun getInstance(context: Context): AuthManager {
            return instance ?: synchronized(this) {
                instance ?: AuthManager(context.applicationContext).also { instance = it }
            }
        }
    }
}