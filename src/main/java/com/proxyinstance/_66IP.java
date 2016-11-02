package com.proxyinstance;

import java.util.ArrayList;

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
			String html = EntityUtils.toString(entity,"gb2312");
			if (html.isEmpty() || entity == null) {
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
				list.add(data);
				
				System.out.println(data.getIp()+":"
						+data.getPort()+":"
						+data.getDegreeOfConfidentiality()+":"
						+data.getAddress()+":"
						+data.getType()+":"
						+data.getWebSite());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ;
	}
	
	/**
	 * 全国代理ip地址添加
	 * @param queue
	 * @param pageNum
	 * @throws InterruptedException
	 */
	public static void urlAddQueueAll(queueManagement queue,int pageNum) throws InterruptedException{
		for(int i = 1;i<pageNum;i++){
			queue.urlQueueAdd(urlAnalyzeAll(i));
		}
	}
	
	/**
	 * 各省市代理ip地址添加
	 * @param queue
	 * @param pageNum
	 * @throws InterruptedException
	 */
	public static void urlAddQueueProvince(queueManagement queue,int pageNum) throws InterruptedException{
		for(int i = 1;i<=provinceNum;i++){
			for(int j = 1;j<pageNum;j++){
				queue.urlQueueAdd(urlAnalyzeProvince(i,j));
			}
		}
	}
}
