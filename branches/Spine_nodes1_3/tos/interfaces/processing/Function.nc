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
 * Interface of the generic SPINE Function.
 *
 *
 * @author Raffaele Gravina
 *
 * @version 1.2
 */
 
 interface Function {

       /**
       * Setup a function with the given parameters array of size 'functionParamsSize'
       *
       * @param  functionParams the setup parameter array
       * @param  functionParamsSize the size of the setup parameter array
       *
       * @return TRUE is the setup has succeeded, FALSE otherwise
       */
       command bool setUpFunction(uint8_t* functionParams, uint8_t functionParamsSize);
       
       /**
       * Activates a function with the given parameters array of size 'functionParamsSize'
       *
       * @param  functionParams the activation parameter array
       * @param  functionParamsSize the size of the activation parameter array
       *
       * @return TRUE is the activation has succeeded, FALSE otherwise
       */
       command bool activateFunction(uint8_t* functionParams, uint8_t functionParamsSize);
       
       /**
       * Disables a function with the given parameters array of size 'functionParamsSize'
       *
       * @param  functionParams the deactivation parameter array
       * @param  functionParamsSize the size of the deactivation parameter array
       *
       * @return TRUE is the deactivation has succeeded, FALSE otherwise
       */
       command bool disableFunction(uint8_t* functionParams, uint8_t functionParamsSize);
       
       /**
       * Returns the list of the registered libraries to the function
       *
       * @param functionCount the number of registered libraries of the function
       *
       * @return the pointer to the list of registered libraries to the function or NULL if the Function doesn't have any libraries
       */
       command uint8_t* getSubFunctionList(uint8_t* functionCount);
       
       /**
       * Starts the computing of the function
       *
       * @return void
       */
       command void startComputing();
       
       /**
       * Stops the computing of the function
       *
       * @return void
       */
       command void stopComputing();

 }




