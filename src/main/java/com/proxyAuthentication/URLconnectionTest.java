package com.proxyAuthentication;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;

public class URLconnectionTest {
	
	public static void main(String []args){
		URL url;
		try {//42.51.13.103:8118  111.1.3.38:8000
			Proxy proxy = new Proxy(Proxy.Type.HTTP, new InetSocketAddress("111.1.3.38", 8000));  
			url = new URL("http://www.163.com");  
			HttpURLConnection uc = (HttpURLConnection)url.openConnection(proxy);  
			uc.setConnectTimeout(10000);
			uc.connect(); 
			/*//url = new URL("http://www.baidu.com");
			//URLConnection conn = url.openConnection();
			conn.setConnectTimeout(10000);
			conn.connect();*/
			InputStream inStream = uc.getInputStream();
			
			if(inStream != null){
				System.out.println("end");
			}else{
				System.out.println("hello");
			}
			
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
}
