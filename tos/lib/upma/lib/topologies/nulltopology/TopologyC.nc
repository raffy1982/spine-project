
configuration TopologyC
{
	uses interface AsyncSend as SubSend;
	uses interface AsyncReceive as SubReceive;

	uses interface PacketPower;
	uses interface AMPacket;
	uses interface Packet;
	
	provides interface AsyncSend as Send;
	provides interface AsyncReceive as Receive;
	
	provides interface SplitControl;
}
implementation
{	
	Send = SubSend;
	Receive = SubReceive;

	components NoTopologyP as Dummy;
	Dummy = PacketPower;
	Dummy = AMPacket;
	Dummy = Packet;
	SplitControl = Dummy;
}
