
# Test - Battery Draining and Background Running Apps Tracker (Android)

## Description

**Test** is an Android app built in **Android Studio** that helps users track battery-draining apps and view apps running in the background. The app provides insights into the power consumption of each app, and identifies which apps are running in the background, allowing users to optimize battery life and manage resources effectively.

## Features

- Identifies apps that are consuming significant battery power.
- Lists all apps that are running in the background.
- Displays the current battery usage statistics.
- User-friendly interface for monitoring battery consumption and running processes.

## Requirements

- **Android Studio** installed.
- **Android 6.0 (API level 23)** or higher.
- **Permissions**: `ACCESS_FINE_LOCATION`, `ACCESS_BACKGROUND_LOCATION`, `BATTERY_STATS` (for accessing battery information, and identifying background apps).
- Works best on Android devices.

## Installation

### Clone the Repository

1. Clone the repository to your local machine using Git:

   ```bash
   git clone https://github.com/your-username/test.git
   ```

### Open the Project in Android Studio

1. Open **Android Studio**.
2. Select **File > Open** and navigate to the cloned repository folder.
3. Open the `Test` project.

### Build the App

1. **Sync the Gradle files** to ensure all dependencies are resolved.
   
   - Click on **Sync Now** when prompted in Android Studio.
   
2. **Build the app**:

   - Click on **Build > Make Project** to compile the app.

3. **Run the app** on an emulator or physical Android device.

   - Click on **Run > Run 'app'** to install and run the app on a connected device.

## Usage

Once the app is launched on your device, it will show the following:

### Battery Draining Apps
This section lists apps that consume a significant amount of battery. The app identifies high battery usage apps by tracking power consumption.

### Background Running Apps
This section displays a list of all apps running in the background, even those not currently in use.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

### Additional Notes:

1. **For Battery Stats Access**: Make sure to declare the necessary permissions (`BATTERY_STATS`) in the `AndroidManifest.xml`, though note that access to battery stats might require a rooted device in some cases or might only be available to system apps.
   
2. **Background App Detection**: Depending on the Android version and device restrictions, you might need to use `UsageStatsManager` or other methods to get accurate information on background-running apps.

