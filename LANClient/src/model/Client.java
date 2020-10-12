/*
 * Created by VyVu
 * 
 *  @author vutrivi99@gmail.com
 */
package model;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

import controller.Utils;

// TODO: Auto-generated Javadoc
/**
 * Class Client. It represents the client
 */
public class Client implements Runnable{
	
	/** The client socket channel. */
	private SocketChannel clientSocketChannel;
	
	/** The selector. The selector will select the existing channel's key*/
	private Selector selector;
	
	/** The server port to connect. */
	private int serverPort;
	
	/** The server address to connect. */
	private String serverAddress;
	
	/**
	 * Instantiates a new client. Create a client channel and set it to non-blocking
	 * Initialize channel selector
	 *
	 * @param serverAddress -the server add
	 * @param serverPort -the server port
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public Client(String serverAddress, int serverPort) throws IOException {
		super();
		this.serverAddress=serverAddress;
		this.serverPort = serverPort;
		this.clientSocketChannel=SocketChannel.open();
		this.clientSocketChannel.configureBlocking(false);
		this.selector=Selector.open();
	}

	/**
	 * Run client.
	 * First, we always have a channel to connect
	 * connect() method helps client shake hands with server
	 * but not really connected in case the server has not responded. 
	 * An infinite loop, instead of the selector collecting active channels, 
	 * it collects channel's state keys every 1 second. 
	 * Loop through keys and process depend on key state, Connectable or Writeable or Readable
	 */
	@Override
	public void run() {
		try {
			this.clientSocketChannel.register(this.selector, SelectionKey.OP_CONNECT);
			this.clientSocketChannel.connect(new InetSocketAddress(this.serverAddress, this.serverPort));
			while(!Thread.currentThread().isInterrupted()) {
				this.selector.select(5000);
				Iterator<SelectionKey> keys=this.selector.selectedKeys().iterator();
				while(keys.hasNext()) {
					SelectionKey key=keys.next();
					keys.remove();
					try{
						if(key.isConnectable()) {
							connect(key);
						}
						if(key.isReadable()){
							SocketChannel socketChannel=(SocketChannel) key.channel();
							if(Utils.selectFunction(key, Utils.readHead(socketChannel))) {
								socketChannel.register(selector, SelectionKey.OP_WRITE);
							}else socketChannel.register(selector, SelectionKey.OP_READ);
						}
						if(key.isWritable()) {
							SocketChannel socketChannel=(SocketChannel) key.channel();
							if(Utils.selectFunction(key, Utils.readHead(socketChannel))) {
								socketChannel.register(selector, SelectionKey.OP_WRITE);
							}else socketChannel.register(selector, SelectionKey.OP_READ);
						}
					} catch (Exception e) {
						// TODO: handle exception
						key.channel().close();
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Connect. Check connection, if ready, call finishConnect() to finish
	 * then select function with head is 1 to send system info
	 * 
	 * @param key the key
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void connect(SelectionKey key) throws IOException {
		SocketChannel socketChannel=(SocketChannel) key.channel();
		socketChannel.configureBlocking(false);
		if(socketChannel.isConnectionPending()) {
			socketChannel.finishConnect();
			Utils.selectFunction(key, (byte)1);
		}
		socketChannel.register(selector, SelectionKey.OP_READ);
	}
	
	/**
	 * Gets the server port.
	 *
	 * @return the server port
	 */
	public int getServerPort() {
		return serverPort;
	}

	/**
	 * Sets the server port.
	 *
	 * @param serverPort the new server port
	 */
	public void setServerPort(int serverPort) {
		this.serverPort = serverPort;
	}

	/**
	 * Gets the server address.
	 *
	 * @return the server address
	 */
	public String getServerAddress() {
		return serverAddress;
	}

	/**
	 * Sets the server address.
	 *
	 * @param serverAdd the new server address
	 */
	public void setServerAddress(String serverAdd) {
		this.serverAddress = serverAdd;
	}
	
}