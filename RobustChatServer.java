import java.io.*;
import java.net.*;
import java.util.*;

public class RobustChatServer {
	/*Instance Variables*/
	private ArrayList<Client> clients;
	
	/*Inner Classes*/
	private class ClientHandler implements Runnable{
		/*Instance Variables*/
		private BufferedReader reader;
		private Socket sock;
		private InputStreamReader isReader;
		private volatile boolean stop = false;
		
		/*Constructors*/
		public ClientHandler(Socket clientSocket){
			try{ //create a new socket for connections and reader for messages
				sock = clientSocket;
				isReader = new InputStreamReader(sock.getInputStream());
				reader = new BufferedReader(isReader);
			}//end try
			catch(Exception ex){ //log any error
				Logger.write(ex.getMessage());
			}
		}//end socket constructor
		
		/*Methods*/
		public void run(){ //override
			while(!stop){	//while running
				String message = "";
				try{	//try to read in a message and broadcast it
					while((message = reader.readLine()) != null){
						tellEveryone(getSock().getInetAddress(), message);
					}//end inner while
				}//end try
				catch(SocketException se){ //catch client disconnect
					//stop the thread, clean up clients, and alert
					stopMe();
					removeClient(this);
					tellEveryone(sock.getInetAddress().toString() +" disconnected.");
				}
				catch(Exception ex){ //log any other exception
					Logger.write(ex.getMessage());
				}
			}//end outer while
		}//end run method
		public void stopMe(){
			//terminate thread
			stop = true;
		}//end stopMe method
		public Socket getSock(){
			//return the socket associated with this client
			return sock;
		}//end getSock method
	}//end ClientHandler class
	
	/*Main Method*/
	public static void main(String[] args){
		//run the server
		new RobustChatServer().go();
	}//end main method
	
	/*Methods*/
	public void go(){
		clients = new ArrayList<Client>();
		try{ //initialize networking
			//create socket
			ServerSocket serverSock = new ServerSocket(10000);
			
			while(true){ //the meat...
				/*accept incoming connections, initialize writer for messages, 
				start a new client thread, and alert of incoming connection*/
				Socket clientSocket = serverSock.accept();
				PrintWriter writer = new PrintWriter(clientSocket.getOutputStream());
				ClientThread t = new ClientThread(new ClientHandler(clientSocket));
				Client c = new Client(t.getTarget(), writer);
				clients.add(c);	//add the Client to the ArrayList
				t.start();
				tellEveryone(clientSocket.getInetAddress() +" connected."); //address everyone when a new user connects
			}//end while
		}//end try
		catch(Exception ex){ //log any exception
			Logger.write(ex.getMessage());
		}
	}//end go method
	
	//broadcast messages from clients
	public void tellEveryone(InetAddress a,String message){
		//setup a client list to iterate over
		Iterator<Client> it = clients.iterator();
		
		while(it.hasNext()){ //while there are clients...
			//ignore empty messages
			if(message.isEmpty() || message.matches("^\\s*$")){ 
				break;
			}
			try{ //try to write the message to the client
				PrintWriter writer = it.next().getStream();
				writer.println(a.toString() +": " +message);
				writer.flush();
			}//end try
			catch(Exception ex){ //log any exception
				Logger.write(ex.getMessage());
			}
		}//end while
	}//end tellEveryone method
	
	//broadcast server messages
	public void tellEveryone(String message){
		//setup a client list to iterate over
		Iterator<Client> it = clients.iterator();
		
		while(it.hasNext()){ //while there are clients to tell
			try{ //try to write the message to the client
				PrintWriter writer = it.next().getStream();
				writer.println(message);
				writer.flush();
			}//end try
			catch(Exception ex){ //log any exception
				Logger.write(ex.getMessage());
			}
		}//end while
	}//end tellEveryone method
	
	//clean up disconnected clients
	public void removeClient(Runnable r){
		//setup a client list to iterate over
		Iterator<Client> it = clients.iterator();
		
		while(it.hasNext()){ //while there are clients
			//if the passed runnable belongs to the client, remove them from the client list and stop
			if(it.next().getHandler().equals(r)) 
				it.remove();
				break;
		}//end while
	}//end removeClient method
}//end RobustChatServer class
