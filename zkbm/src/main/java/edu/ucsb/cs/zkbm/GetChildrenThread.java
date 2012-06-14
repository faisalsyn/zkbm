package edu.ucsb.cs.zkbm;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.AsyncCallback.ChildrenCallback;

class GetChildrenThread extends Thread {

	AsyncCallback.ChildrenCallback outer;
	volatile boolean running = true;
	ZooKeeper zk;

	public static final long POLL_INTERVAL = 5;

	public GetChildrenThread(ZooKeeper zk, ChildrenCallback outer) {
		super();
		this.outer = outer;
		this.zk = zk;
	}

	@Override
	public void run() {
		while (running) {
			zk.getChildren("/lock", false, outer, null);
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
