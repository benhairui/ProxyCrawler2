package com.common.publicMethod.DBconnection;

import com.common.data.DBConnectionParams;

/**
 * 存储数据库连接，增，删，改，查等各种基本方法
 * 
 * 该类下主要是抽象基础类
 * 
 * @author lmd
 */
public abstract class DBCommonConnection {
	protected DBConnectionParams params;
	protected String dataBaseUrl;

	public DBCommonConnection() {
	}

	public String getDataBaseUrl() {
		return dataBaseUrl;
	}

	public void setDataBaseUrl(String dataBaseUrl) {
		this.dataBaseUrl = dataBaseUrl;
	}

	public DBCommonConnection(DBConnectionParams params) {
		this.params = params;
	}

	public String getUrl(DBConnectionParams params) {
		return null;
	}

	// 默认的Params的连接参数
	public DBConnectionParams setDefaultParams() {
		return null;
	}

	public void setParams(DBConnectionParams params) {
		this.params = params;
	}

	public DBConnectionParams getParams() {
		return params;
	}
}
