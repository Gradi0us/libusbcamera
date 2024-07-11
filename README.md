# libusbcamera - Custom AndroidembedBoard
Library using external camera (usb) for Android embed board

# System
UsbManager - Management usb device connection
UsbDevice - Get/set device

+, Manifest
    <uses-permission android:name="android.permission.CAMERA" />
    <uses-permission android:name="android.permission.INTERNET"/>
    <uses-feature android:name="android.hardware.camera" />
    <uses-permission android:name="android.permission.USB_PERMISSION" />
    <uses-permission android:name="android.hardware.usb.action.USB_DEVICE_ATTACHED" />
    <uses-permission android:name="android.hardware.usb.action.USB_DEVICE_DETACHED" />
    <uses-permission android:name="android.hardware.usb.action.USB_DEVICE_ATTACHED" />
    <uses-permission android:name="android.hardware.usb.action.USB_DEVICE_DETACHED" />
+, Layout
TextureView - Preview camera

# Library

USBMonitor for Permission

UVCCamera for connect device

# Demo

   private final USBMonitor.OnDeviceConnectListener onDeviceConnectListener = new USBMonitor.OnDeviceConnectListener() {
        @Override
        public void onAttach(UsbDevice device) {
            // Open and initialize UVCCamera when USB device attached
            if (isUsbCamera(device)) {
                usbMonitor.requestPermission(device);
            }
        }
}
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

source: saki4510t
