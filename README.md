# GrowGuide - Plant Care App

GrowGuide is an Android application designed to help users manage and care for their plants. Built with Jetpack Compose and following the MVVM (Model-View-ViewModel) architecture, the app offers a rich set of features tailored for gardeners.

## Features

- **Plant Catalog**: Browse and search through a comprehensive database of plants
- **My Garden**: Add plants to your personal garden and track their care
- **Plant Care**: Get detailed care instructions and watering schedules
- **Weather Integration**: View local weather conditions for optimal plant care
- **User Authentication**: Login, sign up, or use the app as a guest
- **Detailed Plant Information**: Access comprehensive details about each plant

## Tech Stack

- **UI**: Jetpack Compose for modern, declarative UI
- **Architecture**: MVVM (Model-View-ViewModel)
- **Local Database**: Room for data persistence
- **Network**: Retrofit for API calls
- **Authentication**: Firebase Authentication
- **Dependency Injection**: Manual dependency injection pattern
- **Image Loading**: Coil for efficient image loading

## Project Structure

- **Activities**: Main entry point into the app
- **Screens**: Compose UI for different app screens
- **ViewModels**: Handle UI state and business logic
- **Managers**: Abstract data sources and operations
- **API Services**: Interface with remote APIs
- **Database**: Local data persistence
- **Models**: Data structures and entities

## Database Structure

- **PlantsEntity**: Plant catalog information
- **UserEntity**: User account data
- **UserPlantsEntity**: Links plants to specific users with custom data
- **WeatherEntity**: Cached weather information

## Getting Started

### Prerequisites
- Android Studio Arctic Fox or later
- JDK 11 or later
- Android SDK 21+

### API Keys
To run the app, you'll need to obtain API keys for:
- [Perenual API](https://perenual.com/docs/api)
- [Weather API](https://www.weatherapi.com/)
- Firebase project

### Setup
1. Clone the repository
2. Open in Android Studio
3. Update the API keys in `API.kt`
4. Configure Firebase:
    - Add the `google-services.json` to your project 
    - Enable **Email/Password** and **Anonymous** authentication in the Firebase Console under your project **Authentication → Sign-in method**
5. Build and run