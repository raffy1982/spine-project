create ext-lib folder into Spine_serverApp
download tinyos.jar http://tinyos.cvs.sourceforge.net/tinyos/tinyos-2.x/support/sdk/java/
and place it into the ext-lib folder

if you wish to run SPINETest (ant run), please check COM value into resources/SPINETestApp.properties

If you intend to use shimmer motes and using their Bluetooth radio (instead of the also available 802.15.4 radio), you have to modify the resources/SPINETestApp.properties file by commenting all the present properties and creating the following ones:
PLATFORM=bt
BT_NETWORK_SIZE=n (where 'n' is the number of shimmer motes that will represent the sensor network of the application)

Because the Bluetooth is supported via a free open-source Java API called BlueCove (note that only selected Bluetooth stacks are supported on Windows, MAC OS, or Linux machines), the BlueCove library must be downloaded from the web. 
In particular, "bluecove-2.1.0.jar" can be downloead freely here: http://sourceforge.net/projects/bluecove/files/. It must be placed under the "Spine_serverApp/ext-lib" folder and referenced properly inside the SPINE java project.
Please refer to the SPINE Manual for details about the supported Bluetooth stacks and how to get SPINE 'server-side' working for Bluetooth connections.

