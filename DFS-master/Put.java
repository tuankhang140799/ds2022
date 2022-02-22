import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

public class Put  {
	private static ServerSocket ssock ;

	private Service service_stub;
	private String IP_NAMING_SERVER;
	private int PORT_NAMING_SERVER;

	public Put(String IP , String PORT, int Server_Port ) throws Exception {
		this.IP_NAMING_SERVER = IP ;
		this.PORT_NAMING_SERVER = Integer.parseInt(PORT);

		ssock = new ServerSocket(Server_Port);

		Registry namingserver = LocateRegistry.getRegistry(IP_NAMING_SERVER, PORT_NAMING_SERVER);

		service_stub = (Service)namingserver.lookup("NamingServer");
	}
	private void transfer(String fileName, String IP, String PORT ) throws Exception {

		new Thread (() -> {
			System.out.println(" listening ...");
		try{
		Socket socket = ssock.accept();

		String anim = "|/-\\";
			File file = new File(fileName);
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

				String data = "\r" + anim.charAt(x % anim.length()) + " " + x + "%" + "Sent" ;
				System.out.write(data.getBytes());

			}
			os.flush();
			System.out.println("File sent succesfully!");
			}catch(Exception e){ e.printStackTrace();}
		}).start();
		service_stub.put(IP, PORT, fileName);

	}
	public static void main(String[] args) throws Exception {
		String IP = "localhost";
		String PORT = "1234";
		int Server_Port = 1235;
		Put object = new Put(IP, PORT, Server_Port);
		String fileName ="";
		object.transfer(fileName, IP, PORT);
	}
}

