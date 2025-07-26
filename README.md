🚌 Overview
CTA Bus Tracker is a comprehensive Android application that provides real-time information about Chicago Transit Authority (CTA) bus routes, stops, and arrival predictions. The app helps users track buses within 1000 meters of their location and provides accurate arrival predictions for better trip planning.
✨ Features
Core Functionality

Route Discovery: Browse and search through all CTA bus routes
Real-time Location Services: Find bus stops within 1000m of your current location
Live Arrival Predictions: Get accurate bus arrival times at selected stops
Route Filtering: Search routes by number or name with intelligent filtering
Distance Tracking: View exact distance from buses to stops

User Experience

Modern Splash Screen: Implements the new Android SplashScreen API
Intuitive Navigation: Three-activity flow for seamless user experience
Pull-to-Refresh: Swipe down to update prediction data in real-time
Smart Caching: 24-hour data caching for improved performance
Custom Typography: Uses Helvetica Neue Medium font throughout

Technical Features

Location Permissions: Comprehensive permission handling with rationale dialogs
Network Error Handling: Robust error management for connectivity issues
Ad Integration: Banner ads using AdMob or Unity Ads
Material Design: Modern UI following Material Design guidelines

🛠️ Technical Stack

Language: Java
Architecture: MVVM (Model-View-ViewModel)
Minimum SDK: API 29 (Android 10)
Location Services: Android Location Manager
Networking: Retrofit2, OkHttp3
UI Components: TextInputLayout, AlertDialogs, RecyclerView
Ads: AdMob/Unity Ads integration
Typography: Custom font (Helvetica Neue Medium)

📋 Prerequisites

Android Version: 10.0 (API level 29) or higher
Permissions: Location access, Internet connectivity
Hardware: GPS-enabled Android device
CTA API Key: Required for accessing CTA Bus Tracker API

🚀 Getting Started
1. CTA API Setup
Before running the app, you need to obtain a CTA Bus Tracker API key:

Create an account at CTA Bus Tracker
Check your email for activation instructions (check spam folder)
Login and navigate to Developer API
Select "Show me my API Key" to reveal your key

2. Installation
bash# Clone the repository
git clone https://github.com/yourusername/cta-bus-tracker.git
cd cta-bus-tracker

# Open in Android Studio
# File > Open > Select the cloned directory
3. Configuration
Add your CTA API key to your project:
java// In your constants file or build configuration
public static final String CTA_API_KEY = "YOUR_API_KEY_HERE";
4. Build and Run
bash# Clean and build the project
./gradlew clean
./gradlew build

# Install on device/emulator
./gradlew installDebug
📱 App Flow
MainActivity

Route List: Displays all CTA bus routes with color-coded backgrounds
Search Functionality: Filter routes by route number or name
Route Selection: Tap to view available directions

StopsActivity

Direction Selection: Choose route direction via popup menu
Nearby Stops: Shows stops within 1000m of user location
Distance Display: Shows exact distance to each stop

PredictionsActivity

Live Arrivals: Real-time bus arrival predictions
Refresh Support: Pull-to-refresh for updated data
Bus Details: Tap predictions to see bus distance and timing
Map Integration: "Show on Map" functionality for bus locations

🗂️ Project Structure
app/
├── src/main/java/com/cta/bustracker/
│   ├── activities/          # Main app activities
│   │   ├── MainActivity.java
│   │   ├── StopsActivity.java
│   │   └── PredictionsActivity.java
│   ├── models/              # Data models
│   │   ├── Route.java
│   │   ├── Stop.java
│   │   ├── Prediction.java
│   │   └── Vehicle.java
│   ├── api/                 # API interfaces and services
│   │   ├── CTAApiService.java
│   │   └── RetrofitClient.java
│   ├── adapters/            # RecyclerView adapters
│   │   ├── RouteAdapter.java
│   │   ├── StopAdapter.java
│   │   └── PredictionAdapter.java
│   ├── utils/               # Utility classes
│   │   ├── LocationHelper.java
│   │   ├── CacheManager.java
│   │   └── PermissionUtils.java
│   └── constants/           # App constants
│       └── ApiConstants.java
├── src/main/res/
│   ├── layout/              # XML layout files
│   ├── values/              # Colors, strings, styles
│   ├── drawable/            # Icons and graphics
│   └── font/                # Custom fonts
└── build.gradle             # App dependencies
🌐 API Integration
The app integrates with multiple CTA Bus Tracker API endpoints:
Routes API
GET /getroutes?key={API_KEY}&format=json
Directions API
GET /getdirections?key={API_KEY}&format=json&rt={ROUTE}
Stops API
GET /getstops?key={API_KEY}&format=json&rt={ROUTE}&dir={DIRECTION}
Predictions API
GET /getpredictions?key={API_KEY}&format=json&rt={ROUTE}&stpid={STOP_ID}
Vehicles API
GET /getvehicles?key={API_KEY}&format=json&vid={VEHICLE_ID}
💾 Data Management
Caching Strategy

Route Data: Cached for 24 hours (86400000ms)
Direction Data: Cached for 24 hours
Stop Data: Cached for 24 hours
Prediction Data: Never cached (always fresh)
Vehicle Data: Real-time only

Location Filtering

All stops and predictions filtered to within 1000 meters of user location
Uses Android Location Services for precise positioning
Handles location permission requests with user-friendly rationale dialogs

🔧 Error Handling
The app includes comprehensive error handling for:

Location Permission Denial: Shows rationale dialog, closes app on second denial
No Location Available: Displays helpful error message and closes app
Network Connectivity Issues: Shows appropriate error dialogs
API Failures: Graceful degradation with cached data when available

🎨 UI/UX Features
Dynamic Route Colors

Uses CTA-provided route colors for authentic appearance
Intelligent text color selection (white/black) based on background luminance
Formula: luminance < 0.25 ? WHITE : BLACK

Typography

Custom Helvetica Neue Medium font throughout the application
Consistent styling for professional appearance

Material Design Elements

TextInputLayout with clearable search functionality
AlertDialogs for user interactions
Proper elevation and shadows
Material color schemes

🏆 Extra Credit Features
Customer Alerts (20 points)

Integration with CTA Service Alerts API
Displays route-specific service disruptions
Smart alert management (prevents duplicate displays)
No API key required for alerts endpoint

GET /alerts.aspx?routeid={ROUTE}&activeonly=true&outputType=JSON
🧪 Testing
Supported Devices

Minimum SDK: API 29 (Android 10)
Target Resolutions:

1080 x 1920 (Pixel, Pixel 2)
1080 x 2400 (Pixel 7, Pixel 8)


Emulator Support: With Google Play Store

Test Scenarios

Location permission granted/denied flows
Network connectivity issues
API error responses
Data caching validation
Real-time data updates

🤝 Contributing
We welcome contributions! Please follow these guidelines:

Fork the repository
Create a feature branch: git checkout -b feature/amazing-feature
Follow class concepts: Use only concepts covered in CSC 392/492
Test thoroughly: Ensure all features work on target devices
Commit changes: git commit -m 'Add amazing feature'
Push to branch: git push origin feature/amazing-feature
Open a Pull Request

Code Standards

Follow Java coding conventions
Use MVVM architecture patterns
Implement proper error handling
Include appropriate comments
Test on multiple screen sizes

📚 API Documentation
For detailed API documentation, refer to:

CTA Bus Tracker API Guide
CTA Developers Portal
Service Alerts API

📝 Assignment Requirements
This project fulfills the requirements for:

Course: CSC 392/492 - Mobile Applications Development for Android II
Assignment: Assignment 2 - CTA Bus Tracker (250 points)
Institution: DePaul University - Jarvis College of Computing and Digital Media

Grading Criteria

Proper implementation of all three required activities
Correct API integration and data handling
Location services and permission management
UI/UX following specified designs
Error handling and edge cases
Code quality and organization

📄 License
This project is licensed under the MIT License - see the LICENSE file for details.
🐛 Bug Reports & Support
Found a bug or need help?

Issues: Create an issue
Questions: Contact the development team
Documentation: Check the CTA API documentation

🙏 Acknowledgments

Chicago Transit Authority for providing the Bus Tracker API
DePaul University - Jarvis College of Computing and Digital Media
Professor Christopher Hield for project guidance and specifications
Google for Android development tools and Material Design guidelines

📞 Contact
Developer: Adarsh Purushothama Reddy

Email: reddyadarsh164@gmail.com
LinkedIn: https://www.linkedin.com/in/adarsh-p-reddy2/

<img width="910" height="555" alt="image" src="https://github.com/user-attachments/assets/beb3c50a-036f-49a2-97fb-d6c7e709cb58" />
<img width="763" height="518" alt="image" src="https://github.com/user-attachments/assets/e6a5926b-8ec6-42c3-870e-48bc481e40b0" />

