Custom Android Embed Board with External USB Camera Support

This project demonstrates the integration of an external USB camera with an Android embed board. The system utilizes UsbManager and UsbDevice for managing USB device connections, and the libusbcamera library for handling the camera functionality.

System Requirements

Ensure the following permissions and features are declared in your AndroidManifest.xml:

xml
Copy code
<uses-permission android:name="android.permission.CAMERA" />
<uses-permission android:name="android.permission.INTERNET"/>
<uses-feature android:name="android.hardware.camera" />
<uses-permission android:name="android.permission.USB_PERMISSION" />
<uses-permission android:name="android.hardware.usb.action.USB_DEVICE_ATTACHED" />
<uses-permission android:name="android.hardware.usb.action.USB_DEVICE_DETACHED" />
Layout

Use a TextureView to preview the camera feed in your layout:

xml
Copy code
<TextureView
    android:id="@+id/textureView1"
    android:layout_width="match_parent"
    android:layout_height="match_parent"/>
<TextureView
    android:id="@+id/textureView2"
    android:layout_width="match_parent"
    android:layout_height="match_parent"/>
Library Integration

USB Monitor for Permission
Include the USBMonitor class to manage USB permissions.

java
Copy code
private final USBMonitor.OnDeviceConnectListener onDeviceConnectListener = new USBMonitor.OnDeviceConnectListener() {
    @Override
    public void onAttach(UsbDevice device) {
        // Open and initialize UVCCamera when USB device attached
        if (isUsbCamera(device)) {
            usbMonitor.requestPermission(device);
        }
    }
};
UVCCamera for Device Connection
Handle the connection and preview of the USB camera using the UVCCamera class.

java
Copy code
public void onConnect(UsbDevice device, USBMonitor.UsbControlBlock ctrlBlock, boolean createNew) {
    if (isUsbCamera(device)) {
        if (camera1 == null) {
            Log.d(TAG, "Connecting to camera1: " + device.getDeviceName());
            camera1 = new UVCCamera();
            camera1.open(ctrlBlock);
            startPreview(camera1, textureView1);
        } else if (camera2 == null) {
            Log.d(TAG, "Connecting to camera2: " + device.getDeviceName());
            camera2 = new UVCCamera();
            camera2.open(ctrlBlock);
            startPreview2(camera2, textureView2);
        }
    }
}
Usage

USBMonitor Initialization
Initialize the USBMonitor and set the OnDeviceConnectListener to handle device connections.

java
Copy code
USBMonitor usbMonitor = new USBMonitor(this, onDeviceConnectListener);
usbMonitor.register();
Starting the Preview
Define methods to start the camera preview using the TextureView.

java
Copy code
private void startPreview(UVCCamera camera, TextureView textureView) {
    // Set up the camera preview display
    SurfaceTexture surfaceTexture = textureView.getSurfaceTexture();
    if (surfaceTexture != null) {
        Surface surface = new Surface(surfaceTexture);
        camera.setPreviewDisplay(surface);
        camera.startPreview();
    }
}

private void startPreview2(UVCCamera camera, TextureView textureView) {
    // Set up the second camera preview display
    SurfaceTexture surfaceTexture = textureView.getSurfaceTexture();
    if (surfaceTexture != null) {
        Surface surface = new Surface(surfaceTexture);
        camera.setPreviewDisplay(surface);
        camera.startPreview();
    }
}
Acknowledgments

This project is based on the work of saki4510t.
