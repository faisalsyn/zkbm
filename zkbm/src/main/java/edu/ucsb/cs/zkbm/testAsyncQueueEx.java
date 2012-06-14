package edu.ucsb.cs.zkbm;

public class testAsyncQueueEx {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		for (int i = 0; i < 10; i++) {
			ZkAsyncQueueEx zktas = new ZkAsyncQueueEx("aa", "rubicon.cs.ucsb.edu");

			long startt = System.currentTimeMillis();
			while (!zktas.acquire())
				;
			zktas.release();
			System.out.println((System.currentTimeMillis() - startt));

			zktas.close();
		}
	}

}
