package com.card.lp_server.card.device.call;

/**
 *
 * @author SEO-Dev
 *
 *         底层输入输出设备
 */
public interface IIODevice {
	/**
	 * 从设备接收数据
	 *
	 * @param buffer 数据缓冲
	 * @param offset 偏移
	 * @param length 读取长度
	 * @return 实际读取长度,-1表示失败
	 */
	 int read(byte[] buffer, int offset, int length);

	/**
	 * 向设备写入数据
	 *
	 * @param buffer 数据缓冲
	 * @param offset 偏移
	 * @param length 读取长度
	 * @return 实际写入长度,-1表示失败
	 */
	 int write(byte[] buffer, int offset, int length);
}
