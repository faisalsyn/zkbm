package Primitives;

import java.io.IOException;
import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;


public class ZkLock implements Watcher {
	
	
	String name;
	String myPath;
	boolean gotLock;
	ZooKeeper zk;
	
	@Override
	public void process(WatchedEvent event) {
		// TODO Auto-generated method stub
		//System.out.println(event + " " + event.getType());
		if (event.getType().toString() == "NodeDeleted" )
			acquire_helper();
		
	}
	
	public ZkLock (String name, String zkhost) {
	
		this.name = name;
		this.myPath = null;
		try {
			zk = new ZooKeeper(zkhost, 3000, this);
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public void close () {
		try {
			zk.close();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void release () {
		//try {
			zk.delete(myPath, -1, null, null);
		/*} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeeperException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		myPath = null;

	}
	
	public void acquire () {
		
		if (myPath != null) {
			System.err.println ("Already have the lock on "+name);
			return;
		}
		
		gotLock = false;
		
		acquire_helper();
		
		while (gotLock == false ) {
			try {
				synchronized (name) {
					name.wait ();
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		//System.out.println("YAY! I can do whatever I want");

	}
	
	private int acquire_helper () {
		List<String> children;
		
		try {
			if (myPath == null) {
				//System.out.println ("CREATING! " + myPath);
				myPath = zk.create("/lock/"+name+"-", new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
				//System.out.println ("a " + Thread.currentThread() + "-" + myPath);

			}
			
			children = zk.getChildren("/lock", false);
			
			int myPri = Integer.parseInt(myPath.split("/", 0)[2].split("-")[1]);
			int nextToMe = -1;
			String nextToMeS = "-1";
			boolean success = true;
			for (String child : children) {
				if (!child.split("-")[0].equals(name)) continue;
				int thisPri = Integer.parseInt(child.split("-")[1]);
				if ( thisPri < myPri ){
					success = false;
					if ( thisPri > nextToMe ) {
						nextToMe = thisPri;
						nextToMeS = child;
					}
				}
			}
			
			//System.out.println(success + " " + myPri + " " + nextToMe);

			if ( success ){
				gotLock = true;
				synchronized (name) {
					name.notifyAll();
				}
				return 1;
			}

			Stat st = zk.exists("/lock/"+nextToMeS, true);
			if ( st == null  ){
				acquire_helper();
			}
			
		} catch (KeeperException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return 2;
	}
	

}
