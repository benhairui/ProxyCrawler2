package com.ImpTest;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import com.common.data.DBConnectionParams;
import com.common.data.queueManagement;
import com.common.data.webData;
import com.common.publicMethod.DBconnection.MysqlConnection;
import com.google.gson.Gson;
import com.imp.MultipleThread.ThreadImp;
import com.proxyinstance.KDL;
import com.proxyinstance.XCDL;
import com.proxyinstance._66IP;

public class test {
	
	public static Integer threadCount=0;
	

	public static void test1() throws ClientProtocolException, IOException {
		HttpClient httpClient = HttpClients.createDefault();

		String path = "http://www.kuaidaili.com/free/inha/1";
		HttpGet httpGet = new HttpGet(path);

		HttpResponse response = httpClient.execute(httpGet);
		HttpEntity entity = response.getEntity();
		String html = EntityUtils.toString(entity);
		if (entity != null) {
			System.out.println();
		} else {
			System.out.println("null");
		}
		Document doc = Jsoup.parse(html);
		System.out.println("hello world");
	}

	public void testGson() {
		ArrayList<String> str = new ArrayList<String>();
		str.add("hello world");
		str.add("ni hao");

		Gson gson = new Gson();
		String s = gson.toJson(str);
		System.out.println(s);
	}

	public void connStrTest() throws ClassNotFoundException, SQLException {
		MysqlConnection conn = new MysqlConnection();
		DBConnectionParams params = conn.setDefaultParams();
		conn.setParams(params);

		Connection cn = conn.getConnection();
		// Statement stmt = cn.createStatement();
		// String sql = "select *from ProxyData";
		webData d = new webData();
		d.setIp("192.168.1.128");
		d.setPort(3306);
		d.setDegreeOfConfidentiality("高匿");
		d.setAddress("beijing");
		d.setType("http");
		conn.insertSingleDataToDB(cn, d);
		System.out.println("end");
	}

	public static void testKdl() throws ClientProtocolException, IOException {
		KDL k = new KDL();
		XCDL x = new XCDL();
		String url = "";
		HttpEntity entity = null;
		for (int i = 1; i < 3; i++) {
			for (int j = 0; j < x.typeList.length; j++) {
				url = x.urlAnalyze(x.typeList[j], i);
				System.out.println(url);
				entity = x.getHtmlPage(url);
				System.out.println(entity);
				//x.htmlParser(entity);
			}
		}
	}
	
	public static void test_66IP() throws ClientProtocolException, IOException{
		_66IP p = new _66IP();
		String url = "";
		HttpEntity entity;
		for(int i = 1;i<2;i++){
			url = p.urlAnalyzeProvince(i,1);
			System.out.println(url);
			entity = p.getHtmlPage(url);
			//System.out.println(EntityUtils.toString(entity,"gb2312"));
			//System.out.println(entity == null?0:1);
			p.htmlParse(entity);
		}
	}
	
	/**
	 * 将url加入队列中
	 * @throws InterruptedException 
	 */
	public void urlAddQueue(queueManagement queue) throws InterruptedException{
		
	}
	

	public static void main(String[] args) {
		test t = new test();
		queueManagement queue = new queueManagement();
		
		KDL.urlAddQueue(queue, 3);
		XCDL.urlAddQueue(queue, 3);
		_66IP.urlAddQueueAll(queue, 3);
		_66IP.urlAddQueueProvince(queue, 3);
		
		for(int i = 1;i<=3;i++){ //开启三个线程
			new ThreadImp(queue).start();
		}
	}
}
