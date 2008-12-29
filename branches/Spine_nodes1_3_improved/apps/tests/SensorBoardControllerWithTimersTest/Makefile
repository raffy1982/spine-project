COMPONENT=SensorBoardControllerTestAppC

#radio channel for TX-RX
PFLAGS += -DCC2420_DEF_CHANNEL=14

#node group id ( must match with the coordinator one)
PFLAGS += -DGROUP_ID=0xAB

#sending messages queue size (the queue is preallocated, so this parameter affects the global memory usage)
PFLAGS += -DSPINE_MSG_QUEUE_SIZE=20

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

include ../../Makefile.spine
include $(MAKERULES)

