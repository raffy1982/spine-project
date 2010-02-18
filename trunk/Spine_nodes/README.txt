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


Note that the Telosb and Tmote Sky are basically identical. Thus, e.g. a 'spine' sensor board can be attached to a Tmote Sky as well, 
and the Telosb can also be equipped with the moteiv optional sensor kit and used in SPINE.
Please refer to the SPINE Manual for details about the various sensor boards.

SPINE 1.3 features an hardware encryption service based on the AES-128 of the CC2420 radio module. The security service is disabled by default, 
and can be enabled by setting the SECURE env variable to 'Y'.
Please refer to the SPINE Manual for details about the optional security service.

If you want your specific sensor or mote platform to be supported by SPINE and if you have any specific questions, please email spine-dev@avalon.tilab.com.
