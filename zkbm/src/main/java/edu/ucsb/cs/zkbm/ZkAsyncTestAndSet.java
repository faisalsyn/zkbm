package edu.ucsb.cs.zkbm;

import java.io.IOException;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

public class ZkAsyncTestAndSet implements Lock, AsyncCallback.StringCallback {

	String name;
	ZooKeeper zk;

	String myPath;
	byte[] msg;

	volatile boolean created;
	Object createdMonitor = new Object();

	public ZkAsyncTestAndSet(String name, String zkhost) {
		this.name = name;
		try {
			zk = new ZooKeeper(zkhost, 3000, null);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * A parallel test and set issue
	 * 
	 * @return
	 */
	@Override
	public boolean acquire() {
		if (myPath != null) {
			System.err.println ("Already have the lock on "+name);
			return false;
		}
		
		myPath = "/lock/" + name;
		msg = new byte[] { (byte) 0x40 };
		CreatNodeRunnable runnable = null;
		created = false;

		try {
			runnable = new CreatNodeRunnable(this);
			new Thread(runnable).start();

			synchronized (createdMonitor) {
				while (!created) {
					createdMonitor.wait(1000);
				}
			}

			runnable.cancel();

			return true;

		} catch (InterruptedException e) {
			if (runnable != null) {
				runnable.cancel();
			}
			e.printStackTrace();
		}

		return false;

	}

	@Override
	public void close() {
		try {
			zk.close();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void release() {
		zk.delete("/lock/" + name, -1, null, null);
	}

	@Override
	public void processResult(int rc, String path, Object ctx, String name) {
		if (path.equals(myPath)) {
			synchronized (createdMonitor) {
				created = true;
				createdMonitor.notify();
			}
		}
	}

	class CreatNodeRunnable implements Runnable {

		AsyncCallback.StringCallback outer;
		volatile boolean running = true;

		public static final long POLL_INTERVAL = 5;

		public CreatNodeRunnable(StringCallback outer) {
			super();
			this.outer = outer;
		}

		@Override
		public void run() {
			while (running) {
				zk.create(myPath, msg, Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL, outer, null);
				try {
					Thread.sleep(POLL_INTERVAL);
				} catch (InterruptedException e) {
					// ignore
				}
			}
		}

		public void cancel() {
			running = false;
		}

	}

}
