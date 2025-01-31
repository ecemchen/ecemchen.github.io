[results_usabilitytest_MoonApp.csv](https://github.com/user-attachments/files/18618233/results_usabilitytest_MoonApp.csv)# ecemchen.github.io

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


[UploTimestamp,How old are you?,What is your gender?,How easy or difficult was it to complete the registration process and understand your zodiac sign?,What could be improved about the registration and zodiac sign process?,"How easy or difficult was it to understand the daily, weekly and monthly advice displayed?","Did the default screen meet your expectations? If not, what could be improved?",How easy or difficult was it to add a day as a favorite?,Was it clear that the day was added as a favorite and easy to find in the calendar?,How easy or difficult was it to add a note?,Where there any steps that felt unclear or unnecessary?,How easy or difficult was it to change your profile settings?,What challenges did you face in modifying your settings?,How easy or difficult was it to add a note?,Was it easy to find the note in the calendar?,How easy or difficult  was it to find the logout and login button?,Were there any steps that felt unclear or unnecessary?,Did you encounter any other issues or have any suggestions for improving the MoonApp that we haven't covered in the tasks?
21/01/2025 15:25:10,21,female,4,"when i finish writing in a text field and i click enter, it does not close the keyboard. maybe a quicker selection/picker for the birth year etc would be cool (right now i have to click the arrows loads of times) but thats not necessary ",4,"Yes, it looked very nice! The monthly text was a bit difficult to understand as it uses lots of astrology terminology that some people might not know. I also encountered a bug when sometimes it was showing me eg the weekly text in the daily tab, or the monthly text in the weekly tab, and so on",2,"It was clear to see that it is saved but I got confused trying to look for the “favourite” button. I thought the “save” button to save a note was what I was supposed to do, and I did not look in the tabbar to find the heart button",,,5,"none, maybe i would remove the text field focus after saving the changes",5,yes,5,"when i clicked enter to get out of the password text field, it added an empty character, which i dont think should happen. also i am not sure if i get a feedback when i log in with the wrong password, as it was just loading for me until i went back to put in the password correctly ","on the horoscope page I thought i could add 3 different types of notes: one for the day, one for the week and one for the month. however, it will only add the note to the day. idk how you could do it differently at the moment though "
21/01/2025 21:11:11,32,male,5,Short Info about the sign,5,Yes,5,Yes,,,3,"ON my vivox100 i have a blackscreen the color of the Numbers and Background is black ,hard to See the Numbers /settings",5,Was clear and smooth,4,No,"The Birthday settings was hard to edit, anything Else was smooth and Well made"
23/01/2025 12:18:35,24,male,4,"The older you are, the more years you have to click back. Maybe you could introduce have an additional scollable list for the years",3,It would be cool to have an info screen. I don't know much about crescents or opposing marses :(,5,Yes,,,5, ,4, ,5,No,Add more informations for dumb people
23/01/2025 22:34:07,38,male,4,"Privacy and security on starting of App.
",4,"yes, it is very good",5,yes it is,,,5,easy acess to settings.,5,yes it was,1,There was not dificult to find logout but there was not login.,"
At first, everything is fine, but I would like a sensor to be implemented to automatically determine the temperature, as well as better adjust the main screen."
23/01/2025 23:23:03,21,male,4,The requirements for the password should be displayed,5,The screen was well designed,4,The icon could be a little bigger ,,,5,Very simple,5,It was simple,5,The location was expected,No the only thing was the password requirements were not explicit 
24/01/2025 00:56:24,24,male,4,i think it was fairly easy,4,yes it di,4,yes,,,4,didnt change much but it seemed straigh forward,4,yes,5,"no,not realy",
24/01/2025 10:58:52,21,male,5,idk,5,yes,5,yes,,,5,no challenges,5,yes,5,no,
24/01/2025 18:23:24,21,male,5,nothing,4,"Yes, it did, and I wouldn’t change anything.",5,It was very practical and easy to find.,,,5,none,4,"yes, it was",4,"In my view, none",ading results_usabilitytest_MoonApp.csv…]()


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






