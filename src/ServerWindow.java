import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.*;
import java.util.Collections;
import java.util.Enumeration;

public class ServerWindow extends ServerListener implements ActionListener{
	
	public RemoteDataServer server;
	
	private Thread sThread; //server thread
	
	private static final int WINDOW_HEIGHT = 285;
	private static final int WINDOW_WIDTH = 360;
	
	private String ipAddress;
	
	private JFrame window = new JFrame(" Truy cap vao PC");
	
	private JLabel addressLabel = new JLabel("");
	private JLabel portLabel = new JLabel("PORT SERVER: ");
	private JLabel clientPortLabel = new JLabel("PORT CLIENT: ");
	
	private JTextArea[] buffers = new JTextArea[4];
	private JTextField portTxt = new JTextField(5);
	
	private JTextField clientPortTxt = new JTextField(5);
	private JTextField ipTxt = new JTextField(10);
	private JLabel serverMessages = new JLabel("Chưa kết nối");
	
	private JButton connectButton = new JButton("Kết-nối");
	private JButton disconnectButton = new JButton("Ngắt");
	private JButton shutdownButton = new JButton("Tắt máy");
	
	
	public ServerWindow(){
		
		server = new RemoteDataServer();
		server.setServerListener( this );
		
		window.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		connectButton.addActionListener(this);
		disconnectButton.addActionListener(this);
		//shutdownButton.addActionListener(this);
		
		Container c = window.getContentPane();
		c.setLayout(new FlowLayout());
		
		try{
			InetAddress ip = getIpAddress();
			ipAddress = ip.getHostAddress();
			addressLabel.setText("IP máy Host: ");
			ipTxt.setText(ipAddress);
		}
		catch(Exception e){addressLabel.setText("IP không hợp lệ, bạn hãy nhập lại ip.");}
		
		int x;
		for(x = 0; x < 4; x++){
			buffers[x] = new JTextArea("", 1, 30);
			buffers[x].setEditable(false);
			buffers[x].setBackground(window.getBackground());
		}
		
		c.add(addressLabel);
		c.add(ipTxt);
		c.add(buffers[0]);
		c.add(portLabel);
		portTxt.setText("5444");
		c.add(portTxt);

		c.add(buffers[3]);
		
		c.add(clientPortLabel);
		clientPortTxt.setText("5555");
		c.add(clientPortTxt);
		
		c.add(buffers[1]);
		c.add(connectButton);
		c.add(disconnectButton);
		c.add(buffers[2]);
		c.add(serverMessages);
		
		//c.add(shutdownButton);
		ipTxt.setSize(100, 20);
		window.setLocationRelativeTo(null);
		window.setVisible(true);
		window.setResizable(false);
		
		int port = Integer.parseInt(portTxt.getText());
		int clientPort = Integer.parseInt(clientPortTxt.getText());

		try{
			InetAddress ip = InetAddress.getByName(ipTxt.getText());
			runServer(port, clientPort, ip);
		}catch(UnknownHostException err){
			serverMessages.setText("Error: Kiểm tra lại địa chỉ ip vừa nhập.");
		}
	}
	
	
	private InetAddress getIpAddress() throws Exception
	{
		// this first line generally works on mac and windows
		InetAddress ip = InetAddress.getLocalHost();
		
		// but on linux...
		if(ip.isLoopbackAddress())
		{
			//loop trough all network interfaces
			Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
            for (NetworkInterface netint : Collections.list(nets))
            {
            	//loop through the ip address associated with the interface
            	Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
                for (InetAddress inetAddress : Collections.list(inetAddresses)) {
                	
                	// if the address is no the loopback and is not ipv6
                	if(!inetAddress.isLoopbackAddress() && !inetAddress.toString().contains(":"))
                		return inetAddress;
                }
            }
		}
		
		return ip;
	}
	
	public void actionPerformed(ActionEvent e){
		Object src = e.getSource();
		
		if(src instanceof JButton){
			if((JButton)src == connectButton){
				int port = Integer.parseInt(portTxt.getText());
				int clientPort = Integer.parseInt(clientPortTxt.getText());
				try{
					InetAddress ip = InetAddress.getByName(ipTxt.getText());
					runServer(port, clientPort, ip);
				}catch(UnknownHostException err){
					serverMessages.setText("Error: Kiểm tra lại địa chỉ ip vừa nhập.");
				}
			}
				
			else if((JButton)src == disconnectButton){
				closeServer();
				setConnectButtonEnabled(true);
			}
			
			else if((JButton)src == shutdownButton){
				closeServer();
				shutdown();
				System.exit(0);
			}
		}
	}
	
	public void runServer(int port, int listenerPort, InetAddress ip){
		if(port < 65535){
			server.setPort(port);
			server.setClientPort(listenerPort);
			server.setIP(ip);
			sThread = new Thread(server);
			sThread.start();
			serverMessages.setText("Đang kết nối tới  " + ip);
			connectButton.setEnabled(false);
		}else{
			serverMessages.setText("Cổng phải là một số nhỏ hơn 65535");
			connectButton.setEnabled(true);
		}
	}
	
	public void closeServer(){
		server.shutdown();
		setMessage("Ngắt kết nối");
	}
	
	public void setMessage(String msg){
		serverMessages.setText(msg);
	}
	
	public void setConnectButtonEnabled(boolean enable){
		connectButton.setEnabled(enable);
	}
	
	private static void shutdown(){
	    String shutdownCommand;
	    String operatingSystem = System.getProperty("os.name");

	    // Linux va osx cung nhan unix, co the shutdown bang 1 lenh chung.
	    if ("Linux".equals(operatingSystem) || "Mac OS X".equals(operatingSystem)) {
	        shutdownCommand = "shutdown -h now";
	    }
	    else if ("Windows".equals(operatingSystem)) {
	        shutdownCommand = "shutdown.exe -s -t 0";
	    }
	    else {
	    	shutdownCommand = "null";
	    }

	    Runtime runtime = Runtime.getRuntime();
        try {
			Process proc = runtime.exec(shutdownCommand);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	public static void main(String[] args){
		new ServerWindow();
	}
}
