package com.card.lp_server.card.device.kaimai.cama;

import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbInterface;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * 凯迈测控检测结果转录仪的设备类
 *
 * @author SEO-Dev
 *
 */
public class DetectResultStore {
	private static final String TAG = "DetectResultStore";

	private UsbDeviceConnection mUsbDeviceConnection;
	private UsbInterface mUsbInterface;
	/**
	 * 构造函数
	 *
	 */
	public DetectResultStore(UsbDeviceConnection usbDeviceConnection, UsbInterface usbInterface) {
		this.mUsbDeviceConnection = usbDeviceConnection;
		this.mUsbInterface  =usbInterface;
	}

	int setHidReportInterrupt(byte[] req){
		int ret = mUsbDeviceConnection.bulkTransfer(mUsbInterface.getEndpoint(1), req, req.length, 100);
	    return ret;
	}
	int getHidReportInterrupt(byte[] req){
		int ret = mUsbDeviceConnection.bulkTransfer(mUsbInterface.getEndpoint(0), req, req.length, 100);
		return ret;
	}

	boolean setHidReportControl(byte[] req){
		int ret = mUsbDeviceConnection.controlTransfer(0x21,0x09,2,1,req,req.length,30);
		return true;
	}
	boolean getHidReportControl(byte[] req){
		int ret = mUsbDeviceConnection.controlTransfer(0xA1,0x01,1,1,req,req.length,30);
		return true;
	}
	/**
	 * 读取所有数据
	 *
	 * @return 所有检测结果
	 * @throws Exception
	 */
	public ResultTable[] readAll(StringBuilder msg) throws Exception {
		Exception exp = new Exception("设备响应异常");
		// 最先发准备
		Log.d(TAG, "readAll: 准备命令!");
//		msg.append("Prepare :").append(Constants.print(CommandPacket.Prepare.getHidReport(),64)).append("$$$");
		int ret = setHidReportInterrupt(CommandPacket.Prepare.getHidReport());
//		msg.append("send data1 -ret:").append(ret);
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		byte[] res = new byte[64];
		// 设备返回数据准备好
		ret = getHidReportInterrupt(res);
//		msg.append("recv data1 -ret:").append(ret);
//		msg.append(Constants.print(res,64)).append("$$$");
		/*if (!ret || !CommandPacket.class.equals(TransPacket.parse(res).getClass()))
			throw exp;*/

		// 发开始传输
		ret = setHidReportInterrupt(CommandPacket.Transfer.getHidReport());
//		msg.append("send data2 -ret:").append(ret);
		try {
			Thread.sleep(20);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		FragmentPacket lastFrag = null;
		Map<Integer, byte[]> data = new HashMap<Integer, byte[]>();
		ByteArrayOutputStream ms = new ByteArrayOutputStream();
		List<ResultTable> results = new ArrayList<ResultTable>();

		while (true) {
			ret = getHidReportInterrupt(res);
//			msg.append(Constants.print(res,64)).append("###");
//			msg.append("recv data2 -ret:").append(ret);

			// 收到Fragment或Final数据包
			try {
				TransPacket p = TransPacket.parse(res);

			FragmentPacket fp = FragmentPacket.class.equals(p.getClass()) ? (FragmentPacket) p : null;
			if (lastFrag != null && (fp == null || fp.getID() != lastFrag.getID())) {
				// 一条收完
				byte[] item = ms.toByteArray();
				String str = new String(item, "GBK").replace('\0', ' ').trim();
//				msg.append("str:").append(str).append("**");
				ResultTable table = new ResultTable();
				table.setModel(
						"PL" + lastFrag.getMissile() + (lastFrag.getRemodel() == 0 ? "" : lastFrag.getRemodel() + ""));

				Pattern pattern = Pattern.compile("(^|\n)([^\n]+)");
				for (java.util.regex.Matcher m = pattern.matcher(str); m.find();) {
					String[] strs = m.group(2).split("[\t\r\n]");
					ResultRow row = new ResultRow();
					row.setPrjID(strs[0]);
					row.setOptor(strs[1]);
					row.setDate(strs[2]);
					row.setNum(strs[3]);
					table.getRows().add(row);

					for (int i = 4; i < strs.length-2; i += 3) {
						ResultItem ri = new ResultItem();
						ri.setName(strs[i]);
						ri.setScope(strs[i + 1]);
						ri.setResult(strs[i + 2]);
						row.getItems().add(ri);
					}
				}
//				msg.append("收到了一个表");
				Log.d(TAG, "readAll: 收到了一个表");
				results.add(table);
				ms.reset();
			}

			if (FinalPacket.class.equals(p.getClass()))
				return results.toArray(new ResultTable[0]);

			data.put(fp.getIndex(), fp.getFragment());
			ms.write(fp.getFragment());
			lastFrag = fp;
			ret = setHidReportInterrupt(CommandPacket.Continue.getHidReport());
//			msg.append("send dataNNN -ret:").append(ret);
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				Log.e(TAG, "readAll: "+e.getMessage());
				e.printStackTrace();
			}
			} catch (Exception ex) {
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				ex.printStackTrace(pw);
				Log.e(TAG, "readAll: "+ex.getMessage());
//				msg.append("Exception ex:").append(ex.getMessage()+sw.toString());
				throw ex;
			}
		}
	}

	public static void main(String[] args) {
		/*
		String[] devPaths = getHidDevices(0x5A48, 0x5750);
		System.out.println(devPaths.length + " device" + (devPaths.length > 1 ? "s" : "") + " found");
		if (devPaths.length > 0) {
			DetectResultStore dev = new DetectResultStore(devPaths[0]);
			try {
				ResultTable[] results = dev.readAll();
				System.out.println("共读取到" + results.length + "型弹药检测数据\n");
				for (int i = 0; i < results.length; i++)
					System.out.print(results[i].toString());

				System.out.print("\n读取完毕，按任意键继续...");
				System.in.read();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.exit(0);

		 */
	}
}
