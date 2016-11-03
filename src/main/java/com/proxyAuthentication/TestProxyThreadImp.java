package com.proxyAuthentication;

import java.sql.Connection;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;

import com.common.data.DBConnectionParams;
import com.common.data.queueManagement;
import com.common.data.webData;
import com.common.publicMethod.DBconnection.MysqlConnection;
import com.common.publicMethod.webCrawler.WebCrawler;

public class TestProxyThreadImp extends Thread {
	public Log log = LogFactory.getLog(this.getClass());
	private queueManagement queue;

	public TestProxyThreadImp(queueManagement queue) {
		this.queue = queue;
	}

	@Override
	public void run() {
		webData data;
		int port;
		String ip;
		HttpEntity entity ;
		long responseTime;
		int times;
		FreeProxyAuth f = new FreeProxyAuth();
		synchronized(FreeProxyAuth.ThreadCount){
			++FreeProxyAuth.ThreadCount;
		}
		while (!queue.proxyQueue.isEmpty()) {
			try {
				data = queue.proxyQueueGet();
				ip = data.getIp();
				port = data.getPort();
				synchronized(this){
					long starttime = System.currentTimeMillis();
					entity = f.getHtmlPageByProxy(f.URL,ip,port);
					System.out.println(ip+":"+port);
					long endtime = System.currentTimeMillis();
					responseTime = endtime - starttime;
				}
				if(entity != null){
					data.setFlag(1);
					data.setTestTimes(0);
					data.setResponseTime(responseTime);
				}else{
					data.setFlag(0);
					times = data.getTestTimes() + 1;
					data.setTestTimes(times);
				}
				//将数据加入list中
				synchronized(queue.list){
					queue.list.add(data);
					System.out.println("============list的大小："+ queue.list.size());
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				log.error(e.getMessage());
			}
		}
		
		synchronized(FreeProxyAuth.ThreadCount){
			--FreeProxyAuth.ThreadCount;
		}
		//到最后，就生一个线程，将数据插入到临时数据库中
		/*if(FreeProxyAuth.ThreadCount == 1){
			MysqlConnection mysql = new MysqlConnection();
			DBConnectionParams params = mysql.setDefaultParams();
			mysql.setParams(params);
			Connection conn = mysql.getConnection();
			mysql.insertBatchDataToDB(conn, queue.list);
		}*/
	}
}
