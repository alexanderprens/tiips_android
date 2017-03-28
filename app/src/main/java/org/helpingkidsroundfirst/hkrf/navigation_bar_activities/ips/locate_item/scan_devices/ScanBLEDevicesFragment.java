package org.helpingkidsroundfirst.hkrf.navigation_bar_activities.ips.locate_item.scan_devices;


import android.app.Activity;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
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
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.helpingkidsroundfirst.hkrf.R;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class ScanBLEDevicesFragment extends Fragment {

    private final static int REQUEST_ENABLE_BT = 10;
    private static final long SCAN_PERIOD = 10000;
    private ListView listView;
    private LeDeviceListAdapter mLeDeviceListAdapter;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothLeScanner mBluetoothLeScanner;
    private Handler mHandler;
    private ProgressDialog progressDialog;
    // Device scan callback.
    private ScanCallback mScanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);
            mLeDeviceListAdapter.addResult(result);
            mLeDeviceListAdapter.notifyDataSetChanged();
            progressDialog.hide();
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            for (ScanResult sr : results) {
                mLeDeviceListAdapter.addResult(sr);
                mLeDeviceListAdapter.notifyDataSetChanged();
            }
            progressDialog.hide();
        }

        @Override
        public void onScanFailed(int errorCode) {
            //Toast.makeText(getContext(), "FUCK", Toast.LENGTH_SHORT).show();
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

        listView = (ListView) rootView.findViewById(R.id.choose_locate_item_list);
        listView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((ScanBLEListener) getActivity()).onResultSelected(
                        mLeDeviceListAdapter.getScanResult(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        final BluetoothManager bluetoothManager = (BluetoothManager)
                getContext().getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        if (mBluetoothAdapter == null) {
            ((ScanBLEListener) getActivity()).BTNotEnabled();
        }

        progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage(getResources().getString(R.string.scanning_devices));
        progressDialog.show();

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
        mLeDeviceListAdapter = new LeDeviceListAdapter(getActivity());
        listView.setAdapter(mLeDeviceListAdapter);
        scanLeDevice(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        scanLeDevice(false);
        mLeDeviceListAdapter.clear();
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
            //mBluetoothLeScanner.startScan(mScanCallback);
        } else {
            mBluetoothLeScanner.stopScan(mScanCallback);
        }
    }

    public interface ScanBLEListener {
        void BTNotEnabled();

        void onResultSelected(ScanResult result);
    }

    static class ViewHolder {
        TextView deviceName;
        TextView deviceAddress;
        TextView deviceRSSI;
    }

    // Adapter for holding devices found through scanning.
    private class LeDeviceListAdapter extends BaseAdapter {
        private Context context;
        private ArrayList<ScanResult> mLeDevices;
        private LayoutInflater inflater;

        public LeDeviceListAdapter(Context c) {
            super();
            context = c;
            mLeDevices = new ArrayList<>();
            inflater = (LayoutInflater.from(context));
        }

        public void addResult(ScanResult result) {

            boolean exists = false;
            BluetoothDevice device = result.getDevice();

            for (ScanResult sr : mLeDevices) {
                if (device.equals(sr.getDevice())) {
                    exists = true;
                    mLeDevices.remove(sr);
                    mLeDevices.add(result);
                    break;
                }
            }
            if (!exists) {
                mLeDevices.add(result);
            }
        }

        public ScanResult getScanResult(int position) {
            return mLeDevices.get(position);
        }

        public void clear() {
            mLeDevices.clear();
        }

        @Override
        public int getCount() {
            return mLeDevices.size();
        }

        @Override
        public Object getItem(int i) {
            return mLeDevices.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            ViewHolder viewHolder;
            // General ListView optimization code.
            if (view == null) {
                view = inflater.inflate(R.layout.fragment_scan_ble_device_item, null);
                viewHolder = new ViewHolder();
                viewHolder.deviceAddress = (TextView) view.findViewById(R.id.scan_ble_adress);
                viewHolder.deviceName = (TextView) view.findViewById(R.id.scan_ble_name);
                viewHolder.deviceRSSI = (TextView) view.findViewById(R.id.scan_ble_rssi);
                view.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) view.getTag();
            }

            BluetoothDevice device = mLeDevices.get(i).getDevice();
            final String deviceName = device.getName();
            if (deviceName != null && deviceName.length() > 0)
                viewHolder.deviceName.setText(deviceName);
            else {
                viewHolder.deviceName.setText(R.string.unknown_device);
            }
            viewHolder.deviceAddress.setText(device.getAddress());
            String rssi = "" + mLeDevices.get(i).getRssi();
            viewHolder.deviceRSSI.setText(rssi);

            return view;
        }
    }
}
