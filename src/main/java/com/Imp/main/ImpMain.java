package com.Imp.main;

import java.util.Calendar;
import java.util.Date;

import com.ImpTest.CrawlerMainClass;
import com.common.data.queueManagement;
import com.common.publicMethod.common.commonMethod;
import com.proxyAuthentication.ProxyTestMainClass;

/**
 * 程序的入口类
 * 
 * @author lmd
 */
public class ImpMain {
	public static Boolean flag = true; // 用来控制循环
	public static Boolean IsActive = true;

	public static Date startCrawlerDate = new Date();
	public static Date startProxyDate = new Date();
	public static Date endDate;

	public static int startPage = 1;
	public static int totalNum = 200;
	public static int pageNum = 20;

	public static void setDefaultPageNum() {
		pageNum = 20;
	}
	
	public static boolean isChanged(int next,int pre){
		if(next != pre){
			return true;
		}else{//next == pre
			return false;
		}
	}
	
	public static void setPageNum() {
		/**
		 * 如果起始页超过总页数，那么startPage = 1,间隔页数设为默认值； 如果没超过总页数，判断下一次的终止页数是否超过总页数；
		 * （1）如果超过，重新设置pageNum的值； (2)没有超过，继续运行
		 */
		if (ImpMain.startPage <= ImpMain.totalNum) {
			if (ImpMain.startPage + ImpMain.pageNum >= ImpMain.totalNum){
				if(isChanged(ImpMain.pageNum,20)) { //如果不相等，那么上一轮发生变化
					ImpMain.startPage = 1;
					ImpMain.setDefaultPageNum();
				}else{//如果相等，那么上一轮没有发生变化
					ImpMain.pageNum = ImpMain.totalNum - ImpMain.startPage;
				}
			}
		}//else这部分可能就执行不到，因为pageNum在结尾处都会变化，不超过总页数
	}

	public static void main(String[] args) {

		int dis1 = 0;
		int dis2 = 0;
		// 队列初始化
		queueManagement queue = new queueManagement();
		queue.InitQueue();
		// 网页抓取对象初始化
		CrawlerMainClass t = new CrawlerMainClass();
		// 代理测试对象初始化
		ProxyTestMainClass p = new ProxyTestMainClass();

		while (true) {
			endDate = new Date();
			dis1 = commonMethod.minuteDistance(startCrawlerDate, endDate);
			dis2 = commonMethod.minuteDistance(startProxyDate, endDate);
			/**
			 * 这两个任务是互斥进行的，用一个flag进行控制; 如果flag=true,并且符合前后时间差，那么抓取任务启动；
			 * 否则，更改flag = false,判断代理测试任务是否满足条件， 如果满足，则代理测试任务启动；否则,更改flag =
			 * true,重新进行抓取任务的判断
			 * 
			 */
			if (flag == true) {
				if (IsActive && dis1 >= 1) {
					// System.out.println(flag + ":" + dis1 + ":" + dis2);
					t.ImpMain(queue, startPage, pageNum);
				}
			} else {
				if (IsActive && dis2 >= 2) {
					// System.out.println(flag + ":" + dis1 + ":" + dis2);
					p.ProxyMain(queue);
					System.out.println("=================我在ImpMain里");
				}
			}
		}
	}
}
