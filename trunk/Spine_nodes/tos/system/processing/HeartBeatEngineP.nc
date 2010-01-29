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
 * Module component of the SPINE HeartBeat Engine.
 *
 *
 * @author Alessandro Andreoli
 * @author Raffaele Gravina
 *
 * @version 1.3
 */

#include <UserButton.h>

#ifndef SIZE
#define SIZE 20
#endif

#ifndef REFRESH_RATE
#define REFRESH_RATE 5
#endif

module HeartBeatEngineP {
	
	provides {
		interface Function;
	}

	uses {
		interface Boot;
		interface FunctionManager;
		interface Notify<button_state_t>;
		
		interface LocalTime<TMilli>;

                interface Leds;
	}
}

implementation {

	bool registered = FALSE;
	bool setup, active = FALSE;
        bool start = FALSE;

        bool startTimeValued = FALSE;

	bool bufferFull = FALSE;
        uint32_t times[SIZE];
        uint8_t timesIndex = 0;

        uint32_t time;
        
        uint16_t startTime = 0;

        uint8_t msg[2];


	event void Boot.booted() {
		if (!registered) {
			call FunctionManager.registerFunction(HEART_BEAT);
			registered = TRUE;
		}
	}
	
	
	command bool Function.setUpFunction(uint8_t* functionParams, uint8_t functionParamsSize) {
		setup = TRUE;
                return TRUE;
	}
	
	command bool Function.activateFunction(uint8_t* functionParams, uint8_t functionParamsSize) {
                active = TRUE;
                call Notify.enable();
                return TRUE;
	}

	command bool Function.disableFunction(uint8_t* functionParams, uint8_t functionParamsSize) {
		active = FALSE;
		call Notify.disable();
                return TRUE;
	}

	command uint8_t* Function.getSubFunctionList(uint8_t* functionCount) {
		*functionCount = 0;
		return NULL;
	}

	command void Function.startComputing() {
		// enables INTERRUPT
                call Notify.enable();
                start = TRUE;
	}
	
	command void Function.stopComputing() {
		// disables INTERRUPT
		call Notify.disable();
                start = FALSE;
	}
	
	task void computeBPM() {
                uint8_t i;
                uint16_t avgBPM = 0;

                times[timesIndex++] = time;
                timesIndex %= SIZE;

                if(timesIndex == 0)
                   bufferFull = TRUE;

                if (bufferFull) {
                   for(i=0; i<SIZE; i++)
                      avgBPM += times[i];

                   avgBPM /= SIZE;

                   avgBPM = (0x0F000/avgBPM); // 0x0F000 = 60 * 1024 = 1 min .... 1024 ticks = 1 sec

                   msg[0] = avgBPM>>8;
                   msg[1] = avgBPM;

                   if((timesIndex %= REFRESH_RATE) == 0)
                      call FunctionManager.send(HEART_BEAT, msg, 2);
                }
        }
	

	event void Notify.notify( button_state_t state ) {
                if ( state == BUTTON_RELEASED ) {
                   
                   if(startTimeValued) {
                      time = (call LocalTime.get()-startTime);
                      startTimeValued = FALSE;
                   }
                   else {
                      startTime = call LocalTime.get();
                      startTimeValued = TRUE;
                   }
                   
                   call Leds.led1On();
                   
                   post computeBPM();

                } else if ( state == BUTTON_PRESSED )
                   call Leds.led1Off();
        }
        
        event void FunctionManager.sensorWasSampledAndBuffered(enum SensorCode sensorCode) {}

}

