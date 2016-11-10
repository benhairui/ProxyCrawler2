package com.proxyAuthentication.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;

import com.common.data.queueManagement;
import com.common.data.webData;
import com.proxyAuthentication.FreeProxyAuth;

public class ProxyTest2 {
	public static void getHttpGet(){
		RequestConfig requestConfig = RequestConfig.custom()
				.setSocketTimeout(50000)
				.setConnectTimeout(50000)
				.setConnectionRequestTimeout(50000)
				.build();
		//如果使用高版本(4.1.2)的httpasyncclient，那么会提示NoClassDefFound,高版本的内部可能缺失类
		CloseableHttpAsyncClient httpClient = HttpAsyncClientBuilder.create()
				.setDefaultRequestConfig(requestConfig).build();
		httpClient.start();

		//获得数据库中的数据
		FreeProxyAuth f = new FreeProxyAuth();
		queueManagement queue = new queueManagement();
		String sql = "select *from ProxyData";
		ArrayList<webData> list = f.getResult(sql);
		System.out.println("数据库中的数量：" + list.size());
		f.duplicateRemove(queue);
//====上面部分，需要在main函数中进行启动
		ArrayList<HttpGet> httpget = new ArrayList<HttpGet>();
		String ip = "";
		int port;
		for(int i =38;i>0;i--){
			ip = list.get(i).getIp();
			port = list.get(i).getPort();
			HttpHost proxy = new HttpHost(ip,port);
	        RequestConfig config = RequestConfig.custom()
	                    .setProxy(proxy)
	                    .build();
			HttpGet  get = new HttpGet("http://www.163.com");
			get.setConfig(config);
			httpget.add(get);
		}
		final CountDownLatch latch = new CountDownLatch(httpget.size());
		for(int i = 0;i<httpget.size();i++){
			final HttpGet get = httpget.get(i);
			System.out.println("第"+(i+1)+"个："+get.getConfig().getProxy().getHostName());
			httpClient.execute(get,new FutureCallback<HttpResponse>(){
				
				public void completed(HttpResponse result) {
					// TODO Auto-generated method stub
					latch.countDown();
					System.out.println("==================="+get.getConfig().getProxy().getHostName());
					System.out.println(get.getRequestLine() + "->"
							+ result.getStatusLine());
				}

				public void failed(Exception ex) {
					// TODO Auto-generated method stub
					latch.countDown();
					//System.out.println("==================="+get.getConfig().getProxy().getHostName());
					System.out.println(get.getRequestLine() + "->" + ex);
				}

				public void cancelled() {
					// TODO Auto-generated method stub
					latch.countDown();
					//System.out.println("==================="+get.getConfig().getProxy().getHostName());
					System.out.println(get.getRequestLine() + "cancelled");
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
	
	public static void main(String[]args){
		getHttpGet();
	}
	
	
	
}
