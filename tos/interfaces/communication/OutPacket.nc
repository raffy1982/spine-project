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
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA 02111-1307, USA.
*****************************************************************/

/**
 *  Interface for the generic outgoing SPINE packet.
 *  Every actual outgoing SPINE packet should provide this interface in order to be SPINE v1.2 compliant
 * 
 *
 * @author Raffaele Gravina
 *
 * @version 1.2
 */ 
interface OutPacket {

    /**
    * Compress the payload of the outgoing packet into a returning buffer.
    *
    *
    * @param payload    the packet payload to build and eventually send.
    * @param len        the packet payload length  (in terms of number of bytes)
    * @param builtLen   the variable where to store the length of the compressed packet  (in terms of number of bytes)
    *
    * @return the pointer to the compressed packet
    */
    command void* build(void* payload, uint8_t len, uint8_t* builtLen);

}
