package com.card.lp_server.card.device.kaimai.cama;

/**
 * 
 * 传输数据包
 * 
 * @author SEO-Dev
 *
 */
public class TransPacket {
	/**
	 * 数据准备命令
	 */
	public static final byte Prepare = 0x07;

	/**
	 * 数据准备好
	 */
	public static final byte Ready = (byte) 0x80;

	/**
	 * 开始数据传输
	 */
	public static final byte Transfer = 0x08;

	/**
	 * 接收到数据片
	 */
	public static final byte Fragment = (byte) 0x90;

	/**
	 * 开始下一数据片
	 */
	public static final byte Continue = 0x09;

	/**
	 * 数据传输完成
	 */
	public static final byte Final = (byte) 0xA0;

	/**
	 * 开始下一条数据
	 */
	public static final byte Next = 0x0A;

	/**
	 * 复位
	 */
	public static final byte Reset = 0x0F;

	private byte[] data;
	private byte command;

	/**
	 * @return 传输方向，true表示设备到主机
	 */
	public boolean direction() {
		return command < 0;
	}

	/**
	 * @return 命令字
	 */
	public byte getCommand() {
		return command;
	}

	/**
	 * @param 命令字
	 */
	public void setCommand(byte command) {
		this.command = command;
	}

	/**
	 * @return 获取数据
	 */
	public byte[] getData() {
		return data == null ? new byte[0] : data;
	}

	/**
	 * @param 设置数据
	 */
	public void setData(byte[] data) {
		this.data = data;
	}

	/**
	 * @return 数据校验值
	 */
	public byte getCheckSum() {
		if (data == null)
			return command;
		byte sum = command;
		for (byte b : data)
			sum += b;
		return sum;
	}

	/**
	 * @return HID数据报
	 */
	public byte[] getHidReport() {
		byte[] data = getData();
		byte[] report = new byte[64];
		int index = 0;
		//report[index++] = 0x00;
		report[index++] = 0x11;
		report[index++] = 0x55;
		report[index++] = command;
		byte sum = command;
		for (int i = 0; i < data.length; i++) {
			sum += data[i];
			report[index++] = data[i];
		}
		report[index++] = sum;
		report[index++] = 0x11;
		report[index++] = 0x66;
		return report;
	}

	/**
	 * @param value HID数据报
	 */
	public void setHidReport(byte[] value) {
		if (value == null || value.length < 7 ||  value[1-1] != 0x11 || value[2-1] != 0x55)
			throw new RuntimeException("不可识别的数据包格式");
		int len = 6;
		while (len < value.length && (value[len - 2] != 0x11 || value[len - 1] != 0x66))
			len++;
		if (value[len - 2] != 0x11 || value[len - 1] != 0x66)
			throw new RuntimeException("不可识别的数据包格式");

		byte[] data = new byte[len - 6];
		byte sum = value[2];
		int index = 3;
		for (int i = 0; i < len - 6; i++, index++) {
			data[i] = value[index];
			sum += value[index];
		}
		if (sum != value[index])
			throw new RuntimeException("数据校验不正确");

		command = value[2];
		this.data = data;
	}

	/**
	 * 构造函数
	 * 
	 * @param command 命令字
	 * @param data    数据
	 */
	protected TransPacket(byte command, byte[] data) {
		this.command = command;
		this.data = data;
	}

	/**
	 * 私有构造函数，允许在内部创建无配置的传输包
	 */
	private TransPacket() {
	}

	/**
	 * 解析数据包
	 * 
	 * @param report HID数据报
	 * @return 数据包
	 */
	public static TransPacket parse(byte[] report) {
		TransPacket packet = new TransPacket();
		packet.setHidReport(report);
		if (packet.getData().length == 0) {
			switch (packet.getCommand()) {
			case TransPacket.Prepare:
				return CommandPacket.Prepare;
			case TransPacket.Transfer:
				return CommandPacket.Transfer;
			case TransPacket.Continue:
				return CommandPacket.Continue;
			case TransPacket.Next:
				return CommandPacket.Next;
			case TransPacket.Reset:
				return CommandPacket.Reset;
			case TransPacket.Ready:
				return CommandPacket.Ready;
			}
		}
		if (packet.getCommand() == TransPacket.Fragment)
			return new FragmentPacket(packet);
		if (packet.getCommand() == TransPacket.Final)
			return new FinalPacket(packet);
		return packet;
	}

	@Override
	public int hashCode() {
		return toString().hashCode();
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(getCommand());
		for (byte b : getData())
			sb.append(String.format("%02x", b));
		return sb.toString();
	}

	@Override
	public boolean equals(Object obj) {
		return obj != null && obj.getClass().equals(TransPacket.class) && toString().equals(obj.toString());
	}
}
