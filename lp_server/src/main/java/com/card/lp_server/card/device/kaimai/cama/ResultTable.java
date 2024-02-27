package com.card.lp_server.card.device.kaimai.cama;

import java.util.ArrayList;
import java.util.List;

/**
 * 结果表
 * 
 * @author SEO-Dev
 *
 */
public class ResultTable {
	/**
	 * 弹型
	 */
	private String model;

	/**
	 * 所有记录
	 */
	private List<ResultRow> rows = new ArrayList<ResultRow>();

	/**
	 * 获取弹型
	 * 
	 * @return 弹型
	 */
	public String getModel() {
		return model;
	}

	/**
	 * 设置弹型
	 * 
	 * @param model 弹型
	 */
	public void setModel(String model) {
		this.model = model;
	}

	/**
	 * 获取检测记录集合
	 * 
	 * @return 检测记录集合
	 */
	public List<ResultRow> getRows() {
		return rows;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("弹型:").append(model).append("\n");
		for (ResultRow row : rows) {
			sb.append(row.toString()).append("\n");
		}
		return sb.toString();
	}

}
