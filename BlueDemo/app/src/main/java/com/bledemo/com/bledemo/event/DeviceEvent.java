package com.bledemo.com.bledemo.event;

/**
 * Created by wesker on 2017/11/1716:59.
 */

public class DeviceEvent {
    String device_name;
    String device_address;

    public DeviceEvent(String mDevice_name, String mDevice_address) {
        device_name = mDevice_name;
        device_address = mDevice_address;
    }

    public String getDevice_address() {
        return device_address;
    }

    public void setDevice_address(String mDevice_address) {
        device_address = mDevice_address;
    }

    public String getDevice_name() {
        return device_name;
    }

    public void setDevice_name(String mDevice_name) {
        device_name = mDevice_name;
    }
}
