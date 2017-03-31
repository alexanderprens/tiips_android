package org.helpingkidsroundfirst.hkrf.navigation_bar_activities.ips.locate_item.scan_devices;


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
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.helpingkidsroundfirst.hkrf.R;

import java.util.List;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 */
public class ScanBLEDevicesFragment extends Fragment {

    private static final int REQUEST_ENABLE_BT = 10;
    private static final long SCAN_PERIOD = 500;
    private static final String MASTER_NAME = "TIIPS";
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBluetoothLeScanner;
    private Handler mHandler;
    private ProgressBar progressBar;
    private TextView textView;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothDevice mDevice = null;
    private int count = 0;
    private int numBeacons = 1;
    private final BluetoothGattCallback mGattCallback = new BluetoothGattCallback() {

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt,
                                         BluetoothGattCharacteristic characteristic,
                                         int status) {
            readCharResponse(characteristic);
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, final BluetoothGattCharacteristic characteristic) {
            // this will get called anytime you perform a read or write characteristic operation
            readCharResponse(characteristic);
        }

        @Override
        public void onConnectionStateChange(final BluetoothGatt gatt, final int status, final int newState) {
            // this will get called when a device connects or disconnects
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                mBluetoothGatt.discoverServices();
                textView.setText(getResources().getString(R.string.reading_data));
            }
        }

        @Override
        public void onServicesDiscovered(final BluetoothGatt gatt, final int status) {
            // this will get called after the client initiates a BluetoothGatt.discoverServices() call
            sendCharReads();
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

    public ScanBLEDevicesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_scan_ble_devices, container, false);

        mHandler = new Handler();

        final BluetoothManager bluetoothManager = (BluetoothManager)
                getContext().getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        if (mBluetoothAdapter == null) {
            ((ScanBLEListener) getActivity()).BTNotEnabled();
        }

        progressBar = (ProgressBar) rootView.findViewById(R.id.scan_ble_progress);
        textView = (TextView) rootView.findViewById(R.id.scan_ble_text);
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
            ((ScanBLEListener) getActivity()).BTNotEnabled();
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
            String name = mDevice.getName();

            if (name != null && name.equals(MASTER_NAME)) {
                // connect to gatt server
                textView.setText(getResources().getString(R.string.connecting_to_master));
                mBluetoothGatt = mDevice.connectGatt(getContext(), true, mGattCallback);
                scanLeDevice(false);
            }
        }
    }

    private void sendCharReads() {
        if (mDevice != null) {
            List<BluetoothGattService> services = mBluetoothGatt.getServices();

            for (BluetoothGattService service : services) {

                UUID uuid = service.getUuid();
                String uuidString = uuid.toString();

                if (uuidString.contains("00003651")) {
                    List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();

                    for (BluetoothGattCharacteristic characteristic : characteristics) {

                        // if readable, send read request
                        final int charaProp = characteristic.getProperties();
                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {

                            mBluetoothGatt.readCharacteristic(characteristic);
                        }
                    }
                }
            }
        }
    }

    private void readCharResponse(BluetoothGattCharacteristic characteristic) {
        byte[] data = characteristic.getValue();
        final StringBuilder stringBuilder = new StringBuilder(data.length);
        for (byte byteChar : data)
            stringBuilder.append(String.format("%02X", byteChar));
        String tempString = stringBuilder.toString();

        count++;
        if (count >= numBeacons) {
            ((ScanBLEListener) getActivity()).scanComplete();
        }
    }

    public interface ScanBLEListener {
        void BTNotEnabled();

        void scanComplete();
    }
}
