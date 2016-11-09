package com.proxyAuthentication;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.common.data.DBConnectionParams;
import com.common.data.queueManagement;
import com.common.data.webData;
import com.common.publicMethod.DBconnection.MysqlConnection;
import com.common.publicMethod.webCrawler.WebCrawler;

public class FreeProxyAuth extends WebCrawler {
	public Log log = LogFactory.getLog(this.getClass());
	public static final String URL = "http://www.163.com";

	
	public ArrayList<webData> getResult(String sql){
		MysqlConnection mysql = new MysqlConnection();
		DBConnectionParams params = mysql.setDefaultParams();
		mysql.setParams(params);
		Connection conn = mysql.getConnection();
		ArrayList<webData> data = mysql.getDataFromDB(conn,sql);
		//System.out.println(data.size());
		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			System.out.println(e.getMessage());
			log.error(e.getMessage());
		}
		return data;
	}
	
	/**
	 * 首先从ProxyAfterTest中获得上一次检测的数据，并放入map中，
	 * 获得去重后的结果,在多线程测试代理ip前，直接做这一步
	 * @param data（来自数据库）
	 * @return
	 */
	public void duplicateRemove(queueManagement queue){
		ConcurrentHashMap<String,webData> map = new ConcurrentHashMap<String,webData>();
		String orignalDataSql = "select *from ProxyAfterTest where IsNew = 0";
		ArrayList<webData> orignalData = getResult(orignalDataSql);//已经经过测试且去重的数据
		
		String newDataSql = "select *from ProxyData where IsNew = 1";
		ArrayList<webData> newData = getResult(newDataSql);//刚抓下来还未经过测试的数据
		
		String ip = "",key = "";
		int port;
		webData d = null;
		//将已经检测过的数据放入map中
		for(int i = 0;i<orignalData.size();i++){
			ip = orignalData.get(i).getIp();
			port = orignalData.get(i).getPort();
			key = ip + ":" + port;
			try {
				map.put(key, orignalData.get(i));
				queue.proxyQueueAdd(orignalData.get(i));
			} catch (InterruptedException e) {
				System.out.println(e.getMessage());
				log.error(e.getMessage());
			}
		}
		
		//对新入的数据去重，如果map中存在相应的ip地址，那么不更新值
		for(int i = 0;i<newData.size();i++){
			ip = newData.get(i).getIp();
			port = newData.get(i).getPort();
			key = ip+":"+port;
			d = map.get(key);
			if(d == null){ //如果新入的数据中存在重复的，不再将其放入队列中
				try {
					map.put(key, newData.get(i));
					queue.proxyQueueAdd(newData.get(i));
				} catch (InterruptedException e) {
					System.out.println(e.getMessage());
					log.error(e.getMessage());
				}
			}
		}
	}
	
	public void InsertDB(queueManagement queue){
		MysqlConnection mysql = new MysqlConnection();
		DBConnectionParams params = mysql.setDefaultParams();
		mysql.setParams(params);
		Connection conn = mysql.getConnection();
		//删除中间数据库的数据
		mysql.deleteDB(conn, "ProxyAfterTestTemp"); 
		System.out.println("中间数据表删除成功");
		
		//在测试结束后，将测试结果插入到备份数据库中
		mysql.insertBatchDataToDB(conn, queue.list,"ProxyAfterTestTemp");
		System.out.println("中间数据表插入成功");
		
		//删除主表的数据
		mysql.deleteDB(conn, "ProxyAfterTest");
		System.out.println("主数据表数据表删除成功");
		
		//将测试结果插入到备份数据库中
		mysql.insertBatchDataToDB(conn, queue.list,"ProxyAfterTest");
		System.out.println("主数据表数据表插入成功");
		
		//在主数据表和中间数据表更新成功后，删除历史数据表的数据，以便在做下一次的检测时，减少无谓的匹配
		mysql.deleteDB(conn, "ProxyData");
		System.out.println("历史数据库数据删除成功");
		
		try {
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage());
		}
	}
	
}
