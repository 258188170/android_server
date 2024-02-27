package com.card.lp_server.card.device.kaimai.cama;

/**
 * 数据完成包
 * 
 * @author SEO-Dev
 *
 */
public final class FinalPacket extends TransPacket {
	/**
	 * @param fragments 分包数
	 */
	public FinalPacket(int fragments) {
		super(TransPacket.Final, null);
		setFragments(fragments);
	}

	/**
	 * @param packet 内容为完成包的传输包
	 */
	public FinalPacket(TransPacket packet) {
		super(packet.getCommand(), packet.getData());
	}

	/**
	 * @return 总分包数
	 */
	public int getFragments() {
		byte[] data = getData();
		return (data[0] & 0xff) | ((data[1] & 0xff) << 8);
	}

	/**
	 * @param value 总分包数
	 */
	public void setFragments(int value) {
		setData(new byte[] { (byte) (value & 0xff), (byte) ((value >> 8) & 0xff) });
	}
}
