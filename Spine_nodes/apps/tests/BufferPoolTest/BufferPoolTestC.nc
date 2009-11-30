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
 * Test component of the Buffer Pool.
 *
 * @author Raffaele Gravina
 *
 * @version 1.0
 */
 module BufferPoolTestC {
  uses interface Boot;
  uses interface Leds;
  uses interface BufferPool;
}

implementation {

  uint16_t data1[10];
  uint16_t data2[10];
  uint16_t data3[10];

  uint8_t bufID1;
  uint8_t bufID2;
  uint8_t bufID3;
  
  uint8_t window1 = 5;
  uint8_t window2 = 2;
  uint8_t window3 = 10;
  
  uint16_t buffer1[10];
  uint16_t buffer2[10];
  uint16_t buffer3[10];

  event void Boot.booted() {
     uint8_t i;

     data1[0] = 0x9981;
     data1[1] = 0x00AB;
     data1[2] = 0x0067;
     data1[3] = 0x0034;
     data1[4] = 0x1234;
     data1[5] = 0x00D2;
     data1[6] = 0x0003;
     data1[7] = 0x0001;
     data1[8] = 0x0001;
     data1[9] = 0x00CC;

     data2[0] = 0x45CC;
     data2[1] = 0x9090;
     data2[2] = 0x10A0;
     data2[3] = 0x5634;
     data2[4] = 0x234C;
     data2[5] = 0x0034;
     data2[6] = 0x0889;
     data2[7] = 0x00FF;
     data2[8] = 0x0033;
     data2[9] = 0x00DC;

     data3[0] = 0x0CCA;
     data3[1] = 0x0012;
     data3[2] = 0x0547;
     data3[3] = 0xBBCD;
     data3[4] = 0xFC00;
     data3[5] = 0x0B67;
     data3[6] = 0x0FCC;
     data3[7] = 0x0034;
     data3[8] = 0x0900;
     data3[9] = 0xF400;

     bufID1 = call BufferPool.getAvailableBuffer();
     bufID2 = call BufferPool.getAvailableBuffer();
     bufID3 = call BufferPool.getAvailableBuffer();

     //if (bufID1==0 && bufID2==1 && bufID3==2) call Leds.led0Toggle();      // OK

     //if (call BufferPool.getBufferPoolSize() == 3) call Leds.led0Toggle(); // OK
     //if (call BufferPool.getBufferSize(0) == 10) call Leds.led1Toggle();   // OK

     for (i=0; i<10; i++) {
        call BufferPool.putElem(bufID1, data1[i]);
        call BufferPool.putElem(bufID3, data3[i]);
        call BufferPool.putElem(bufID2, data2[i]);
     }
     call BufferPool.putElem(bufID1, 0xF400);
     call BufferPool.putElem(bufID2, 0x4577);
     call BufferPool.putElem(bufID2, 0x4578);
     call BufferPool.putElem(bufID1, 0xF800);
     call BufferPool.putElem(bufID3, 0xF800);

     call BufferPool.getData(bufID2, window2, buffer2);
     call BufferPool.getData(bufID3, window3, buffer3);
     call BufferPool.getData(bufID1, window1, buffer1);

     if (buffer1[0]==0xF800 && buffer1[1]==0xF400 && buffer1[2]==data1[9] && buffer1[3]==data1[8] && buffer1[4]==data1[7]) call Leds.led0On();
     if (buffer2[0]==0x4578 && buffer2[1]==0x4577) call Leds.led1On();

     if (buffer3[0]==0xF800 && buffer3[1]==data3[9] && buffer3[2]==data3[8] && buffer3[3]==data3[7] && buffer3[4]==data3[6] &&
         buffer3[5]==data3[5] && buffer3[6]==data3[4] && buffer3[7]==data3[3] && buffer3[8]==data3[2] && buffer3[9]==data3[1]) call Leds.led2On();
  }

  event void BufferPool.newElem(uint8_t bufferID, uint16_t elem) {}

}

