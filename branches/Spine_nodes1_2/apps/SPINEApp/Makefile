COMPONENT=SPINEApp_AppC

#self-finding of the local tinyos root
LOCAL_TOSROOT=$(shell SUPPORT_MAKE_PATH=.; while [ ! -d $$SUPPORT_MAKE_PATH/support/make ]; do SUPPORT_MAKE_PATH=../$$SUPPORT_MAKE_PATH; done; echo $$SUPPORT_MAKE_PATH)
TOSMAKE_PATH=$(LOCAL_TOSROOT)/support/make
GOALS += spine

#radio channel for TX-RX
PFLAGS += -DCC2420_DEF_CHANNEL=12

#uncomment if tinyos cvs version higher than 2.0.2 is used as OS
//PFLAGS += -DTINYOS_CVS


#current spine version used (2 stands for 1.2)
PFLAGS += -DSPINE_VERSION=2

#node group id ( must match with the coordinator one)
PFLAGS += -DGROUP_ID=0xAB


#sending messages queue size (the queue is preallocated, so this parameter affects the global memory usage)
PFLAGS += -DRADIO_QUEUE_MAX_SIZE=20
#Radio control flag (TRUE if the spine node must turn-on the the radio at boot time; FALSE otherwise)
PFLAGS += -DBOOT_RADIO_ON=TRUE
#sw TDMA control flag (TRUE if radio must be TDMA accessed; FALSE if it's possible to rely on the CSMA-CA provided by the 802.15.4)
PFLAGS += -DENABLE_TDMA=FALSE
#TDMA frame time interval
PFLAGS += -DTDMA_FRAME_PERIOD=600
#Radio low-power mode control FLAG (if TRUE, the radio will turn off when there aren't any messages to send; if FALSE, the radio will be always on)
PFLAGS += -DRADIO_LOW_POWER=TRUE

#registry size for handling sensor self-registration; set this value equal to the number of sensors on the node (the array is preallocated, so this parameter affects the global memory usage)
PFLAGS += -DSENSORS_REGISTRY_SIZE=4

#buffer pool size; set this value equal to the total number of channels of all sensors on the node (the pool is preallocated, so this parameter affects the global memory usage)
#each buffer of the pool will be used to store data from a single channel
PFLAGS += -DBUFFER_POOL_SIZE=6

#buffer size for each buffer of the pool; the buffers are circular, so the buffer size, with the sampling time over a sensor,
#influence the max data time interval storable in that buffer (the buffers are preallocated, so this parameter affects the global memory usage)
ifdef BUFFER_LENGTH
      PFLAGS += -DBUFFER_LENGTH=$(BUFFER_LENGTH)
else
      PFLAGS += -DBUFFER_LENGTH=80
endif


#registry size for handling functions self-registration; set this value equal to the number of functions on the node (the array is preallocated, so this parameter affects the global memory usage)
PFLAGS += -DFUNCTION_LIST_SIZE=4
#registry size for handling function libraries self-registration; set this value equal to the total number of function libraries on the node (the array is preallocated, so this parameter affects the global memory usage)
PFLAGS += -DFUNCTION_LIBRARIES_LIST_SIZE=64


//PFLAGS += -DFEATURE_LIST_SIZE=32
//PFLAGS += -DACT_FEATS_LIST_SIZE=64

ifeq ($(SENSORBOARD),spine)
     PFLAGS += -DSPINE_SENSOR_BOARD
endif
ifeq ($(SENSORBOARD),shimmer)
     PFLAGS += -DSHIMMER_SENSOR_BOARD
endif
ifeq ($(SENSORBOARD),mts300)
     PFLAGS += -DMTS300_SENSOR_BOARD
endif
ifeq ($(SENSORBOARD),biosensor)
     PFLAGS += -DWISEPLA_BIOSENSOR
endif


include $(MAKERULES)
