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
 * Interface of the Function Engine. Each specific feature implementation must register itself to the FeatureEngine at boot time.
 *
 * @author Raffaele Gravina
 * @author Philip Kuryloski
 *
 * @version 1.2
 */
 
#include "Functions.h"

interface FeatureEngine {

	/**
	* Registers a new feature. This command must be called by each SPINE feature at boot time to allow the inclusion of that feature
	* among the service advertisement message.
	*
	* @param FeatureCode the code of the feature to register
	*
	* @return
	*/
	command error_t registerFeature(enum FeatureCodes featureCode);
	 
}




