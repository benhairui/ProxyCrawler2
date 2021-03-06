package com.common.publicMethod.DBconnection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
			System.out.println(e.getMessage());
			log.error(e.getMessage());
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
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
			System.out.println(e.getMessage());
			log.error(e.getMessage());
		}
	}

	/**
	 * 将数据插入数据库中 这里采用批量插入数据
	 * 
	 * @throws SQLException
	 */
	public boolean insertBatchDataToDB(Connection conn, ArrayList<webData> dataList,String dataBase) {
		int count = 0;
		int updateCount = 0;
		try {
			conn.setAutoCommit(false);
			PreparedStatement pst = conn
					.prepareStatement("insert into "+ dataBase +"(Ip,Port,Address,DegreeOfConfidentiality,Type,WebSite,ResponseTime,TestTimes,Flag,IsNew) values"
							+ "(?,?,?,?,?,?,?,?,?,?)");
			
			for (webData d : dataList) {
				pst.setString(1, d.getIp());
				pst.setInt(2, d.getPort());
				pst.setString(3, d.getAddress());
				pst.setString(4, d.getDegreeOfConfidentiality());
				pst.setString(5, d.getType());
				pst.setString(6, d.getWebSite());
				pst.setLong(7, d.getResponseTime());
				pst.setInt(8, d.getTestTimes());
				pst.setBoolean(9, d.getFlag());
				pst.setBoolean(10, d.getIsNew());
				++count;
				pst.addBatch();
				//如果pst加载了5000，那么执行批量插入
				if(count / 500 == 1){
					updateCount = updateCount + pst.executeBatch().length; 
					pst.clearBatch();
					conn.commit();
					count = 0;
				}
			}
			if(count<500 && count > 0){//还有剩余部分
				updateCount = updateCount + pst.executeBatch().length;//最终可能没有超过5000，那么需要再次执行 
				conn.commit();
				pst.clearBatch();// int[] 可以使用一个返回值来判断插入是否成功
			}
			
			if(updateCount  == dataList.size()){
				return true;
			}
			System.out.println("插入结束");
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
			log.error(e.getMessage());
		}
		return false;
	}
	
	/**
	 * 从数据库中获取数据
	 * @param conn
	 * @param sql
	 * @return
	 */
	public ArrayList<webData> getDataFromDB(Connection conn,String sql) {
		try {
			Statement stmt = conn.createStatement();
			stmt.execute(sql);
			ResultSet rs = stmt.getResultSet();
			if (rs == null) {
				System.out.println("为空");
				return null;
			}
			ArrayList<webData> list = new ArrayList<webData>();
			while (rs.next()) {
				webData d = new webData();
				d.setIndex(rs.getInt("Id"));
				d.setIp(rs.getString("Ip"));
				d.setPort(rs.getInt("Port"));
				d.setAddress(rs.getString("Address"));
				d.setDegreeOfConfidentiality(rs.getString("DegreeOfConfidentiality"));
				d.setWebSite(rs.getString("WebSite"));
				d.setType(rs.getString("Type"));
				d.setResponseTime(rs.getInt("ResponseTime"));
				d.setTestTimes(rs.getInt("TestTimes"));
				d.setFlag(rs.getBoolean("Flag"));
				d.setIsNew(rs.getBoolean("IsNew"));
				list.add(d);
			}
			return list;
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			log.error(e.getMessage());
		}finally{
			try {
				conn.close();
			} catch (SQLException e) {
				System.out.println(e.getMessage());
				log.error(e.getMessage());
			}
		}
		return null;
	}
	/**
	 * 删除中间表数据
	 * 删除表中数据，主要是针对代理检测结束后，中间表数据插入后，对主表数据删除，插入新数据
	 */
	public void deleteDB(Connection conn,String dataBase){
		MysqlConnection mysql = new MysqlConnection();
		DBConnectionParams params = mysql.setDefaultParams();
		mysql.setParams(params);
		Connection cn = mysql.getConnection();
		String sql = "delete from " + dataBase;
		//System.out.println(sql);
		try {
			PreparedStatement ps = cn.prepareStatement(sql);
			ps.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
			log.error(e.getMessage());
		}finally{
			try {
				cn.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				System.out.println(e.getMessage());
				log.error(e.getMessage());
			}
		}
	}
	
	public static void main(String []args){
		MysqlConnection mysql = new MysqlConnection();
		DBConnectionParams params = mysql.setDefaultParams();
		mysql.setParams(params);
		Connection conn = mysql.getConnection();
		mysql.deleteDB(conn, "ProxyData");
		System.out.println("end");
	}
	
}
