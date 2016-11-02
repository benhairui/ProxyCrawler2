package com.common.data;
/**
 * 连接数据库的各个主要参数
 * @author lmd
 *
 */
public class DBConnectionParams {
	private String Driver;
	private String Dbname;  //要连接哪种数据库	
	private String Connectivity;
	private String host;
	private int port;
	private String User;
	private String Password;
	private String Database;
	private String UseUnicode;
	private String charEncoding;
	
	public String getUseUnicode() {
		return UseUnicode;
	}
	public void setUseUnicode(String useUnicode) {
		UseUnicode = useUnicode;
	}
	public String getCharEncoding() {
		return charEncoding;
	}
	public void setCharEncoding(String charEncoding) {
		this.charEncoding = charEncoding;
	}
	public String getConnectivity() {
		return Connectivity;
	}
	public void setConnectivity(String connectivity) {
		Connectivity = connectivity;
	}
	public String getDbname() {
		return Dbname;
	}
	public void setDbname(String dbname) {
		Dbname = dbname;
	}
	public String getDriver() {
		return Driver;
	}
	public void setDriver(String driver) {
		Driver = driver;
	}
	public String getHost() {
		return host;
	}
	public void setHost(String host) {
		this.host = host;
	}
	public int getPort() {
		return port;
	}
	public void setPort(int port) {
		this.port = port;
	}
	public String getUser() {
		return User;
	}
	public void setUser(String user) {
		User = user;
	}
	public String getPassword() {
		return Password;
	}
	public void setPassword(String password) {
		Password = password;
	}
	public String getDatabase() {
		return Database;
	}
	public void setDatabase(String database) {
		Database = database;
	}
}
