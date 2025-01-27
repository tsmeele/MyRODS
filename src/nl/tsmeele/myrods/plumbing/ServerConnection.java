package nl.tsmeele.myrods.plumbing;


import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

/**
 * Class IrodsSession manages a single peer-peer connection between data grid participants.
 * @author Ton Smeele
 *
 */
public class ServerConnection {
	private Socket socket = null;
	private SSLSocket sslSocket = null;
	private boolean sslActive = false;
	private IrodsInputStream irodsIn = null;
	private IrodsOutputStream irodsOut = null;
	private IrodsProtocolType protocol = null;
	private SessionDetails sessionDetails = new SessionDetails();
	
	
	
	public boolean isConnected() {
		return socket != null;
	}
	
	public boolean isSsl() {
		return sslActive;
	}
	
	public IrodsInputStream getInputStream()  {
		return irodsIn;
	}
	
	public IrodsOutputStream getOutputStream() {
		return irodsOut;
	}
	
	public IrodsProtocolType getProtocol() {
		return protocol;
	}
	
	public void updateProtocol(IrodsProtocolType protocol) {
		this.protocol = protocol;
		irodsIn.setProtocol(protocol);
		irodsOut.setProtocol(protocol);
	}
	
	public SessionDetails getSessionDetails() {
		return sessionDetails;
	}
	
	
	
	
	
	public void connect(String hostname, int port) throws MyRodsException {
		try {
			InetAddress ip = InetAddress.getByName(hostname);
			connect(ip, port);
		} catch (UnknownHostException e) {
			throw new MyRodsException("Hostname lookup failed: " + e.getMessage());
		} 	
		sessionDetails.host = hostname;
		sessionDetails.port = port;
	}

	private void connect(InetAddress ip, int port) throws MyRodsException {
		// close any old connection
		disconnect();
		
		// open new connection
		try {
			socket = new Socket(ip, port);
			protocol = IrodsProtocolType.NATIVE_PROT; // default protocol for new connections
			irodsIn = new IrodsInputStream(socket.getInputStream(), protocol);
			irodsOut = new IrodsOutputStream(socket.getOutputStream(), protocol);
		}
		catch (IOException e) {
			tryCloseSocket(socket);
			socket = null;
			throw new MyRodsException("Unable to connect to host: " + e.getMessage());
		}
	}	
	
	public void disconnect()  {
		if (socket == null) {
			return;
		}
		tryFlushOutputStream();
		
		// if we are using SSL, we need to first downgrade the connection
		stopSSL();
		
		// now close the connection
		tryCloseSocket(socket);
		socket = null;
		irodsOut = null;
		sessionDetails = new SessionDetails();
	}
	
	
	/**
	 * Upgrade current connection to SSL.
	 * @throws IOException 
	 */
	public void startSSL() throws IOException  {
		if (sslActive) {
			// already on SSL, no further action needed
			return;
		}
		// ensure no data in flight on old socket
		tryFlushOutputStream();
		
		// start SSL socket on top of regular socket

		sslSocket = (SSLSocket) ((SSLSocketFactory) SSLSocketFactory.getDefault()).createSocket(
				        socket, 
				        socket.getInetAddress().getHostAddress(), 
				        socket.getPort(), 
				        false	// no autoclose of underlying socket
				        );
		// update streams to use the SSL socket going forward
		irodsIn = new IrodsInputStream(sslSocket.getInputStream(), protocol);
		irodsOut = new IrodsOutputStream(sslSocket.getOutputStream(), protocol);   
		sslActive = true;
	}
	
	
	/**
	 * Downgrade current connection (if we are using SSL)  
	 */
	public void stopSSL() {
		if (!sslActive) {
			return;
		}

		// ensure no more data in flight on SSL socket
		tryFlushOutputStream();

		
		// close down SSL socket
		tryCloseSocket(sslSocket);
		sslSocket = null;
		sslActive = false;

		// reinstate streams that correspond to underlying socket
		try {
			irodsIn = new IrodsInputStream(socket.getInputStream(), protocol);
			irodsOut = new IrodsOutputStream(socket.getOutputStream(), protocol);
		} 
		catch (IOException e) {
			// maybe meanwhile original socket has been reset by other side?  
			tryCloseSocket(socket);
			socket = null;
		}
	}
	
	/**
	 * Make an attempt to flush outputstream, ignore failure
	 */
	private void tryFlushOutputStream() {
		if (irodsOut == null) {
			return;
		}
		try {
			irodsOut.flush();
		} 
		catch (IOException e) {
		}
	}
	
	/**
	 * Make an attempt to close a socket, ignore failure
	 * @param socket
	 */
	private void tryCloseSocket(Socket socket) {
		try {
			if (socket != null) {
				socket.close();
			}
		}
		catch (IOException e) {
		}
	}
	

}
