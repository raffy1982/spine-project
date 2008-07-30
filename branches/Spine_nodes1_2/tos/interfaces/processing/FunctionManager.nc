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
	* Registers a new function. This command must be called by each SPINE function at boot time to allow the inclusion of that function
	* among the service advertisement message.
	*
	* @param 'functionCode' the code of the function to register
	*
	* @return 'error_t' SUCCESS if the registration has success; FAIL otherwise
	*/
	command error_t registerFunction(enum FunctionCodes functionCode);
	
	/**
        * Returns the list of the libraries of all the registered function
        *
        * @param 'functionCount' the number of registered libraries
        *
        * @return the pointer to the list of registered libraries
        */
	command uint8_t* getFunctionList(uint8_t* functionsCount);
	
	/**
        * Setup the given function with the given parameters array of size 'functionParamsSize'
        *
        * @param  'functionCode' the code of the function to be disabled
        * @param  'functionParams' the setup parameter array
        * @param  'functionParamsSize' the size of the setup parameter array
        *
        * @return TRUE is the setup has succeeded, FALSE otherwise
        */
	command bool setUpFunction(enum FunctionCodes functionCode, uint8_t* functionParams, uint8_t functionParamsSize);
	
	/**
        * Activates the given function with the given parameters array of size 'functionParamsSize'
        *
        * @param  'functionCode' the code of the function to be disabled
        * @param  'functionParams' the activation parameter array
        * @param  'functionParamsSize' the size of the deactivation parameter array
        *
        * @return TRUE is the activation has succeeded, FALSE otherwise
        */
	command bool activateFunction(enum FunctionCodes functionCode, uint8_t* functionParams, uint8_t functionParamsSize);
	
	/**
        * Disables the given function with the given parameters array of size 'functionParamsSize'
        *
        * @param  'functionCode' the code of the function to be disabled
        * @param  'functionParams' the deactivation parameter array
        * @param  'functionParamsSize' the size of the deactivation parameter array
        *
        * @return TRUE is the deactivation has succeeded, FALSE otherwise
        */
	command bool disableFunction(enum FunctionCodes functionCode, uint8_t* functionParams, uint8_t functionParamsSize);
	
	/**
        * Starts the computing of all the registered functions
        *
        * @return void
        */
	command void startComputing();
	
	/**
        * Stops the computing of all the registered functions
        *
        * @return void
        */
	command void stopComputing();
	
	/**
	* This command can be used by the functions that need to send their results over the air
	*
	* @param 'functionCode' the code of the function that is asking the Ota send
	* @param 'functionData' the function result data to be sent
	* @param 'len' the length of the function result data array
	*
	* @return  void
	*/
	command void send(enum FunctionCodes functionCode, uint8_t* functionData, uint8_t len);

        /**
        * Resets the state of the Function Manager. 
        * It also calls the 'reset' to all the registered functions.
        *
        * @return void
        */
	command void reset();

	/**
	* Lets the function manager (and therefore registered functions) know when another sample has been taken
        * to allow the triggering of feature calculation
	*
	* @param the sensor that has been sampled
	*/
	event void sensorWasSampled(enum SensorCode sensorCode);
}




