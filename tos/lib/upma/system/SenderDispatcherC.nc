configuration SenderDispatcherC {
	uses interface AsyncSend as SubSend;
	uses interface CcaControl as SubCcaControl[am_id_t];
	
	
	provides interface CcaControl as SlotsCcaControl[uint8_t];
	provides interface AsyncSend as Send[uint8_t];
} implementation {
	components SenderDispatcherP;
	components MainC;
	
	#if TRACE_SEND == 1
	components HplMsp430GeneralIOC;
	SenderDispatcherP.Pin -> HplMsp430GeneralIOC.Port23;
	#endif
	
	MainC.SoftwareInit -> SenderDispatcherP.Init;
	
	SenderDispatcherP.Send = Send;
	SenderDispatcherP.SubSend = SubSend;
	SenderDispatcherP.SubCcaControl = SubCcaControl;
	SenderDispatcherP.SlotsCcaControl = SlotsCcaControl;
	
}
