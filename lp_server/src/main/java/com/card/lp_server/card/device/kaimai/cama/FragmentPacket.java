package com.card.lp_server.card.device.kaimai.cama;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * 数据片包
 * 
 * @author SEO-Dev
 *
 */
public final class FragmentPacket extends TransPacket {
	/**
	 * 构造函数
	 * 
	 * @param missile 弹型
	 * @param remodel 改型
	 * @param id      测试表序号
	 * @param index   数据片序号
	 * @param data    数据
	 */
	public FragmentPacket(byte missile, char remodel, int id, byte index, byte[] data) {
		super(TransPacket.Fragment, null);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		bos.write(missile);
		bos.write((byte) remodel);
		bos.write((byte) (id & 0xff));
		bos.write((byte) ((id >> 8) & 0xff));
		bos.write(index);
		if (data != null)
			try {
				bos.write(data);
			} catch (IOException e) {
				e.printStackTrace();
			}

		setData(bos.toByteArray());
	}

	/**
	 * 构造函数
	 * 
	 * @param packet 内容为数据片包的传输包
	 */
	public FragmentPacket(TransPacket packet) {
		super(packet.getCommand(), packet.getData());
	}

	/**
	 * 
	 * @return 弹型
	 */
	public byte getMissile() {
		return getData()[0];
	}

	/**
	 * 
	 * @param value 弹型
	 */
	public void setMissile(byte value) {
		getData()[0] = value;
	}

	/**
	 * 
	 * @return 改型
	 */
	public char getRemodel() {
		return (char) getData()[1];
	}

	/**
	 * @param value 改型
	 */
	public void setRemodel(char value) {
		getData()[1] = (byte) value;
	}

	/**
	 * @return 测试表序号
	 */
	public int getID() {
		byte[] data = getData();
		return (data[2] & 0xff) | ((data[3] & 0xff) << 8);
	}

	/**
	 * @param value 测试表序号
	 */
	public void setID(int value) {
		byte[] data = getData();
		data[2] = (byte) (value & 0xff);
		data[3] = (byte) ((value >> 8) & 0xff);
	}

	/**
	 * @return 数据包序号
	 */
	public int getIndex() {
		return getData()[4] & 0xff;
	}

	/**
	 * @param value 数据包序号
	 */
	public void setIndex(int value) {
		getData()[4] = (byte)(value & 0xff);
	}

	/**
	 * 
	 * @return 数据片
	 */
	public byte[] getFragment() {
		byte[] data = getData();
		byte[] frag = new byte[data.length - 5];
		System.arraycopy(data, 5, frag, 0, frag.length);
		return frag;
	}

	/**
	 * @param value 数据片
	 */
	public void setFragment(byte[] value) {
		byte[] data = new byte[value.length + 5];
		System.arraycopy(this.getData(), 0, data, 0, 5);
		System.arraycopy(value, 0, data, 5, value.length);
		setData(data);
	}

}
