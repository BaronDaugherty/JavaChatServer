/*This class extends the thread class, overrides a constructor, and adds two new methods for the purpose of handling
 * client disconnects gracefully.*/

public class ClientThread extends Thread {
	/*Instance Variables*/
	private Runnable r;	//this represents the runnable object passed to the Thread at instantiation.
	
	/*Constructors*/
	public ClientThread(Runnable target){	//this is an overridden constructor
		super(target);	//we use the same constructor as the superclass Thread
		setTarget(target);	//then we call setTarget and pass it the runnable object (ClientHandler in our case)
	}//end Runnable constructor()
	
	/*Methods*/
	public void setTarget(Runnable target){ //new method
		r = target;	//set the Runnable instance variable to target
	}//end setTarget
	public Runnable getTarget(){ //new method
		return r;	//return the runnable object assigned to this thread (the ClientHandler passed in)
	}//end getTarget method
}//end ClientThread class
