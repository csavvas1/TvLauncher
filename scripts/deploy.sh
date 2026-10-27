#!/bin/bash
# Deploy launcher APK to TV via ADB
# Usage: bash scripts/deploy.sh <TV_IP> <TV_PORT>
# Example: bash scripts/deploy.sh 192.168.40.53 42247

TV_IP="${1:?Usage: deploy.sh <TV_IP> <TV_PORT>}"
TV_PORT="${2:?Usage: deploy.sh <TV_IP> <TV_PORT>}"
APK="app/build/outputs/apk/debug/app-debug.apk"

echo "Connecting to $TV_IP:$TV_PORT ..."
adb connect "$TV_IP:$TV_PORT"

echo "Installing APK..."
adb install -r "$APK"

echo "Setting as default launcher..."
adb shell cmd package set-home-activity com.stavros.tvlauncher/.LauncherActivity

echo "Done! Press Home on your remote."
