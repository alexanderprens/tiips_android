package org.helpingkidsroundfirst.hkrf.navigation_bar_activities.ips.locate_item.show_location;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.helpingkidsroundfirst.hkrf.R;
import org.helpingkidsroundfirst.hkrf.data.InventoryContract;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class AlertTagFragment extends Fragment {

    public static final String URI_KEY = "uri_key";
    private static final String[] TAG_DEVICE_NAMES = {
            "00:0B:57:36:03:3B",
            "00:0B:57:36:03:33",
            "00:0B:57:36:03:34",
            "00:0B:57:35:FD:0B",
            "00:0B:57:34:C8:FE",
            "00:0B:57:35:FD:06",
            "00:0B:57:36:03:2C",
            "00:0B:57:35:E9:D3",
            "00:0B:57:34:CA:A0",
            "00:0B:57:35:FD:07",
            "00:0B:57:35:EE:CF"
    };
    private static final String ALERT_SERVICE_UUID = "00004a00-0000-1000-8000-00805f9b34fb";
    private static final String ALERT_CHAR_UUID = "00004a01-0000-1000-8000-00805f9b34fb";
    private static final long SCAN_PERIOD = 500;
    private static final int REQUEST_ENABLE_BT = 10;
    private static final byte[] CHAR_VALUE = {0x01};
    private TextView textView;
    private String tagDeviceName;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBluetoothLeScanner;
    private Handler mHandler;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothDevice mDevice = null;
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            ((AlertTagListener) getActivity()).writeComplete();
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
            // this will get called anytime you perform a read or write characteristic operation
            ((AlertTagListener) getActivity()).writeComplete();
        }

        @Override
        public void onConnectionStateChange(final BluetoothGatt gatt, final int status, final int newState) {
            // this will get called when a device connects or disconnects
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                mBluetoothGatt.discoverServices();
                textView.setText(getResources().getString(R.string.writing_data));
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED || newState == BluetoothProfile.STATE_DISCONNECTING) {
                ((AlertTagListener) getActivity()).disconnected();
            }
        }

        @Override
        public void onServicesDiscovered(final BluetoothGatt gatt, final int status) {
            // this will get called after the client initiates a BluetoothGatt.discoverServices() call
            sendCharWrite();
        }
    };
    // Device scan callback.
    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            connectToGatt(result);
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            for (ScanResult sr : results) {
                connectToGatt(sr);
            }
        }

        @Override
        public void onScanFailed(int errorCode) {

        }
    };

    public AlertTagFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_alert_tag, container, false);

        // init variables
        Uri mUri = null;
        tagDeviceName = "";

        // get id
        Bundle bundle = this.getArguments();
        if (bundle != null && bundle.containsKey(URI_KEY)) {
            mUri = bundle.getParcelable(URI_KEY);
        }

        long id = InventoryContract.TagEntry.getTagIdFromUri(mUri) - 1;
        tagDeviceName = TAG_DEVICE_NAMES[(int) id];

        textView = (TextView) rootView.findViewById(R.id.alert_tag_text);

        mHandler = new Handler();

        final BluetoothManager bluetoothManager = (BluetoothManager)
                getContext().getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        if (mBluetoothAdapter == null) {
            ((AlertTagListener) getActivity()).BTNotEnabled();
        }

        textView.setText(getResources().getString(R.string.scanning_devices));

        return rootView;
    }


    @Override
    public void onResume() {
        super.onResume();
        // Ensures Bluetooth is enabled on the device.  If Bluetooth is not currently enabled,
        // fire an intent to display a dialog asking the user to grant permission to enable it.
        if (!mBluetoothAdapter.isEnabled()) {
            if (!mBluetoothAdapter.isEnabled()) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            } else {
                mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
            }
        } else {
            mBluetoothLeScanner = mBluetoothAdapter.getBluetoothLeScanner();
        }
        // Initializes list view adapter.
        scanLeDevice(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        scanLeDevice(false);
        if (mBluetoothGatt != null) {
            mBluetoothGatt.close();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // User chose not to enable Bluetooth.
        if (requestCode == REQUEST_ENABLE_BT && resultCode == Activity.RESULT_CANCELED) {
            ((AlertTagListener) getActivity()).BTNotEnabled();
            return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void scanLeDevice(final boolean enable) {
        if (enable) {
            // Stops scanning after a pre-defined scan period.
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mBluetoothLeScanner.startScan(mScanCallback);
                }
            }, SCAN_PERIOD);
        } else {
            mBluetoothLeScanner.stopScan(mScanCallback);
        }
    }

    private void connectToGatt(ScanResult result) {
        // check if selected is a master beacon
        mDevice = result.getDevice();
        if (mDevice != null) {
            String name = mDevice.getAddress();

            if (name != null && name.equals(tagDeviceName)) {
                // connect to gatt server
                textView.setText(getResources().getString(R.string.connecting_to_tag));
                mBluetoothGatt = mDevice.connectGatt(getContext(), true, mGattCallback);
                scanLeDevice(false);
            }
        }
    }

    private void sendCharWrite() {

        if (mDevice != null) {
            List<BluetoothGattService> services = mBluetoothGatt.getServices();

            for (BluetoothGattService service : services) {

                String uuid = service.getUuid().toString();

                if (uuid.equals(ALERT_SERVICE_UUID)) {
                    List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();

                    for (BluetoothGattCharacteristic characteristic : characteristics) {
                        String charUUID = characteristic.getUuid().toString();

                        if (charUUID.equals(ALERT_CHAR_UUID)) {
                            characteristic.setValue(CHAR_VALUE);
                            mBluetoothGatt.writeCharacteristic(characteristic);
                        }
                    }
                }
            }
        }
    }

    public interface AlertTagListener {
        void BTNotEnabled();

        void writeComplete();

        void disconnected();
    }
}
