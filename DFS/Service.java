import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface Service  extends Remote
{
         boolean createFile(String file ) throws RemoteException, FileNotFoundException;
		 List<String> getStorage(String file ) throws RemoteException, FileNotFoundException ,IOException ;
		 boolean put(String IP , String PORT , String path ) throws Exception;
		 List<String> list() throws Exception;
}

