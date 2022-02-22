import java.io.FileNotFoundException;
import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;


public class NamingServer extends UnicastRemoteObject implements Registration, Service {

    private Map<String, List<Storage>> Replicaloc;
    private Map<Storage, List<String>> Replica;
    private Set<Storage> StorageServers;

    public NamingServer() throws RemoteException {
        Replicaloc = new HashMap<>();
        Replica = new HashMap<>();
        StorageServers = new HashSet<>();
    }

    public synchronized void start(String PORT) throws RemoteException {
        Registry r = LocateRegistry.createRegistry(Integer.parseInt(PORT));
        r.rebind("NamingServer", new NamingServer()); // bind remote obj with name
    }

    public String[] register(String IP_STORAGE_SERVER, int PORT_STORAGE_SERVER, String[] files, Storage command_stub) throws RemoteException, NotBoundException {


        StorageServers.add(command_stub); //check if server is active

        System.out.println("Storage server : " + IP_STORAGE_SERVER + " " + PORT_STORAGE_SERVER + " connected");
        for (String file : files) {
            if (Replicaloc.get(file) == null) {
                List<Storage> temp = new ArrayList<Storage>();
                temp.add(command_stub);
                Replicaloc.put(file, temp);
            } else
                Replicaloc.get(file).add(command_stub);
        }
        if (Replica.get(command_stub) == null) {
            List<String> temp = new ArrayList<String>();
            temp.add(IP_STORAGE_SERVER);
            temp.add(PORT_STORAGE_SERVER + "");
            Replica.put(command_stub, temp);
        }
        return new String[2];
    }

    public boolean createFile(String file) throws RemoteException, FileNotFoundException {

        return true;
    }

    public List<String> getStorage(String file) throws RemoteException, FileNotFoundException, IOException {

        Storage lucky_replicaserver = Replicaloc.get(file).get(new Random().nextInt(Replicaloc.get(file).size()));
        System.out.println("Lucky Replicaserver " + Replica.get(lucky_replicaserver));

        lucky_replicaserver.read(file); // start read thread at server
        return Replica.get(lucky_replicaserver);
    }

    public boolean put(String IP, String PORT, String path) throws Exception {
        System.out.println("Senfing file to : ");

        if (Replicaloc.get(path) == null) {
            System.out.println("File " + path + "not exist" + " storing file ");

            for (Storage stub : StorageServers) {

                stub.write(IP, PORT, path);

            }
            return true;
        } else
            return false;

    }

    public List<String> list() {
        return new ArrayList(Replicaloc.keySet());
    }

    public static void main(String args[]) throws IOException, NotBoundException {
        String IP = "localhost";
        System.setProperty("java.rmi.server.hostname",IP);//ip
        String PORT = "1234";
        new NamingServer().start(PORT);
        System.out.println("\nListening Incoming  Connections on :: " + PORT);
    }

}
