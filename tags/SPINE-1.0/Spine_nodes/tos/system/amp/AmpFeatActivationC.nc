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
 
module AmpFeatActivationC
{
   provides interface AmpFeatActivation;
}
implementation
{
      uint8_t featureCode;

      uint8_t nrSamples;

      uint8_t shift;

      uint16_t samplingTime;

      uint8_t sensorCode;
      bool activatedAxis0;
      bool activatedAxis1;
      bool activatedAxis2;


      uint8_t partTemp;


      /*
      * Parses the given data structure into a meaningful AMP Feature Activation packet
      *
      * @param payload : the pointer to the data structure containing the non-formatted payload
      *
      * @return void
      */
      command void AmpFeatActivation.parse(uint8_t* payload) {
          memcpy(&partTemp, payload, 1);
          featureCode = (partTemp & 0xf8)>>3; // 0xf8 = 11111000 binario

          memcpy(&nrSamples, (payload+1) , 1);

          memcpy(&shift, (payload+2) , 1);

          memcpy(&partTemp, (payload+3), 1);
          samplingTime = (partTemp << 8);

          memcpy(&partTemp, (payload+4), 1);
          samplingTime = samplingTime | partTemp;

          memcpy(&partTemp, (payload+5) , 1);
          sensorCode = (partTemp & 0xc0)>>6; // 0xc0 = 11000000 binario
          if( ((partTemp>>5) & 0x01) == 0x01)
               activatedAxis0 = TRUE;
          else activatedAxis0 = FALSE;
          if( ((partTemp>>4) & 0x01) == 0x01)
               activatedAxis1 = TRUE;
          else activatedAxis1 = FALSE;
          if( ((partTemp>>3) & 0x01) == 0x01)
               activatedAxis2 = TRUE;
          else activatedAxis2 = FALSE;
      }

      /*
      * Gets the Feature Code field of the parsed packet
      *
      * @return 'uint8_t' : the Feature Code representing the feature to be computed
      */
      command uint8_t AmpFeatActivation.getFeatureCode() {
          return featureCode;
      }

      /*
      * Gets the Window size, in term of nr of samples, of the parsed packet
      *
      * @return 'uint8_t' : the Window size requested
      */
      command uint8_t AmpFeatActivation.getNrSamples() {
          return nrSamples;
      }

      /*
      * Gets the Shift field of the parsed packet
      *
      * @return 'uint8_t' : the Shift requested
      */
      command uint8_t AmpFeatActivation.getShift() {
          return shift;
      }

      /*
      * Gets the Sampling Time field of the parsed packet
      *
      * @return 'uint8_t' : the Sampling Time, expressed in ms, for the specified sensor
      */
      command uint16_t AmpFeatActivation.getSamplingTime() {
          return samplingTime;
      }

      /*
      * Gets the Sensor Code field of the parsed packet
      *
      * @return 'uint8_t' : the Sensor Code representing the sensor where the samples belongs to.
      */
      command uint8_t AmpFeatActivation.getSensorCode() {
          return sensorCode;
      }

      /*
      * Gets the X-Axis feature enabling flag
      *
      * @return 'uint8_t' : <code>TRUE</code> if the feature has to be computed on the 
      *                      X-Axis on the specified sensor, <code>FALSE</code> otherwise
      */
      command bool AmpFeatActivation.isActivatedAxis0() {
          return activatedAxis0;
      }

      /*
      * Gets the Y-Axis feature enabling flag
      *
      * @return 'uint8_t' : <code>TRUE</code> if the feature has to be computed on the 
      *                      Y-Axis on the specified sensor, <code>FALSE</code> otherwise
      */
      command bool AmpFeatActivation.isActivatedAxis1() {
          return activatedAxis1;
      }

      /*
      * Gets the Z-Axis feature enabling flag
      *
      * @return 'uint8_t' : <code>TRUE</code> if the feature has to be computed on the 
      *                      Z-Axis on the specified sensor, <code>FALSE</code> otherwise
      */
      command bool AmpFeatActivation.isActivatedAxis2() {
          return activatedAxis2;
      }


}




