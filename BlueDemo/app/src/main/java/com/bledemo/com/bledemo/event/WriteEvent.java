package com.bledemo.com.bledemo.event;

import android.bluetooth.BluetoothGatt;

import com.bledemo.C;
import com.bledemo.CRC8Util;
import com.bledemo.LogUtils;

/**
 * Created by wesker on 2017/11/169:55.
 */

public class WriteEvent {
    String service_UUID;
    String UUId;
    BluetoothGatt gatt;
    final static String uuid = "-0000-1000-8000-00805f9b34fb";//手机端协议统一部分
    byte[] data;
    /**
     * 拼接返回完整的uuid
     * @param mService_UUID Service
     * @param mUUId uuid
     */
    public WriteEvent(BluetoothGatt gatt,String mService_UUID, String mUUId,byte[] command) {
        byte crc8 = CRC8Util.calcCrc8(command);
        byte[] data = new byte[command.length+2];
        data[0] = C.BleCommand.SIGN;
        for(int i = 1;i < command.length+2;i++){
            if(i == command.length + 1){
                data[i] = crc8;
            }else {
                data[i] = command[i - 1];
            }
        }
        LogUtils.LogD("data length:"+data.length+"crc8 is :"+Integer.toHexString(0x00ff & crc8));
        this.data = data;
        this.gatt = gatt;
        StringBuilder service = new StringBuilder(uuid);
        service.insert(0, "0000");
        service_UUID = service.insert(4, mService_UUID).toString();
        StringBuilder UUID = new StringBuilder(uuid);
        UUID.insert(0, "0000");
        UUId = UUID.insert(4, mUUId).toString();
        LogUtils.LogE("service_UUID:"+service_UUID+"----UUID:"+UUId);
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] mData) {
        data = mData;
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
