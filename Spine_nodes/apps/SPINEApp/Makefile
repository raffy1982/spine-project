COMPONENT=SPINEApp_AppC


#self-finding of the local tinyos root
LOCAL_TOSROOT=$(shell SUPPORT_MAKE_PATH=.; while [ ! -d $$SUPPORT_MAKE_PATH/support/make ]; do SUPPORT_MAKE_PATH=../$$SUPPORT_MAKE_PATH; done; echo $$SUPPORT_MAKE_PATH)
TOSMAKE_PATH=$(LOCAL_TOSROOT)/support/make
GOALS += spine

# default to security disabled, define 'SECURE=Y' to enable
#SECURE=Y

PFLAGS += -DTOSH_DATA_LENGTH=114

#radio channel for TX-RX
PFLAGS += -DCC2420_DEF_CHANNEL=12

#uncomment if TinyOS 2.0.2 or older is used
#PFLAGS += -DTINYOS_2_0_2
#and if TinyOS 2.0.2 or older is used, comment the following line
PFLAGS += -I$(TOSDIR)/lib/printf/

#node group id ( must match with the coordinator one)
PFLAGS += -DGROUP_ID=0xAB


#sending messages queue size (the queue is preallocated, so this parameter affects the global memory usage)
PFLAGS += -DRADIO_QUEUE_MAX_SIZE=5
#Radio control flag (TRUE if the spine node must turn-on the the radio at boot time; FALSE otherwise)
PFLAGS += -DBOOT_RADIO_ON=TRUE
#sw TDMA control flag (TRUE if radio must be TDMA accessed; FALSE if it's possible to rely on the CSMA-CA provided by the 802.15.4)
PFLAGS += -DENABLE_TDMA=FALSE
#TDMA frame time interval
PFLAGS += -DTDMA_FRAME_PERIOD=1024
#Radio low-power mode control FLAG (if TRUE, the radio will turn off when there aren't any messages to send; if FALSE, the radio will be always on)
PFLAGS += -DRADIO_LOW_POWER=TRUE


#buffer pool size; set this value equal to the total number of channels of all sensors on the node (the pool is preallocated, so this parameter affects the global memory usage)
#each buffer of the pool will be used to store data from a single channel
#
# IMPORTANT: for the predefined sensorboard, this BUFFER_POOL_SIZE is chosen automatically (see /support/make/spine.extra), 
#            and you don't normally need to redefine it here.
# PFLAGS += -DBUFFER_POOL_SIZE=n

#buffer size for each buffer of the pool; the buffers are circular, so the buffer size, with the sampling time over a sensor,
#influence the max data time interval storable in that buffer (the buffers are preallocated, so this parameter affects the global memory usage)
ifdef BUFFER_LENGTH
      PFLAGS += -DBUFFER_LENGTH=$(BUFFER_LENGTH)
else
      PFLAGS += -DBUFFER_LENGTH=80
endif

CFLAGS += -DENABLE_BUFFERED_RAWDATA
CFLAGS += -DENABLE_FEATURES
#CFLAGS += -DENABLE_ALARMS
#CFLAGS += -DENABLE_STEPCOUNTER
#CFLAGS += -DENABLE_HMM
#CFLAGS += -DENABLE_HEARTBEAT

#registry size for handling functions self-registration; set this value equal to the number of functions on the node (the array is preallocated, so this parameter affects the global memory usage)
PFLAGS += -DFUNCTION_LIST_SIZE=4
#registry size for handling function libraries self-registration; set this value equal to the total number of function libraries on the node (the array is preallocated, so this parameter affects the global memory usage)
PFLAGS += -DFUNCTION_LIBRARIES_LIST_SIZE=64


#PFLAGS += -DFEATURE_LIST_SIZE=32
#PFLAGS += -DACT_FEATS_LIST_SIZE=64


include $(MAKERULES)

#include $(TOSDIR)/lib/mac/tkn154/Makefile.include
