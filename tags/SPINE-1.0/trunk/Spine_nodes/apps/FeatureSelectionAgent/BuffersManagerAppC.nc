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
 * Implementation of the Buffer Manager. This is an ad-hoc component, developed for the 
 * Feature Selection Agent application, that handles (initializes, allocates, fills and releases)
 * the 50 buffers needed by the appplication.
 *
 * @author Raffaele Gravina
 * @author Antonio Guerrieri
 *
 * @version 1.0
 */

#include "FeatureSelectionAgent.h"

configuration BuffersManagerAppC {
     provides interface BuffersManager;
}

implementation {
     components MainC;
     components LedsC;

     components BuffersManagerC as BM;

     components new GenericSharedCircularBufferC(SAMPLE_SIZE) as BAccX01;
     components new GenericSharedCircularBufferC(SAMPLE_SIZE) as BAccX02;
     components new GenericSharedCircularBufferC(SAMPLE_SIZE) as BAccX03;
     components new GenericSharedCircularBufferC(SAMPLE_SIZE) as BAccX04;
     components new GenericSharedCircularBufferC(SAMPLE_SIZE) as BAccX05;
     components new GenericSharedCircularBufferC(SAMPLE_SIZE) as BAccX06;
     components new GenericSharedCircularBufferC(SAMPLE_SIZE) as BAccX07;
     components new GenericSharedCircularBufferC(SAMPLE_SIZE) as BAccX08;
     components new GenericSharedCircularBufferC(SAMPLE_SIZE) as BAccX09;
     components new GenericSharedCircularBufferC(SAMPLE_SIZE) as BAccX10;
     
     components new GenericSharedCircularBufferC(SAMPLE_SIZE) as BAccY01;
     components new GenericSharedCircularBufferC(SAMPLE_SIZE) as BAccY02;
     components new GenericSharedCircularBufferC(SAMPLE_SIZE) as BAccY03;
     components new GenericSharedCircularBufferC(SAMPLE_SIZE) as BAccY04;
     components new GenericSharedCircularBufferC(SAMPLE_SIZE) as BAccY05;
     components new GenericSharedCircularBufferC(SAMPLE_SIZE) as BAccY06;
     components new GenericSharedCircularBufferC(SAMPLE_SIZE) as BAccY07;
     components new GenericSharedCircularBufferC(SAMPLE_SIZE) as BAccY08;
     components new GenericSharedCircularBufferC(SAMPLE_SIZE) as BAccY09;
     components new GenericSharedCircularBufferC(SAMPLE_SIZE) as BAccY10;
     
     components new GenericSharedCircularBufferC(SAMPLE_SIZE) as BAccZ01;
     components new GenericSharedCircularBufferC(SAMPLE_SIZE) as BAccZ02;
     components new GenericSharedCircularBufferC(SAMPLE_SIZE) as BAccZ03;
     components new GenericSharedCircularBufferC(SAMPLE_SIZE) as BAccZ04;
     components new GenericSharedCircularBufferC(SAMPLE_SIZE) as BAccZ05;
     components new GenericSharedCircularBufferC(SAMPLE_SIZE) as BAccZ06;
     components new GenericSharedCircularBufferC(SAMPLE_SIZE) as BAccZ07;
     components new GenericSharedCircularBufferC(SAMPLE_SIZE) as BAccZ08;
     components new GenericSharedCircularBufferC(SAMPLE_SIZE) as BAccZ09;
     components new GenericSharedCircularBufferC(SAMPLE_SIZE) as BAccZ10;
     
     components new GenericSharedCircularBufferC(SAMPLE_SIZE) as BGyroX01;
     components new GenericSharedCircularBufferC(SAMPLE_SIZE) as BGyroX02;
     components new GenericSharedCircularBufferC(SAMPLE_SIZE) as BGyroX03;
     components new GenericSharedCircularBufferC(SAMPLE_SIZE) as BGyroX04;
     components new GenericSharedCircularBufferC(SAMPLE_SIZE) as BGyroX05;
     components new GenericSharedCircularBufferC(SAMPLE_SIZE) as BGyroX06;
     components new GenericSharedCircularBufferC(SAMPLE_SIZE) as BGyroX07;
     components new GenericSharedCircularBufferC(SAMPLE_SIZE) as BGyroX08;
     components new GenericSharedCircularBufferC(SAMPLE_SIZE) as BGyroX09;
     components new GenericSharedCircularBufferC(SAMPLE_SIZE) as BGyroX10;

     components new GenericSharedCircularBufferC(SAMPLE_SIZE) as BGyroY01;
     components new GenericSharedCircularBufferC(SAMPLE_SIZE) as BGyroY02;
     components new GenericSharedCircularBufferC(SAMPLE_SIZE) as BGyroY03;
     components new GenericSharedCircularBufferC(SAMPLE_SIZE) as BGyroY04;
     components new GenericSharedCircularBufferC(SAMPLE_SIZE) as BGyroY05;
     components new GenericSharedCircularBufferC(SAMPLE_SIZE) as BGyroY06;
     components new GenericSharedCircularBufferC(SAMPLE_SIZE) as BGyroY07;
     components new GenericSharedCircularBufferC(SAMPLE_SIZE) as BGyroY08;
     components new GenericSharedCircularBufferC(SAMPLE_SIZE) as BGyroY09;
     components new GenericSharedCircularBufferC(SAMPLE_SIZE) as BGyroY10;
     
     BM.Boot -> MainC;
     BM.Leds -> LedsC;

     BuffersManager = BM;
     
     BM.BAccX01 -> BAccX01;
     BM.BAccX02 -> BAccX02;
     BM.BAccX03 -> BAccX03;
     BM.BAccX04 -> BAccX04;
     BM.BAccX05 -> BAccX05;
     BM.BAccX06 -> BAccX06;
     BM.BAccX07 -> BAccX07;
     BM.BAccX08 -> BAccX08;
     BM.BAccX09 -> BAccX09;
     BM.BAccX10 -> BAccX10;
     
     BM.BAccY01 -> BAccY01;
     BM.BAccY02 -> BAccY02;
     BM.BAccY03 -> BAccY03;
     BM.BAccY04 -> BAccY04;
     BM.BAccY05 -> BAccY05;
     BM.BAccY06 -> BAccY06;
     BM.BAccY07 -> BAccY07;
     BM.BAccY08 -> BAccY08;
     BM.BAccY09 -> BAccY09;
     BM.BAccY10 -> BAccY10;
     
     BM.BAccZ01 -> BAccZ01;
     BM.BAccZ02 -> BAccZ02;
     BM.BAccZ03 -> BAccZ03;
     BM.BAccZ04 -> BAccZ04;
     BM.BAccZ05 -> BAccZ05;
     BM.BAccZ06 -> BAccZ06;
     BM.BAccZ07 -> BAccZ07;
     BM.BAccZ08 -> BAccZ08;
     BM.BAccZ09 -> BAccZ09;
     BM.BAccZ10 -> BAccZ10;
     
     BM.BGyroX01 -> BGyroX01;
     BM.BGyroX02 -> BGyroX02;
     BM.BGyroX03 -> BGyroX03;
     BM.BGyroX04 -> BGyroX04;
     BM.BGyroX05 -> BGyroX05;
     BM.BGyroX06 -> BGyroX06;
     BM.BGyroX07 -> BGyroX07;
     BM.BGyroX08 -> BGyroX08;
     BM.BGyroX09 -> BGyroX09;
     BM.BGyroX10 -> BGyroX10;

     BM.BGyroY01 -> BGyroY01;
     BM.BGyroY02 -> BGyroY02;
     BM.BGyroY03 -> BGyroY03;
     BM.BGyroY04 -> BGyroY04;
     BM.BGyroY05 -> BGyroY05;
     BM.BGyroY06 -> BGyroY06;
     BM.BGyroY07 -> BGyroY07;
     BM.BGyroY08 -> BGyroY08;
     BM.BGyroY09 -> BGyroY09;
     BM.BGyroY10 -> BGyroY10;
}
