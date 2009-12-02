/*****************************************************************
SPINE - Signal Processing In-Node Environment is a framework that 
allows dynamic on node configuration for feature extraction and a 
OtA protocol for the management for WSN

Copyright (C) 2007 Telecom Italia S.p.A. 
†
GNU Lesser General Public License
†
This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, 
version 2.1 of the License. 
†
This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.† See the GNU
Lesser General Public License for more details.
†
You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA† 02111-1307, USA.
*****************************************************************/

/**
 *
 *  This class represents a SPINE TinyOS message into its lowest level.
 *  It is used only by the TinyOS platform dependent classes of the framework.
 *  
 *  Note that this class is only used internally at the framework. 
 *
 * @author Raffaele Gravina
 * @author Alessia Salmeri
 *
 * @version 1.3
 */

package spine.communication.tinyos;

import spine.Properties;
import spine.SPINEPacketsConstants;
import spine.SPINESupportedPlatforms;

public class SpineTOSMessage extends net.tinyos.message.Message {
	
	private static final String TINYOS_URL_PREFIX = Properties.getDefaultProperties().getProperty(SPINESupportedPlatforms.TINYOS + "_" + Properties.URL_PREFIX_KEY);
	
	private static final int DEFAULT_MESSAGE_SIZE = 0; 	// it represents a variable-size array and 
														// does not check the corresponding array index

	private static final int AM_TYPE = SPINEPacketsConstants.AM_SPINE;
	
	protected static final int AM_HEADER_SIZE = 8;
	
	protected SPINEHeader header = null;
	
	protected byte[] payloadBuf = null;
	

	protected SpineTOSMessage() {
        super(DEFAULT_MESSAGE_SIZE);
        this.amTypeSet(AM_TYPE);
    }
    
	protected SpineTOSMessage(byte pktType, byte groupID, int sourceID, int destID, byte sequenceNumber, byte fragmentNr, byte totalFragments, byte[] payload) {
    	super(SPINEPacketsConstants.SPINE_HEADER_SIZE + payload.length);
    	
    	this.amTypeSet(AM_TYPE); 
    	
    	this.header = new SPINEHeader(SPINEPacketsConstants.CURRENT_SPINE_VERSION, false, pktType, 
    										 groupID, sourceID, destID, sequenceNumber, fragmentNr, totalFragments);   
    	
    	this.payloadBuf = payload;
    	
    	byte[] msgBuf = new byte[SPINEPacketsConstants.SPINE_HEADER_SIZE + payload.length];
    	System.arraycopy(header.build(), 0, msgBuf, 0, SPINEPacketsConstants.SPINE_HEADER_SIZE);
    	System.arraycopy(payload, 0, msgBuf, SPINEPacketsConstants.SPINE_HEADER_SIZE, payload.length);
    	
    	this.dataSet(msgBuf);        
	}
	
	/**
	 * Construct a SpineTOSMessage from a raw SERIAL ACTIVE MESSAGE from a
	 * Serial Forwarder.
	 *
	 * @param	rawsfmessage raw message bytes from a serial forwarder
	 */
	public static SpineTOSMessage Construct(byte[] rawsfmessage) {
		byte[] payload;
		
		SPINEHeader h;
		
		byte[] sh_bytes = new byte[SPINEPacketsConstants.SPINE_HEADER_SIZE];
		System.arraycopy(rawsfmessage, AM_HEADER_SIZE, sh_bytes, 0, sh_bytes.length);
		try {
			h = new SPINEHeader(sh_bytes);
		}
		catch (IllegalSpineHeaderSizeException ishse) {
			return null;
		}
		
		payload = new byte[rawsfmessage.length - SPINEPacketsConstants.SPINE_HEADER_SIZE - AM_HEADER_SIZE];
		System.arraycopy(rawsfmessage, AM_HEADER_SIZE+SPINEPacketsConstants.SPINE_HEADER_SIZE,
						 payload, 0, payload.length);
		
		
		return new SpineTOSMessage(h.getPktType(), h.getGroupID(), h.getSourceID(), h.getDestID(), h.getSequenceNumber(), h.getFragmentNumber(), h.getTotalFragments(), payload);
	}
    
	protected SPINEHeader getHeader() throws IllegalSpineHeaderSizeException {
    	byte[] msgBuf = this.dataGet();
    	
    	if (msgBuf.length < SPINEPacketsConstants.SPINE_HEADER_SIZE) 
    		throw new IllegalSpineHeaderSizeException(SPINEPacketsConstants.SPINE_HEADER_SIZE, msgBuf.length);
    	
		byte[] headerBuf = new byte[SPINEPacketsConstants.SPINE_HEADER_SIZE];
		System.arraycopy(msgBuf, 0, headerBuf, 0, SPINEPacketsConstants.SPINE_HEADER_SIZE);		
		
		return new SPINEHeader(headerBuf); 
    }
    
	protected byte[] getRawPayload() {
    	if (this.payloadBuf == null) {
	    	this.payloadBuf = new byte[this.dataGet().length - SPINEPacketsConstants.SPINE_HEADER_SIZE];
			System.arraycopy(this.dataGet(), SPINEPacketsConstants.SPINE_HEADER_SIZE, this.payloadBuf, 0, this.payloadBuf.length);
    	}				
		return this.payloadBuf;
    }
    
	protected void setRawPayload(byte[] payload) {
    	this.payloadBuf = payload;
    }

	protected TOSMessage parse() throws IllegalSpineHeaderSizeException {
		TOSMessage msg = new TOSMessage();
		
		byte[] msgBuf = this.dataGet();
		
		byte[] headerBuf = new byte[SPINEPacketsConstants.SPINE_HEADER_SIZE];
		System.arraycopy(msgBuf, 0, headerBuf, 0, SPINEPacketsConstants.SPINE_HEADER_SIZE);		
		
		if(this.payloadBuf == null) {
			this.payloadBuf = new byte[msgBuf.length - SPINEPacketsConstants.SPINE_HEADER_SIZE];
			System.arraycopy(msgBuf, SPINEPacketsConstants.SPINE_HEADER_SIZE, this.payloadBuf, 0, this.payloadBuf.length);
		}
		
		SPINEHeader header = new SPINEHeader(headerBuf); 
		
		msg.setClusterId(header.getPktType());
		msg.setProfileId(header.getGroupID());
		msg.setSourceURL(TINYOS_URL_PREFIX + header.getSourceID()); 
		msg.setDestinationURL(TINYOS_URL_PREFIX + header.getDestID());
		msg.setSeqNo(header.getSequenceNumber());
		
		short[] payloadBufShort = new short[this.payloadBuf.length];
		for (int i = 0; i<this.payloadBuf.length; i++)
			payloadBufShort[i] = payloadBuf[i]; 
		
		msg.setPayload(payloadBufShort);
		
		return msg;
	}
    
	public String toString() {
		String s = null;
		
		try {
			s = getHeader() + " - ";
		
			s += "Payload (hex) { ";
			int len = 0;
			if(payloadBuf != null && payloadBuf.length > 0) 
				for (int i = 0; i<payloadBuf.length; i++) {
					short b =  payloadBuf[i];
					if (b<0) b += 256;
					s += Integer.toHexString(b).toUpperCase() + " ";
					len++;
				}
			else if(this.dataGet() != null && this.dataGet().length > SPINEPacketsConstants.SPINE_HEADER_SIZE) 
				for (int i = SPINEPacketsConstants.SPINE_HEADER_SIZE; i<this.dataGet().length; i++) {
					short b =  this.dataGet()[i];
					if (b<0) b += 256;
					s += Integer.toHexString(b).toUpperCase() + " ";
					len++;
				}
			else 
				s += "empty payload ";
			
			s += "} [len=" + len + "]";
			
		} catch (IllegalSpineHeaderSizeException e) {
			return e.getMessage();
		}
		
		return s;
	}

}


class SPINEHeader {
	
	private byte headerBuf[] = new byte[SPINEPacketsConstants.SPINE_HEADER_SIZE];
	
	private boolean canParse = false;
	private boolean canBuild = false;
	

	private byte vers;      // 2 bits
	private boolean ext;    // 1 bit
	private byte pktT;      // 5 bits

	private byte grpID;     // 8 bits

	private int srcID;    	// 16 bits

	private int dstID;    	// 16 bits

	private byte seqNr;     // 8 bits

	private byte fragNr;    // 8 bits
	private byte totFrags;  // 8 bits
	
	protected SPINEHeader (byte version, boolean extension, byte pktType, byte groupID, int sourceID, int destID, 
						byte sequenceNumber, byte fragmentNr, byte totalFragments) {
		
		this.vers = version;
		this.ext = extension;       
		this.pktT = pktType;      
		this.grpID = groupID;
		this.srcID = sourceID;
		this.dstID = destID; 
		this.seqNr = sequenceNumber; 
		this.fragNr = fragmentNr;    
		this.totFrags = totalFragments;
		
		this.canBuild = true;
	}
	
	protected SPINEHeader(byte[] header) throws IllegalSpineHeaderSizeException {
		if (header.length != SPINEPacketsConstants.SPINE_HEADER_SIZE) 
			throw new IllegalSpineHeaderSizeException(SPINEPacketsConstants.SPINE_HEADER_SIZE, header.length);
		else {
			this.headerBuf = header;
			this.canParse = true;
			parse();
		}
	}
	
	protected byte[] build() {
		
		if (!canBuild)
			return null;
		
		byte e = (this.ext)? (byte)1: (byte)0;    	
		headerBuf[0] = (byte)((this.vers<<6) | (e<<5) | this.pktT);
		
		headerBuf[1] = this.grpID;
		
		headerBuf[2] = (byte)(this.srcID>>8);
		headerBuf[3] = (byte)this.srcID;
		
		headerBuf[4] = (byte)(this.dstID>>8);
		headerBuf[5] = (byte)this.dstID;
		
		headerBuf[6] = this.seqNr;
		
		headerBuf[7] = this.fragNr;
		
		headerBuf[8] = this.totFrags;
		
		return headerBuf;
	}

	private boolean parse() {       
		if (!canParse)
			return false;
		
		vers = (byte)((headerBuf[0] & 0xC0)>>6);    		//  0xC0 = 11000000 binary
		ext = ((byte)((headerBuf[0] & 0x20)>>5) == 1);     	//  0x20 = 00100000 binary
		pktT = (byte)(headerBuf[0] & 0x1F);       			//  0x1F = 00011111 binary
		grpID = headerBuf[1];
	   
		srcID = headerBuf[2];                  // check
		srcID = ((srcID<<8) | headerBuf[3]);

		dstID = headerBuf[4];  	              // check
		dstID = ((dstID<<8) | headerBuf[5]);

		seqNr = headerBuf[6];
	   
		fragNr = headerBuf[7];

		totFrags = headerBuf[8];

		return true;
	}

	protected byte getVersion() {
	   return vers;
	}

	protected boolean isExtended() {
	   return ext;
	}

	protected byte getPktType() {
	   return pktT;
	}

	protected byte getGroupID() {
	   return grpID;
	}

	protected int getSourceID() {
	   return srcID;
	}

	protected int getDestID() {
	   return dstID;
	}
	
	protected byte getSequenceNumber() {
	  return seqNr;
	}

	protected byte getFragmentNumber() {
	   return fragNr;
	}

	protected byte getTotalFragments() {
	   return totFrags;
	}
	
	protected byte[] getHeaderBuf() {
		return headerBuf;
	}
	
	public String toString() {
		
		String grp = (grpID<0)? Integer.toHexString(grpID+256): Integer.toHexString(grpID);		
		String seq = (seqNr<0)? ""+(seqNr+256): ""+seqNr;
		String dst = (dstID==-1)? "BROADCAST": (dstID==0)? "BASESTATION": ""+dstID;
		
		String s = "Spine Header {";
		
		s += "ver: 1." + vers + ", ext:" + ext + 
			 ", pktType:" + SPINEPacketsConstants.packetTypeToString(pktT).toUpperCase() + 
			 ", groupID:" + grp.toUpperCase() + ", srcID:" + srcID + ", dstID:" + dst + 
			 ", seqNr:" + seq + ", fragNr:" + fragNr + ", totFrags:" + totFrags + "}";
		
		return s;
	}
	
}
