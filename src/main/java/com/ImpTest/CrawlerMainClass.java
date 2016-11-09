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
import com.imp.MultipleThread.CrawlerThreadImp;
import com.proxyinstance.KDL;
import com.proxyinstance.XCDL;
import com.proxyinstance._66IP;

public class CrawlerMainClass {
	
	public static Integer threadCount=0;
	
	public synchronized void ImpMain(queueManagement queue,int startPage,int pageNum){
		queue.clearUrl();
		System.out.println("=================================开始添加url");
		KDL.urlAddQueue(queue, startPage,pageNum);
		XCDL.urlAddQueue(queue,startPage, pageNum);
		_66IP.urlAddQueueAll(queue, startPage,pageNum);
		_66IP.urlAddQueueProvince(queue, startPage,pageNum);
		System.out.println("=================================开始开启线程");
		for(int i = 1;i<=3;i++){ //开启三个线程
			new CrawlerThreadImp(queue).start();
			System.out.println("=================================启动一个线程" + i);
		}
	}

	public static void main(String[] args) {
		queueManagement queue = new queueManagement();
		queue.InitQueue();
		CrawlerMainClass t = new CrawlerMainClass();
		t.ImpMain(queue,1,20);
	}
}
