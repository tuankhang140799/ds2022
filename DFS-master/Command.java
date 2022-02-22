import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface Command extends Remote  
{
	public boolean create (String file) throws RemoteException, IOException;
}

