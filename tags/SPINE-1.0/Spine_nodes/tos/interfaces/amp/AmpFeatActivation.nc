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

/**
 * This component parses the AMP (Activity Monitoring Feature Selection Protocol) 
 * Feature Activation packet.
 * It also allows the retrieval of the packet fields.
 *
 * @author Raffaele Gravina
 * @author Antonio Guerrieri
 *
 * @version 1.0
 */
 
 interface AmpFeatActivation {
      
      /*
      * Parses the given data structure into a meaningful AMP Feature Activation packet
      *
      * @param payload : the pointer to the data structure containing the non-formatted payload
      *
      * @return void
      */
      command void parse(uint8_t* payload);

      /*
      * Gets the Feature Code field of the parsed packet
      *
      * @return 'uint8_t' : the Feature Code representing the feature to be computed
      */
      command uint8_t getFeatureCode();

      /*
      * Gets the Window size, in term of nr of samples, of the parsed packet
      *
      * @return 'uint8_t' : the Window size requested
      */
      command uint8_t getNrSamples();

      /*
      * Gets the Shift field of the parsed packet
      *
      * @return 'uint8_t' : the Shift requested
      */
      command uint8_t getShift();

      /*
      * Gets the Sampling Time field of the parsed packet
      *
      * @return 'uint8_t' : the Sampling Time, expressed in ms, for the specified sensor
      */
      command uint16_t getSamplingTime();

      /*
      * Gets the Sensor Code field of the parsed packet
      *
      * @return 'uint8_t' : the Sensor Code representing the sensor where the samples belongs to.
      */
      command uint8_t getSensorCode();

      /*
      * Gets the X-Axis feature enabling flag
      *
      * @return 'uint8_t' : <code>TRUE</code> if the feature has to be computed on the 
      *                      X-Axis on the specified sensor, <code>FALSE</code> otherwise
      */
      command bool isActivatedAxis0();
      
      /*
      * Gets the Y-Axis feature enabling flag
      *
      * @return 'uint8_t' : <code>TRUE</code> if the feature has to be computed on the 
      *                      Y-Axis on the specified sensor, <code>FALSE</code> otherwise
      */
      command bool isActivatedAxis1();
      
      /*
      * Gets the Z-Axis feature enabling flag
      *
      * @return 'uint8_t' : <code>TRUE</code> if the feature has to be computed on the 
      *                      Z-Axis on the specified sensor, <code>FALSE</code> otherwise
      */
      command bool isActivatedAxis2();
 }




