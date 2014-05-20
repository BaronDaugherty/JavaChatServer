import java.io.*;
import java.net.*;
import java.util.*;

public class RobustChatServer {
	/*Instance Variables*/
	private ArrayList<Client> clients;	//created a client class and changed the array list to hold those instead
	
	/*Inner Classes*/
	private class ClientHandler implements Runnable{	//changed ClientHandler to private (only this needs access to it)
		/*Instance Variables*/
		private BufferedReader reader;		//made private
		private Socket sock;				//made private
		private InputStreamReader isReader;	//made private
		private volatile boolean stop = false;	//this will allow us to stop the thread once a client disconnects
		
		/*Constructors*/
		public ClientHandler(Socket clientSocket){
			try{
				sock = clientSocket;
				isReader = new InputStreamReader(sock.getInputStream());
				reader = new BufferedReader(isReader);
			}//end try
			catch(Exception ex){
				Logger.write(ex.getMessage());
			}
		}//end socket constructor
		
		/*Methods*/
		public void run(){
			while(!stop){	//while not stop
				String message = "";
				try{
					while((message = reader.readLine()) != null){
						System.out.println("Read: " +message);
						tellEveryone(getSock().getInetAddress(), message);
					}//end inner while
				}//end try
				catch(SocketException se){	//catch a SocketException ("connection reset" when client closes)
					System.out.println("Client disconnected from " +getSock().getInetAddress());	//let us know a client disconnected
					stopMe();	//call stop me to terminate the run loop
					removeClient(this); //remove the client thread from the list of threads
					tellEveryone(sock.getInetAddress().toString() +" disconnected."); //address everyone that a user disconnected
				}
				catch(Exception ex){
					Logger.write(ex.getMessage());
				}
			}//end outer while
		}//end run method
		public void stopMe(){	//this method will terminate the loop in run(the thread will complete it's run method and end)
			stop = true;		//set stop to true
		}//end stopMe method
		public Socket getSock(){
			return sock;	//return the socket associated with this client
		}//end getSock method
	}//end ClientHandler class
	
	/*Main Method*/
	public static void main(String[] args){
		new RobustChatServer().go();
	}//end main method
	
	/*Methods*/
	public void go(){
		System.out.println("Creating clients ArrayList...");
		clients = new ArrayList<Client>();
		try{
			System.out.println("Creating ServerSocket...");
			ServerSocket serverSock = new ServerSocket(10000);
			
			while(true){
				System.out.println("In main loop...");
				Socket clientSocket = serverSock.accept();
				System.out.println("Client socket created...");
				PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
				System.out.println("Print writer created...");
				System.out.println("Got a connection from " +clientSocket.getInetAddress());
				ClientThread t = new ClientThread(new ClientHandler(clientSocket));
				Client c = new Client(t.getTarget(), writer);
				clients.add(c);	//add the Client to the ArrayList
				t.start();
				System.out.println("Telling everyone...");
				tellEveryone(clientSocket.getInetAddress() +" connected."); //address everyone when a new user connects
				
				
			}//end while
		}//end try
		catch(Exception ex){
			//ex.printStackTrace();
			Logger.write(ex.getMessage());
		}
	}//end go method
	
	public void tellEveryone(InetAddress a,String message){
		Iterator<Client> it = clients.iterator();	//changed Iterator to Iterator<PrintWriter> to ensure type safety
		
		while(it.hasNext()){
			if(message.isEmpty() || message.matches("^\\s*$")){ //this checks if the message is empty ("") or contains only whitespace (regex: ^\s*$) and breaks (doesn't send) if it is
				break;
			}
			try{
				PrintWriter writer = it.next().getStream();
				writer.println(a.toString() +": " +message);
				writer.flush();
			}//end try
			catch(Exception ex){
				Logger.write(ex.getMessage());
			}
		}//end while
	}//end tellEveryone method
	public void tellEveryone(String message){
		Iterator<Client> it = clients.iterator();
		
		while(it.hasNext()){
			try{
				PrintWriter writer = it.next().getStream();
				writer.println(message);
				writer.flush();
			}//end try
			catch(Exception ex){
				Logger.write(ex.getMessage());
			}
		}//end while
	}//end tellEveryone method
	public void removeClient(Runnable r){
		Iterator<Client> it = clients.iterator();
		
		while(it.hasNext()){
			if(it.next().getHandler().equals(r)) //if the clients runnable is the passed runnable (they share a handler)...
				it.remove(); //remove the client
				break;	//and call it good (no two clients will share a handler)
		}//end while
	}//end removeClient method
}//end VerySimpleChatServer class
