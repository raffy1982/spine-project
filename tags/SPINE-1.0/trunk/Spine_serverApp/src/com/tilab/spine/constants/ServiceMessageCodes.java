/*****************************************************************
SPINE - Signal Processing In-Note Environment is a framework that 
allows dynamic configuration of feature extraction capabilities 
of WSN nodes via an OtA protocol

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

package com.tilab.spine.constants;


/**
 *
 * This interface contains the codes associated to the errors eventually thrown by a mote
 * implementing AMP (Activity Monitoring Features Selection Protocol)
 *
 * @author Raffaele Gravina
 * @author Antonio Guerrieri
 *
 * @version 1.0
 */
public class ServiceMessageCodes {
	  // message types codes
	  public final static byte MESSAGE = 0x1;

	  public final static byte WARNING = 0x2;
	  
	  public final static byte ALERT = 0x3;

	  public final static byte ERROR = 0x4;

	  public final static byte FATAL_ERROR = 0x5;
	  
	  public final static byte EVENT = 0x6;
	  
	  public final static byte ACTIVATE_FEATURE_ERROR = 0x7;
	  
	  // message names
	  public final static String MESSAGE_CAPTION = "Message";

	  public final static String WARNING_CAPTION = "Warning";
	  
	  public final static String ALERT_CAPTION = "Alert";

	  public final static String ERROR_CAPTION = "Error";

	  public final static String FATAL_ERROR_CAPTION = "Fatal Error";
	  
	  public final static String EVENT_CAPTION = "Event";
	  
	  public final static String ACTIVATE_FEATURE_ERROR_CAPTION = "Activate Feature Error";
	  
	  
	  // message detail codes
	  public final static byte WINDOW_SIZE_TOO_BIG = 0x1;

	  public final static byte TEMPORARY_OUT_OF_RESOURCES = 0x2;
	  
	  public final static byte SENSOR_UNKNOWN = 0x3;
	  
	  public final static byte INVALID_AXIS = 0x4;
	  
	  public final static byte FALL_DETECTED = 0x5;
	  
	  public static final byte CONNECTION_FAIL = 0x6;
	  
	  // message detail names
	  public final static String WINDOW_SIZE_TOO_BIG_CAPTION = "Window Size Too Big";

	  public final static String TEMPORARY_OUT_OF_RESOURCES_CAPTION = "Temporary Out Of Resources";
	  
	  public final static String SENSOR_UNKNOWN_CAPTION = "Sensor Unknown";
	  
	  public final static String INVALID_AXIS_CAPTION = "Invalid Axis";
	  
	  public final static String FALL_DETECTED_CAPTION = "Fall Detected";
	  
	  public final static String CONNECTION_FAIL_CAPTION = "Unable to find BaseStation and/or any sensor nodes.\nPlease check COM Port manually and sensors batteries.";
	  
	  
	  public final static String TYPE_UNKNOWN_CAPTION = "Message Type Unknown";
	  
	  public final static String DETAIL_UNKNOWN_CAPTION = "Message Detail Unknown";

	  /**
		 * Utility method
		 * 
		 * @return the caption associated to the message type code
		 */
		public static String getMessageTypeCaption(short messageType) {
			switch (messageType) {
				case ALERT: return ALERT_CAPTION;
				case ERROR: return ERROR_CAPTION;
				case FATAL_ERROR: return FATAL_ERROR_CAPTION;
				case MESSAGE: return MESSAGE_CAPTION;
				case WARNING: return WARNING_CAPTION;
				case EVENT: return EVENT_CAPTION;
				case ACTIVATE_FEATURE_ERROR: return ACTIVATE_FEATURE_ERROR_CAPTION;
			}
			return TYPE_UNKNOWN_CAPTION;
		}
		
		/**
		 * Utility method
		 * 
		 * @return the caption associated to the message detail code
		 */
		public static String getMessageDetailCaption(short messageDetail) {
			switch (messageDetail) {
				case INVALID_AXIS: return INVALID_AXIS_CAPTION;
				case SENSOR_UNKNOWN: return SENSOR_UNKNOWN_CAPTION;
				case TEMPORARY_OUT_OF_RESOURCES: return TEMPORARY_OUT_OF_RESOURCES_CAPTION;
				case WINDOW_SIZE_TOO_BIG: return WINDOW_SIZE_TOO_BIG_CAPTION;
				case FALL_DETECTED: return FALL_DETECTED_CAPTION;
				case CONNECTION_FAIL: return CONNECTION_FAIL_CAPTION; 
			}
			return DETAIL_UNKNOWN_CAPTION;
		}
}
