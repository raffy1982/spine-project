/*****************************************************************
SPINE - Signal Processing In-Node Environment is a framework that 
allows dynamic configuration of feature extraction capabilities 
of WSN nodes via an OtA protocol

Copyright (C) 2007 Telecom Italia S.p.A. 
 
GNU Lesser General Public License
 
This library is free software; you can redistribute it and/or
modify it under the terms of the GNU Lesser General Public
License as published by the Free Software Foundation, 
version 2.1 of the License. 
 
This library is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
Lesser General Public License for more details.
 
You should have received a copy of the GNU Lesser General Public
License along with this library; if not, write to the
Free Software Foundation, Inc., 59 Temple Place - Suite 330,
Boston, MA  02111-1307, USA.
*****************************************************************/

package spine.utils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class JarUtils {

   public static byte[] getResource(File jarFile, String resourceName) {    
	   try {
		   FileInputStream fis = new FileInputStream(jarFile);
		   BufferedInputStream bis = new BufferedInputStream(fis);
		   ZipInputStream zis = new ZipInputStream(bis);
		   ZipEntry ze = zis.getNextEntry();
		   String currRes = ze.getName();
		   while (currRes != null) {
			   if ((currRes).equals(resourceName)) {
				   int size = (int)ze.getSize();
				   if (size <=0 ) 
					   throw new RuntimeException("ERROR: " + jarFile + " is compressed!");
				   else {
					   byte[] b = new byte[size];
						int rb = 0;
						int chunk = 0;
						while ((size - rb) > 0) {
							chunk = zis.read(b, rb, (size - rb));
							if (chunk < 0)
								break;
							rb += chunk;
						}
						return b;
				}
			   }
			   
			   ze = zis.getNextEntry();
			   currRes = ze.getName();				   			
		   }
	   } catch (NullPointerException e) {} 
	     catch (FileNotFoundException e) {} 
	     catch (IOException e) {}
	     
	   return null;	   
   }
   
}