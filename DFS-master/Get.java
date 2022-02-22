
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

public class Get extends NamingServer {
	private Service service_stub;
	private String IP_NAMING_SERVER;
	private int PORT_NAMING_SERVER;

	public Get(String IP , String PORT ) throws RemoteException , NotBoundException {
		this.IP_NAMING_SERVER = IP ;
		this.PORT_NAMING_SERVER = Integer.parseInt(PORT);

		Registry namingserver = LocateRegistry.getRegistry(IP_NAMING_SERVER, PORT_NAMING_SERVER);
		service_stub = (Service)namingserver.lookup("NamingServer");
	}
	public void run (String path) throws IOException
	{
		List <String> fileServer = service_stub.getStorage(path); // get storge server hosting "path" file 
		
		System.out.println(fileServer);
		String addr  = fileServer.get(0);
		int port  = Integer.parseInt(fileServer.get(1));

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
	public static void main (String args[] ) throws IOException, NotBoundException {
		String IP ="";
		String PORT = "";
		String fileName = "";
		Get object = new Get(IP, PORT);
		object.run(fileName);
	}

}

