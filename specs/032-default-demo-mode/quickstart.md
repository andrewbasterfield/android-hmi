# Quickstart: Testing Default Demo Mode

1. **Clean Environment**: Clear the app's data or uninstall/reinstall the app to ensure no saved layouts exist.
2. **Launch Application**: Open the app. It should immediately display the "Demo Layout" containing a text instruction widget and active gauges (Temperature, Pressure).
3. **Verify Connection**: The `DemoPlcServer` runs automatically on `localhost:9999`. The gauges should reflect varying simulated data (e.g., Temperature around 25.5, Pressure around 101.3).
4. **Save Behavior**: Attempt to modify the layout (if editing is allowed). When prompted to save, it should save as a new layout rather than overwriting the demo layout.
5. **Config Transfer**: Send a new configuration via JSON to the device. The app should smoothly transition from the demo layout to the new user-defined layout.