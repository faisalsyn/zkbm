package Primitives;

public interface Lock {

	public boolean acquire ();
	
	public void release ();
	
	public void close();
}
