package com.imp.MultipleThread;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;

import com.Imp.main.ImpMain;
import com.common.data.DBConnectionParams;
import com.common.data.queueManagement;
import com.common.data.webData;
import com.common.publicMethod.DBconnection.MysqlConnection;
import com.common.publicMethod.webCrawler.WebCrawler;
import com.proxyAuthentication.FreeProxyAuth;
import com.proxyAuthentication.ProxyTestMainClass;
import com.proxyAuthentication.Test.FreeProxyAuthTest;

/**
 * 代理检测，采用多线程的方法来做； 通过比较多线程与异步多线程的运行时间， 发现多线程的运行时间反而比较少
 * 
 * @author lmd
 *
 */
public class ProxyTestThreadImp extends Thread {
	public Log log = LogFactory.getLog(this.getClass());
	private queueManagement queue;
	private int index;

	public ProxyTestThreadImp(queueManagement queue, int index) {
		this.queue = queue;
		this.index = index;
	}

	@Override
	public void run() {
		webData data;
		int port;
		String ip;
		HttpEntity entity;
		long responseTime;
		int times;
		FreeProxyAuth f = new FreeProxyAuth();
		synchronized(ImpMain.IsActive){
			ImpMain.IsActive = false;
		}
		synchronized (ProxyTestMainClass.ThreadCount) {
			
			++ProxyTestMainClass.ThreadCount;
			System.out
					.println("=========线程数量" + ProxyTestMainClass.ThreadCount);
		}
		while (!queue.proxyQueue.isEmpty()) {
			System.out.println("===================================线程" + index);
			try {
				data = queue.proxyQueueGet();
				ip = data.getIp();
				port = data.getPort();
				System.out.println(ip + ":" + port + ":  =========:"
						+ queue.proxyQueue.size());
				synchronized (this) {
					long starttime = System.currentTimeMillis();
					System.out.println("===============================页面抓取"
							+ index + ":" + ip + ":" + port);
					entity = f.getHtmlPageByProxy(f.URL, ip, port);
					long endtime = System.currentTimeMillis();
					responseTime = endtime - starttime;
				//	System.out.println("响应时间：" + responseTime);
				}
				if (entity != null) {
					data.setFlag(true);
					data.setTestTimes(0);
					data.setIsNew(false);
					data.setResponseTime(responseTime);
				} else {
					data.setFlag(false);
					data.setIsNew(false);
					times = data.getTestTimes() + 1;
					data.setTestTimes(times);
					data.setResponseTime(-1);
				}

				// 将数据加入list中,如果检测次数大于10次，那么不加入list中，丢弃；
				synchronized (queue.list) {
					if (data.getTestTimes() < 10) {
						queue.list.add(data);
						System.out.println("============list的大小："
								+ queue.list.size());
					} else {
						System.out.println(data.getIndex()
								+ "=======================================");
					}
				}

			} catch (InterruptedException e) {
				e.printStackTrace();
				System.out.println(e.getMessage());
				log.error(e.getMessage());
			}
		}

		synchronized (ProxyTestMainClass.ThreadCount) {
			--ProxyTestMainClass.ThreadCount;
			System.out.println("=========结束线程数量:"
					+ ProxyTestMainClass.ThreadCount + " : " + index);
			// 到最后，就生一个线程，将数据插入到临时数据库中
			// 如果测试线程结束了，那么程序退出
			if (ProxyTestMainClass.ThreadCount == 0) {
				f.InsertDB(queue);
				synchronized (ImpMain.flag) {
					ImpMain.flag = true;
					ImpMain.IsActive = true;
					ImpMain.startProxyDate = new Date();
					ImpMain.pageNum += 20;
				}
				System.out.println("===============当前抓取页数：" + ImpMain.pageNum);
				System.out.println("finish");
			}
		}
	}
}
