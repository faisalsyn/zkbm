package edu.ucsb.cs.zkbm;

import java.io.IOException;
import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;


public class ZkQueue implements Watcher, Lock {
	
	String name;
	String myPath;
	boolean gotLock = false;
	ZooKeeper zk;

	Object gotLockMonitor = new Object();
	
	@Override
	public void process(WatchedEvent event) {
		// TODO Auto-generated method stub
		//System.out.println(event + " " + event.getType());
		if (event.getType().toString() == "NodeDeleted" ) {
			synchronized(gotLockMonitor) {
				gotLock = true;
				gotLockMonitor.notify();
			}
		}
		
	}
	
	public ZkQueue (String name, String zkhost) {
	
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
	public void close () {
		try {
			zk.close();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void release () {
		zk.delete(myPath, -1, null, null);
		myPath = null;
	}
	
	@Override
	public boolean acquire () {
		
		if (myPath != null) {
			System.err.println ("Already have the lock on "+name);
			return false;
		}
		
		List<String> children;
		
		try {
			if (myPath == null) {
				myPath = zk.create("/lock/"+name+"-", new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
			}
			
			children = zk.getChildren("/lock", false);

			int myIndex = children.indexOf(myPath.split("/", 0)[2]);

			assert(myIndex >= 0);

			if(myIndex == 0) {
				return true;
			}

			Stat st = zk.exists("/lock/" + children.get(myIndex - 1), true);
			if ( st == null  ){
				return true;
			}

			synchronized(gotLockMonitor) {
				while(!gotLock) {
					gotLockMonitor.wait(1000);
				}
			}

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
	

}
