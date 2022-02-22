import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class StorageServer extends UnicastRemoteObject implements Storage, Command, Serializable  
{
	private static String IP_NAMING_SERVER ;
	private static int PORT_NAMING_SERVER ;
	private static int PORT_TCP ;
	private static String IP_RMI;
	private static int PORT_RMI ;  
	private String [] files ; 
	public Storage storage; 

	private static ServerSocket ssock ;
	private static Socket socket;
	public StorageServer (File  root ) throws RemoteException {
	}
	public StorageServer () throws Exception {
		ssock = new ServerSocket(PORT_TCP);
		createServer(IP_RMI, PORT_RMI);
		getFiles();
		registerNaming(IP_NAMING_SERVER, PORT_NAMING_SERVER);
	}
	private void createServer(String IP , int PORT)throws Exception{
		System.setProperty("java.rmi.server.hostname",IP);

		Registry r = LocateRegistry.createRegistry(PORT);  
		r.rebind("Service" , new StorageServer());
		Registry storageserver = LocateRegistry.getRegistry("localhost", PORT);

		storage = (Storage)storageserver.lookup("Service"); 
	}
	private void getFiles()throws Exception {
		File curDir = new File(".");
		File [] filesList = curDir.listFiles();
		ArrayList <String> list = new ArrayList<String>();
		for (File f : filesList){
			if (f.isFile())
				list.add(f.getName());
		}
		files  = list.toArray(new String[list.size()]);
	} 
	private void registerNaming(String IP , int PORT)throws Exception{

		Registry namingserver = LocateRegistry.getRegistry(IP, PORT);
		Registration registration_stub = (Registration)namingserver.lookup("NamingServer");	

		registration_stub.register(IP_RMI ,PORT_TCP ,files ,storage);
	}

	public boolean create(String file )throws IOException {
		File f = new File(file) ;
		if (f.exists() && !f.isDirectory()){
			return false;	
		}
		else {
			f.createNewFile();
			return true;
		}
	}
	public byte[] read( ) throws RemoteException {
		byte[] b = "Read String from Storage server ".getBytes();
		System.out.println(new String (b));	
		return b;	
	}
	public void read(String path ) throws IOException , RemoteException {
		//ServerSocket ssock = new ServerSocket(PORT_TCP); 
		new Thread(() -> {
			String anim = "|/-\\";
			try{
				//Socket socket = ssock.accept();
				socket = ssock.accept();

				File file = new File(path);
				FileInputStream fis = new FileInputStream(file);
				BufferedInputStream bis = new BufferedInputStream(fis);
				OutputStream os = socket.getOutputStream();
				byte[] contents;
				long fileLength = file.length();
				long current = 0;
				long start = System.nanoTime();
				while(current!=fileLength){
					int size = 10000;
					if(fileLength - current >= size)
						current += size;
					else{
						size = (int)(fileLength - current);
						current = fileLength;
					}
					contents = new byte[size];
					bis.read(contents, 0, size);
					os.write(contents);
					int x = (int)((current * 100)/fileLength) ;

					String data = "\r" + anim.charAt(x % anim.length()) + " " + x + "%" ;
					System.out.write(data.getBytes());

				}
				os.flush();
				//socket.close();
			}catch(Exception e ){e.printStackTrace();}

			System.out.println("File sent succesfully!");
		}).start();
	}	
	public void write(String IP , String PORT , String  path ) throws UnknownHostException, IOException{

		System.out.println("Write "+ path +"in Storage Server " + IP_RMI + " "+PORT_RMI );

		String addr  = new String (IP);  // ip o
		int port  = Integer.parseInt(PORT);// Tcp port listening on sender (put)

		Socket socket = new Socket(InetAddress.getByName(addr), port);// crate socket 

		byte[] contents = new byte[10000];
		FileOutputStream fos = new FileOutputStream(path);
		BufferedOutputStream bos = new BufferedOutputStream(fos);
		InputStream is = socket.getInputStream();
		int bytesRead = 0;
		while((bytesRead=is.read(contents))!=-1)
			bos.write(contents, 0, bytesRead);

		bos.flush();
		socket.close();
		System.out.println("File saved successfully!");

	}
	public static void main (String args[]) throws RemoteException {
		try{
			IP_RMI = "localhost";
			PORT_TCP = 1234;
			PORT_RMI = 1235;
			IP_NAMING_SERVER = "localhost";
			PORT_NAMING_SERVER = 1236;
			StorageServer server =  new StorageServer();
		}catch(Exception e ){e.printStackTrace();}

		System.out.println(IP_RMI);
	}

}

