/*****************************************************************
SPINE - Signal Processing In-Node Environment is a framework that 
allows dynamic on node configuration for feature extraction and a 
OtA protocol for the management for WSN

Copyright (C) 2007 Telecom Italia S.p.A. 
 
GNU Lesser General Public License
 
This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, 
version 2.1 of the License. 
 
This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.
 
You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA  02111-1307, USA.
*****************************************************************/

package spine.communication.tinyos;

import jade.util.Logger;

import java.io.*;
import java.net.*;

import spine.SPINEManager;

import net.tinyos.message.*;


/**
 * Connects to a SerialForwarder and brokers the sending and receiving of packets.
 * 
 * Note that this class is only used internally at the framework.
 *
 * @author Philip Kuryloski
 *
 * @version 1.2
 */
public final class SFReadWriteThread extends Thread {
    
    public static final byte TOS_SERIAL_ACTIVE_MESSAGE_ID = 0;

	String host;
	int port;
	MessageListener listener;
	
	Socket sfSocket;
	InputStream is;
	OutputStream os;
	
	boolean handshakedone = false;

    /**
     * 
     */
	public SFReadWriteThread(String host, int port, MessageListener listener) throws UnknownHostException {
		this.host = host;
		this.port = port;
		this.listener = listener;

		try {
			sfSocket = new Socket(host, port);
			//sfSocket.setKeepAlive(true);
			//sfSocket.setSoTimeout(100);
			//sfSocket.setReuseAddress(true);
			
			is = sfSocket.getInputStream();
			os = sfSocket.getOutputStream();
		}
		catch (ConnectException ce) {
			ce.printStackTrace();
			if (SPINEManager.getLogger().isLoggable(Logger.SEVERE)) { 
				StringBuffer str = new StringBuffer();
				str.append(ce.getMessage());
				str.append("\r\nCheck to make sure that your serial forwarder is running and listening on ");
				str.append(host);
				str.append(":");
				str.append(port);
				str.append("\r\nWill exit now!");
				SPINEManager.getLogger().log(Logger.SEVERE, str.toString());
			}
			System.exit(1);
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
			if (SPINEManager.getLogger().isLoggable(Logger.SEVERE)) 
				SPINEManager.getLogger().log(Logger.SEVERE, ioe.getMessage());
			System.exit(1);
		}
	}
	
	public void run() {
		byte[] rawmessage;
		SpineTOSMessage tosmsg = null;
		
		/* read the SF version bytes {@link http://cents.cs.berkeley.edu/tinywiki/index.php/Serial_Forwarder_Protocol}
		 * although the above mentioned link incorrectly lists the sfcookie as 84 rather than 85 */
		try {
			// announce ourselves to the SF
			final byte VERSION[] = {'U', ' '};
			os.write(VERSION);
			
			// listen for response
			int sfcookie = is.read();
			if (sfcookie != 85) {
				if (SPINEManager.getLogger().isLoggable(Logger.SEVERE)) { 
					StringBuffer str = new StringBuffer();
					str.append("Received bad SFP cookie (");
					str.append(sfcookie);
					str.append(") from ");
					str.append(host);
					str.append(":");
					str.append(port);
					str.append("\r\nWill exit now!");
					SPINEManager.getLogger().log(Logger.SEVERE, str.toString());
				}
				sfSocket.close();
				System.exit(1);
			}
			int sfversion = is.read();
			if (sfversion != 32) {
				if (SPINEManager.getLogger().isLoggable(Logger.SEVERE)) { 
					StringBuffer str = new StringBuffer();
					str.append("Unrecognized SFP version (");
					str.append(sfversion);
					str.append(") from ");
					str.append(host);
					str.append(":");
					str.append(port);
					str.append("\r\nWill exit now!");
					SPINEManager.getLogger().log(Logger.SEVERE, str.toString());
				}
				sfSocket.close();
				System.exit(1);
			}
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
			if (SPINEManager.getLogger().isLoggable(Logger.SEVERE)) 
				SPINEManager.getLogger().log(Logger.SEVERE, ioe.getMessage());
			System.exit(1);
		}
		
		if (SPINEManager.getLogger().isLoggable(Logger.INFO)) { 
			StringBuffer str = new StringBuffer();
			str.append("Connected and handshaked Serial forwarder at ");
			str.append(host);
			str.append(":");
			str.append(port);
			SPINEManager.getLogger().log(Logger.INFO, str.toString());
		}
		
		handshakedone = true;
		
		while (true) {
			try {
				// grab a raw message
				rawmessage = readMessage();
			
				if (rawmessage[0] != TOS_SERIAL_ACTIVE_MESSAGE_ID) {
					// ignore strange packet
					continue;
				}
				
				tosmsg = SpineTOSMessage.Construct(rawmessage);
								
			} catch (SFLocalNodeAdapterException e) {
				e.printStackTrace();
				if (SPINEManager.getLogger().isLoggable(Logger.SEVERE)) 
					SPINEManager.getLogger().log(Logger.SEVERE, e.getMessage());
				break;
			}
			
			int srcID = 0;
			try {
				srcID = tosmsg.getHeader().getSourceID();
			}
			catch (IllegalSpineHeaderSizeException ishse) {
				ishse.printStackTrace();
				if (SPINEManager.getLogger().isLoggable(Logger.SEVERE)) 
					SPINEManager.getLogger().log(Logger.SEVERE, ishse.getMessage());
				continue;
			}

			listener.messageReceived(srcID, tosmsg);
		}
	}
	
	/**
	 * readN cloned from net.tinyos.packet.SFProtocol
	 */
    protected byte[] readN(int n) throws IOException {
		byte[] data = new byte[n];
		int offset = 0;
		
		// A timeout would be nice, but there's no obvious way to
		// write it before java 1.4 (probably some trickery with
		// a thread and closing the stream would do the trick, but...)
		while (offset < n) {
			int count = is.read(data, offset, n - offset);
			
			if (count == -1)
				throw new IOException("end-of-stream");
			offset += count;
		}
		return data;
    }

	protected byte[] readMessage() throws SFReadException {
		// Protocol is straightforward: 1 size byte, <n> data bytes
		byte[] read = null;
		try {
			byte[] size = readN(1);
			
			if (size[0] == 0)
				throw new IOException("0-byte packet");
			read = readN(size[0] & 0xff);
		}
		catch (IOException ioe) {
			throw new SFReadException(ioe);
		}
		
		return read;
	}
	
	/**
	 * extractRaw - cloned from net.tinyos.message.Sender
	 *
	 */
	protected byte[] extractRaw(int moteId, Message m) throws IOException {
		int amType = m.amType();
		byte[] data = m.dataGet();
		
		if (amType < 0) {
			throw new IOException("unknown AM type for message " +
								  m.getClass().getName());
		}
		
		SerialPacket packet = new SerialPacket(SerialPacket.offset_data(0) + data.length);
		packet.set_header_dest(moteId);
		packet.set_header_type((short)amType);
		packet.set_header_length((short)data.length);
		packet.dataSet(data, 0, SerialPacket.offset_data(0), data.length);
		
		byte[] packetData = packet.dataGet();
		byte[] fullPacket = new byte[packetData.length + 1];
		fullPacket[0] = TOS_SERIAL_ACTIVE_MESSAGE_ID;
		System.arraycopy(packetData, 0, fullPacket, 1, packetData.length);
		
		return fullPacket;
	}
	
	protected synchronized void sendMessage(int moteID, Message message) throws SFWriteException {
		while (!handshakedone) {
			try { Thread.sleep(100); } catch (InterruptedException ie) {}
		}
		
		// decode Message into raw bytes
		byte[] rawBytes = null;
		try {
			rawBytes = extractRaw(moteID, message);
			os.write((byte)rawBytes.length);
			os.write(rawBytes);
			os.flush();
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
			if (SPINEManager.getLogger().isLoggable(Logger.SEVERE)) 
				SPINEManager.getLogger().log(Logger.SEVERE, ioe.getMessage());
			throw new SFWriteException();
		}
	}
	
	public static String byteArrayToHexString(byte[] data) {
		int i = 0;
		
		String result = Integer.toHexString(data[i]<0 ? data[i]+256 : data[i]);

		while (i<data.length-1) {
			i++;
			result += " " + Integer.toHexString(data[i]<0 ? data[i]+256 : data[i]);
		}

		return result;
	}
}
