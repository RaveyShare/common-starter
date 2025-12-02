package com.ravey.common.utils.http;

import org.apache.http.HttpStatus;

public class HttpRespons {

	private int code;

	private String content;

	/**
	 * 通信状态正常
	 *
	 * @return
	 */
	public boolean ok() {
		return getCode() == HttpStatus.SC_OK;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
