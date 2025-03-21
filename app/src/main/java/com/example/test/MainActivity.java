package com.example.test;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class MainActivity extends AppCompatActivity {
    private LinearLayout backgroundAppsLayout;
    private LinearLayout batteryDrainingAppsLayout;
    private Set<String> addedAppsSet;
    private ActivityResultLauncher<Intent> usageAccessLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        backgroundAppsLayout = findViewById(R.id.backgroundAppsLayout);
        batteryDrainingAppsLayout = findViewById(R.id.batteryDrainingAppsLayout);
        addedAppsSet = new HashSet<>();

        usageAccessLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (isUsageStatsPermissionGranted()) {
                        loadApps();
                    } else {
                        Toast.makeText(this, "Permission not granted", Toast.LENGTH_SHORT).show();
                    }
                });

        if (isUsageStatsPermissionGranted()) {
            loadApps();
        } else {
            Toast.makeText(this, "Please enable usage access in settings.", Toast.LENGTH_SHORT).show();
            usageAccessLauncher.launch(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));
        }
    }

    private boolean isUsageStatsPermissionGranted() {
        UsageStatsManager usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
        long currentTime = System.currentTimeMillis();
        List<UsageStats> stats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, currentTime - 1000 * 60, currentTime);
        return stats != null && !stats.isEmpty();
    }

    private void loadApps() {
        UsageStatsManager usageStatsManager = (UsageStatsManager) getSystemService(Context.USAGE_STATS_SERVICE);
        long currentTime = System.currentTimeMillis();
        long oneWeekAgo = currentTime - (1000 * 60 * 60 * 24 * 7);

        List<UsageStats> stats = usageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, oneWeekAgo, currentTime);

        if (stats != null && !stats.isEmpty()) {
            processAndDisplayApps(stats);
        } else {
            Toast.makeText(this, "No usage stats available.", Toast.LENGTH_SHORT).show();
        }
    }

    private void processAndDisplayApps(List<UsageStats> stats) {
        PackageManager packageManager = getPackageManager();

        backgroundAppsLayout.removeAllViews();
        batteryDrainingAppsLayout.removeAllViews();
        addedAppsSet.clear();

        long currentTime = System.currentTimeMillis();
        long oneWeekAgo = currentTime - (1000 * 60 * 60 * 24 * 7);

        Collections.sort(stats, new Comparator<UsageStats>() {
            @Override
            public int compare(UsageStats u1, UsageStats u2) {
                return Long.compare(u2.getTotalTimeInForeground(), u1.getTotalTimeInForeground());
            }
        });

        for (UsageStats usageStats : stats) {
            String packageName = usageStats.getPackageName();
            long totalTimeInForeground = usageStats.getTotalTimeInForeground();

            if (addedAppsSet.contains(packageName)) continue;

            String appName = getAppName(packageName, packageManager);
            Drawable appIcon = getAppIcon(packageName, packageManager);

            String lastUsed = formatLastUsed(usageStats.getLastTimeUsed());
            String timeUsed = formatTime(totalTimeInForeground);

            long timeSinceLastUsed = currentTime - usageStats.getLastTimeUsed();
            long totalTimeInBackground = timeSinceLastUsed - totalTimeInForeground;

            if (totalTimeInBackground < 0) {
                totalTimeInBackground = 0;
            }

            if (totalTimeInBackground > 1000 * 60 * 10 && totalTimeInForeground < 1000 * 60 * 5 &&
                    usageStats.getLastTimeUsed() >= oneWeekAgo) {
                addAppToLayout(backgroundAppsLayout, appName, packageName, lastUsed, timeUsed, appIcon, formatTime(totalTimeInBackground));
            }

            if (totalTimeInForeground > 1000 * 60 * 30) {
                addAppNameToLayout(batteryDrainingAppsLayout, appName);
            }

            addedAppsSet.add(packageName);
        }
    }

    private String formatTime(long timeInMillis) {
        if (timeInMillis <= 0) {
            return "No data";
        }

        long seconds = timeInMillis / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        minutes = minutes % 60;
        seconds = seconds % 60;

        if (hours >= 24 * 365) {
            return "More than a year";
        } else if (hours > 24) {
            return String.format("%d days, %d hours, %d minutes, %d seconds", hours / 24, hours % 24, minutes, seconds);
        } else if (hours > 0) {
            return String.format("%d hours, %d minutes, %d seconds", hours, minutes, seconds);
        } else if (minutes > 0) {
            return String.format("%d minutes, %d seconds", minutes, seconds);
        } else {
            return String.format("%d seconds", seconds);
        }
    }

    private void addAppToLayout(LinearLayout layout, String appName, String packageName, String lastUsed, String timeUsed, Drawable appIcon, String backgroundTime) {
        CardView appCardView = new CardView(this);
        appCardView.setCardElevation(8);
        appCardView.setRadius(12);
        appCardView.setContentPadding(16, 16, 16, 16);
        appCardView.setUseCompatPadding(true);

        LinearLayout appLayout = new LinearLayout(this);
        appLayout.setOrientation(LinearLayout.HORIZONTAL);

        ImageView appIconView = new ImageView(this);
        appIconView.setImageDrawable(appIcon);
        appIconView.setLayoutParams(new LinearLayout.LayoutParams(100, 100));
        appIconView.setPadding(8, 8, 8, 8);

        LinearLayout textLayout = new LinearLayout(this);
        textLayout.setOrientation(LinearLayout.VERTICAL);
        textLayout.setPadding(16, 0, 0, 0);

        TextView nameView = new TextView(this);
        nameView.setText("App Name: " + appName);
        nameView.setTextSize(16);
        nameView.setTextColor(getResources().getColor(android.R.color.black));

        TextView packageView = new TextView(this);
        packageView.setText("Package: " + packageName);
        packageView.setTextSize(14);
        packageView.setTextColor(getResources().getColor(android.R.color.darker_gray));

        TextView backgroundTimeView = new TextView(this);
        backgroundTimeView.setText("Total Time in Background: " + backgroundTime);

        textLayout.addView(nameView);
        textLayout.addView(packageView);
        textLayout.addView(backgroundTimeView);

        appLayout.addView(appIconView);
        appLayout.addView(textLayout);
        appCardView.addView(appLayout);

        appCardView.setOnClickListener(v -> openAppSettings(packageName));

        layout.addView(appCardView);
    }

    private void addAppNameToLayout(LinearLayout layout, String packageName) {
        String appName = getAppName(packageName, getPackageManager());

        TextView appNameView = new TextView(this);
        appNameView.setText(appName);
        appNameView.setTextSize(16);
        appNameView.setTextColor(getResources().getColor(android.R.color.black));
        appNameView.setPadding(16, 16, 16, 16);

        LinearLayout textLayout = new LinearLayout(this);
        textLayout.setOrientation(LinearLayout.HORIZONTAL);
        textLayout.setLayoutParams(new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));

        appNameView.setGravity(Gravity.END | Gravity.CENTER_VERTICAL);

        appNameView.setOnClickListener(v -> openAppSettings(packageName));

        textLayout.addView(appNameView);

        layout.addView(textLayout);
    }

    private String formatLastUsed(long timeInMillis) {
        if (timeInMillis == 0) {
            return "Not used recently";
        }

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
        return sdf.format(new Date(timeInMillis));
    }

    private String getAppName(String packageName, PackageManager packageManager) {
        String appName = packageName;

        try {
            appName = packageManager.getApplicationLabel(packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)).toString();
            if (appName == null || appName.isEmpty()) {
                appName = packageName;
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            appName = packageName;
        }

        Log.d("App Name Debug", "App Name: " + appName + ", Package: " + packageName);
        return appName;
    }

    private Drawable getAppIcon(String packageName, PackageManager packageManager) {
        Drawable appIcon = null;
        try {
            appIcon = packageManager.getApplicationIcon(packageName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            appIcon = getResources().getDrawable(R.drawable.default_icon);
        }
        return appIcon;
    }

    private void openAppSettings(String packageName) {
        Intent intentSettings = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intentSettings.setData(android.net.Uri.parse("package:" + packageName));
        startActivity(intentSettings);
    }
}
