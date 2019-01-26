package com.bledemo.com.bledemo.event;

import android.bluetooth.BluetoothGatt;

import com.bledemo.LogUtils;

/**
 * Created by wesker on 2017/11/1714:44.
 */

public class NotifyEvent {
    String service_UUID;
    String UUId;
    BluetoothGatt gatt;
    final static String uuid = "-0000-1000-8000-00805f9b34fb";//手机端协议统一部分

    /**
     * 拼接返回完整的uuid
     * @param mService_UUID Service
     * @param mUUId uuid
     */
    public NotifyEvent(BluetoothGatt gatt,String mService_UUID, String mUUId) {

        this.gatt = gatt;

        StringBuilder service = new StringBuilder(uuid);
        service.insert(0, "0000");
        service_UUID = service.insert(4, mService_UUID).toString();
        StringBuilder UUID = new StringBuilder(uuid);
        UUID.insert(0, "0000");
        UUId = UUID.insert(4, mUUId).toString();
        LogUtils.LogE("service_UUID:"+service_UUID+"----UUID:"+UUId);
    }

    public BluetoothGatt getGatt() {
        return gatt;
    }

    public void setGatt(BluetoothGatt mGatt) {
        gatt = mGatt;
    }

    public String getService_UUID() {
        return service_UUID;
    }

    public void setService_UUID(String mService_UUID) {
        service_UUID = mService_UUID;
    }

    public String getUUId() {
        return UUId;
    }

    public void setUUId(String mUUId) {
        UUId = mUUId;
    }
}
