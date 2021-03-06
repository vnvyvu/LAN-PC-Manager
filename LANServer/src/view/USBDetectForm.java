/*
 * Created by VyVu
 * 
 *  @author vutrivi99@gmail.com
 */
package view;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.HashMap;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import com.amihaiemil.eoyaml.Yaml;
import com.amihaiemil.eoyaml.YamlMapping;
import com.amihaiemil.eoyaml.extensions.MergedYamlMapping;

import controller.PacketHandler;
import controller.send.USBDetectSender;
import model.Device;

// TODO: Auto-generated Javadoc
/**
 * The Class USBDetectForm.
 */
public class USBDetectForm extends JFrame {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -9049335934110357874L;
	
	/** The content pane. */
	private JPanel contentPane;
	
	/** The turn off btn. */
	private JButton btnOff;
	
	/** The mode */
	private int mode;
	
	/** The config. */
	private YamlMapping config;
	/**
	 * Create the frame.
	 */
	public USBDetectForm(HashMap<Device, SocketChannel> devices) {
		try{
			config=PacketHandler.getConfig("config.yml");
			mode=config.integer("usbmode");
		} catch (Exception e) {
			// TODO: handle exception
			JOptionPane.showMessageDialog(this, "config.yml not found or invalid format", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		
		setResizable(false);
		setTitle("USB Detector");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 300, 170);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new GridLayout(4, 2, 0, 5));
		
		JLabel lblNewLabel_1 = new JLabel("Action when usb detected:");
		contentPane.add(lblNewLabel_1);
		
		JComboBox<String> comboMode = new JComboBox<String>();
		comboMode.setModel(new DefaultComboBoxModel<String>(new String[] {"DO_NOTHING", "EJECT_HIM", "TURN_OFF_PC"}));
		comboMode.setSelectedIndex(mode);
		contentPane.add(comboMode);
		
		JButton btnUpdate = new JButton("Update/Enable");
		btnUpdate.setMultiClickThreshhold(1500);
		btnUpdate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				btnOff.setEnabled(false);
				try {
					config=new MergedYamlMapping(config, ()->
					Yaml.createYamlMappingBuilder().add("usbmode", ""+comboMode.getSelectedIndex()).build(), true);
					Yaml.createYamlPrinter(new FileWriter("config.yml", false)).print(config);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				devices.forEach((device, socketChannel)->{
					try {
						USBDetectSender.on(socketChannel);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				});
				btnOff.setEnabled(true);
			}
		});
		contentPane.add(btnUpdate);
		btnOff = new JButton("Disable");
		btnOff.setMultiClickThreshhold(1500);
		btnOff.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				devices.forEach((device, socketChannel)->{
					try {
						USBDetectSender.off(socketChannel);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				});
			}
		});
		contentPane.add(btnOff);
	}

}
