/**
 *
 */
package com.card.lp_server.card.device.call;

/**
 * @author SEO-Dev
 *
 *         创建设备的回调接口
 */
public interface ICreateDevice {
    /**
     * 创建串口设备
     *
     * @param baudrate
     * @return 设备
     */
     IIODevice create(int baudrate);

    /**
     * 创建HID设备
     *
     * @param vid 产商ID
     * @param pid 产品ID
     * @return 设备
     */
     IIODevice create(int vid, int pid);
}
