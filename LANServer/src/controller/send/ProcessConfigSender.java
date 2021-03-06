/*
 * Created by VyVu
 * 
 *  @author vutrivi99@gmail.com
 */
package controller.send;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.channels.SocketChannel;

import com.amihaiemil.eoyaml.YamlMapping;

import controller.PacketHandler;

public class ProcessConfigSender {
	
	/**
	 * Turn on process monitor in client side.
	 *
	 * @param socketChannel the socket channel
	 * @throws FileNotFoundException the file not found exception
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void on(SocketChannel socketChannel) throws FileNotFoundException, IOException {
		YamlMapping config=PacketHandler.getConfig("config.yml");
		String data=config.string("processmode")+"?"+config.string("blacklist");
		PacketHandler.write2Socket(socketChannel, (byte)5, data.getBytes("UTF-8"));
	}
	
	/**
	 * Turn off process monitor in client side.
	 *
	 * @param socketChannel the socket channel
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public static void off(SocketChannel socketChannel) throws IOException {
		PacketHandler.writeHead(socketChannel, (byte)6);
	}
}
