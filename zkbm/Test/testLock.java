package Test;

import Primitives.ZkLock;

public class testLock {

	String name;
	testLock () {
		this.name = "A";
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ZkLock zkl = new ZkLock ("name", "127.0.0.1");

		System.out.println ("acquiring lock");
		
		zkl.acquire();
		
		System.out.println ("acquired");
		
		zkl.release();
		
		System.out.println ("released");
	}

}
