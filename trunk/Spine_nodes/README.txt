INSTALLATION INSTRUCTIONS:

1. Copy the Spine_nodes folder under your tinyos-2.x-contrib folder

2. type "make telosb install,x" from "Spine_nodes\apps\FeatureSelectionAgent" path to program the terminal (sensor) mote
   (note: x must be replaced with a number; usually numbers from 0 to 4 are used)

3. type "make telosb install,y" from "...\apps\BaseStation" path to program the sink mote (base station) connected 
   directly to the PC.
   (note: y must be replaced with a number; usually numbers starting from 4 are used)

5. Now the entire system is ready to work. Enjoy!!




NOTE : into "Makefile" file of both applications you can find the " PFLAGS += -DCC2420_DEF_CHANNEL=12 " option, needed 
       in order to specify a particular radio channel for the application.