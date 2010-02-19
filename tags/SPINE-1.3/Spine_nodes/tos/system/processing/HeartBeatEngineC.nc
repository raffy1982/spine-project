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
 * Configuration component of the SPINE HeartBeat Engine.
 *
 *
 * @author Alessandro Andreoli
 * @author Raffaele Gravina
 *
 * @version 1.3
 */

configuration HeartBeatEngineC {
	
        provides interface Function;

}

implementation {
	components MainC, FunctionManagerC, HeartBeatEngineP;

        components HplMsp430InterruptC as InterruptC;
        components new Msp430InterruptC() as HBInterrupt;
        HBInterrupt.HplInterrupt -> InterruptC.Port27;
        HeartBeatEngineP.GpioInterrupt -> HBInterrupt.Interrupt;

	components LocalTimeMilliC as LocalTime;
	//components Counter32khz32C as LocalTime;
	HeartBeatEngineP.LocalTime -> LocalTime;

	components LedsC;
	HeartBeatEngineP.Leds -> LedsC;

	HeartBeatEngineP.Function = Function;

	HeartBeatEngineP.FunctionManager -> FunctionManagerC;

	HeartBeatEngineP.Boot -> MainC.Boot;
}
