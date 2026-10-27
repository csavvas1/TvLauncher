#!/bin/bash
# Disables TCL bloatware via ADB (not deleted — fully reversible)
# Run: bash scripts/disable_bloat.sh

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

echo "Disabling TCL bloatware..."
for pkg in "${PACKAGES[@]}"; do
  echo -n "  $pkg ... "
  adb shell pm disable-user --user 0 "$pkg" 2>/dev/null && echo "disabled" || echo "not found (skipped)"
done
echo "Done."
