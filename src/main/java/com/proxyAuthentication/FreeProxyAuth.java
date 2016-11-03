package com.proxyAuthentication;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import com.common.data.DBConnectionParams;
import com.common.data.ProxyQualified;
import com.common.data.queueManagement;
import com.common.data.webData;
import com.common.publicMethod.DBconnection.MysqlConnection;
import com.common.publicMethod.webCrawler.WebCrawler;

/**
 * 主要思路：
 * (1)数据读取，去重，插入数据库
 *（2）
 * @author lmd
 *
 */

public class FreeProxyAuth extends WebCrawler {
	
	public Log log = LogFactory.getLog(this.getClass());
	public static final String URL = "http://www.163.com";

	public static Integer ThreadCount = 0;
	
	public ArrayList<webData> getResult(){
		MysqlConnection mysql = new MysqlConnection();
		DBConnectionParams params = mysql.setDefaultParams();
		mysql.setParams(params);
		Connection conn = mysql.getConnection();
		ArrayList<webData> data = mysql.getDataFromDB(conn);
		System.out.println(data.size());
		return data;
	}
	
	/**
	 * 获得去重后的结果,在多线程测试代理ip前，直接做这一步
	 * @param data（来自数据库）
	 * @return
	 */
	public void duplicateRemove(ArrayList<webData> data,queueManagement queue){
		ConcurrentHashMap<String,webData> map = new ConcurrentHashMap<String,webData>();
		ArrayList<Integer> indexList = new ArrayList<Integer>();//存储重复的index
		String ip = "";
		int port;
		String key = "";
		webData d = null;
		for(int i = 0;i<data.size();i++){
			ip = data.get(i).getIp();
			port = data.get(i).getPort();
			key = ip + ":" + port;
			d = map.get(key);
			//如果map中存在该对象，那么将该index加入indexList中（在数据库中以主键为判断条件删除重复的值）
			//；如果不存在该对象，那么添加key-value；
			if(d == null){
				try {
					map.put(key, data.get(i));
					queue.proxyQueueAdd(data.get(i));
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					log.error(e.getMessage());
				}
			}
		}
	}

	
	public static void main(String []args) throws ParseException, IOException{
		System.out.println("start");
		FreeProxyAuth f = new FreeProxyAuth();
		queueManagement queue = new queueManagement();

		ArrayList<webData> list = f.getResult();
		System.out.println("数据库中的数量：" + list.size());
		f.duplicateRemove(list, queue);
		
		for(int i = 1;i<4;i++){
			new TestProxyThreadImp(queue).start();;
		}
	}

}
