# MoonApp Technical Documentation

## Overview
MoonApp is an Android application built using modern Android development practices and Jetpack Compose for the UI. The app provides moon phase information and allows users to track their zodiac-related information.

## Technical Stack

### Core Technologies
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture Pattern**: MVVM (Model-View-ViewModel)
- **Database**: Room (Local Storage)
- **Cloud Storage**: Firebase Firestore
- **Authentication**: Firebase Auth

## Project Structure

```
app/
└── src/main/java/dev/christina/moonapp/
    ├── data/                 # Data layer
    │   ├── db/              # Local database
    │   └── remote/          # Remote data sources
    ├── repository/          # Data repositories
    │   ├── FirebaseRepository.kt
    │   ├── MoonRepository.kt
    │   └── NoteRepository.kt
    ├── ui/                  # UI components
    └── MainActivity.kt      # Main entry point
```

## Core Components

### Activities
- **MainActivity**: The single-activity entry point that hosts the navigation components

### Screens (Composables)
1. **WelcomeScreen**: Initial landing screen
2. **RegisterScreen**: User registration interface
3. **LoginScreen**: User login interface
4. **SecondScreen**: Primary dashboard displaying current moon phase information alongside personalized daily, weekly, and monthly astrological guidance with integrated note-taking functionality
5. **MoonListScreen**: Calendar-based overview that highlights dates with user-saved favorites and personal annotations
6. **ProfileSettingsScreen**: User profile management
7. **UserZodiacScreen**: Zodiac sign information display after registration

### Repositories
1. **MoonRepository**
   - Handles moon phase data operations
   - Interfaces with local Room database

2. **FirebaseRepository**
   - Manages Firebase Firestore operations
   - Handles cloud data synchronization

3. **NoteRepository**
   - Manages user notes
   - Utilizes Firebase for storage

### ViewModels
1. **MoonViewModel**
   - Manages moon phase data state
   - Handles moon-related business logic

2. **NoteViewModel**
   - Manages note-related operations
   - Handles note data state

## Navigation
The app uses the Jetpack Navigation component with Compose integration:
- Type-safe navigation arguments
- Deep linking support
- Screen state preservation

## Database Schema

### Local Database (Room)
- Moon phase information
- User preferences
- Cached data

### Cloud Database (Firebase)
- User authentication data
- Notes and personal data
- Synchronization data

## Features

### Moon Phase Tracking
- View current moon phase
- Browse moon phase calendar
- Save favorite dates

### User Profile
- Zodiac sign selection
- Personal preferences
- Settings management

### Notes System
- Create and store personal notes
- Associate notes with specific dates
- Cloud synchronization

## Build Configuration

### Dependencies
```kotlin
dependencies {
    // Jetpack Compose
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material:material")
    
    // Firebase
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-auth")
    
    // Room Database
    implementation("androidx.room:room-runtime")
    implementation("androidx.room:room-ktx")
    
    // Navigation
    implementation("androidx.navigation:navigation-compose")
}
```

## Security

### Authentication
- Firebase Authentication integration
- Secure credential management
- Session handling

### Data Storage
- Encrypted storage
- Secure cloud data transmission
- User data privacy





