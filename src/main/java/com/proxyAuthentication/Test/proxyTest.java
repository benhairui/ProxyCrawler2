package com.proxyAuthentication.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.DefaultProxyRoutePlanner;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.client.HttpAsyncClients;
import org.apache.http.nio.client.HttpAsyncClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import com.common.data.queueManagement;
import com.common.data.webData;
import com.imp.MultipleThread.ProxyTestThreadImp;
import com.proxyAuthentication.FreeProxyAuth;

public class proxyTest {

	private static final String userAgent = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.116 Safari/537.36";
	private static final String accept = "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8";
	private String contentType = "text/html; charset=gb2312";

	// 加入头部信息
	public void setHeaders(HttpGet httpget) {
		httpget.setHeader("User-Agent", userAgent);
		httpget.setHeader("Accept", accept);
		// httpget.setHeader("Content-Type", contentType);
	}

	/**
	 * 返回一个get对象
	 * 
	 * @param urlPath
	 * @param ip
	 * @param port
	 * @return
	 */
	public HttpGet AsyncHttpClientTest(String urlPath, String ip, int port) {
		HttpHost proxy = new HttpHost(ip, port);
		RequestConfig requestConfig = RequestConfig.custom().setProxy(proxy)
				.setAuthenticationEnabled(true).setSocketTimeout(50000)
				.setConnectTimeout(50000).setConnectionRequestTimeout(50000)
				.build();

		HttpContext httpContext = new BasicHttpContext();

		HttpGet get = new HttpGet(urlPath);
		setHeaders(get);
		get.setConfig(requestConfig);
		return get;
	}

	/**
	 * 根据自定义的数量来获得同时进行异步的数组 可以从队列中获取，也可以从集合中获取
	 * 
	 * @param num
	 * @return
	 */
	public ArrayList<webData> getDataByNum(int num, ArrayList<webData> dataList) {
		ArrayList<webData> list = new ArrayList<webData>();

		return list;
	}
	
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
		
		/*CloseableHttpAsyncClient httpClient = HttpAsyncClients.custom()
				.setRoutePlanner(routePlanner)
				.setDefaultRequestConfig(requestConfig) .build();*/
		
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
        final HttpGet request = new HttpGet("http://www.baidu.com");
        final Future future = httpClient.execute(request, null);
        try {
            HttpResponse response = (HttpResponse) future.get();
            System.out.println("Response:" + response.getStatusLine());
            HttpEntity entity = response.getEntity();
            if(entity!=null){
            	System.out.println("有内容");
            	//System.out.println(EntityUtils.toString(entity));
            }else{
            	System.out.println("没内容");
            }
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
	
	public static void oneRequest2(String ip,int port) throws InterruptedException, ExecutionException, IOException{
		RequestConfig requestConfig = RequestConfig.custom()
				.setSocketTimeout(5000)
				.setConnectTimeout(5000)
				.build();
		HttpHost proxy = new HttpHost(ip, port, "http");
		DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
		HttpClientBuilder builder  = HttpClients.custom().setRoutePlanner(routePlanner);
		HttpClient httpClient = builder.create()
				.setDefaultRequestConfig(requestConfig).build();
		//CloseableHttpAsyncClient httpclient = HttpAsyncClients.createDefault();
	        try {
	           /* httpclient.start();
	            //HttpHost proxy = new HttpHost(ip, port);
	            RequestConfig config = RequestConfig.custom()
	                    .setProxy(proxy)
	                    .build();*/
	            HttpGet httpGet = new HttpGet("https://www.google.com.hk");
	          //  request.setConfig(config);
	            HttpResponse response = httpClient.execute(httpGet);
	            //HttpResponse response = future.get();
	            System.out.println("Response: " + response.getStatusLine());
	            HttpEntity entity = response.getEntity();
	            System.out.println(EntityUtils.toString(entity));
	            System.out.println("Shutting down");
	            httpGet.releaseConnection();
	        } finally {
	        	
	        }
	}
	
	public static void oneRequest3(String ip,int port) throws InterruptedException, ExecutionException, IOException{
		RequestConfig requestConfig = RequestConfig.custom()
				.setSocketTimeout(5000)
				.setConnectTimeout(5000)
				.build();
		//如果使用高版本(4.1.2)的httpasyncclient，那么会提示NoClassDefFound,高版本的内部可能缺失类
		CloseableHttpAsyncClient httpClient = HttpAsyncClientBuilder.create()
				.setDefaultRequestConfig(requestConfig).build();
		try {
			httpClient.start();
            HttpHost proxy = new HttpHost(ip, port);
            RequestConfig config = RequestConfig.custom()
                    .setProxy(proxy)
                    .build();
            HttpGet request = new HttpGet("https://issues.apache.org/");
            request.setConfig(config);
            Future<HttpResponse> future = httpClient.execute(request, null);
            HttpResponse response = future.get();
            HttpEntity entity = response.getEntity();
            if(entity != null){
            	System.out.println("不为空");
            }else{
            	System.out.println("为空");
            }
            System.out.println("Response: " + response.getStatusLine());
            System.out.println("Shutting down");
        } finally {
        	httpClient.close();
        }
	}
	
	
	//返回设置好config的httpClient
	public CloseableHttpAsyncClient setRequestConfig(){
		RequestConfig requestConfig = RequestConfig.custom()
				.setSocketTimeout(5000)
				.setConnectTimeout(5000)
				.setConnectionRequestTimeout(5000)
				.build();
		//如果使用高版本(4.1.2)的httpasyncclient，那么会提示NoClassDefFound,高版本的内部可能缺失类
		CloseableHttpAsyncClient httpClient = HttpAsyncClientBuilder.create()
				.setDefaultRequestConfig(requestConfig).build();
		
		return httpClient;
	}
	
	//线程内部执行同步方法
	public void execute(ArrayList<webData> dataList,CloseableHttpAsyncClient httpClient,final queueManagement queue){
		String ip = "";
		int port = 0;
		final CountDownLatch latch = new CountDownLatch(dataList.size());
		for(int i = 0;i<dataList.size();i++){
			//设置HttpGet请求config
			final webData data = dataList.get(i);  //final修饰对象，引用的对象不能改变
			ip = dataList.get(i).getIp();
			port = dataList.get(i).getPort();
			HttpHost proxy = new HttpHost(ip,port);
	        RequestConfig config = RequestConfig.custom()
	                    .setProxy(proxy)
	                    .build();
			final HttpGet  get = new HttpGet("http://www.163.com");
			get.setConfig(config);
			httpClient.execute(get, new FutureCallback<HttpResponse>(){
				public void completed(HttpResponse result) {
					// TODO Auto-generated method stub
					latch.countDown();
					HttpEntity entity ;
					entity = result.getEntity();
					System.out.println("==================="+get.getConfig().getProxy().getHostName());
					System.out.println(get.getRequestLine() + "->"+ result.getStatusLine());
					
					if(result.getStatusLine().getStatusCode() == 200){
						if(entity != null){
							data.setFlag(true);
							data.setTestTimes(0);
							//data.setResponseTime(responseTime);
						}else{
							data.setFlag(false);
							//times = data.getTestTimes() + 1;
							//data.setTestTimes(times);
						}
					}
					synchronized(queue.list){
						queue.list.add(data);
						System.out.println("========================list数量:" + queue.list.size());
					}
				}

				public void failed(Exception ex) {
					// TODO Auto-generated method stub
					latch.countDown();
					//System.out.println("==================="+get.getConfig().getProxy().getHostName());
					System.out.println(get.getRequestLine() + "->" + ex);
					synchronized(queue.list){
						queue.list.add(data);
						System.out.println("========================list数量:" + queue.list.size());
					}
				}

				public void cancelled() {
					// TODO Auto-generated method stub
					latch.countDown();
					//System.out.println("==================="+get.getConfig().getProxy().getHostName());
					System.out.println(get.getRequestLine() + "cancelled");
				}
			});
		}
		try{
			latch.await();
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws InterruptedException, IOException, ExecutionException {
		//42.51.13.103:8118  111.1.3.38:8000
		String[] ip={"42.51.13.103","111.1.3.38"};
		int [] port = {8118,8000};
		
		proxyTest p = new proxyTest();
		System.out.println("start");
		FreeProxyAuth f = new FreeProxyAuth();
		queueManagement queue = new queueManagement();

		//ArrayList<webData> list = f.getResult();
		//System.out.println("数据库中的数量：" + list.size());
		f.duplicateRemove(queue); //去重
		CloseableHttpAsyncClient httpClient = p.setRequestConfig();
		httpClient.start();
		for(int i = 1;i<=30;i++){
			new ProxyTestThreadImp(queue,i).start();;
		}
		
		
		String IP = "";
		int Port ;
		//proxyTest.oneRequest2("42.51.13.103", 2542);;
		/*for(int i = 0;i<queue.proxyQueue.size();i++){
			System.out.println("=========第" + (i+1) + "个");
			IP = queue.proxyQueueGet().getIp();
			Port = queue.proxyQueueGet().getPort();
			System.out.println(IP + ":" + Port);
			new TestProxyThreadImp(queue,httpClient,i).start();;
			//proxyTest.oneReuest(IP,Port);
			//proxyTest.oneRequest2("42.51.13.103", 8118);
			//proxyTest.oneRequest2(IP, Port);;
			//proxyTest.oneRequest3(IP,Port);
		}*/
	}

}
