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
 * Exception thrown whenever there's a 'read' problem into the SerialForwarder.
 * 
 * Note that this class is only used internally at the framework.
 *
 * @author Philip Kuryloski
 *
 * @version 1.2
 */
package spine.communication.tinyos;

import java.io.IOException;

public final class SFReadException extends SFLocalNodeAdapterException {
    
	private static final long serialVersionUID = 1L;

	/** Version control identifier strings. */
    public static final String[] RCS_ID = {
	"$URL: http://macromates.com/svn/Bundles/trunk/Bundles/Java.tmbundle/Templates/Java Class/class-insert.java $",
	"$Id$",
    };
	
	IOException ioe;
    
    /**
     * 
     */
    public SFReadException(IOException ioe) {
		super("Error reading from Serial Forwarder socket.");
		this.ioe = ioe;
    }
	
	public SFReadException() {
		this(null);
	}
	
	public String toString() {
		String s = super.toString();
		if (ioe != null) {
			s += " <- " + ioe;
		}
		return s;
	}
}
