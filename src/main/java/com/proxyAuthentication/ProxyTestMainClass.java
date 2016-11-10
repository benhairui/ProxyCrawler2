package com.proxyAuthentication;

import java.util.ArrayList;

import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;

import com.common.data.queueManagement;
import com.common.data.webData;
import com.imp.MultipleThread.ProxyTestThreadImp;

public class ProxyTestMainClass {
	public static Integer ThreadCount = 0;
	
	public void ProxyMain(queueManagement queue){
		queue.clearUrl();
		FreeProxyAuth f = new FreeProxyAuth();
		f.duplicateRemove(queue); //去重
		//System.out.println(queue.proxyQueue.size());
		for(int i = 1;i<=30;i++){
			new ProxyTestThreadImp(queue,i).start();
		}
	}
	
	public static void main(String[]args){
		queueManagement queue = new queueManagement();
		queue.InitQueue();
		ProxyTestMainClass p = new ProxyTestMainClass();
		p.ProxyMain(queue);
	}
}
