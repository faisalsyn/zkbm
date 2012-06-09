package Test;

import Primitives.ZkTestAndSet;

public class testTestAndSet {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		ZkTestAndSet zktas = new ZkTestAndSet ("aa", "127.0.0.1");

		for (int i = 0 ; i < 10 ; i++ ) {

			long startt = System.currentTimeMillis();
			
			boolean state;
			
			state = zktas.acquire();
			
			System.out.println( state) ;

			zktas.release();
			System.out.println ((System.currentTimeMillis() - startt));
		}

//		System.in.read(arg0);
	}

}
