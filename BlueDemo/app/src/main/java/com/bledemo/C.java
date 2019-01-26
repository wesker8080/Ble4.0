package com.bledemo;

/**
 * Created by wesker on 2017/11/1717:52.
 */

public interface C {
    interface BleCommand {
        /*
        *   2、绑定：3A 00 04 04 00 32 CRC8
3、获取设备当前时间：3A 00 04 04 00 22 CRC8
4、设置时间：3A 00 08 04 00 23 XX XX XX XX CRC8（XXXXXXXX为32位UTC北京时间）
5、获取历史数据总包数：3A 00 04 04 00 30 CRC8
6、获取历史数据第1包：3A 00 06 04 00 31 00 01 CRC8
     获取历史数据第2包：3A 00 06 04 00 31 00 02 CRC8
7、历史数据回复确认第1包： 帧序 + 结果（0=成功，1=失败）
   3A 00 07 04 00 39 00 01 00 CRC8
8、获取实时数据：3A 00 04 04 00 37 CRC8
9、控制命令－水质检测：3A 00 06 04 00 40 03 FE CRC8
10、控制命令－基线校正：3A 00 06 04 00 40 02 03 CRC8
        * */
        byte SIGN = 0x3a;//起始标志
        byte[] CERTIFICATION = {0x00, 0x04, 0x04, 0x00, 0x20};//认证确认
        byte[] BIND = {0x00, 0x04, 0x04, 0x00, 0x32};//绑定
        byte[] GET_CURRENT_TIME = {0x00, 0x04, 0x04, 0x00, 0x22};//获取设备当前时间
        byte[] SET_CURRENT_TIME = {0x00, 0x08, 0x04, 0x00, 0x23,};//设置时间
        byte[] GET_HISTORY_TOTAL = {0x00, 0x04, 0x04, 0x00, 0x30};//获取历史数据总包数
        byte[] GET_HISTORY_DATA1 = {0x00, 0x06, 0x04, 0x00, 0x31,0X00,0X01};//获取历史数据第1包
        byte[] GET_HISTORY_DATA2 = {0x00, 0x04, 0x04, 0x00, 0x31,0X00,0X02};//获取历史数据第2包
        byte[] WATER_QUALITY = {0x00, 0x06, 0x04, 0x00, 0x40,0X03,(byte)0xfe};//控制命令－水质检测
        byte[] GET_REAL_TIME = {0x00, 0x04, 0x04, 0x00, 0x20};//获取实时数据
        byte[] BASE_QUALITY = {0x00, 0x04, 0x04, 0x00, 0x20};//控制命令－基线校正
    }
}
