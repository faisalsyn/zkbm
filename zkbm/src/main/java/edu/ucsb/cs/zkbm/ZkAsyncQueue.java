package edu.ucsb.cs.zkbm;

import java.io.IOException;
import java.util.List;

import org.apache.zookeeper.AsyncCallback;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;

public class ZkAsyncQueue implements Lock, AsyncCallback.ChildrenCallback {

	String name;
	volatile String myPath;
	volatile boolean gotLock = false;
	volatile boolean released = false;

	ZooKeeper zk;

	Object gotLockMonitor = new Object();
	
	GetChildrenThread myThread;

	public ZkAsyncQueue(String name, String zkhost) {

		this.name = name;
		this.myPath = null;
		try {
			zk = new ZooKeeper(zkhost, 3000, null);
			myThread = new GetChildrenThread(zk, this);

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

			myThread.start();

			if (myPath == null) {
				myPath = zk.create("/lock/" + name + "-", new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
			}

			synchronized (gotLockMonitor) {
				while (!gotLock) {
					gotLockMonitor.wait(1000);
				}
			}

			myThread.cancel();

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
	public void processResult(int rc, String path, Object ctx, List<String> children) {
		if (released)
			return;

		if (myPath != null) {
			// System.err.println (children + " - " + myPath);
			if (children.indexOf(myPath.split("/")[2]) == 0) {
				synchronized (gotLockMonitor) {
					gotLock = true;
					gotLockMonitor.notify();
				}
			}
		}
	}

}
