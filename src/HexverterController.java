import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

/* Control module for XBee communication 
 * Allows to send the data, receive the data and log.
 * To send the data press command or set of commands and press <enter> 
 */

public class HexverterController {

	static SerialPort serialPort;
	static BufferedWriter log = null;

	public static void main(String[] args) {
		System.out.println("*** Control panel ***");

		// Set this value to address of port number where XBee explorer dongle
		// is connected
		serialPort = new SerialPort("/dev/tty.usbserial-DA00T2QK");
		try {
			// Create a log file to log all the received data
			FileWriter fstream = new FileWriter("log.txt", true);
			log = new BufferedWriter(fstream);

			// Open serial port
			serialPort.openPort();
			serialPort.setParams(SerialPort.BAUDRATE_9600,
					SerialPort.DATABITS_8, SerialPort.STOPBITS_1,
					SerialPort.PARITY_NONE);
			serialPort.addEventListener(new SerialPortReader());
			Scanner sc = new Scanner(System.in);
			try {
				while (true) {
					if (sc.hasNext()) {
						String command = sc.next();
						if (command.toLowerCase().equals("exit")) {
							break;
						}
						serialPort.writeBytes(command.getBytes());
					}
				}
			} finally {
				sc.close();
				serialPort.closePort();
				if (log != null) {
					log.close();
				}
			}
		} catch (SerialPortException ex) {
			System.err.println(ex);
		} catch (IOException ex) {
			System.err.println(ex);
		}
	}

	static class SerialPortReader implements SerialPortEventListener {

		public void serialEvent(SerialPortEvent event) {
			// If data is available
			if (event.isRXCHAR()) {
				try {
					byte buffer[] = serialPort.readBytes();
					if (buffer!=null){
						for (byte b : buffer) {
							System.out.print((char) b);
							log.write(b);
						}
					}
				} catch (SerialPortException ex) {
					System.err.println(ex);
				} catch (IOException ex) {
					System.err.println(ex);
				}
			}
		}
	}

}