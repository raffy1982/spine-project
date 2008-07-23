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

/**
 *
 *  
 *
 * @author Raffaele Gravina
 *
 * @version 1.2
 */

package spine.communication.tinyos;

import spine.Properties;
import spine.SPINEPacketsConstants;

public class SpineTOSMessage extends net.tinyos.message.Message {
	
	private static final String TINYOS_URL_PREFIX = Properties.getProperties().getProperty(Properties.URL_PREFIX_KEY);
	
	private static final int DEFAULT_MESSAGE_SIZE = 0; 	// it represents a variable-size array and 
														// does not check the corresponding array index

	private static final int AM_TYPE = SPINEPacketsConstants.AM_SPINE;
	
	private byte[] payloadBuf = null;
	

	protected SpineTOSMessage() {
        super(DEFAULT_MESSAGE_SIZE);
        this.amTypeSet(AM_TYPE);
    }
    
	protected SpineTOSMessage(byte pktType, byte groupID, int sourceID, int destID, byte sequenceNumber, byte fragmentNr, byte totalFragments, byte[] payload) {
    	super(SPINEHeader.SPINE_HEADER_SIZE + payload.length);
    	
    	this.amTypeSet(AM_TYPE); 
    	
    	SPINEHeader header = new SPINEHeader(SPINEPacketsConstants.CURRENT_SPINE_VERSION, false, pktType, 
    										 groupID, sourceID, destID, sequenceNumber, fragmentNr, totalFragments);    	
    	
    	byte[] msgBuf = new byte[SPINEHeader.SPINE_HEADER_SIZE + payload.length];
    	System.arraycopy(header.build(), 0, msgBuf, 0, SPINEHeader.SPINE_HEADER_SIZE);
    	System.arraycopy(payload, 0, msgBuf, SPINEHeader.SPINE_HEADER_SIZE, payload.length);
    	
    	this.dataSet(msgBuf);        
	}
    
	protected SPINEHeader getHeader() throws IllegalSpineHeaderSizeException {
    	byte[] msgBuf = this.dataGet();
    	
    	if (msgBuf.length < SPINEHeader.SPINE_HEADER_SIZE) 
    		throw new IllegalSpineHeaderSizeException(SPINEHeader.SPINE_HEADER_SIZE, msgBuf.length);
    	
		byte[] headerBuf = new byte[SPINEHeader.SPINE_HEADER_SIZE];
		System.arraycopy(msgBuf, 0, headerBuf, 0, SPINEHeader.SPINE_HEADER_SIZE);		
		
		return new SPINEHeader(headerBuf); 
    }
    
	protected byte[] getRawPayload() {
    	if (this.payloadBuf == null) {
	    	this.payloadBuf = new byte[this.dataGet().length - SPINEHeader.SPINE_HEADER_SIZE];
			System.arraycopy(this.dataGet(), SPINEHeader.SPINE_HEADER_SIZE, this.payloadBuf, 0, this.payloadBuf.length);
    	}				
		return this.payloadBuf;
    }
    
	protected void setRawPayload(byte[] payload) {
    	this.payloadBuf = payload;
    }

	protected TOSMessage parse() throws IllegalSpineHeaderSizeException {
		TOSMessage msg = new TOSMessage();
		
		byte[] msgBuf = this.dataGet();
		
		byte[] headerBuf = new byte[SPINEHeader.SPINE_HEADER_SIZE];
		System.arraycopy(msgBuf, 0, headerBuf, 0, SPINEHeader.SPINE_HEADER_SIZE);		
		
		if(this.payloadBuf == null) {
			this.payloadBuf = new byte[msgBuf.length - SPINEHeader.SPINE_HEADER_SIZE];
			System.arraycopy(msgBuf, SPINEHeader.SPINE_HEADER_SIZE, this.payloadBuf, 0, this.payloadBuf.length);
		}
		
		SPINEHeader header = new SPINEHeader(headerBuf); 
		
		msg.setClusterId(header.getPktType());
		msg.setProfileId(header.getGroupID());
		msg.setSourceURL(TINYOS_URL_PREFIX + header.getSourceID()); 
		msg.setDestinationURL(TINYOS_URL_PREFIX + header.getDestID());
		
		byte[] payload = PacketManager.parse(header.getPktType(), this.payloadBuf); 
		
		msg.setPayload(payload);
		
		return msg;
	}
    
    class SPINEHeader {
    	
    	public final static byte SPINE_HEADER_SIZE = 9;
    	
    	
    	private byte headerBuf[] = new byte[SPINE_HEADER_SIZE];
    	
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
        
    	private SPINEHeader (byte version, boolean extension, byte pktType, byte groupID, int sourceID, int destID, 
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
    	
    	private SPINEHeader(byte[] header) throws IllegalSpineHeaderSizeException {
    		if (header.length != SPINE_HEADER_SIZE) 
    			throw new IllegalSpineHeaderSizeException(SPINE_HEADER_SIZE, header.length);
    		else {
    			this.headerBuf = header;
    			this.canParse = true;
    			parse();
    		}
    	}
    	
        private byte[] build() {
        	
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
        
    }

}
