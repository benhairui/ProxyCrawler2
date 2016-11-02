package com.common.publicMethod.common;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class commonMethod {
	public static Log log = LogFactory.getLog(commonMethod.class);
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
				log.error(e.getMessage());
			}
			System.out.println(host);
		}
		return host;
	}
}
