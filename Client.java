import java.io.PrintWriter;

/*This class will hold references to our client thread and output stream for each client.*/
public class Client {
	/*Instance Variables*/
	private Runnable handler;
	private PrintWriter outputStream;
	
	/*Constructors*/
	public Client(Runnable r, PrintWriter pw){
		setHandler(r);
		setOutputStream(pw);
	}//end constructor
	
	/*Methods*/
	public void setHandler(Runnable handler){
		this.handler = handler;
	}//end setHandler
	public void setOutputStream(PrintWriter stream){
		this.outputStream = stream;
	}//end setOutputStream
	public Runnable getHandler(){
		return handler;
	}//end getHandler method
	public PrintWriter getStream(){
		return outputStream;
	}//end getStream method
}//end Client class
