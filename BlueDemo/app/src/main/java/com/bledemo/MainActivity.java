package com.bledemo;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bledemo.com.bledemo.event.CommonEvent;
import com.bledemo.com.bledemo.event.ConnectEvent;
import com.bledemo.com.bledemo.event.DeviceEvent;
import com.bledemo.com.bledemo.event.NotifyEvent;
import com.bledemo.com.bledemo.event.WriteEvent;
import com.clj.fastble.conn.BleCharacterCallback;
import com.clj.fastble.data.ScanResult;
import com.clj.fastble.exception.BleException;
import com.clj.fastble.utils.HexUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    @Bind(R.id.open_notify)
    Button btn_notify;
    @Bind(R.id.bind_device)
    Button btn_bind;
    @Bind(R.id.control_work)
    Button btn_work;
    @Bind(R.id.find_ble_device)
    Button btn_find;
    @Bind(R.id.connect_ble_device)
    Button btn_connect;
    @Bind(R.id.device_info)
    TextView tv_device_info;
    @Bind(R.id.work_info)
    TextView tv_work_info;
    private BluetoothService mBluetoothService;
    private BluetoothGatt gatt;
    ScanResult result;
    String serviceUUID;
    String uuid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        checkPermissions();
    }
    private void initView(){
        EventBus.getDefault().register(this);
        ButterKnife.bind(this);
    }
    @OnClick({R.id.connect_ble_device,R.id.find_ble_device,R.id.open_notify,R.id.bind_device,R.id.control_work})
    public void onClick(View view) {
        switch (view.getId()) {
            //查找设备
            case R.id.find_ble_device:
                btn_find.setClickable(false);
                startScan();
                break;
            //连接设备
            case R.id.connect_ble_device:
                LogUtils.LogV("startConnectDevice");
                btn_connect.setClickable(false);
                mBluetoothService.connectDevice(result);
                break;
            //认证
            case R.id.open_notify:
                LogUtils.LogV("start Notification");
                btn_notify.setClickable(false);
                startNotify();
                break;
            //绑定
            case R.id.bind_device:
                btn_bind.setClickable(false);
                startWrite(C.BleCommand.BIND);
                break;
            //检测
            case R.id.control_work:
                startWrite(C.BleCommand.WATER_QUALITY);
                break;
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void checkPermissions() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
        List<String> permissionDeniedList = new ArrayList<>();
        for (String permission : permissions) {
            int permissionCheck = ContextCompat.checkSelfPermission(this, permission);
            if (permissionCheck == PackageManager.PERMISSION_GRANTED) {
                onPermissionGranted(permission);
            } else {
                permissionDeniedList.add(permission);
            }
        }
        if (!permissionDeniedList.isEmpty()) {
            String[] deniedPermissions = permissionDeniedList.toArray(new String[permissionDeniedList.size()]);
            ActivityCompat.requestPermissions(this, deniedPermissions, 12);
        }
    }
    private void onPermissionGranted(String permission) {
        switch (permission) {
            case Manifest.permission.ACCESS_FINE_LOCATION:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !checkGPSIsOpen()) {
                    new AlertDialog.Builder(this)
                            .setTitle(R.string.notifyTitle)
                            .setMessage(R.string.gpsNotifyMsg)
                            .setNegativeButton(R.string.cancel,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            finish();
                                        }
                                    })
                            .setPositiveButton(R.string.setting,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                            startActivityForResult(intent, 1);
                                        }
                                    })

                            .setCancelable(false)
                            .show();
                } else {
                    startScan();
                }
                break;
        }
    }

    private boolean checkGPSIsOpen() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager == null)
            return false;
        return locationManager.isProviderEnabled(android.location.LocationManager.GPS_PROVIDER);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (checkGPSIsOpen()) {
                //startScan();
            }
        }
    }

    private void startNotify(){
        boolean status = mBluetoothService.notify(serviceUUID,uuid,new BleCharacterCallback() {
            @Override
            public void onSuccess(BluetoothGattCharacteristic characteristic) {
                LogUtils.LogV("notifySuccess");
            }

            @Override
            public void onFailure(BleException exception) {
                LogUtils.LogV("notifySuccess");
            }

            @Override
            public void onInitiatedResult(boolean result) {
                LogUtils.LogV("onInitiatedResult:"+result);
            }
        });
        if (status) {
            SystemClock.sleep(500);
            EventBus.getDefault().post(new WriteEvent(gatt, "ff12", "ff01",C.BleCommand.CERTIFICATION));
            btn_bind.setClickable(true);
        }
    }
    private void startWrite(byte[] data){
        EventBus.getDefault().post(new WriteEvent(gatt, "ff12", "ff01",data));
    }

    /**
     * 开始扫描BLE设备
     */
    private void startScan() {
        if (mBluetoothService == null) {
            bindService();
        } else {
            mBluetoothService.scanDevice();
        }
    }
    private void bindService() {
        Intent bindIntent = new Intent(this, BluetoothService.class);
        this.bindService(bindIntent, mFhrSCon, Context.BIND_AUTO_CREATE);
    }
    private ServiceConnection mFhrSCon = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBluetoothService = ((BluetoothService.BluetoothBinder) service).getService();
            //mBluetoothService.setScanCallback(callback);
            mBluetoothService.scanDevice();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBluetoothService = null;
        }
    };

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void startConnectDevice(ConnectEvent event){
        //LogUtils.LogV("startConnectDevice");
        btn_connect.setClickable(true);
        this.result = event.getResult();
        //mBluetoothService.connectDevice(event.getResult());
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void enableBtn(CommonEvent event){

    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void startNotifyDevice(NotifyEvent event){
        btn_notify.setClickable(true);
        MainActivity.this.gatt = event.getGatt();
            //LogUtils.LogV("start Notification");
        serviceUUID = event.getService_UUID();
        uuid = event.getUUId();
        if(false) {
            boolean status = mBluetoothService.notify(event.getService_UUID(), event.getUUId(), new BleCharacterCallback() {
                @Override
                public void onSuccess(BluetoothGattCharacteristic characteristic) {
                    LogUtils.LogV("notifySuccess");
                }

                @Override
                public void onFailure(BleException exception) {
                    LogUtils.LogV("notifySuccess");
                }

                @Override
                public void onInitiatedResult(boolean result) {
                    LogUtils.LogV("onInitiatedResult:" + result);
                }
            });
            if (status) {
                SystemClock.sleep(500);
                EventBus.getDefault().post(new WriteEvent(gatt, "ff12", "ff01",C.BleCommand.BIND));
            }
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void deviceInfo(DeviceEvent event){
        tv_device_info.setText("deviceName:"+event.getDevice_name()+"=====deviceAddress:"+event.getDevice_address());
        btn_connect.setClickable(true);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void startWriteDevice(WriteEvent event){
            LogUtils.LogV("startWriteDevice  data is :"+HexUtil.bytesToHexString(event.getData()));
            /*byte crc8 = CRC8Util.calcCrc8(new byte[]{0x00, 0x04, 0x04, 0x00, 0x32});
            LogUtils.LogV("value of crc8=====" + Integer.toHexString(0x00ff & crc8));
            byte[] data = new byte[] {0x3a, 0x00, 0x04, 0x04,0x00,0x32,crc8};*/
             mBluetoothService.writedevice(event.getService_UUID(), event.getUUId(), event.getData(), new BleCharacterCallback() {
                 @Override
                 public void onSuccess(BluetoothGattCharacteristic characteristic) {
                     LogUtils.LogV("write success:"+String.valueOf(HexUtil.encodeHex(characteristic.getValue())));
                 }

                 @Override
                 public void onFailure(BleException exception) {
                     LogUtils.LogV("write onFailure");
                 }

                 @Override
                 public void onInitiatedResult(boolean result) {
                     LogUtils.LogV("write onInitiatedResult:"+result);
                 }
             });
        }


}

