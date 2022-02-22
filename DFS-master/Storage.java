import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.UnknownHostException;

public interface Storage  extends Remote 
{
	byte[] read() throws RemoteException;
	void read(String path) throws IOException , RemoteException;
	boolean create(String file) throws RemoteException, IOException;
	void write(String IP, String PORT, String path) throws UnknownHostException, IOException;
	
}

