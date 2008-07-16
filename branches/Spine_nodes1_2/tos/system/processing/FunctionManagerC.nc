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
 * Configuration Component of the Function Manager. Each specific function implementation must register itself to the FunctionManager at boot time.
 * This component allows the retrieval of the Function list.
 *
 * @author Raffaele Gravina
 *
 * @version 1.0
 */
 
 #include "Functions.h"

 configuration FunctionManagerC {
     provides interface FunctionManager;
     
     uses interface Function as Functions[uint8_t functionID];
 }

 implementation {
     components PacketManagerC, FunctionManagerP;

components FeatureEngineC;

     FunctionManager = FunctionManagerP;

     FunctionManagerP.PacketManager -> PacketManagerC;

     FunctionManagerP.Functions = Functions;
     FunctionManagerP.Functions[FEATURE] -> FeatureEngineC;
 }
