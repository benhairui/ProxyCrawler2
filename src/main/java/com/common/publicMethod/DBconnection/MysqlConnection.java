package com.common.publicMethod.DBconnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import com.common.data.DBConnectionParams;
import com.common.data.webData;

public class MysqlConnection extends DBCommonConnection {
	
	public MysqlConnection(){
		super();
	}

	public MysqlConnection(DBConnectionParams params) {
		super(params);
		// TODO Auto-generated constructor stub
	}
	
	public void setUrl(){
		String connUrl = ""; 
		connUrl = params.getConnectivity()+":"
					+ params.getDbname()+"://"
					+ params.getHost()
					+":"
					+ params.getPort()
					+"/"
					+ params.getDatabase()+"?user="
					+ params.getUser()+"&password="
					+ params.getPassword()
					+"&useUnicode=true&characterEncoding=utf8"
					+"&useSSL=true";
		this.dataBaseUrl = connUrl;
		System.out.println(connUrl);
	}

	
	public DBConnectionParams setDefaultParams(){
		DBConnectionParams params = new DBConnectionParams();
		params.setHost("localhost");
		params.setConnectivity("jdbc");
		params.setDbname("mysql");
		params.setPort(3306);
		params.setUser("root");
		params.setPassword("root");
		params.setDatabase("CrawlerData");
		params.setDriver("com.mysql.cj.jdbc.Driver");
		return params;
	}
	
	public Connection getConnection() throws ClassNotFoundException, SQLException{
		setUrl();//设置连接url
		Class.forName(params.getDriver());
		Connection conn = DriverManager.getConnection(dataBaseUrl);
		return conn;
	}
	
	public void insertSingleDataToDB(Connection conn,webData d) throws SQLException{
		conn.setAutoCommit(false);
		PreparedStatement pst = conn.prepareStatement("insert into ProxyData(Ip,Port,Address,DegreeOfConfidentiality,Type,WebSite)"
				+ " values ('"+d.getIp()+"','"+d.getPort()+"','"+d.getAddress()+"','"+d.getDegreeOfConfidentiality()+"','"+d.getType()+"','"+d.getWebSite()+"')");
		pst.execute();
		conn.commit();
	}
	
	/**
	 * 将数据插入数据库中
	 * 这里采用批量插入数据
	 * @throws SQLException 
	 */
	public void insertBatchDataToDB(Connection conn,ArrayList<webData> dataList) throws SQLException{
		conn.setAutoCommit(false);
		PreparedStatement pst = conn.prepareStatement("insert into ProxyData(Ip,Port,Address,DegreeOfConfidentiality,Type,WebSite) values"
				+ "(?,?,?,?,?,?)");
		
		for(webData d : dataList){
			pst.setString(1, d.getIp());
			pst.setInt(2, d.getPort());
			pst.setString(3, d.getAddress());
			pst.setString(4, d.getDegreeOfConfidentiality());
			pst.setString(5, d.getType());
			pst.setString(6, d.getWebSite());
			pst.addBatch();
		}
		pst.executeBatch();  //int[] 可以使用一个返回值来判断插入是否成功
		conn.commit();
		System.out.println("插入结束");
	}
	public ArrayList<webData> getDataFromDB(Connection conn,String degree) throws SQLException{
		PreparedStatement pst = conn.prepareStatement("select *from ProxyCrawler where DegreeOfConfidentiality = ‘"+degree+"’");
		ResultSet rs = pst.getResultSet();
		if(rs == null){
			return null;
		}
		ArrayList<webData> list = new ArrayList<webData>();
		while(rs.next()){
			webData d = new webData();
			d.setIp(rs.getString("Ip"));
			d.setPort(rs.getInt("Port"));
			d.setDegreeOfConfidentiality(rs.getString("DegreeOfConfidentiality"));
			d.setType(rs.getString("type"));
			list.add(d);
		}
		return list;
	}
}
