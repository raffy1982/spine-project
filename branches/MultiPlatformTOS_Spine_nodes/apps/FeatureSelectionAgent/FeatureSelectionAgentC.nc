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

#include <Timer.h>
#include "FeatureSelectionAgent.h"
#include "AmpPacketsConstants.h"
#include "FeaturesCodes.h"
#include "SensorCodes.h"
#include "ServiceMessageCodes.h"

/**
 * Implementation of the Feature Selection Agent (mote side).
 * It contains the AMP protocol support and the required business logic
 * in order to activate, disable features computation and send the results
 * and to sense and send the battery level.
 *
 * @author Raffaele Gravina
 * @author Antonio Guerrieri
 *
 * @version 1.0
 **/
 module FeatureSelectionAgentC {

  uses {
	interface Leds;

	interface Timer<TMilli> as SamplingTimer;
	interface Timer<TMilli> as SendTimer;
	interface Timer<TMilli> as PostTimer;
	interface Timer<TMilli> as BatteryInfoTimer;

	interface Packet as PacketServiceAdvertisement;
	interface Packet as PacketData;
	interface Packet as PacketServiceMessage;
	interface Packet as PacketBatteryInfo;

        interface AMSend as ServiceAdvertisementSender;
	interface AMSend as DataSender;
	interface AMSend as ServiceMessageSender;
	interface AMSend as BatteryInfoSender;
	interface Receive as ServiceDiscoveryReceiver;
	interface Receive as FeatureActivationReceiver;
	interface Receive as RemoveFeatureReceiver;
	interface Receive as BatteryInfoReqReceiver;


  	interface AccSensor;
  	//interface STAccelerometer;

        //interface Read<uint16_t> as ReadGyroX;
	//interface Read<uint16_t> as ReadGyroY;
	
	interface VoltageSensor as VoltSensor;
	//interface Read<uint16_t> as VoltSensor;


        interface BuffersManager as BM;

        interface JobEngine;

        interface AmpHeader;
        interface AmpServiceDiscovery;
        interface AmpFeatActivation;
        interface AmpBatteryInfo;
        interface AmpBatteryInfoReq;
        interface AmpData;
        interface AmpServiceMessage;
        interface AmpRemoveFeature;
        interface AmpServiceAdvertisement;

  }

}

implementation {

      message_t pkt;
      bool busy = FALSE;

      uint16_t i, j, j2, i3, j3, i4, j5, j6, i7, j7;

      uint16_t accX;
      uint16_t accY;
      uint16_t accZ;
      uint16_t gyroX;
      uint16_t gyroY;

      uint16_t samplingPeriod;
      uint16_t sendPeriod;
      uint8_t sendTimerCounter = 0;
      bool registered = FALSE;
      bool running = FALSE;
      bool accelRequested = FALSE;
      bool gyroRequested = FALSE;
      bool temperatureRequested = FALSE;
      uint8_t myGroupID;
      uint8_t srvAdvArray [SERVICE_ADVERTISEMENT_PKT_SIZE];
      uint8_t dataArray [DATA_PKT_SIZE - 6];
      int16_t dataFeatArray [3];
      uint8_t serviceMessageArray [SERVICE_MESSAGE_PKT_SIZE];
      uint8_t batteryInfoArray[BATTERY_INFO_PKT_SIZE];

      uint8_t sensor [50];
      uint8_t feature [50];
      uint8_t axis [50];
      uint8_t id [50];
      uint8_t iJob = 0;
      bool dataSendState = FALSE;
      uint8_t dataSendIndex = 0;

      // the following are used in 'sendDataPacket()'
      bool activeAxX;
      bool activeAxY;
      bool activeAxZ;
      uint8_t dataSendIndexCurr;
      int16_t feat[3];

      // the following are used for battery info section...
      uint16_t batteryInfoPeriod;
      bool batteryPeriodicReq;
      bool isBatteryPeriodicReqInTime;
      uint16_t sentPacketsCounter4BatteryInfo;

      // used for 'checkAndSendBatteryInfo()'
      bool sendBatteryReady = FALSE;
      uint16_t lastBatteryLevel = 4096;

      uint8_t tmp; // used in 'insertJob'
      uint8_t tmp2;

      // the following are used in 'activateFeature()'
      int8_t idTemp1;
      int8_t idTemp2;
      int8_t idTemp3;
      uint8_t featureTemp;
      uint8_t sensorTemp;
      uint8_t windowTemp;
      uint8_t shiftTemp;
      bool error;

      // the following are used in 'removeFeature()'
      uint8_t deleting[3];
      uint8_t toDel;
      uint8_t featTemp;
      uint8_t sensTemp;

      uint8_t currSendNode = 1;
      
      bool fallDetectionActive = FALSE;
      int16_t currentAccelReadings[3];
      uint8_t fallDetectionPause = 0;
      
      uint8_t numMotes = 1;

      /*
      * The function starts the sampling process and activate the features computation
      * and the results communication.
      * It is called only when the System Coordinator requests to a 'sleeping' node some features.
      *
      * @return void
      */
      void startTimers() {

           call SamplingTimer.startPeriodic(samplingPeriod);
           call SendTimer.startPeriodic(sendPeriod);

           sendTimerCounter = 0;
           running = TRUE;
      }

      /*
      * The function stops the sampling process and the features computation procedure.
      * It is called when the node realizes hasn't currently any features to compute.
      *
      * @return void
      */
      void stopTimers() {
           call Leds.set(0);
           call SamplingTimer.stop();
           call SendTimer.stop();
           running = FALSE;
      }
      
      /*
      * The function resets the node state and leaves only the bind to its Coordinator.
      * It is called when the System Coordinator sends a Service Discovery Packet with the RESET Flag
      * enabled, meaning that nodes already connected have to reset their state.
      *
      * @return void
      */
      void reset() {
          if (running)
              stopTimers();

          busy = FALSE;
          sendTimerCounter = 0;
          accelRequested = FALSE;
          gyroRequested = FALSE;
          temperatureRequested = FALSE;
          
          for (i=0; i<iJob; i++)
              call BM.bufferRelease(id[i]);

          iJob = 0;
          dataSendState = FALSE;
          dataSendIndex = 0;
          sendBatteryReady = FALSE;
          batteryPeriodicReq = FALSE;
          call BatteryInfoTimer.stop();
      }

      /*
      * The event is thrown when a new gyroscope x-axis reading is ready.
      * It sets a global variable and put the reading in the system buffer.
      *
      * @param result : indicates whether the reading process has succeed or not.
      * @param data : if the result is <code>SUCCESS</code>, it contains a consistent reading.
      *
      * @return void
      */
      // commented because we are just simulating the data gathering
      /*
      event void ReadGyroX.readDone(error_t result, uint16_t data) {
	    gyroX = data;
	    call BM.putElem(GYROSCOPE_CODE, AXIS_X, &gyroX);
      }
      */

      /*
      * The event is thrown when a new gyroscope y-axis reading is ready.
      * It sets a global variable and put the reading in the system buffer.
      * 
      * @param result : indicates whether the reading process has succeed or not.
      * @param data : if the result is <code>SUCCESS</code>, it contains a consistent reading.
      *
      * @return void
      */
      // commented because we are just simulating the data gathering
      /*
      event void ReadGyroY.readDone(error_t result, uint16_t data) {
	    gyroY = data;
	    call BM.putElem(GYROSCOPE_CODE, AXIS_Y, &gyroY);
      }
      */
      
      /*
      * The function check whether there's some active battery info to send. 
      * If yes, it sends the last voltage level reading.
      *
      * @return void
      */
      void checkAndSendBatteryInfo(){
         if( (!dataSendState) && sendBatteryReady && (!busy) ) {
             BatteryInfoPkt* btrpkt = (BatteryInfoPkt*)(call PacketBatteryInfo.getPayload(&pkt, NULL));
             call AmpHeader.build(batteryInfoArray, AMP_VERSION, FALSE, BATTERY_INFO_PKT_CODE, myGroupID,
                                 (uint8_t)TOS_NODE_ID, BASE_STATION_ADDRESS, 0);

             call AmpBatteryInfo.build(batteryInfoArray + PKT_HEADER_SIZE, lastBatteryLevel); // to obtain the actual voltage value :
                                                                                  // voltage = (data * 3) / 4096;

             for( j = 0; j < BATTERY_INFO_PKT_SIZE; j++)
                 btrpkt->part[j] = batteryInfoArray[j];

             if (call BatteryInfoSender.send(AM_BROADCAST_ADDR, &pkt, sizeof(BatteryInfoPkt)) == SUCCESS){
		 busy = TRUE;
             }
         }

      }

      /*
      * The event is thrown when a new voltage level reading is ready.
      * It sets a global variable and notify there is a new voltage info ready to send.
      * 
      * @param result : indicates whether the reading process has succeed or not.
      * @param data : if the result is <code>SUCCESS</code>, it contains a consistent reading.
      *
      * @return void
      */
      /*
      event void VoltSensor.readDone(error_t result, uint16_t data) {
          lastBatteryLevel = data;
          sendBatteryReady = TRUE;
          checkAndSendBatteryInfo();
      }
      */
      

      /*
      * The function is called when a new Data Packet has to be sent.
      *
      * In detail, if there are related data (same feature on the same sensor and on its different axis),
      * it sends them together in the same Data Packet.
      *
      * @return void
      */
      void sendDataPacket() {
           DataPkt* btrpkt = (DataPkt*)(call PacketData.getPayload(&pkt, NULL));
           activeAxX = FALSE;
           activeAxY = FALSE;
           activeAxZ = FALSE;
           dataSendIndexCurr = dataSendIndex;
           feat[0] = 0; feat[1] = 0; feat[2] = 0;



           call AmpHeader.build(dataArray, AMP_VERSION, FALSE, DATA_PKT_CODE, myGroupID,
                                (uint8_t)TOS_NODE_ID, BASE_STATION_ADDRESS, sendTimerCounter);

           if (axis[dataSendIndexCurr] == AXIS_X) {
               feat[0] = call JobEngine.execute(feature[dataSendIndexCurr],
                                              call BM.getWindow(id[dataSendIndexCurr]),
                                              call BM.getWindowSize(id[dataSendIndexCurr]));
               activeAxX = TRUE;
               dataSendIndex++;


               if (dataSendIndexCurr<iJob-1 && feature[dataSendIndexCurr]==feature[dataSendIndexCurr+1] &&
                         sensor[dataSendIndexCurr]==sensor[dataSendIndexCurr+1] ) {
                      if (axis[dataSendIndexCurr+1]==AXIS_Y) {
                          feat[1] = call JobEngine.execute(feature[dataSendIndexCurr+1],
                                                         call BM.getWindow(id[dataSendIndexCurr+1]),
                                                         call BM.getWindowSize(id[dataSendIndexCurr+1]));
                          activeAxY = TRUE;
                          dataSendIndex++;

                          if (dataSendIndexCurr<iJob-2 && feature[dataSendIndexCurr]==feature[dataSendIndexCurr+2] &&
                                 sensor[dataSendIndexCurr]==sensor[dataSendIndexCurr+2] && axis[dataSendIndexCurr+2]==AXIS_Z) {
                                 feat[2] = call JobEngine.execute(feature[dataSendIndexCurr+2],
                                                                call BM.getWindow(id[dataSendIndexCurr+2]),
                                                                call BM.getWindowSize(id[dataSendIndexCurr+2]));
                                 activeAxZ = TRUE;
                                 dataSendIndex++;
                          }
                      }
                      else if (axis[dataSendIndexCurr+1]==AXIS_Z) {
                          feat[2] = call JobEngine.execute(feature[dataSendIndexCurr+1],
                                                         call BM.getWindow(id[dataSendIndexCurr+1]),
                                                         call BM.getWindowSize(id[dataSendIndexCurr+1]));
                          activeAxZ = TRUE;
                          dataSendIndex++;
                      }
               }
          } // if AXIS_X
          else if (axis[dataSendIndexCurr] == AXIS_Y) {
               feat[1] = call JobEngine.execute(feature[dataSendIndexCurr],
                                              call BM.getWindow(id[dataSendIndexCurr]),
                                              call BM.getWindowSize(id[dataSendIndexCurr]));
               activeAxY = TRUE;
               dataSendIndex++;

               if (dataSendIndexCurr<iJob-1 && feature[dataSendIndexCurr]==feature[dataSendIndexCurr+1] &&
                         sensor[dataSendIndexCurr]==sensor[dataSendIndexCurr+1] ) {
                      if(axis[dataSendIndexCurr+1]==AXIS_Z) {
                          feat[2] = call JobEngine.execute(feature[dataSendIndexCurr+1],
                                                         call BM.getWindow(id[dataSendIndexCurr+1]),
                                                         call BM.getWindowSize(id[dataSendIndexCurr+1]));
                          activeAxZ = TRUE;
                          dataSendIndex++;
                      }
               }
          } // if AXIS_Y
          else if (axis[dataSendIndexCurr] == AXIS_Z) {
               feat[2] = call JobEngine.execute(feature[dataSendIndexCurr],
                                              call BM.getWindow(id[dataSendIndexCurr]),
                                              call BM.getWindowSize(id[dataSendIndexCurr]));
               activeAxZ = TRUE;
               dataSendIndex++;
          } // if AXIS_Z

          call AmpData.build(dataArray + PKT_HEADER_SIZE, dataFeatArray, feature[dataSendIndexCurr],
                             sensor[dataSendIndexCurr],
                             activeAxX, activeAxY, activeAxZ, feat[0], feat[1], feat[2]);

          for( j2 = 0; j2 < DATA_PKT_SIZE - 6; j2++)
                 btrpkt->part[j2] = dataArray[j2];
          for( j2 = 0; j2 < 3; j2++)
                 btrpkt->feature[j2] = dataFeatArray[j2];

          if (call DataSender.send(AM_BROADCAST_ADDR, &pkt, sizeof(DataPkt)) == SUCCESS)
             busy = TRUE;
      }
      
      /*
      * This timer fires when the node can access the radio channel. 
      * It has been implemented to allow more than one node trasmit on the same radio channel
      * without the risk of mutual interferences.
      *
      * @return void
      */
      event void PostTimer.fired() {
          if (!busy) {
              call Leds.led0Toggle();
              dataSendState = TRUE;
              sendDataPacket();
          }
      }

      /*
      * The event is thrown when is time to execute the jobs (compute the features) and sends
      * over the radio the result in one or more Data Packets.
      *
      * @return void
      */
      event void SendTimer.fired() {
          //uint16_t poste = ((uint16_t)((((uint16_t)(TOS_NODE_ID - 1)) * ((uint16_t)POST_TIMER_UNIT)) + 1));
          //uint16_t poste = ((uint16_t)(TOS_NODE_ID - 1)) * ((uint16_t)POST_TIMER_UNIT);
          //call PostTimer.startOneShot(poste);
          if (TOS_NODE_ID == currSendNode && !busy) {
             call Leds.led0Toggle();
             dataSendState = TRUE;
             sendDataPacket();
          }
          
          currSendNode = (currSendNode == numMotes)? 1: (currSendNode+1);

      }

      /*
      * The function is called when occurs the need to read the voltage level and send the value
      * in a Battery Info Packet.
      *
      * @return void
      */
      void sendBatteryInfoPkt() {
          //call VoltSensor.read();
          call VoltSensor.readVolt();
          lastBatteryLevel = call VoltSensor.getVolt();
          sendBatteryReady = TRUE;
          checkAndSendBatteryInfo();
      }

      /*
      * The event is thrown when is time to read the voltage level and send the value
      * in a Battery Info Packet.
      *
      * This timer only runs when the System Coordinator ask to node for a time-based periodic request.
      *
      * @return void
      */
      event void BatteryInfoTimer.fired() {
          sendBatteryInfoPkt();
      }

      /*
      * This utility function is used to verify if there is an active packet-based periodic
      * voltage level request. If yes, check if the number of sent packets has reached the requested threshold,
      * and in case sends the Battery Info Packet.
      *
      * @return void
      */
      void batteryInfoSendCheck() {
          if (batteryPeriodicReq && !isBatteryPeriodicReqInTime) {
              sentPacketsCounter4BatteryInfo++;
              if (sentPacketsCounter4BatteryInfo == batteryInfoPeriod) {
                  sendBatteryInfoPkt();
                  sentPacketsCounter4BatteryInfo = 0;
              }
          }
      }

      /*
      * The event is thrown when a Data Packet transmission has been completed.
      * It checks if there are more Data packets to send, and if yes, it sends them.
      * Moreover checks the presence of active battery info requests.
      *
      * @return void
      */
      event void DataSender.sendDone(message_t* msg, error_t err) {
         if (&pkt == msg) {
             busy = FALSE;
             batteryInfoSendCheck();
              //call Leds.led0Toggle();
         }
         if (dataSendState) {
              if (dataSendIndex == iJob) {
                   sendTimerCounter = (sendTimerCounter+1)%255;
                   dataSendState = FALSE;
                   dataSendIndex = 0;
                   checkAndSendBatteryInfo();
              }
              else
                  sendDataPacket();
         }
      }

      /*
      * The event is thrown when a ServiceMessage Packet transmission has been completed.
      * It checks the presence of active battery info requests.
      *
      * @return void
      */
      event void ServiceMessageSender.sendDone(message_t* msg, error_t err) {
         if (&pkt == msg) {
             busy = FALSE;
             batteryInfoSendCheck();
             //call Leds.led0Toggle();
         }
      }

      /*
      * The event is thrown when a Battery Info Packet transmission has been completed.
      * It checks the presence of active battery info requests.
      *
      * @return void
      */
      event void BatteryInfoSender.sendDone(message_t* msg, error_t err) {
         if (&pkt == msg) {
             busy = FALSE;
             batteryInfoSendCheck();
             sendBatteryReady = FALSE;
             //call Leds.led0Toggle();
         }
      }

      /*
      * The event is thrown when a Service Advertisement Packet transmission has been completed.
      * It checks the presence of active battery info requests.
      *
      * @return void
      */
      event void ServiceAdvertisementSender.sendDone(message_t* msg, error_t err) {
         if (&pkt == msg) {
             busy = FALSE;
             batteryInfoSendCheck();
             //call Leds.led0Toggle();
         }
      }

      /*
      * This function insert a new job (here, 'job' is a feature computation process) 
      * in an execution queue and always hold this queue ordered 
      * (first by sensor code, then by feature code and finally by axis code). This is done to
      * simplify to optimize the transmission task, sending together related features.
      *
      * @param sens : the sensor of the new job
      * @param ft : the feature to be computed
      * @param ax : the axis of the sensor
      * @param idB : is an unique ID given by the Buffer Manager and it's referred to this job.
      *
      * @return void
      */
      void insertJob(uint8_t sens, uint8_t ft, uint8_t ax, uint8_t idB) {
          sensor[iJob] = sens;
          feature[iJob] = ft;
          axis[iJob] = ax;
          id[iJob] = idB;
          iJob++;

          for (i3 = 0; i3 < iJob - 1; i3++)
            for (j3 = 0; j3 < iJob - 1; j3++) {
                if ((sensor[j3] > sensor[j3+1]) ||
                    ((sensor[j3] == sensor[j3+1]) && (feature[j3] > feature[j3+1])) ||
                    ((sensor[j3] == sensor[j3+1]) && (feature[j3] == feature[j3+1]) && (axis[j3] > axis[j3+1]))) {
                    tmp = sensor[j3]; sensor[j3] = sensor[j3+1]; sensor[j3+1] = tmp;
                    tmp = feature[j3]; feature[j3] = feature[j3+1]; feature[j3+1] = tmp;
                    tmp = axis[j3]; axis[j3] = axis[j3+1]; axis[j3+1] = tmp;
                    tmp = id[j3]; id[j3] = id[j3+1]; id[j3+1] = tmp;
                 }
            }
      }

     /*
     * This utility function checks if the given triple is already a job in the execution queue.
     *
     * @param sens : the sensor code
     * @param ft : the feature code
     * @param ax : the axis of the sensor
     *
     * @return 'bool' : <code>TRUE</code> if the triple represent a job in the queue, 
     *                  <code>FALSE</code> otherwise
     */
     bool isJob(uint8_t sens, uint8_t ft, uint8_t ax) {
         for (i4 = 0; i4 < iJob; i4++)
           if(sens == sensor[i4] && ft == feature[i4] && ax == axis[i4])
              return TRUE;
         return FALSE;
     }

     /*
     * This function is called if some errors has occurred and there is the need to send the related info
     * in an ServiceMessage Packet.
     *
     * @param errorCode : the serviceMessage code, as specified in the AMP standard
     * @param errorDetail : the serviceMessage detail code, as specified in the AMP standard
     *
     * @return void
     */
     void sendErrorPkt(uint8_t errorCode, uint8_t errorDetail) {
        if (!busy) {
             ServiceMessagePkt* btrpkt = (ServiceMessagePkt*)(call PacketServiceMessage.getPayload(&pkt, NULL));

             call AmpHeader.build(serviceMessageArray, AMP_VERSION, FALSE, SERVICE_MESSAGE_PKT_CODE, myGroupID,
                                 (uint8_t)TOS_NODE_ID, BASE_STATION_ADDRESS, 0);
             call AmpServiceMessage.build(serviceMessageArray + PKT_HEADER_SIZE, errorCode, errorDetail);

             for( j5 = 0; j5 < SERVICE_MESSAGE_PKT_SIZE; j5++)
                 btrpkt->part[j5] = serviceMessageArray[j5];

             if (call ServiceMessageSender.send(AM_BROADCAST_ADDR, &pkt, sizeof(ServiceMessagePkt)) == SUCCESS)
		 busy = TRUE;
         }
      }
      
      /*
      * The event is thrown when the sampling timer fires. It is directly related
      * to the sampling rate. 
      *
      * At this time, there isn't supported the chance to sample from different sensors 
      * at different rate.
      *
      * @return void
      */
      event void SamplingTimer.fired() {
           //call Leds.led1Toggle();
           if (accelRequested) {
               //call STAccelerometer.readAccel();
               //accX = call STAccelerometer.getAccelX();
               //accY = call STAccelerometer.getAccelY();
               //accZ = call STAccelerometer.getAccelZ();
               call AccSensor.readAccel();
               accX = call AccSensor.getAccelX();
               accY = call AccSensor.getAccelY();
               accZ = call AccSensor.getAccelZ();

               if (accX > 0x8000)
                   accX -= 65536;
               call BM.putElem(ACCELEROMETER_CODE, AXIS_X, &accX);

               if (accY > 0x8000)
                   accY -= 65536;
               call BM.putElem(ACCELEROMETER_CODE, AXIS_Y, &accY);

               if (accZ > 0x8000)
                   accZ -= 65536;
               call BM.putElem(ACCELEROMETER_CODE, AXIS_Z, &accZ);

               if(fallDetectionPause > 0){
                  fallDetectionPause++;
                  if(fallDetectionPause == DETECTION_PAUSE_LENGHT){
                       fallDetectionActive = TRUE;
                       fallDetectionPause = 0;
                  }
               }

               if (fallDetectionActive) {
                  currentAccelReadings[0] = accX;
                  currentAccelReadings[1] = accY;
                  currentAccelReadings[2] = accZ;

                  if (call JobEngine.execute(TOTAL_ENERGY_CODE, currentAccelReadings, 3) >= FALL_THRESHOLD){
                      sendErrorPkt(EVENT_CODE, FALL_DETECTED_CODE);
                      fallDetectionPause = 1;
                      fallDetectionActive = FALSE;
                  }
               }
           }

           if (gyroRequested) {
              //call ReadGyroX.read();
              //call ReadGyroY.read();
              gyroX = 0;
              gyroY = 0;
           }
           
           if (temperatureRequested) {
              // TODO
           }

      }

     /*
     * This function is called when a 'free' node (not already registered to any Coordinator) receives
     * a Service Discovery Packet. It sends all the feature it supports and the sensors it is equipped with.
     *
     * @return void
     */
      void sendServiceAdvertisementPkt() {
         if (!busy) {
             ServiceAdvertisementPkt* btrpkt = (ServiceAdvertisementPkt*)(call PacketServiceAdvertisement.getPayload(&pkt, NULL));

             call AmpHeader.build(srvAdvArray, AMP_VERSION, FALSE, SERVICE_ADVERTISEMENT_PKT_CODE, myGroupID,
                              (uint8_t)TOS_NODE_ID, BASE_STATION_ADDRESS, 0);
             call AmpServiceAdvertisement.build(srvAdvArray + PKT_HEADER_SIZE);

             for( j6 = 0; j6 < SERVICE_ADVERTISEMENT_PKT_SIZE; j6++)
                 btrpkt->part[j6] = srvAdvArray[j6];

             if (call ServiceAdvertisementSender.send(AM_BROADCAST_ADDR, &pkt, sizeof(ServiceAdvertisementPkt)) == SUCCESS)
		 busy = TRUE;
         }
      }

     /*
     * This function is called when a Feature Activation Packet is received.
     * It checks if the request can be handled. If not, the corresponding error is sent, otherwise 
     * it asks the Buffer Manager for an available buffer and inserts the job in the execution queue.
     *
     * @return void
     */
      void activateFeature() {
         featureTemp = call AmpFeatActivation.getFeatureCode();

         if (featureTemp == TOTAL_ENERGY_CODE) {  // at this time this feature is used only to enable
                                                  // the real-time fall detector
            accelRequested = TRUE;
            fallDetectionActive = TRUE;
            return;
         }
         
         idTemp1 = -100;
         idTemp2 = -100;
         idTemp3 = -100;
         sensorTemp = call AmpFeatActivation.getSensorCode();
         windowTemp = call AmpFeatActivation.getNrSamples();
         shiftTemp = call AmpFeatActivation.getShift();
         error = FALSE;

         if (call AmpFeatActivation.isActivatedAxis0())
            if(!isJob(sensorTemp, featureTemp, AXIS_X))
                idTemp1 = call BM.bufferAllocReq(sensorTemp, AXIS_X, windowTemp, shiftTemp);
         if (call AmpFeatActivation.isActivatedAxis1())
            if(!isJob(sensorTemp, featureTemp, AXIS_Y))
                idTemp2 = call BM.bufferAllocReq(sensorTemp, AXIS_Y, windowTemp, shiftTemp);
         if (call AmpFeatActivation.isActivatedAxis2())
            if(!isJob(sensorTemp, featureTemp, AXIS_Z))
                idTemp3 = call BM.bufferAllocReq(sensorTemp, AXIS_Z, windowTemp, shiftTemp);

         if (idTemp1>-100 && idTemp1<0) {
            error = TRUE;
            sendErrorPkt(ACTIVATE_FEATURE_ERROR_CODE, -idTemp1);
         }
         if (idTemp2>-100 && idTemp2<0) {
            error = TRUE;
            sendErrorPkt(ACTIVATE_FEATURE_ERROR_CODE, -idTemp2);
         }
         if (idTemp3>-100 && idTemp3<0) {
            error = TRUE;
            sendErrorPkt(ACTIVATE_FEATURE_ERROR_CODE, -idTemp3);
         }

         if (error) {
             if(idTemp1 >= 0) call BM.bufferRelease(idTemp1);
             if(idTemp2 >= 0) call BM.bufferRelease(idTemp2);
             if(idTemp3 >= 0) call BM.bufferRelease(idTemp3);
         }
         else {
           switch (sensorTemp) {
               case ACCELEROMETER_CODE: accelRequested = TRUE; break;
               case GYROSCOPE_CODE: gyroRequested = TRUE; break;
               case TEMPERATURE_CODE: temperatureRequested = TRUE; break;
               default : break;
           }
           if (idTemp1>=0)
               insertJob(sensorTemp, featureTemp, AXIS_X, idTemp1);
           if (idTemp2>=0)
               insertJob(sensorTemp, featureTemp, AXIS_Y, idTemp2);
           if (idTemp3>=0)
               insertJob(sensorTemp, featureTemp, AXIS_Z, idTemp3);

           samplingPeriod = call AmpFeatActivation.getSamplingTime();
           //sendPeriod = (samplingPeriod * shiftTemp) / numMotes;
           //startTimers();
         }
      }

     /*
     * This function removes the given job from the execution queue. Then, it reorders this queue.
     * It is called by the  removeFeature() function.
     *
     * @param iRJ : the job position in the execution queue
     *
     * @return void
     */
      void removeJob(uint8_t iRJ) {
           for (tmp2=iRJ; tmp2<iJob-1; tmp2++){
                  sensor[tmp2] = sensor[tmp2+1] ;
                  feature[tmp2] = feature[tmp2+1];
                  axis[tmp2] = axis[tmp2+1];
                  id[tmp2] = id[tmp2+1];
           }
           iJob--;
      }

     /*
     * This function is called when a Remove Feature Packet is received.
     * It checks how many feature the Coordinator asks to remove and then releases the corresponding
     * buffers and removes the related jobs from the execution queue.
     *
     * @return void
     */
      void removeFeature() {
         deleting[0] = 0; deleting[1] = 0; deleting[2] = 0;
         toDel = 0;
         featTemp = call AmpRemoveFeature.getFeatureCode();
         sensTemp = call AmpRemoveFeature.getSensorCode();

         if (featTemp == TOTAL_ENERGY_CODE && fallDetectionActive)
             fallDetectionActive = FALSE;
         else {
             if (call AmpRemoveFeature.disableAxis0()) {
                 deleting[toDel] = AXIS_X;
                 toDel++;
             }
             if (call AmpRemoveFeature.disableAxis1()) {
                 deleting[toDel] = AXIS_Y;
                 toDel++;
             }
             if (call AmpRemoveFeature.disableAxis2()) {
                 deleting[toDel] = AXIS_Z;
                 toDel++;
             }
    
             for (i7=0; i7<toDel; i7++){
                 for (j7=0; j7<iJob; j7++)
                     if (sensor[j7] == sensTemp  &&  feature[j7] == featTemp  &&  axis[j7] == deleting[i7] ) {
                          call BM.bufferRelease(id[j7]);
                          removeJob(j7);
                     }
             }
         }
         if (iJob == 0 && !fallDetectionActive)
             stopTimers();

      }

      /*
      * This function is called when a Battery Info Request Packet is received.
      * It contains the code to handle the request.
      *
      * @return void
      */
      void batteryInfoCompute() {

          batteryInfoPeriod = call AmpBatteryInfoReq.getPeriod();
          if (batteryInfoPeriod == 0) {// request 'on-fly'
              sendBatteryInfoPkt();
          }
          else if (batteryInfoPeriod == 0x1FFF) { // 0x1FFF = '1111111111111' --> cancel
              batteryPeriodicReq = FALSE;
              if (isBatteryPeriodicReqInTime)
                 call BatteryInfoTimer.stop();
          }

          else { // periodic request
              if(batteryPeriodicReq){ // if we already have a periodic request, we cancel it
                if (isBatteryPeriodicReqInTime)
                   call BatteryInfoTimer.stop();
              }
              else batteryPeriodicReq = TRUE;

              isBatteryPeriodicReqInTime = call AmpBatteryInfoReq.isTime();
              if (isBatteryPeriodicReqInTime)
                 call BatteryInfoTimer.startPeriodic( ((uint32_t)batteryInfoPeriod) * ((uint32_t)call AmpBatteryInfoReq.getTimeScale()));
              else
                 sentPacketsCounter4BatteryInfo = 0;
          }
      }

      /*
      * The event is thrown when a Service Discovery Packet is received.
      * It contains the code to handle the request.
      *
      * @param msg : the pointer to the received packet
      * @param payload : the pointer to the payload portion of the packet
      * @param len : the payload length
      *
      * @return 'message_t*' : the pointer to the received packet
      */
      event message_t* ServiceDiscoveryReceiver.receive(message_t* msg, void* payload, uint8_t len) {
           call Leds.led2Toggle();
           call AmpHeader.parse(payload);
           call AmpServiceDiscovery.parse(payload + PKT_HEADER_SIZE);

           if (!registered ) {
              if ( !(call AmpServiceDiscovery.isStart()) && !(call AmpServiceDiscovery.isReset()) && (call AmpHeader.getDestID() == BROADCAST_ADDRESS)) {
                myGroupID = call AmpHeader.getGroupID();
                registered = TRUE;
                sendServiceAdvertisementPkt();
              }
           }
           else if (call AmpHeader.getGroupID() == myGroupID
                    && call AmpHeader.getSourceID() == BASE_STATION_ADDRESS
                    && ( (call AmpHeader.getDestID() == TOS_NODE_ID) || (call AmpHeader.getDestID() == BROADCAST_ADDRESS) )) {
                 
                 if(call AmpServiceDiscovery.isStart()) { // it can be a restart, too
                      numMotes = call AmpServiceDiscovery.getNumMotes();
                      sendPeriod = (samplingPeriod * shiftTemp) / numMotes;
                      call BM.setNumMotes(numMotes);
                      startTimers();
                 }
                 else if (call AmpServiceDiscovery.isReset()) {
                      reset();
                      registered = FALSE;
                 }
                 else { //
                      reset();
                      sendServiceAdvertisementPkt();
                 }

           }

           return msg;
      }
      
      /*
      * The event is thrown when a Feature Activation Packet is received.
      * It contains the code to handle the request.
      *
      * @param msg : the pointer to the received packet
      * @param payload : the pointer to the payload portion of the packet
      * @param len : the payload length
      *
      * @return 'message_t*' : the pointer to the received packet
      */
      event message_t* FeatureActivationReceiver.receive(message_t* msg, void* payload, uint8_t len) {
           call AmpHeader.parse(payload);

           if (registered) {
              if (call AmpHeader.getDestID() == TOS_NODE_ID && call AmpHeader.getGroupID() == myGroupID
                 && call AmpHeader.getSourceID() == BASE_STATION_ADDRESS) {
                   if (call AmpHeader.getPktType() == FEATURE_ACTIVATION_PKT_CODE) {
                     call AmpFeatActivation.parse(payload + PKT_HEADER_SIZE);
                     activateFeature();
                   }
             }
           }

           return msg;
      }
      
      /*
      * The event is thrown when a Remove Feature Packet is received.
      * It contains the code to handle the request.
      *
      * @param msg : the pointer to the received packet
      * @param payload : the pointer to the payload portion of the packet
      * @param len : the payload length
      *
      * @return 'message_t*' : the pointer to the received packet
      */
      event message_t* RemoveFeatureReceiver.receive(message_t* msg, void* payload, uint8_t len) {
           call AmpHeader.parse(payload);

           if (registered) {
             if (call AmpHeader.getDestID() == TOS_NODE_ID && call AmpHeader.getGroupID() == myGroupID
                 && call AmpHeader.getSourceID() == BASE_STATION_ADDRESS) {
                   if (call AmpHeader.getPktType() == REMOVE_FEATURE_PKT_CODE) {
                     call AmpRemoveFeature.parse(payload + PKT_HEADER_SIZE);
                     removeFeature();
                   }
             }
           }

           return msg;
      }
      
      /*
      * The event is thrown when a Battery Info Request Packet is received.
      * It contains the code to handle the request.
      *
      * @param msg : the pointer to the received packet
      * @param payload : the pointer to the payload portion of the packet
      * @param len : the payload length
      *
      * @return 'message_t*' : the pointer to the received packet
      */
      event message_t* BatteryInfoReqReceiver.receive(message_t* msg, void* payload, uint8_t len) {
           //call Leds.led2Toggle();
           call AmpHeader.parse(payload);

           if (registered) {
              if (call AmpHeader.getDestID() == TOS_NODE_ID && call AmpHeader.getGroupID() == myGroupID
                 && call AmpHeader.getSourceID() == BASE_STATION_ADDRESS) {
                   if(call AmpHeader.getPktType() == BATTERY_INFO_REQUEST_PKT_CODE) {
                      call AmpBatteryInfoReq.parse(payload + PKT_HEADER_SIZE);
                      batteryInfoCompute();
                   }
             }
           }

           return msg;
      }


}

