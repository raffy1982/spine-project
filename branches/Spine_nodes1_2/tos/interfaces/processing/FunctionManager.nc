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
 * Interface of the Function Manager. Each specific function implementation must register itself to the FunctionManager at boot time.
 * This component allows the retrieval of the Function list.
 *
 * @author Raffaele Gravina
 * @author Philip Kuryloski
 *
 * @version 1.0
 */

#include "Functions.h"
#include "SensorsConstants.h"

interface FunctionManager {
	
	/**
	 * Used by the specific function managers to register their functions.
	 * The function code will be placed in a list that is eventually used by the SPINE Application to send the Service Advertisement.
	 *
	 * @param 'functionCode' the code of the function to register
	 *
	 * @return
	 */
	command error_t registerFunction(enum FunctionCodes functionCode);
	
	/**
	 *
	 *
	 * @param
	 *
	 * @return
	 */
	command uint8_t* getFunctionList(uint8_t* functionsCount);
	
	/**
	 *
	 *
	 * @param
	 *
	 * @return
	 */
	command bool setUpFunction(enum FunctionCodes functionCode, uint8_t* functionParams, uint8_t functionParamsSize);
	
	/**
	 *
	 *
	 * @param
	 *
	 * @return
	 */
	command bool activateFunction(enum FunctionCodes functionCode, uint8_t* functionParams, uint8_t functionParamsSize);
	
	/**
	 *
	 *
	 * @param
	 *
	 * @return
	 */
	command bool disableFunction(enum FunctionCodes functionCode, uint8_t* functionParams, uint8_t functionParamsSize);
	
	/**
	 *
	 *
	 * @return 'void'
	 */
	command void startComputing();
	
	/**
	 *
	 *
	 * @return 'void'
	 */
	command void stopComputing();
	
	/**
	 *
	 *
	 * @param
	 *
	 * @return
	 */
	command void send(enum FunctionCodes functionCode, uint8_t* functionData, uint8_t len);
	
	command void reset();
	
	/**
	 * lets the function manager (and therefore registered functions) know when another sample has been taken to allow the triggering of feature calculation
	 *
	 * @param sensorCode
	 */
	event void sensorWasSampled(enum SensorCode sensorCode);
}




