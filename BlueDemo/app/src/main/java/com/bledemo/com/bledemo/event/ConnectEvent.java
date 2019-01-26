package com.bledemo.com.bledemo.event;

/**
 * Created by wesker on 2017/11/169:16.
 */

import com.clj.fastble.data.ScanResult;

/**
 * 连接ble的Event
 */
public class ConnectEvent {
    ScanResult result;


    public ScanResult getResult() {
        return result;
    }

    public void setResult(ScanResult mResult) {
        result = mResult;
    }

    public ConnectEvent(ScanResult mResult) {
        result = mResult;
    }


    @Override
    public String toString() {
        return "ConnectEvent{" +
                "result=" + result +
                '}';
    }
}
