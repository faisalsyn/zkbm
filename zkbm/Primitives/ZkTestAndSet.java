package Primitives;

import java.io.IOException;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.AsyncCallback.StringCallback;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;

public class ZkTestAndSet implements Watcher {
	
	String name;
	ZooKeeper zk;
	
	@Override
	public void process(WatchedEvent event) {
		// TODO Auto-generated method stub
		
	}
	
	public ZkTestAndSet (String name, String zkhost) {
		this.name = name;
		try {
			zk = new ZooKeeper(zkhost, 3000, this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean TestAndSet () {
		String path = "/TestAndSet/"+name;
		byte[] msg = {(byte) 0x40};
		boolean success = true;
		try {
			//Stat st = zk.exists(path, true);
			//String myPath = zk.create(path, msg, Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
			zk.create(path, msg, Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);
			//System.out.println (myPath);
		} catch (KeeperException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();\
			success = false;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return success;
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
			//zk.delete("/TestAndSet/"+name, -1);
			zk.delete("/TestAndSet/"+name, -1, null, null);
		/*} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeeperException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
		
	}
	
	
	
	
	
	
	
}
