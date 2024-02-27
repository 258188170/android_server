/**
 * 
 */
package com.card.lp_server.card.device.kaimai.cama;

/**
 * 命令包，特指只包含命令、无数据的数据包
 * 
 * @author SEO-Dev
 *
 */
public final class CommandPacket extends TransPacket {
	/**
	 * 表示数据准备的数据包
	 */
	public static final CommandPacket Prepare = new CommandPacket(TransPacket.Prepare);

	/**
	 * 表示开始传输的数据包
	 */
	public static final CommandPacket Transfer = new CommandPacket(TransPacket.Transfer);

	/**
	 * 表示继续接收的数据包
	 */
	public static final CommandPacket Continue = new CommandPacket(TransPacket.Continue);

	/**
	 * 表示下一传输的数据包
	 */
	public static final CommandPacket Next = new CommandPacket(TransPacket.Next);

	/**
	 * 表示复位的数据包
	 */
	public static final CommandPacket Reset = new CommandPacket(TransPacket.Reset);

	/**
	 * 表示数据准备就绪的数据包
	 */
	public static final CommandPacket Ready = new CommandPacket(TransPacket.Ready);

	/**
	 * 构造函数
	 * 
	 * @param command 命令字
	 */
	private CommandPacket(byte command) {
		super(command, null);
	}

}
