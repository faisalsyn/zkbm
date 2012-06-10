package edu.ucsb.cs.zkbm;

public interface Lock {

	public boolean acquire ();
	
	public void release ();
	
	public void close();
}
