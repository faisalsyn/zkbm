package edu.ucsb.cs.zkbm;

public class testAsyncTestAndSet {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		for (int i = 0 ; i < 10 ; i++ ) {

			ZkAsyncTestAndSet zktas = new ZkAsyncTestAndSet ("aa", "rubicon.cs.ucsb.edu");

			long startt = System.currentTimeMillis();
			
			boolean state;
			
			state = zktas.acquire();
			
			System.out.println( state) ;

			zktas.release();
			System.out.println ((System.currentTimeMillis() - startt));
			
			zktas.close();
		}

//		System.in.read(arg0);
	}

}
