#!/bin/bash
# Full revert — restores TV to original state
# Run: bash scripts/revert_all.sh

echo "Step 1: Re-enable all bloatware..."
bash "$(dirname "$0")/restore_bloat.sh"

echo ""
echo "Step 2: Restore original launcher..."
adb shell cmd package set-home-activity com.tcl.tv/.launcher.TclLauncherActivity

echo ""
echo "Step 3: Uninstall custom launcher..."
adb uninstall com.stavros.tvlauncher

echo ""
echo "Revert complete. TV is back to factory defaults."
