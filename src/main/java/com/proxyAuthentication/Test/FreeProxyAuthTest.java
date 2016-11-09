package com.proxyAuthentication.Test;

import java.io.IOException;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Future;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.client.HttpAsyncClients;
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

public class FreeProxyAuthTest extends WebCrawler {
	
	
	
	@SuppressWarnings("unchecked")
	public static void moreRequest(String ip,int port) {
		RequestConfig requestConfig = RequestConfig.custom()
				.setSocketTimeout(5000)
				.setConnectTimeout(5000)
				.build();
		HttpHost proxy = new HttpHost(ip, port, "http");
		DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
		HttpAsyncClientBuilder builder = HttpAsyncClients
				.custom()
				.setRoutePlanner(routePlanner);
		
		
		//如果使用高版本(4.1.2)的httpasyncclient，那么会提示NoClassDefFound,高版本的内部可能缺失类
		CloseableHttpAsyncClient httpClient = builder.create()
				.setDefaultRequestConfig(requestConfig).build();
		
		httpClient.start();
		final HttpGet[] requests = new HttpGet[] {
				/*new HttpGet("http://www.apache.org/"),
				new HttpGet("http://www.baidu.com/"),
				new HttpGet("http://www.oschina.net/")*/ 
				new HttpGet("http://www.163.com"),
		};

		final CountDownLatch latch = new CountDownLatch(requests.length);
		for (final HttpGet request : requests) {
			httpClient.execute(request, new FutureCallback() {
				
				public void completed(Object result) {
					// TODO Auto-generated method stub
					final HttpResponse response = (HttpResponse) result;
					latch.countDown();
					System.out.println(request.getRequestLine() + "->"
							+ response.getStatusLine());
				}

				public void failed(Exception ex) {
					// TODO Auto-generated method stub
					latch.countDown();
					System.out.println(request.getRequestLine() + "->" + ex);
				}

				public void cancelled() {
					// TODO Auto-generated method stub
					latch.countDown();
					System.out.println(request.getRequestLine() + "cancelled");
				}
			});
		}

		try {
			latch.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				httpClient.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println("finish!");
	}

	public static void oneReuest(String ip,int port){
		RequestConfig requestConfig = RequestConfig.custom()
				.setSocketTimeout(5000)
				.setConnectTimeout(5000)
				.build();
		HttpHost proxy = new HttpHost(ip, port, "http");
		DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
		HttpAsyncClientBuilder builder = HttpAsyncClients
				.custom()
				.setRoutePlanner(routePlanner);
		
		
		//如果使用高版本(4.1.2)的httpasyncclient，那么会提示NoClassDefFound,高版本的内部可能缺失类
		CloseableHttpAsyncClient httpClient = builder.create()
				.setDefaultRequestConfig(requestConfig).build();
        httpClient.start();
        final HttpGet request = new HttpGet("http://www.apache.org/");
        final Future future = httpClient.execute(request, null);
        try {
            HttpResponse response = (HttpResponse) future.get();
            System.out.println("Response:" + response.getStatusLine());
            System.out.println("Shutting down");
        } catch (Exception ex) {
           // Logger.getLogger(Httpasyncclient.class.getName()).log(Level.SEVERE, null, ex);
        }finally{
            try {
            	httpClient.close();
            } catch (IOException ex) {
               // Logger.getLogger(Httpasyncclient.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        System.out.println("执行完毕");
    }
	
	public static void main(String []args) throws ParseException, IOException{
		System.out.println("start");
		FreeProxyAuthTest f = new FreeProxyAuthTest();
		queueManagement queue = new queueManagement();

	//	ArrayList<webData> list = f.getResult();
		//System.out.println("数据库中的数量：" + list.size());
		//f.duplicateRemove(list, queue);
		
		for(int i = 1;i<10;i++){
			//new TestProxyThreadImp(queue).start();;
		}
	}

}
