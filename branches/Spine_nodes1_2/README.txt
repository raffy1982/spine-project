INSTALLATION INSTRUCTIONS:

Spine_nodes contains code to be compiled in TinyOS2.x and then flashed on sensor nodes.
Spine_nodes 1.2 has been developed and tested with TinyOS version 2.1.0. Older TinyOS2.x versions have also been tested, and Makefile can be configured to support an older version, but the SPINE Team strongly suggests to use to TinyOS2.1.0 release.

1.	Copy Spine_nodes folder into your tinyos-2.x-contrib folder
2.	From the app/SPINEApp folder compile and install SPINE1.2 framework on your platform. 

At this time, platforms supported by SPINE1.2 are
a.	Telosb motes with spine sensor board
	SENSORBOARD=spine make telosb
b.	Telosb motes with biosensor sensor board
	SENSORBOARD=biosensor make telosb
c.	Micaz motes with mts300 board
	SENSORBOARD=mts300 make micaz
d.	shimmer motes 
	SENSORBOARD=shimmer make shimmer

If you want your specific sensor or mote platform to be supported by SPINE and if you have any specific questions, please email spine-dev@avalon.tilab.com.
