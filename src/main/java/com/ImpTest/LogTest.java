package com.ImpTest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class LogTest {

	Log log = LogFactory.getLog(this.getClass());

	public void one() {
		System.out.println("start");
		log.info("into one method");
		try {
			System.out.println(9 / 0);
		} catch (RuntimeException e) {
			log.error(e.getMessage());
		}
		System.out.println("test2");
		log.info("out one method");
	}

	public static void main(String[] args) {
		new LogTest().one();
	}

}
