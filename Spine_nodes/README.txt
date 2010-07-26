INSTALLATION INSTRUCTIONS:

Spine_nodes contains code to be compiled in TinyOS2.x and then flashed on sensor nodes.
Spine_nodes 1.3 has been developed and tested with TinyOS version 2.1.0. Older TinyOS2.x versions have also been tested, 
and Makefile can be configured to support an older version, but the SPINE Team strongly suggests to use to TinyOS2.1.0 release.

1.	Copy Spine_nodes folder into your tinyos-2.x-contrib folder
2.	From the app/SPINEApp folder compile and install SPINE1.3 framework on your platform. 

At this time, platforms supported by SPINE1.3 are

a.	Telosb motes with 'spine' sensor board (default sensor-board for 'telosb' platform)
	- make telosb (or equally SENSORBOARD=spine make telosb)

b.	Telosb motes with 'moteiv' 'moteiv' optional sensor kit 
	- SENSORBOARD=moteiv make telosb

c.	Telosb motes with 'biosensor' sensor board
	- SENSORBOARD=biosensor make telosb

d.	Telosb motes with 'cardioshield' sensor board
	- SENSORBOARD=cardioshield make telosb

e.	Tmote SKy motes with 'moteiv' optional sensor kit (default for 'tmote' platform)
	- make tmote (or equally SENSORBOARD=moteiv make tmote)

f.	Micaz motes with 'mts300' board
	- make micaz (or equally SENSORBOARD=mts300 make micaz)

g.	Shimmer motes 
	- make shimmer (or equally SENSORBOARD=shimmer make shimmer)

h.	Shimmer motes using the BLUETOOTH radio (also read notes below)
	- SENSORBOARD=shimmerbt make shimmer bsl,x (where 'x' is the first COM port of the shimmer device when attached - 1. ex. COM17 would be 'bsl,16')


Note that the Telosb and Tmote Sky are basically identical. Thus, e.g. a 'spine' sensor board can be attached to a Tmote Sky as well, 
and the Telosb can also be equipped with the moteiv optional sensor kit and used in SPINE.
Please refer to the SPINE Manual for details about the various sensor boards.

SPINE 1.3 features an hardware encryption service based on the AES-128 of the CC2420 radio module. The security service is disabled by default, 
and can be enabled by setting the SECURE env variable to 'Y'.
Please refer to the SPINE Manual for details about the optional security service.

BLUETOOTH radio is supported on the SHIMMER platform. To compile SPINE using the Bluetooth radio of the Shimmer mote, users need to download from the tinyos CVS repository the folder "tinyos-2.x/tos/platforms/shimmer/chips/bluetooth" and copy it under "tinyos-2.x-contrib/Spine_nodes/tos/platforms/shimmer/chips/bluetooth" (the "chips" folder under ""tinyos-2.x-contrib/Spine_nodes/tos/platforms/shimmer" must be created manually).
On the server side, a free open-source Java API called BlueCove has been used and only selected 
Bluetooth stacks are supported on Windows, MAC OS, or Linux machines. 
Please refer to the SPINE Manual for details about the supported Bluetooth stacks and how to get SPINE 'server-side' working for Bluetooth connections.

If you want your specific sensor or mote platform to be supported by SPINE and if you have any specific questions, please email spine-dev@avalon.tilab.com.
