// This application create a file, delete it, and create it again ...

import java.io.IOException;
import java.util.List;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.data.Stat;








public class baseline extends Thread implements Watcher {

	static ZooKeeper zk;
	static int counter = 1000;
	String name;
	static long starttime;
	
	/**
	 * This function is notified when an event we watched
	 * happens.
	 */
	@Override
	public void process(WatchedEvent event) {
		//System.out.println ("I watched! "+counter);

	}
	
	baseline (String name) {
		super (name);
		this.name = name;
		try {
			zk = new ZooKeeper("127.0.0.1", 3000, this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
	}
	
	public void run () {
		System.out.println (name);
		
	}
	
	
	public static void main (String argv[]) {
		
		
		baseline mt = new baseline ("/faisal2/");
		
		starttime = System.currentTimeMillis();
		
		
		try {
			
			for ( int i=0 ; i < 1000 ; i++ ) {
				String zkpath = zk.create("/faisal2/baseline", new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT_SEQUENTIAL);
				zk.delete(zkpath, -1, null, null);
				
				//new baseline (zkpath).start();
			}			
			

		} catch (KeeperException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Elapsed: "+ (System.currentTimeMillis() - starttime) );
		
	}
	
	
}

