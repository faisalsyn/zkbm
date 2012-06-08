package Test;

import java.io.RandomAccessFile;
import java.util.Random;
import java.util.RandomAccess;

import org.apache.commons.math3.distribution.ExponentialDistribution;

import Primitives.ZkLock;
import Primitives.ZkTestAndSet;



class LockThread implements Runnable {

	@Override
	public void run() {
		
		long startt0 = System.currentTimeMillis();

		ZkLock zkl = new ZkLock("name", "pacific");
		
		long startt = System.currentTimeMillis();

		zkl.acquire();
		
		long startt2 = System.currentTimeMillis();

		long toSleep = 0;
		
		try {
			ExponentialDistribution exd = new ExponentialDistribution(500.0/5.0);
			toSleep = (long) exd.sample();
			String myS = "I want to sleep for: "+toSleep;
			long start = System.currentTimeMillis();
			/*if (toSleep > 25)
				toSleep -= 25;
			else
				toSleep = 1;*/
			Thread.sleep(toSleep);
			System.out.println (myS+ " - but slept for "+(System.currentTimeMillis()-start));
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		zkl.release();
		
		System.out.println ("VERYALL: "+(System.currentTimeMillis() - startt) +" ALL: "+(System.currentTimeMillis() - startt)+ " - PART:" + (System.currentTimeMillis() - startt2) + " - slept for: "+toSleep);
		synchronized (testMulti.x) {
			testMulti.sum = testMulti.sum + (System.currentTimeMillis() - startt);
		}
		zkl.close();
		
		System.out.println ("## VERYALL: "+(System.currentTimeMillis() - startt) +" ALL: "+(System.currentTimeMillis() - startt)+ " - PART:" + (System.currentTimeMillis() - startt2) + " - slept for: "+toSleep);

	}
	
}

class TestAndSetThread implements Runnable {

	@Override
	public void run() {

		ZkTestAndSet zktas = new ZkTestAndSet("name", "127.0.0.1");
		
		long startt = System.currentTimeMillis();

		while(!zktas.TestAndSet());
		
		try {
			Thread.sleep (200);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		zktas.release();
		
		System.out.println ((System.currentTimeMillis() - startt));
		synchronized (testMulti.x) {
			testMulti.sum = testMulti.sum + (System.currentTimeMillis() - startt);
		}
		zktas.close();
	}
	
}

public class testMulti implements Runnable {

	static long sum;
	static String x;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		sum = 0;
		x = "";
		
		long startt = System.currentTimeMillis();

		testMulti tm = new testMulti();
		Thread[] threads = new Thread[1000];
		for (int i =0 ; i < threads.length ; i++ ){
			threads[i] = new Thread(new LockThread());
			threads[i].start();
			try {
				ExponentialDistribution exd = new ExponentialDistribution(500.0/4.0);
				long toSleep = (long) exd.sample();
				String myS = "I want to sleep for: "+toSleep;
				long start = System.currentTimeMillis();
				Thread.sleep(toSleep);
				System.out.println (myS+ " - but slept for "+(System.currentTimeMillis()-start));
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		for (int i = 0 ; i < threads.length ; i++){
			try {
				threads[i].join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		System.out.println ("TOTAL: "+(System.currentTimeMillis() - startt));
		System.out.println ("SUM: "+sum+" - AVERAGE: "+(sum/threads.length));

	}

	@Override
	public void run() {

	}

}
