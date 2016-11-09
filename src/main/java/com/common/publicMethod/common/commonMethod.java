package com.common.publicMethod.common;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;

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
				System.out.println(e.getMessage());
				log.error(e.getMessage());
			}
			System.out.println(host);
		}
		return host;
	}
	
	public static int getHourOrMinute(Date d,int flag){
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		if(flag == 1){
			return c.get(Calendar.HOUR_OF_DAY);
		}
		if(flag == 0){
			return c.get(Calendar.MINUTE);
		}
		return -1;
	}
	
	public static int minuteDistance(Date start,Date end){
		int starthour = getHourOrMinute(start,1);
		int startminute = getHourOrMinute(start,0);
		int endhour = getHourOrMinute(end,1);
		int endminute = getHourOrMinute(end,0);
		
		if(endhour > starthour){
			if(endminute >= startminute){
				return (endhour-starthour)*60 + (endminute - startminute);
			}else{//endminute(00) < startminute(50)
				--endhour;
				endminute += 60;
				return (endhour-starthour)*60 + (endminute - startminute);
			}
		}
		if(endhour == starthour){
			if(endminute >= startminute){
				return (endhour-starthour)*60 + (endminute - startminute);
			}
		}
		return -1;
	}
	
	public static void main(String[] args){
		Date start = new Date();
		Date end;
		while(true){
			end = new Date();
			minuteDistance(start,end);
		}
	}
	
	
	
}
