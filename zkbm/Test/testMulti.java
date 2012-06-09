package Test;

import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Random;
import java.util.RandomAccess;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.math3.distribution.ExponentialDistribution;

import Primitives.ZkLock;
import Primitives.ZkTestAndSet;



class LockThread implements Runnable {

	
	String name;
	String[] servers;
	double serviceTime;
	Logger logger;
	
	public LockThread(String name, String[] servers, double serviceTime) {
		this.name = name;
		this.servers = servers;
		this.serviceTime = serviceTime;
		
		String loggerName = name+"-"+Thread.currentThread();
		logger = Logger.getLogger(loggerName);
		
		logger.setLevel(Level.ALL);

	}
	
	@Override
	public void run() {
		
		// t0: absolute beginning
		long t0 = System.currentTimeMillis();

		Random r = new Random();
		int whicho = r.nextInt(servers.length);
		ZkLock zkl = new ZkLock(name, servers[whicho]);

		long t1 = System.currentTimeMillis();

		zkl.acquire();

		// t2: after the lock was acquired
		// t2-t1: latency of waiting for the lock
		long t2 = System.currentTimeMillis();
		
		logger.info("waiting latency: "+(t2-t1));

		long toSleep = 0;
		
		try {
			ExponentialDistribution exd = new ExponentialDistribution(this.serviceTime);
			// toSleep: time sampled to be slept
			toSleep = (long) exd.sample();
			Thread.sleep(toSleep);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// t3: time after spending time in mutual exclusion block
		// t3-t2: time spent inside mutual exclusion block
		long t3 = System.currentTimeMillis();

		logger.info("time in mutual exclusion: "+(t3-t2));

		zkl.release();
		
		// t4: time after releasing the lock and before closing the connection
		// t4-t3: time required to release the lock
		long t4 = System.currentTimeMillis();
		
		zkl.close();

		// t5: time after closing connection
		// t5-t4: time required to close connection
		long t5 = System.currentTimeMillis();
		
		// add the 
		synchronized (testMulti.x) {
			testMulti.sum = testMulti.sum + (System.currentTimeMillis() - t0);
		}

			
		

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
		
		Options ops = new Options();
		
		ops.addOption("name", true, "name of lock");
		ops.addOption("servers", true, "a comma separated list of servers");
		ops.addOption("i","interarrivaltime", true, "interarrival time");
		ops.addOption("s", "servicetime", true, "service time");
		ops.addOption("n", "number", true, "number of customers (trying to acquire the lock)");
		
		CommandLineParser parser = new PosixParser();
		CommandLine cmd = null;
		try {
			cmd = parser.parse( ops, args);
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		String name = "default";
		if (cmd.hasOption("name")){
			name = cmd.getOptionValue("name"); 
		}

		String[] servers  = {"127.0.0.1"};
		if (cmd.hasOption("servers")){
			String allServers = cmd.getOptionValue("servers");
			servers = allServers.split(",");
			System.out.print ("Servers: ");
			for (String server : servers )
				System.out.print (server+", ");
			System.out.println("");
		}

		double interarrivalTime = 250;
		if (cmd.hasOption('i')){
			interarrivalTime = Double.parseDouble( cmd.getOptionValue('i') ); 
		}

		double serviceTime = 250;
		if (cmd.hasOption('s')){
			serviceTime = Double.parseDouble(cmd.getOptionValue('s')); 
		}

		int num = 100;
		if (cmd.hasOption('n')){
			num = Integer.parseInt(cmd.getOptionValue('n')); 
		}

		System.out.println (name);		
		
		long startt = System.currentTimeMillis();

		testMulti tm = new testMulti();
		Thread[] threads = new Thread[num];
		for (int i =0 ; i < threads.length ; i++ ){
			threads[i] = new Thread(new LockThread(name, servers, serviceTime ));
			threads[i].start();
			try {
				ExponentialDistribution exd = new ExponentialDistribution(interarrivalTime);
				long toSleep = (long) exd.sample();
				Thread.sleep(toSleep);
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

		System.out.println ((sum/threads.length));

	}

	@Override
	public void run() {

	}

}