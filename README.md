<p align="center">
  <img src="https://github.com/user-attachments/assets/9b19c52c-277b-4d24-b316-07b095792f32" alt="logo"/>
</p>

## Description

**MoonApp** makes it easy to explore moon phases and get daily zodiac insights tailored to you. View a calendar of lunar details, save your favorite days, and add personal notes. Choose your zodiac sign to get advice and mood tips for each day.

**App concept:**  [App Concept document](https://github.com/user-attachments/files/18476226/MoonApp_Christina.Milena.Ecem.pdf)  

**Team members:**
Ecem Tasali -
Milena Biasova -
Christina Gamperl

**MoonApp APK:**  [Download Zip file here to get APK file](https://github.com/user-attachments/files/18503868/app-release.apk.zip)

## Expert Interview
[Expert Interview](https://github.com/user-attachments/files/18478607/expert_interview.pdf)  

## Heuristic Evaluation
[Heuristic Evaluation](https://github.com/user-attachments/files/18478567/Heuristic.Evaluation.pdf)  

## Usability Test Plan
[User Test Design](https://github.com/user-attachments/files/18618028/CCL3.MoonApp.User.Test.Design.1.pdf)

## Results & Improvements
[Test report MoonApp](https://github.com/user-attachments/files/18618076/Final.usability.test.report.MoonApp.pdf)

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
5. **MoonListScreen**: Calendar-based overview that highlights dates with user-saved favorites and personal notes
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


## Design of the database
The MoonApp database design combines local storage, cloud storage, and remote APIs to deliver a dynamic user experience. The architecture ensures offline accessibility for moon phase data, centralized cloud storage for user-specific data, and real-time fetching of zodiac and moon phase details from external sources.

### 1. Room Database (Local Storage)
The Room Database is used for storing moon phases and user notes locally on the device. 
It has the following structure/table: 

1. moon_phases
- Primary Key: date (String)
- Columns:
  - date: String, format YYYY-MM-DD
  - phase: String, moon phase name
  - illumination: Double, percentage illumination
  - isFavorited: Boolean, whether the day is marked as favorite
  - zodiacSign: String (nullable), zodiac sign associated with the moon phase
  - advice: String (nullable), advice based on the moon phase and zodiac
  - mood: String (nullable), mood for the day based on the moon phase and zodiac

### 2. Firebase Firestore (Cloud Storage)
Firebase Firestore is used to manage user data, including registration information, saved favorite days, and notes.

1. users (Main Collection)
- Document ID: User's UID (Unique Identifier from Firebase Auth)
- Fields:
  - uid: String, Firebase user ID
  - birthdate: String, format YYYY-MM-DD
  - email: String, user's email
  - zodiacSign: String, zodiac sign derived from birthdate
  - savedDays: List<String>, favorite days saved by the user in the format YYYY-MM-DD

2. users/{uid}/notes (Sub-Collection for Notes)
- Fields for Each Document:
  - date: String, format YYYY-MM-DD
  - content: String, note content

### 3. Remote APIs
The app integrates with two external APIs to fetch moon phase details and zodiac advice.

1. FarmSense API
- Fetches moon phase details based on a timestamp and location.
- Data Fields:
  - TargetDate: Date of the moon phase
  - Phase: Moon phase name
  - Illumination: Percentage of the moon illuminated

2. Horoscope API
- Fetches daily, weekly, and monthly zodiac advice based on the zodiac sign.
- Data Fields:
  - date: Date for the advice
  - horoscope_data: Advice text
 
#### Entity-Relationship-Diagram
The following diagram illustrates the database architecture of the MoonApp, showcasing the relationships between entities and APIs:
![Entity-Relationship-Diagram](https://github.com/user-attachments/assets/0186c94b-ad49-4246-bc16-bbb364aaf7ff)


## Final Reflection

### Ecem Tasali:
Working on the MoonApp project was both challenging and rewarding. I focused on the heuristic evaluation, analyzing usability issues, and working on the calendar view. Identifying small but impactful UX problems, like missing loading indicators or unclear icons, was an interesting process.

On the development side, implementing the calendar’s favorite days and notes feature came with its own challenges, especially ensuring everything displayed correctly. Debugging and improving the UI helped me learn a lot about state management and user interaction.

The hardest part was making sense of qualitative feedback—some responses were detailed, while others were vague. Overall, this project improved both my UX analysis and coding skills, and I’m happy with what we accomplished.

### Milena Biasova: 
Working on the MoonApp project was definitely challenging, but also really rewarding. I focused on developing the second screen, adding the calendar view, and working on the app’s design. I also handled the integration of the horoscope API, which was a cool experience but not without its struggles.

The biggest challenge for me was getting comfortable with Firebase. It was my first time working with it, and trying to understand how user authentication and moving data from Room to Firebase work was harder than I expected. Fetching data from the horoscope API was another tricky part but figured it out.

I also learned a lot about using Git during this project. At first, I wasn’t super confident, but now I feel way more comfortable with version control and teamwork. Overall, this project helped me grow a lot, both technically and personally, and I’m proud of what I accomplished.

### Christina Gamperl:
Working on MoonApp was both challenging and a good learning experience. The original app, created during the Mobile Coding class, stored everything locally using Room Database. I restructured it to use Firebase Firestore, allowing data like saved days and notes to sync across devices.

I worked on creating the login and registration features, where users can sign up with their email, password, and birthdate. I also styled the calendar view to make it simple and easy to use. Transitioning from local storage to Firebase took some time and effort, as I had to learn how to manage cloud-based data effectively. Additionally, I helped organize the Git repository for better collaboration.

Although there were challenges, like debugging and figuring out Firebase integration, the process helped me improve my skills in backend systems and teamwork. While there is still room for improvement, I think we created a solid foundation for MoonApp.






