package edu.ucsb.cs.zkbm;

public class testTestAndSet {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		for (int i = 0; i < 10; i++) {
			ZkTestAndSet zktas = new ZkTestAndSet("aa", "rubicon.cs.ucsb.edu");

			long startt = System.currentTimeMillis();
			while (!zktas.acquire())
				;
			zktas.release();
			System.out.println((System.currentTimeMillis() - startt));

			zktas.close();
		}
	}

}
