package com.common.publicMethod.webCrawler;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import com.common.data.webData;

public abstract class WebCrawler {
	//日志记录
	Log log = LogFactory.getLog(this.getClass());
	// 每一个继承类拥有一个index标识
	public int INDEX;
	private static final String userAgent = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/52.0.2743.116 Safari/537.36";
	private static final String accept = "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,*/*;q=0.8";
	private String contentType = "text/html; charset=gb2312";
	private static HttpClient httpClient = HttpClients.createDefault();
	
	//加入头部信息
	public void setHeaders(HttpGet httpget){
		httpget.setHeader("User-Agent", userAgent);
		httpget.setHeader("Accept",accept);
		//httpget.setHeader("Content-Type", contentType);
	}
	
	public HttpEntity getHtmlPage(String urlPath){
		RequestConfig defaultConfig = RequestConfig.custom().build();
		RequestConfig requestConfig = RequestConfig.copy(defaultConfig)
				.setSocketTimeout(50000)
				.setConnectTimeout(50000)
				.setConnectionRequestTimeout(50000)
				.build();
		HttpContext httpContext = new BasicHttpContext();
		
		HttpGet get = new HttpGet(urlPath);
		setHeaders(get);
		get.setConfig(requestConfig);
		CloseableHttpResponse response;
		try {
			response = (CloseableHttpResponse) httpClient.execute(get,httpContext);
			System.out.println(response.getStatusLine());
			
			//如果服务器返回错误，那么直接返回null;
			//这里还应该加入4xx的返回结果；
			//最好能加入日志记录，同时，在程序出现异常的时候，可以保存已有的结果；下次运行的时候，运行剩下的数据
			int statusCode = response.getStatusLine().getStatusCode();
			if(statusCode == 500||statusCode == 501||statusCode == 502||statusCode == 503
					||statusCode == 504||statusCode == 505){
				return null;
			}
			
			HttpEntity entity = response.getEntity();
			if(entity != null){
				entity = new BufferedHttpEntity(entity);
			}
			response.close();
			return entity;
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error(e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.error(e.getMessage());
		}
		return null;
	}
	
	/**
	 * 获得对应实体的html后，然后对页面进行解析
	 * @param entity
	 * @return
	 */
	public ArrayList<webData> htmlParse(HttpEntity entity){
		return null;
	}
	
}
