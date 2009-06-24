COMPONENT=SPINEApp_AppC

PFLAGS += -DCC2420_DEF_CHANNEL=14
PFLAGS += -I..

PFLAGS += -DSPINE_SLEEP_INTERVAL=512	# set 0 for radio always on

UPMA_MAC = xmac
include ../../../Makefile.spine
include ../../../Makefile.upma
include $(MAKERULES)
