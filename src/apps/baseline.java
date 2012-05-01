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








public class baseline implements Watcher {

	static ZooKeeper zk;
	static int counter = 1000;
	
	static long starttime;
	
	/**
	 * This function is notified when an event we watched
	 * happens.
	 */
	@Override
	public void process(WatchedEvent event) {
		//System.out.println ("I watched! "+counter);
		counter--;
		if (counter == 0){
			try {
				zk.close();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println ( System.currentTimeMillis() - starttime );
			System.exit(0);
		}
		
		
		Stat s;
		try {
			s = zk.exists("/faisal2", true);
			if ( s == null ) {
				zk.create("/faisal2", new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			}
			zk.delete("/faisal2", -1);
			zk.
		} catch (KeeperException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	baseline () {
		try {
			zk = new ZooKeeper("127.0.0.1", 3000, this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
	}
	
	public static void main (String argv[]) {
		
		
		baseline mt = new baseline ();
		
		starttime = System.currentTimeMillis();
		try {
//			try {
//				zk.delete("/faisal2", -1)
//			} catch (Exception e) {
//				// ignore
//			}
			
			zk.create("/faisal2", new byte[0], Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);

		} catch (KeeperException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        while (true){
        	
        }
		
	}
	
	
}

