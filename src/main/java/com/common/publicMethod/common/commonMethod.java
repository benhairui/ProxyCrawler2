package com.common.publicMethod.common;

import java.net.MalformedURLException;
import java.net.URL;

public class commonMethod {
	
	/**
	 * 提取网站的域名
	 * @param url
	 * @return
	 * @throws MalformedURLException 
	 */
	public static String getDomain(String url){
		String host = "";
		if(!url.equals("")){
			URL Url;
			try {
				Url = new URL(url);
				host = Url.getHost();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println(host);
		}
		return host;
	}
}
