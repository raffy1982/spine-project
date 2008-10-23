module NoTopologyP
{
	provides interface SplitControl;
	
	uses interface PacketPower;
	uses interface AMPacket;
	uses interface Packet;
}
implementation
{
	command error_t SplitControl.start()
	{
		signal SplitControl.startDone(SUCCESS);
		return SUCCESS;
	}
	
	command error_t SplitControl.stop()
	{
		return FAIL;
	}
}
