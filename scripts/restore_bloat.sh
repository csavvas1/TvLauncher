#!/bin/bash
# Re-enables all disabled TCL bloatware
# Run: bash scripts/restore_bloat.sh

PACKAGES=(
  "com.tcl.tv.tclhome"
  "com.tcl.smartsolution"
  "com.tcl.channel.store"
  "com.tcl.tclplus"
  "com.pluto.tv"
  "com.amazon.bueller"
  "com.tcl.tv.tvplus"
  "com.tcl.ui.launcher"
)

echo "Restoring TCL bloatware..."
for pkg in "${PACKAGES[@]}"; do
  echo -n "  $pkg ... "
  adb shell pm enable "$pkg" 2>/dev/null && echo "enabled" || echo "not found (skipped)"
done

echo ""
echo "Restore original launcher:"
echo "  adb shell cmd package set-home-activity com.tcl.tv/.launcher.TclLauncherActivity"
