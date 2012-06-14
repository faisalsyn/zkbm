package edu.ucsb.cs.zkbm;

import java.io.IOException;
import java.util.List;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

public class ZkAsyncQueueEx implements Lock, Watcher, AsyncCallback.ChildrenCallback, AsyncCallback.StatCallback {

	String name;
	volatile String myPath;
	
	GetChildrenRunnable runnable;

	volatile boolean hasLock = false;
	volatile boolean watching = false;

	ZooKeeper zk;

	Object lockMonitor = new Object();
	Object watchMonitor = new Object();

	public ZkAsyncQueueEx(String name, String zkhost) {

		this.name = name;
		this.myPath = null;
		try {
			zk = new ZooKeeper(zkhost, 3000, null);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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
		zk.delete(myPath, -1, null, null);
		myPath = null;
	}

	@Override
	public boolean acquire() {

		if (myPath != null) {
			System.err.println("Already have the lock on " + name);
			return false;
		}

		try {

			runnable = new GetChildrenRunnable(this);
			new Thread(runnable).start();

			if (myPath == null) {
				myPath = zk.create("/lock/" + name + "-", new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
			}

			synchronized (lockMonitor) {
				while (!hasLock) {
					lockMonitor.wait(1000);
				}
			}

			runnable.cancel();

			return true;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}

	@Override
	public void processResult(int rc, String path, Object ctx, List<String> children) {
		if (myPath == null)
			return;

		int myIndex = children.indexOf(myPath.split("/", 0)[2]);

		if (myIndex < 0)
			return;

		if (myIndex == 0) {
			setLocked();
			return;
		}

		synchronized (watchMonitor) {
			if (watching)
				return;

			zk.exists("/lock/" + children.get(myIndex - 1), true, this, null);
			watching = true;
			
			runnable.cancel();
		}

	}

	@Override
	public void processResult(int rc, String path, Object ctx, Stat stat) {
		if(stat == null)
			setLocked();
	}

	@Override
	public void process(WatchedEvent event) {
		if (event.getType().toString() == "NodeDeleted") {
			setLocked();
		}
	}

	class GetChildrenRunnable implements Runnable {

		AsyncCallback.ChildrenCallback outer;
		volatile boolean running = true;

		public static final long POLL_INTERVAL = 5;

		public GetChildrenRunnable(ChildrenCallback outer) {
			super();
			this.outer = outer;
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

	private void setLocked() {
		synchronized (lockMonitor) {
			hasLock = true;
			lockMonitor.notify();
		}
	}

}
