package Test;

import java.io.IOException;
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
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import Primitives.ZkLock;

class CreateThread implements Runnable, Watcher {
	
	String name;
	String[] servers;
	Logger logger;
	
	public CreateThread(String name, String[] servers) {
		this.name = name;
		this.servers = servers;
		
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
		
		ZooKeeper zk = null;
		try {
			zk = new ZooKeeper(servers[whicho], 3000, this);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		long t1 = System.currentTimeMillis();

		
		try {
			zk.close();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		long t5 = System.currentTimeMillis();
		
		synchronized (Replica.x) {
			Replica.sum = Replica.sum + (System.currentTimeMillis() - t0);
		}
		
	}

	@Override
	public void process(WatchedEvent event) {
		// TODO Auto-generated method stub
		
	}
	
}

public class Replica implements Runnable {

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

		double interarrivalTime = 10;
		if (cmd.hasOption('i')){
			interarrivalTime = Double.parseDouble( cmd.getOptionValue('i') ); 
		}

		int num = 500;
		if (cmd.hasOption('n')){
			num = Integer.parseInt(cmd.getOptionValue('n')); 
		}

		System.out.println (name);		
		
		long startt = System.currentTimeMillis();

		testMulti tm = new testMulti();
		Thread[] threads = new Thread[num];
		for (int i =0 ; i < threads.length ; i++ ){
			threads[i] = new Thread(new CreateThread(name, servers ));
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
