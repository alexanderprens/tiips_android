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
import android.content.ContentValues;
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
import android.widget.Toast;

import org.helpingkidsroundfirst.hkrf.R;
import org.helpingkidsroundfirst.hkrf.data.InventoryContract;
import org.helpingkidsroundfirst.hkrf.helper_classes.Utility;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

/**
 * A simple {@link Fragment} subclass.
 */
public class GetLocationDataFragment extends Fragment {

    public static final int NUM_BEACONS = 12;
    public static final String[] CONST_UUIDS = {
            "00003a00-0000-1000-8000-00805f9b34fb",
            "00003a01-0000-1000-8000-00805f9b34fb",
            "00003a02-0000-1000-8000-00805f9b34fb",
            "00003a03-0000-1000-8000-00805f9b34fb",
            "00003a04-0000-1000-8000-00805f9b34fb",
            "00003a05-0000-1000-8000-00805f9b34fb",
            "00003a06-0000-1000-8000-00805f9b34fb",
            "00003a07-0000-1000-8000-00805f9b34fb",
            "00003a08-0000-1000-8000-00805f9b34fb",
            "00003a09-0000-1000-8000-00805f9b34fb",
            "00003a0a-0000-1000-8000-00805f9b34fb",
            "00003a0b-0000-1000-8000-00805f9b34fb",
            "00003a0c-0000-1000-8000-00805f9b34fb"
    };
    public static final int TAG_SERVICE_UUID = 0;
    public static final int BEACON_M_CHAR_UUID = 12;
    private static final int REQUEST_ENABLE_BT = 10;
    private static final long SCAN_PERIOD = 500;
    private static final String MASTER_NAME = "00:0B:57:35:FD:10";
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBluetoothLeScanner;
    private Handler mHandler;
    private TextView textView;
    private BluetoothGatt mBluetoothGatt;
    private BluetoothDevice mDevice = null;
    private List<BluetoothGattCharacteristic> chars = new ArrayList<>();
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
        }

        @Override
        public void onConnectionStateChange(final BluetoothGatt gatt, final int status, final int newState) {
            // this will get called when a device connects or disconnects
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                mBluetoothGatt.discoverServices();
                textView.setText(getResources().getString(R.string.reading_data));
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED || newState == BluetoothProfile.STATE_DISCONNECTING) {
                ((ScanBLEListener) getActivity()).disconnected();
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

    public GetLocationDataFragment() {
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

        if (mBluetoothGatt != null) {
            mBluetoothGatt.disconnect();
            mBluetoothGatt.close();
        }

        // Initializes list view adapter.
        scanLeDevice(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        scanLeDevice(false);
        if (mBluetoothGatt != null) {
            mBluetoothGatt.disconnect();
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
            String name = mDevice.getAddress();

            if (name != null && name.equals(MASTER_NAME)) {
                // connect to gatt server
                textView.setText(getResources().getString(R.string.connecting_to_master));
                mBluetoothGatt = mDevice.connectGatt(getContext(), true, mGattCallback);
                refreshDeviceCache(mBluetoothGatt);
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

                if (uuidString.equals(CONST_UUIDS[TAG_SERVICE_UUID])) {
                    List<BluetoothGattCharacteristic> characteristics = service.getCharacteristics();

                    for (BluetoothGattCharacteristic characteristic : characteristics) {

                        // if readable, send read request
                        final int charaProp = characteristic.getProperties();
                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                            chars.add(characteristic);
                        }
                    }
                }
            }

            if (!chars.isEmpty()) {
                mBluetoothGatt.readCharacteristic(chars.get(chars.size() - 1));
            }
        }
    }

    private void readCharResponse(BluetoothGattCharacteristic characteristic) {

        String charUUID = characteristic.getUuid().toString();
        boolean isKnown = false;

        // check if characteristic is known
        for (int i = 1; i <= NUM_BEACONS; i++) {
            if (charUUID.equals(CONST_UUIDS[i])) {
                isKnown = true;
                break;
            }
        }

        // if characteristic is know, put in database
        if (isKnown) {
            parseCharacteristicData(characteristic, charUUID);
        }

        chars.remove(characteristic);

        // check if there are more characteristics to be read
        if (chars.size() > 0) {
            mBluetoothGatt.readCharacteristic(chars.get(chars.size() - 1));
        } else {
            mBluetoothGatt.disconnect();
            mBluetoothGatt.close();
            ((ScanBLEListener) getActivity()).scanComplete();
        }
    }

    private void parseCharacteristicData(BluetoothGattCharacteristic characteristic,
                                         String uuid) {
        ContentValues contentValues = new ContentValues();

        // get id
        contentValues.put(InventoryContract.TagEntry.COLUMN_ID, uuid);

        // get current data
        final Calendar calendar = Calendar.getInstance();
        String date = Utility.getDatePickerString(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DATE));
        contentValues.put(InventoryContract.TagEntry.COLUMN_DATE, date);

        // get characteristic data
        byte[] data = characteristic.getValue();
        final StringBuilder stringBuilder = new StringBuilder(data.length);
        for (byte byteChar : data)
            stringBuilder.append(String.format("%02X", byteChar));
        String charValue = stringBuilder.toString();

        // parse battery level
        String battHex = charValue.substring(0, 4);
        Long battLong = Long.parseLong(battHex, 16);
        double battDouble = (double) battLong / 1000.0;
        contentValues.put(InventoryContract.TagEntry.COLUMN_BATTERY, battDouble);

        // check if tag or beacon characteristic
        if (charValue.length() > 4) {
            // tag
            // parse missing
            String rssiString = charValue.substring(6, 8) + charValue.substring(4, 6);
            contentValues.put(InventoryContract.TagEntry.COLUMN_MISSING, rssiString);
        }

        // put into table
        putDataIntoTable(contentValues);
    }

    private void putDataIntoTable(ContentValues contentValues) {

        // update tag info
        Uri tagUri = InventoryContract.TagEntry.buildTagUri();
        String selection = InventoryContract.TagEntry.COLUMN_ID + " = ? ";
        String[] selectionArgs = {contentValues.getAsString(InventoryContract.TagEntry.COLUMN_ID)};

        try {
            getContext().getContentResolver().update(
                    tagUri,
                    contentValues,
                    selection,
                    selectionArgs
            );
        } catch (Exception e) {
            String message = e.getMessage();
            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    private boolean refreshDeviceCache(BluetoothGatt gatt) {
        try {
            BluetoothGatt localBluetoothGatt = gatt;
            Method localMethod = localBluetoothGatt.getClass().getMethod("refresh", new Class[0]);
            if (localMethod != null) {
                boolean bool = ((Boolean) localMethod.invoke(localBluetoothGatt, new Object[0])).booleanValue();
                return bool;
            }
        } catch (Exception localException) {
            //Log.e(TAG, "An exception occured while refreshing device");
        }
        return false;
    }

    public interface ScanBLEListener {
        void BTNotEnabled();
        void scanComplete();
        void disconnected();
    }
}
