package com.card.lp_server.card.device.kaimai.cama;

/**
 * 检测项
 * 
 * @author SEO-Dev
 *
 */
public class ResultItem {
	private String name;
	private String scope;
	private String result;

	/**
	 * @return 检测项
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param 检测项
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return 范围
	 */
	public String getScope() {
		return scope;
	}

	/**
	 * @param 范围
	 */
	public void setScope(String scope) {
		this.scope = scope;
	}

	/**
	 * @return 结果
	 */
	public String getResult() {
		return result;
	}

	/**
	 * @param 结果
	 */
	public void setResult(String result) {
		this.result = result;
	}

	@Override
	public String toString() {
		return String.format("名称:%s\t范围:%s\t结果:%s", name, scope, result);
	}
}
