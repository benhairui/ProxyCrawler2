package com.imp.MultipleThread;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;

import com.ImpTest.test;
import com.common.data.DBConnectionParams;
import com.common.data.queueManagement;
import com.common.data.webData;
import com.common.publicMethod.DBconnection.MysqlConnection;
import com.common.publicMethod.common.commonMethod;
import com.proxyinstance.KDL;
import com.proxyinstance.XCDL;
import com.proxyinstance._66IP;

/**
 * 多线程实现
 * 
 * @author lmd
 *
 */
public class ThreadImp extends Thread {
	
	public Log log = LogFactory.getLog(this.getClass());
	private queueManagement queue; // 共享区域的类

	public ThreadImp(queueManagement queue) {
		this.queue = queue;
	}

	@Override
	public void run() {
		String url = "";
		String domain = "";
		HttpEntity entity;
		ArrayList<webData> list;
		
		synchronized(test.threadCount){ //线程数量控制
			++test.threadCount;
		}
		while (!queue.urlQueue.isEmpty()) {
			try {
				// 获取url
				url = queue.urlQueueGet();
				// 获得url的域名
				domain = commonMethod.getDomain(url);

				if (domain.equals("www.kuaidaili.com")) { // 快代理
					KDL k = new KDL();
					entity = k.getHtmlPage(url);
					synchronized (queue.list) {
						k.htmlParser(entity, queue.list);
					}
				} else if (domain.equals("www.xicidaili.com")) {// 西刺代理
					XCDL x = new XCDL();
					entity = x.getHtmlPage(url);
					synchronized (queue.list) {
						x.htmlParser(entity,queue.list);
					}

				} else if (domain.equals("www.66ip.cn")) {// 66IP
					_66IP p = new _66IP();
					entity = p.getHtmlPage(url);
					synchronized (queue.list) {
						p.htmlParse(entity,queue.list);
					}
				}
				System.out.println("===============队列长度:数组长度："+queue.urlQueue.size()+":"+queue.list.size());
			} catch (InterruptedException e) {
				e.printStackTrace();
				log.error(e.getMessage());
			}
		}
		//将数据加入数据库中
		synchronized(test.threadCount){
			--test.threadCount;
		}
		if(test.threadCount == 1){
			MysqlConnection sqlClass = new MysqlConnection();
			DBConnectionParams params = sqlClass.setDefaultParams();
			sqlClass.setParams(params);
			Connection conn = sqlClass.getConnection();
			sqlClass.insertBatchDataToDB(conn, queue.list); //插入数据库中
		}
		System.out.println("end");
	}
}
