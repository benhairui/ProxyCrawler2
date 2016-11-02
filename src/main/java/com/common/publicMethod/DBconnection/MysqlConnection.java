package com.common.publicMethod.DBconnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.common.data.DBConnectionParams;
import com.common.data.webData;

public class MysqlConnection extends DBCommonConnection {
	/**
	 * 日志记录
	 */
	Log log = LogFactory.getLog(this.getClass());

	public MysqlConnection() {
		super();
	}

	public MysqlConnection(DBConnectionParams params) {
		super(params);
		// TODO Auto-generated constructor stub
	}

	public void setUrl() {
		String connUrl = "";
		connUrl = params.getConnectivity() + ":" + params.getDbname() + "://"
				+ params.getHost() + ":" + params.getPort() + "/"
				+ params.getDatabase() + "?user=" + params.getUser()
				+ "&password=" + params.getPassword()
				+ "&useUnicode=true&characterEncoding=utf8" + "&useSSL=true";
		this.dataBaseUrl = connUrl;
		System.out.println(connUrl);
	}

	public DBConnectionParams setDefaultParams() {
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

	public Connection getConnection() {
		setUrl();// 设置连接url
		try {
			Class.forName(params.getDriver());
			Connection conn = DriverManager.getConnection(dataBaseUrl);
			return conn;
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error(e.getMessage());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error(e.getMessage());
		}
		return null;
	}

	public void insertSingleDataToDB(Connection conn, webData d) {

		try {
			conn.setAutoCommit(false);
			PreparedStatement pst;
			pst = conn
					.prepareStatement("insert into ProxyData(Ip,Port,Address,DegreeOfConfidentiality,Type,WebSite)"
							+ " values ('"
							+ d.getIp()
							+ "','"
							+ d.getPort()
							+ "','"
							+ d.getAddress()
							+ "','"
							+ d.getDegreeOfConfidentiality()
							+ "','"
							+ d.getType() + "','" + d.getWebSite() + "')");
			pst.execute();
			conn.commit();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error(e.getMessage());
		}
	}

	/**
	 * 将数据插入数据库中 这里采用批量插入数据
	 * 
	 * @throws SQLException
	 */
	public void insertBatchDataToDB(Connection conn, ArrayList<webData> dataList) {
		try {
			conn.setAutoCommit(false);
			PreparedStatement pst = conn
					.prepareStatement("insert into ProxyData(Ip,Port,Address,DegreeOfConfidentiality,Type,WebSite) values"
							+ "(?,?,?,?,?,?)");

			for (webData d : dataList) {
				pst.setString(1, d.getIp());
				pst.setInt(2, d.getPort());
				pst.setString(3, d.getAddress());
				pst.setString(4, d.getDegreeOfConfidentiality());
				pst.setString(5, d.getType());
				pst.setString(6, d.getWebSite());
				pst.addBatch();
			}
			pst.executeBatch(); // int[] 可以使用一个返回值来判断插入是否成功
			conn.commit();
			System.out.println("插入结束");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error(e.getMessage());
		}

	}

	public ArrayList<webData> getDataFromDB(Connection conn, String degree) {
		try {
			PreparedStatement pst = conn
					.prepareStatement("select *from ProxyCrawler where DegreeOfConfidentiality = ‘"
							+ degree + "’");
			ResultSet rs = pst.getResultSet();
			if (rs == null) {
				return null;
			}
			ArrayList<webData> list = new ArrayList<webData>();
			while (rs.next()) {
				webData d = new webData();
				d.setIp(rs.getString("Ip"));
				d.setPort(rs.getInt("Port"));
				d.setDegreeOfConfidentiality(rs
						.getString("DegreeOfConfidentiality"));
				d.setType(rs.getString("type"));
				list.add(d);
			}
			return list;
		} catch (SQLException e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}
		return null;
	}
}
