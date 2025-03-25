Test - Battery Draining and Background Running Apps Tracker (Android)
Description
Test is an Android app built in Android Studio that helps users track battery-draining apps and view apps running in the background. The app provides insights into the power consumption of each app, and identifies which apps are running in the background, allowing users to optimize battery life and manage resources effectively.

Features
Identifies apps that are consuming significant battery power.

Lists all apps that are running in the background.

Displays the current battery usage statistics.

User-friendly interface for monitoring battery consumption and running processes.

Requirements
Android Studio installed.

Android 6.0 (API level 23) or higher.

Permissions: ACCESS_FINE_LOCATION, ACCESS_BACKGROUND_LOCATION, BATTERY_STATS (for accessing battery information, and identifying background apps).

Works best on Android devices.

Installation
Clone the Repository
Clone the repository to your local machine using Git:

bash
Copy
git clone https://github.com/your-username/test.git
Open the Project in Android Studio
Open Android Studio.

Select File > Open and navigate to the cloned repository folder.

Open the Test project.

Build the App
Sync the Gradle files to ensure all dependencies are resolved.

Click on Sync Now when prompted in Android Studio.

Build the app:

Click on Build > Make Project to compile the app.

Run the app on an emulator or physical Android device.

Click on Run > Run 'app' to install and run the app on a connected device.

Usage
Once the app is launched on your device, it will show the following:

Battery Draining Apps
This section lists apps that consume a significant amount of battery. The app identifies high battery usage apps by tracking power consumption.

Background Running Apps
This section displays a list of all apps running in the background, even those not currently in use.

