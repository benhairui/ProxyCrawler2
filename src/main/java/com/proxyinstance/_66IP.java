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

/**
 * www.66ip.cn 该网站不涉及到typeList
 * 
 * @author lmd
 *
 */
public class _66IP extends WebCrawler {

	public Log log = LogFactory.getLog(this.getClass());
	private static final String URL = "http://www.66ip.cn/";
	private static final int provinceNum = 34;//省数量
	private String contentType = "text/html";
	
	/**
	 * http://www.66ip.cn/1.html
	 * 全国
	 * @param vars
	 * @return
	 */
	public static String urlAnalyzeAll(Object ...vars){
		return (URL+vars[0]+".html");
	}
	
	/**
	 * 各省市
	 * http://www.66ip.cn/areaindex_1/1.html
	 * @param vars
	 * @return
	 */
	public static String urlAnalyzeProvince(Object ...vars){
		return (URL+"areaindex_"+vars[0]+"/"+vars[1]+".html");
	}

	public void htmlParse(HttpEntity entity, ArrayList<webData> list) {
		//ArrayList<webData> list = new ArrayList<webData>();
		String domain = commonMethod.getDomain(URL);
		try {
			if(entity == null){
				return;
			}
			String html = EntityUtils.toString(entity,"gb2312");
			if (html.isEmpty()) {
				return ;
			}
			Document doc = Jsoup.parse(html);
			Element div = doc.getElementById("main");
			if (div == null) {
				return ;
			}

			Elements table = div.select("table");
			if (table == null) {
				return ;
			}
			Elements tbody = table.get(0).select("tbody");
			if (tbody == null) {
				return ;
			}
			Elements tr_tag = tbody.get(0).select("tr");
			if (tr_tag == null) {
				return ;
			}

			for (int i = 1; i < tr_tag.size(); i++) {
				Elements td_tag = tr_tag.get(i).select("td");
				webData data = new webData();
				data.setIp(td_tag.get(0).text());
				data.setPort(Integer.parseInt(td_tag.get(1).text()));
				data.setAddress(td_tag.get(2).text());
				data.setDegreeOfConfidentiality(td_tag.get(3).text());
				data.setType("");
				data.setWebSite(domain);
				data.setFlag(false);
				data.setResponseTime(0);
				data.setTestTimes(0);
				data.setIsNew(true);
				list.add(data);
				
				System.out.println(data.getIp()+":"
						+data.getPort()+":"
						+data.getDegreeOfConfidentiality()+":"
						+data.getAddress()+":"
						+data.getType()+":"
						+data.getWebSite());
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
			log.error(e.getMessage());
		}
		return ;
	}
	
	/**
	 * 全国代理ip地址添加
	 * @param queue
	 * @param pageNum
	 * @throws InterruptedException
	 */
	public static void urlAddQueueAll(queueManagement queue,int startPage,int pageNum){
		Log log = LogFactory.getLog(_66IP.class);
		for(int i = startPage;i<=pageNum+startPage;i++){
			try {
				queue.urlQueueAdd(urlAnalyzeAll(i));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				System.out.println(e.getMessage());
				log.error(e.getMessage());
			}
		}
	}
	
	/**
	 * 各省市代理ip地址添加
	 * @param queue
	 * @param pageNum
	 * @throws InterruptedException
	 */
	public static void urlAddQueueProvince(queueManagement queue,int startPage,int pageNum){
		Log log = LogFactory.getLog(_66IP.class);
		for(int i = 1;i<=provinceNum;i++){
			for(int j = startPage;j<=pageNum+startPage;j++){
				try {
					queue.urlQueueAdd(urlAnalyzeProvince(i,j));
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					System.out.println(e.getMessage());
					log.error(e.getMessage());
				}
			}
		}
	}
}
