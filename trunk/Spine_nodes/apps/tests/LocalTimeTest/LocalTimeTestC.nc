/*****************************************************************
SPINE - Signal Processing In-Node Environment is a framework that
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
 * Test component of the Radio Controller.
 *
 * @author Raffaele Gravina
 *
 * @version 1.0
 */
 #include "printf.h"
 #include "SensorsConstants.h"
 module LocalTimeTestC {
  uses interface Boot;
  uses interface Leds;

  //uses interface RadioController;

  uses interface AMSend as Sender;
  uses interface SplitControl as Radio;

  uses interface Timer<TMilli>;
  
  /*uses {
    interface Sort;
    interface MathUtils;
    interface PacketManager;
    interface InPacket as StpSensPkt;
    interface InPacket as StpFuncPkt;
    interface InPacket as FunReqPkt;
    interface InPacket as StartPkt;
    interface OutPacket as DataPkt;
    interface OutPacket as SvcMsgPkt;
    interface OutPacket as SvcAdvPkt;
    interface SpineHeader;
    interface Feature as Range;
    interface Feature as Rms;
    interface Feature as Ampl;
    interface Feature as StDev;
    interface Feature as Var;
    interface Feature as Mean;
    interface Feature as TotEn;
    interface Feature as PitchRoll;
    interface Feature as VectMagn;
    interface Feature as Max;
    interface Feature as Min;
    interface Feature as Mode;
    interface Feature as Median;
    interface Feature as Raw;
    interface Function as FeatureEng;
    interface Sensor as Acc;
    interface Sensor as Temp;
    interface SensorBoardController;
  }*/

  //uses interface LocalTime<TMilli>;   // ***  FOR MILLI SEC ***
  uses interface Counter<T32khz, uint32_t> as LocalTime;   // ***  FOR USING OSCILLATOR ***
  //uses interface Counter<TMicro, uint16_t> as LocalTime;   // ***  FOR MICRO SEC ***
}

implementation {

  uint32_t timeAfter;
  uint32_t timeBefore;
  uint32_t delay;
  uint8_t timeBuf[sizeof(uint32_t)];

  uint16_t LENGTH = 200;
  uint16_t v[200];
  
  uint8_t v8[200];
  uint8_t tmp;
  
  uint16_t dataMatrix[200][4];     
  
  message_t msg;
  
  uint16_t voltCount = 0;
  
  event void Radio.startDone(error_t res) {
       call Leds.led1On();
  }

  event void Radio.stopDone(error_t res) {
       call Leds.led1Off();
  }

  event void Boot.booted() {

    uint16_t i, j;

    for (i=0; i<LENGTH; i++)
       v[i] = LENGTH-i;       

    for(j=0; j<4; j++)
      for(i=0; i<LENGTH; i++)
         dataMatrix[i][j] = LENGTH-i;

    //v8[0] = 4;
    //v8[1] = sizeof v8;
    //v8[21] = 20;

    //v8[0] = 1;
    //v8[1] = 40;
    //v8[2] = 20;
    
    v8[0] = 1;
    v8[1] = 7;
    v8[3] = 1;
    v8[4] = 0x0E;
    v8[5] = 2;
    v8[6] = 0x0E;
    v8[7] = 3;
    v8[8] = 0x0E;
    v8[9] = 4;
    v8[10] = 0x0E;
    v8[11] = 5;
    v8[12] = 0x0E;
    v8[13] = 6;
    v8[14] = 0x0E;
    v8[15] = 7;
    v8[16] = 0x0E;
    
    v8[17] = 0x01;
    v8[18] = 0x02;
    v8[19] = 0x03;
    v8[20] = 0x04;
    v8[21] = 0x05;
    v8[22] = 0x06;
    v8[23] = 0x07;
    v8[24] = 0x08;
    v8[25] = 0x09;
    v8[26] = 0x00;
    v8[27] = 0xFF;

    //call SensorBoardController.setSamplingTime(ACC_SENSOR, 32);
    //call SensorBoardController.setSamplingTime(VOLTAGE_SENSOR, 3200);
    //call SensorBoardController.setSamplingTime(GYRO_SENSOR, 25);
    //call SensorBoardController.setSamplingTime(INTERNAL_TEMPERATURE_SENSOR, 32);

    //call SensorBoardController.startSensing();

    //call FeatureEng.activateFunction(v8, 17);
    
    memcpy(call Sender.getPayload(&msg, 28), v8, 28);
    
    call Radio.start();
    call Timer.startPeriodic(0x00000400);
  }

  event void Timer.fired() {

     timeBefore = call LocalTime.get();

        // RADIO START-UP TIME: 2.685ms
        // RADIO SHUT-DOWN TIME: 0.244ms
        
        //call Sender.send(0xFFFF, &msg, 1);                                          // from 3ms up to 16ms - mean 7.89ms ( over > 1300 tests )
        call Sender.send(0xFFFF, &msg, 28);                                         // from 5.13ms up to 24.26ms - mean 10.07ms ( over > 2500 tests )
        //call Sort.mergeSort(v, LENGTH, 0, LENGTH-1);                                // for LENGTH=200, 23.28ms - for LENGTH=100, 10.62ms - for LENGTH=50, 4.88ms
        //call Sort.selectionSort(v, LENGTH);                                         // for LENGTH=200, 89ms - for LENGTH=100, 22.37ms - for LENGTH=50, 5.80ms
        //call Sort.insertionSort(v, LENGTH);                                         // for LENGTH=200, 1.25ms - for LENGTH=100, 0.64ms - for LENGTH=50, 0.33ms
        //call Sort.bubbleSort(v, LENGTH);                                            // for LENGTH=200, 145.05ms - for LENGTH=100, 36.13ms - for LENGTH=50, 8.88ms
        //call MathUtils.isqrt(0x773593E5);                                           // with 0x773593E5 (a prime nr), 0.8ms - with 0x3F5, 0.46ms
        //call MathUtils.max(v, LENGTH);                                              // for LENGTH=200, 0.6ms - for LENGTH=100, 0.31ms - for LENGTH=50, 0.18ms
        //call MathUtils.min(v, LENGTH);                                              // for LENGTH=200, 0.71ms - for LENGTH=100, 0.39ms - for LENGTH=50, 0.21ms
        //call MathUtils.mean(v, LENGTH);                                             // for LENGTH=200, 0.92ms - for LENGTH=100, 0.55ms - for LENGTH=50, 0.36ms
        //call MathUtils.variance(v, LENGTH);                                         // for LENGTH=200, 13.67ms - for LENGTH=100, 6.81ms - for LENGTH=50, 3.48ms
        //call RadioController.send(0xFFFF, DATA, v, 1);                              // with RADIO=ON, 1.343ms - with RADIO=OFF, 0.244ms
        //call RadioController.send(0xFFFF, DATA, v, 28);                             // with RADIO=ON, 2.533ms - with RADIO=OFF, 0.305ms
        //call PacketManager.build(DATA, v8, 17);                                     // with no fragmentation, 0.52ms
        //call PacketManager.build(DATA, v8, sizeof v8);                              // with 2 frags, 1.22ms
        //call DataPkt.build(v8, 34, &tmp);                                           // 0.092ms - with 200bytes of data, 0.3ms
        //call SvcMsgPkt.build(v8, 3, &tmp);                                          // 0.031ms
        //call SvcAdvPkt.build(v8, sizeof v8, &tmp);                                  // 0.21ms
        //call SpineHeader.build(2, FALSE, DATA, 0xAB, 0x0001, 0xFFFF, 0, 1, 1);      // 0.031ms
        //call SpineHeader.parse(v8);                                                 // 0.092ms
        //call StpSensPkt.parse(v8, 3);                                               // 0.062ms
        //call StpFuncPkt.parse(v8, 19);                                              // 0.092ms
        //call FunReqPkt.parse(v8, 19);                                               // 0.092ms
        //call FunReqPkt.parse(v8, 4);                                                // 0.062ms
        //call Range.calculate((int16_t **)dataMatrix, 0x0E, LENGTH, &tmp);           // for LENGTH=200, 3.05ms - for LENGTH=100, 1.68ms - for LENGTH=50, 0.88ms    // 0x0E=CH1_CH2_CH3
        //call Raw.calculate((int16_t **)dataMatrix, 0x0E, LENGTH, &tmp);             // for LENGTH=200, 0.062ms - for LENGTH=100, 0.062ms - for LENGTH=50, 0.062ms
        //call Max.calculate((int16_t **)dataMatrix, 0x0E, LENGTH, &tmp);             // for LENGTH=200, 1.67ms - for LENGTH=100, 0.88ms - for LENGTH=50, 0.49ms
        //call Min.calculate((int16_t **)dataMatrix, 0x0E, LENGTH, &tmp);             // for LENGTH=200, 2.01ms - for LENGTH=100, 1.16ms - for LENGTH=50, 0.49ms
        //call Mean.calculate((int16_t **)dataMatrix, 0x0E, LENGTH, &tmp);            // for LENGTH=200, 2.68ms - for LENGTH=100, 1.65ms - for LENGTH=50, 1.1ms
        //call Median.calculate((int16_t **)dataMatrix, 0x0E, LENGTH, &tmp);          // for LENGTH=200, 75.75ms - for LENGTH=100, 34.36ms - for LENGTH=50, 15.56ms
        //call Ampl.calculate((int16_t **)dataMatrix, 0x0E, LENGTH, &tmp);            // for LENGTH=200, 4.36ms - for LENGTH=100, 2.47ms - for LENGTH=50, 1.52ms
        //call Rms.calculate((int16_t **)dataMatrix, 0x0E, LENGTH, &tmp);             // for LENGTH=200, 28.80ms - for LENGTH=100, 18.61ms - for LENGTH=50, 9.43ms
        //call TotEn.calculate((int16_t **)dataMatrix, 0x0E, LENGTH, &tmp);           // for LENGTH=200, 116.61ms - for LENGTH=100, 61.03ms - for LENGTH=50, 30.76ms (we can do better, look at VectMagn!)
        //call Var.calculate((int16_t **)dataMatrix, 0x0E, LENGTH, &tmp);             // for LENGTH=200, 51.66ms - for LENGTH=100, 14.92ms - for LENGTH=50, 15.62ms
        //call StDev.calculate((int16_t **)dataMatrix, 0x0E, LENGTH, &tmp);           // for LENGTH=200, 53.70ms - for LENGTH=100, 16.80ms - for LENGTH=50, 16.99ms
        //call Mode.calculate((int16_t **)dataMatrix, 0x0E, LENGTH, &tmp);            // for LENGTH=200, 84.10ms - for LENGTH=100, 38.69ms - for LENGTH=50, 17.76ms (we can do better!)
        //call VectMagn.calculate((int16_t **)dataMatrix, 0x0E, LENGTH, &tmp);        // for LENGTH=200, 4.58ms - for LENGTH=100, 2.89ms - for LENGTH=50, 2.44ms
        //call PitchRoll.calculate((int16_t **)dataMatrix, 0x0E, LENGTH, &tmp);       // for LENGTH=200, 19.53ms - for LENGTH=100, 18.37ms - for LENGTH=50, 17.90ms
        //call Acc.acquireData(ALL);                                                  // 1.68ms
        //call Temp.acquireData(ALL);                                                 // 0.092ms
        // ACTUAL READING TIME: Accelerometer: 1.68ms - Li-Ion Voltage Sensor: 1.1ms - Internal Temperature Sensor: 17.48ms - Internal Voltage Sensor: 17.48ms
        //call SensorBoardController.setSamplingTime(ACC_SENSOR, 25);                 // 0.062ms
        //call SensorBoardController.startSensing();                                  // with 4 sensors set, 0.427ms
        //call FeatureEng.setUpFunction(v8, 3);                                       // 0.062ms
        //call FeatureEng.activateFunction(v8, 17);                                   // 0.062ms
        //call FeatureEng.disableFunction(v8, 17);                                    // 0.092ms
     
     /*timeAfter = call LocalTime.get();

     delay = timeAfter - timeBefore;

     printf("msg tx time: %ld\n", delay);
     printfflush();*/
  }
  
  event void Sender.sendDone(message_t* mesg, error_t error) {
     timeAfter = call LocalTime.get();

     delay = timeAfter - timeBefore;

     printf("msg tx time: %ld\n", delay);
     printfflush();
  }

  //event void RadioController.radioOn() { call Timer.startPeriodic(0x00000400);   //1sec }
  //event void RadioController.receive(uint16_t source, enum PacketTypes pktType, void* payload, uint8_t len) {}
  //event void PacketManager.messageReceived(enum PacketTypes pktType){}
  //event void Acc.acquisitionDone(error_t result, int8_t resultCode) {}
  //event void Temp.acquisitionDone(error_t result, int8_t resultCode) {}
  /*event void SensorBoardController.acquisitionDone(enum SensorCode sensorCode, error_t result, int8_t resultCode) {
    if(sensorCode == VOLTAGE_SENSOR) {
       voltCount++;
       if(voltCount == 32) {
          voltCount = 0;
          call Leds.led1Toggle();
       }
    }
  } */
  
  async event void LocalTime.overflow() { call Leds.led1Toggle(); }

}

