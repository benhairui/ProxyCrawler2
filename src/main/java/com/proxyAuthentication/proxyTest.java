package com.proxyAuthentication;

public class proxyTest {
	
	public static void main(String[] args){
		String ip = "183.141.159.99";
		int port = 3128;
		
		FreeProxyAuth f = new FreeProxyAuth();
		f.getHtmlPageByProxy(f.URL, ip, port);
		
		
	}
	
}
