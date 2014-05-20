import java.io.*;

public class Logger {
	private static File file = new File("ChatServerLog.txt");
	private static FileWriter fw;
	
	public static void write(String log){
		try{
			fw = new FileWriter(file);
			fw.write(log);	
			fw.close();
		}
		catch(IOException ex){
			ex.printStackTrace();
		}
	}//end write method
}//end Logger class
