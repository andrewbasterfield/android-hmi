# Quickstart: UI Animations and Gauge Improvement

## Objective
Enhance user interaction through 3D tactile button feedback and high-fidelity 270° gauge visualization.

## Setup
1. Open the HMI app on a physical device or emulator.
2. Navigate to the **Dashboard**.

## Verify 3D Button Animation
1. Tap and hold any **Button** widget.
2. Observe the "obvious" 3D depression (button shrinks AND shadow/elevation reduces).
3. Release the button and observe the spring-back effect.
4. If on a physical device with haptics, feel the short vibration pulse.

## Verify 270° Gauge Improvement
1. Locate a **Gauge** widget (or add one).
2. Note the standard 270° "Three-Quarter" dial arc.
3. Observe the smooth needle movement (60fps) during value updates.
4. Verify that tick marks always fall on logical decimal intervals (e.g., 0, 10, 20... NOT 0, 14.3, 28.6...).
5. Edit the Gauge (Edit Mode -> Long-press) to add multiple **Color Zones** (e.g., 0-80 Green, 80-90 Yellow, 90-100 Red).
6. Observe the colored arcs appearing correctly on the gauge face.

## Verify Haptic Settings
1. Tap the **Dashboard Settings** (gear icon) in the top bar.
2. Toggle the **Enable Haptic Feedback** switch.
3. Confirm that button presses no longer vibrate when disabled.
