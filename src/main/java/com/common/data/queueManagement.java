package com.common.data;

import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * （1）该类不涉及到多个线程向队列添加url，因为当前的代理网站网址都比较简单，直接添加（或者使用单线程来添加）；
 * 	   主要的作用是用来作为共享队列控制url的取出；
 * （2）将抓取的数据放入一个ArrayList中，在数量达到一定成都后，写入数据库，
 * 	   这里需要维护一个ArrayList<webData>共享数据
 * @author lmd
 *
 */
public class queueManagement {
	private static final int queueMaxSize = 10000000;
	//共享队列，主要用来存储url
	public ArrayBlockingQueue<String> urlQueue 	;
	//= new ArrayBlockingQueue<String>(queueMaxSize);
	
	//共享队列，主要用来存储代理ip的信息
	public ArrayBlockingQueue<webData> proxyQueue;
	//= new ArrayBlockingQueue<webData>(queueMaxSize);
	
	//用来存储抓下来的代理服务器
	public ArrayList<webData> list ;
	//= new ArrayList<webData>();
	
	public void InitQueue(){
		urlQueue = new ArrayBlockingQueue<String>(queueMaxSize);
		proxyQueue = new ArrayBlockingQueue<webData>(queueMaxSize);
		list = new ArrayList<webData>();
	}
	
	/**
	 * ArrayBlockingQueue的take函数，会在队列为空时，线程wait并且释放cpu
	 * @return
	 * @throws InterruptedException
	 */
	public String urlQueueGet() throws InterruptedException{
		return urlQueue.take();
	}
				
	public void urlQueueAdd(String elem) throws InterruptedException{
		urlQueue.put(elem);
	}
	
	//===proxyQueue添加取出
	public webData proxyQueueGet() throws InterruptedException{
		return proxyQueue.take();
	}
	
	public void proxyQueueAdd(webData data) throws InterruptedException{
		proxyQueue.put(data);
	}
	
	public void clearUrl(){
		if(!urlQueue.isEmpty()){
			urlQueue.clear();
		}
		if(!list.isEmpty()){
			list.clear();
		}
		if(!(proxyQueue.isEmpty())){
			proxyQueue.clear();
		}
	}
}
