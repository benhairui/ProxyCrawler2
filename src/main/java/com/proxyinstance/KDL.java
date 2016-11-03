package com.proxyinstance;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.ParseException;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.common.data.queueManagement;
import com.common.data.webData;
import com.common.publicMethod.common.commonMethod;
import com.common.publicMethod.webCrawler.WebCrawler;


public class KDL extends WebCrawler{
	//日志记录
	public Log log = LogFactory.getLog(this.getClass());
	
	private static final String URL = "http://www.kuaidaili.com/free/";
	public static final String[] typeList = {"inha","intr","outha","outtr"};
	
	public static String urlAnalyze(Object ...vars){
		return (URL+vars[0]+"/"+vars[1]).toString();
	}
	
	/**
	 * 对www.kuaidaili.com中的页面进行解析，获得代理服务器的相关信息
	 * @param entity
	 * @return
	 */
	public void htmlParser(HttpEntity entity,ArrayList<webData> list){
		//ArrayList<webData> list = new ArrayList<webData>(); // 存储webdata
		String domain = commonMethod.getDomain(URL); 
		try {
			String html = EntityUtils.toString(entity);
			if (html.isEmpty()||entity == null) {
				return ;
			}
			
			Document doc = Jsoup.parse(html);
			Element div = doc.getElementById("list");
			if (div == null) {
				return ;
			}
			
			Elements table = div.select("table[class=table table-bordered table-striped]");
			if (table == null) {
				System.out.println("table为空");
				return ;
			}
			
			Elements tbody = table.get(0).select("tbody");
			if(tbody == null){
				System.out.println("tbody为空");
				return ;
			}
			
			Elements tr_tag = tbody.get(0).select("tr");
			if(tr_tag == null){
				return;
			}
			System.out.println(tr_tag.size()+"数量btn center");
			// 循环读取tr标签里的内容
			for (int i = 0; i < tr_tag.size(); i++) {
				webData data = new webData();
				Elements tdTag_Ip = tr_tag.get(i).getElementsByAttributeValue(
						"data-title", "IP");
				data.setIp(setStrPro(tdTag_Ip));

				Elements tdTag_Port = tr_tag.get(i)
						.getElementsByAttributeValue("data-title", "PORT");
				data.setPort(setNumPro(tdTag_Port));

				Elements tdTag_Anony = tr_tag.get(i)
						.getElementsByAttributeValue("data-title", "匿名度");
				//data.setDegreeOfConfidentiality(getDegree(type[0],tdTag_Anony.text()));
				data.setDegreeOfConfidentiality(tdTag_Anony.text());
				Elements tdTag_Type = tr_tag.get(i)
						.getElementsByAttributeValue("data-title", "类型");
				data.setType(setStrPro(tdTag_Type));

				Elements tdTag_Address = tr_tag.get(i)
						.getElementsByAttributeValue("data-title", "位置");

				data.setAddress(setStrPro(tdTag_Address));
				data.setWebSite(domain);
				data.setFlag(0);
				data.setResponseTime(0);
				data.setTestTimes(0);
				
				list.add(data);
				System.out.println(data.getIp()+":"
						+data.getPort()+":"
						+data.getDegreeOfConfidentiality()+":"
						+data.getAddress()+":"
						+data.getType()+":"
						+data.getWebSite());
			}

		} catch (ParseException e) {
			e.printStackTrace();
			log.error(e.getMessage());
		} catch (IOException e) {
			e.printStackTrace();
			log.error(e.getMessage());
		}
		//return list;
	}
	
	/**
	 * 如果标签里的内容为null,那么字符串设置为""
	 * @param tdTag
	 * @return
	 */
	public static String setStrPro(Elements tdTag) {
		if (tdTag != null) {
			return tdTag.get(0).text();
		} else {
			return "";
		}
	}
	/**
	 * 如果td为null,则设置为-1
	 * @param tdTag
	 * @return
	 */
	public static int setNumPro(Elements tdTag) {
		if (tdTag != null) {
			return Integer.parseInt(tdTag.get(0).text());
		} else {
			return -1;
		}
	}
	
	/**
	 * 向队列添加url地址
	 * @param queue
	 * @param pageNum
	 * @throws InterruptedException 
	 */
	public static void urlAddQueue(queueManagement queue,int pageNum){
		Log log = LogFactory.getLog(KDL.class);
		for(int i = 0;i<typeList.length;i++){
			for(int j = 1;j<pageNum;j++){
				try {
					queue.urlQueueAdd(urlAnalyze(typeList[i],j));
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					log.error(e.getMessage());
				}
			}
		}
	}
	
	
}
