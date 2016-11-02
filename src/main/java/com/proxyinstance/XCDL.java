package com.proxyinstance;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.common.data.queueManagement;
import com.common.data.webData;
import com.common.publicMethod.common.commonMethod;
import com.common.publicMethod.webCrawler.WebCrawler;



public class XCDL extends WebCrawler {
	public Log log = LogFactory.getLog(this.getClass());
	private static final String URL = "http://www.xicidaili.com/";
	public static final String[] typeList = {"nn","nt","wn","wt"};
	
	
	/**
	 * 对西刺代理的url进行解析
	 * @param vars
	 * @return
	 */
	public static String urlAnalyze(Object ...vars){
		return (URL+vars[0]+"/"+vars[1]).toString();
	}
	
	public void htmlParser(HttpEntity entity, ArrayList<webData> list){
		//ArrayList<webData> list = new ArrayList<webData>(); // 存储webdata
		String domain = commonMethod.getDomain(URL);
		try{
			String html = EntityUtils.toString(entity);
			if(entity == null){
				return ;
			}
			
			Document doc = Jsoup.parse(html);
			Element table = doc.getElementById("ip_list");
			if(table == null){
				return ;
			}
			Elements tr_tag = table.select("tr");
			if(tr_tag == null){
				return ;
			}
			//第一行是标题
			for(int i = 1;i<tr_tag.size();i++){
				webData data = new webData();
				Elements td_Tag = tr_tag.get(i).select("td");
				if(td_Tag == null){
					return ;
				}
				
				data.setIp(setStrPro(td_Tag.get(1)));
				data.setPort(setNumPro(td_Tag.get(2)));
				data.setAddress(setStrPro(td_Tag.get(3)));
				//data.setDegreeOfConfidentiality(getDegree(type[0],td_Tag.get(4).text()));
				data.setDegreeOfConfidentiality(td_Tag.get(4).text());
				data.setType(setStrPro(td_Tag.get(5)));
				data.setWebSite(domain);
				
				System.out.println(data.getIp()+":"
						+data.getPort()+":"
						+data.getDegreeOfConfidentiality()+":"
						+data.getAddress()+":"
						+data.getType()+":"
						+data.getWebSite());
				
				list.add(data);
			}
		}catch(Exception e){
			e.printStackTrace();
			log.error(e.getMessage());
		}
		return ;
	}
	
	/**
	 * 字符串设置为""
	 * 
	 * @param tdTag
	 * @return
	 */
	public static String setStrPro(Element tdTag) {
		if (tdTag != null) {
			return tdTag.text();
		} else {
			return "";
		}
	}

	/**
	 * 如果td为空，
	 * 则设置为-1
	 * @param tdTag
	 * @return
	 */
	public static int setNumPro(Element tdTag) {
		if (tdTag != null) {
			return Integer.parseInt(tdTag.text());
		} else {
			return -1;
		}
	}
	
	public static void urlAddQueue(queueManagement queue,int pageNum) {
		Log log = LogFactory.getLog(XCDL.class);
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
