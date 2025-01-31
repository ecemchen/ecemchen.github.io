# ecemchen.github.io

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

### Milena Biasova: 
Working on the MoonApp project was definitely challenging, but also really rewarding. I focused on developing the second screen, adding the calendar view, and working on the app’s design. I also handled the integration of the horoscope API, which was a cool experience but not without its struggles.

The biggest challenge for me was getting comfortable with Firebase. It was my first time working with it, and trying to understand how user authentication and moving data from Room to Firebase work was harder than I expected. Fetching data from the horoscope API was another tricky part but figured it out.

I also learned a lot about using Git during this project. At first, I wasn’t super confident, but now I feel way more comfortable with version control and teamwork. Overall, this project helped me grow a lot, both technically and personally, and I’m proud of what I accomplished.

### Christina Gamperl:
Working on MoonApp was both challenging and a good learning experience. The original app, created during the Mobile Coding class, stored everything locally using Room Database. I restructured it to use Firebase Firestore, allowing data like saved days and notes to sync across devices.

I worked on creating the login and registration features, where users can sign up with their email, password, and birthdate. I also styled the calendar view to make it simple and easy to use. Transitioning from local storage to Firebase took some time and effort, as I had to learn how to manage cloud-based data effectively. Additionally, I helped organize the Git repository for better collaboration.

Although there were challenges, like debugging and figuring out Firebase integration, the process helped me improve my skills in backend systems and teamwork. While there is still room for improvement, I think we created a solid foundation for MoonApp.






