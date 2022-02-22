import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.List;

public class list  {
	private Service service_stub;
	private String IP_NAMING_SERVER;
	private int PORT_NAMING_SERVER;

	public list(String IP , String PORT ) throws RemoteException , NotBoundException {
		this.IP_NAMING_SERVER = IP ;
		this.PORT_NAMING_SERVER = Integer.parseInt(PORT);

		Registry namingserver = LocateRegistry.getRegistry(IP_NAMING_SERVER, PORT_NAMING_SERVER);
		service_stub = (Service)namingserver.lookup("NamingServer");
	}
	public void run () throws Exception
	{
		List <String> files = service_stub.list(); // get storge server hosting "path" file 
		for (String file : files)
			System.out.println(file);
	}
	public static void main (String args[] )throws  Exception {
		String IP ="";
		String PORT = "";
		list object = new list(IP, PORT);
		object.run();
		System.exit(1);
	}

}


