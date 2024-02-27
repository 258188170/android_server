package com.card.lp_server.card.device.kaimai.cama;

import java.util.ArrayList;
import java.util.List;

/**
 * 检测记录
 * 
 * @author SEO-Dev
 *
 */
public class ResultRow {
	private String prjID;
	private String optor;
	private String date;
	private String num;
	private List<ResultItem> items = new ArrayList<ResultItem>();

	/**
	 * @return 弹号
	 */
	public String getPrjID() {
		return prjID;
	}

	/**
	 * @param 弹号
	 */
	public void setPrjID(String prjID) {
		this.prjID = prjID;
	}

	/**
	 * @return 操作人
	 */
	public String getOptor() {
		return optor;
	}

	/**
	 * @param 操作人
	 */
	public void setOptor(String optor) {
		this.optor = optor;
	}

	/**
	 * @return 检测日期
	 */
	public String getDate() {
		return date;
	}

	/**
	 * @param 检测日期
	 */
	public void setDate(String date) {
		this.date = date;
	}

	/**
	 * @return 序列号
	 */
	public String getNum() {
		return num;
	}

	/**
	 * @param 序列号
	 */
	public void setNum(String num) {
		this.num = num;
	}

	/**
	 * @return 检测项
	 */
	public List<ResultItem> getItems() {
		return items;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("产品ID:").append(prjID).append("\t操作员:").append(optor).append("\t检测日期:").append(date).append("\t序列号:")
				.append(num).append("\n");
		for (ResultItem item : items) {
			sb.append("\t").append(item.toString()).append("\n");
		}
		return sb.toString();
	}

}
