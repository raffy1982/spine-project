interface PacketQuality
{
  /**
   * Get rssi value for a given packet. For received packets, it is
   * the received signal strength when receiving that packet. For sent
   * packets, it is the received signal strength of the ack if an ack
   * was received.
   */
  async command int8_t getRssi( message_t* p_msg );
  
  /**
   * Return true if the channel during this packet had high quality (few bit errors).
   * A good rule of thumb for "high quality" is that the channel quality
   * would enable MTU packets to have a reception rate of 90% or greater.
   *
   * @param msg A received packet during which the channel was measured.
   * @return Whether the channel had high quality.
   */
  async command bool highChannelQuality(message_t* msg);
}
