package Primitives;

import java.io.IOException;
import java.util.List;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.AsyncCallback.ChildrenCallback;
import org.apache.zookeeper.ZooDefs.Ids;

public class ZkAsyncQueue implements Watcher, Lock,
		AsyncCallback.ChildrenCallback {

	String name;
	volatile String myPath;
	volatile boolean gotLock = false;
	volatile boolean released = false;
	
	ZooKeeper zk;

	Object gotLockMonitor = new Object();

	@Override
	public void process(WatchedEvent event) {
		// TODO Auto-generated method stub

	}

	public ZkAsyncQueue(String name, String zkhost) {

		this.name = name;
		this.myPath = null;
		try {
			zk = new ZooKeeper(zkhost, 3000, this);

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
		released = false;
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

			GetChildrenRunnable runnable = new GetChildrenRunnable(this);
			new Thread(runnable).start();

			if (myPath == null) {
				myPath = zk.create("/lock/" + name + "-", new byte[0],
						Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
			}

			synchronized (gotLockMonitor) {
				while (!gotLock) {
					gotLockMonitor.wait(1000);
				}
			}

			runnable.cancel();

			return true;

		} catch (KeeperException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return false;
	}

	@Override
	public void processResult(int rc, String path, Object ctx,
			List<String> children) {
		if (released)
			return;
		
		if (myPath != null) {
			//System.err.println (children + " - " + myPath);
			if (children.indexOf(myPath.split("/")[2]) == 0) {
				synchronized (gotLockMonitor) {
					gotLock = true;
					gotLockMonitor.notify();
				}
			}
		}
	}

	class GetChildrenRunnable implements Runnable {

		AsyncCallback.ChildrenCallback outer;
		volatile boolean running = true;

		
		
		public GetChildrenRunnable(ChildrenCallback outer) {
			super();
			this.outer = outer;
		}

		@Override
		public void run() {
			while (running) {
				zk.getChildren("/lock", false, outer, null);
				Thread.yield();
			}
		}

		public void cancel() {
			running = false;
		}

	}

}
