COMPONENT=SPINEApp_AppC


LOCAL_TOSROOT=$(shell SUPPORT_MAKE_PATH=.; while [ ! -d $$SUPPORT_MAKE_PATH/support/make ]; do SUPPORT_MAKE_PATH=../$$SUPPORT_MAKE_PATH; done; echo $$SUPPORT_MAKE_PATH)
TOSMAKE_PATH=$(LOCAL_TOSROOT)/support/make
GOALS += spine


PFLAGS += -DCC2420_DEF_CHANNEL=12


PFLAGS += -DSENSORS_REGISTRY_SIZE=4
// PFLAGS += -DNO_GYRO     // <-- remove this comment if your platform do NOT have the gyro
// PFLAGS += -DBOOT_RADIO_ON=FALSE
// PFLAGS += -DRADIO_DUTY_CYCLING=TRUE // <-- remove this comment if you want the radio duty cycling
// PFLAGS += -DGROUP_ID=3 // <-- remove this comment if you want to customize the node group id


include $(MAKERULES)
