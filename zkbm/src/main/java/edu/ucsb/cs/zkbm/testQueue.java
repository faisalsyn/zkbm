package edu.ucsb.cs.zkbm;

public class testQueue {

	String name;

	public testQueue () {
		this.name = "A";
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

		ZkQueue zkl = new ZkQueue ("name", "127.0.0.1");
		//ZkLock zkl2 = new ZkLock ("name2", "127.0.0.1");

		for (int i = 0 ; i < 10 ; i++ ) {

			long startt = System.currentTimeMillis();
			
			zkl.acquire();
			//zkl2.acquire();
			
			
			//System.out.println ("acquired");
			
			//zkl2.release();
			zkl.release();
			
			System.out.println ((System.currentTimeMillis() - startt));
		}
	}

}
