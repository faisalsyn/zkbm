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

		for (int i = 0 ; i < 10 ; i++ ) {

			ZkQueue zkl = new ZkQueue ("aa", "rubicon.cs.ucsb.edu");

			long startt = System.currentTimeMillis();
			
			zkl.acquire();
			//zkl2.acquire();
			
			
			//System.out.println ("acquired");
			
			//zkl2.release();
			zkl.release();
			
			System.out.println ((System.currentTimeMillis() - startt));
			
			zkl.close();
		}
	}

}
