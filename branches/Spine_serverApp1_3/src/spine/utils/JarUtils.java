package spine.utils;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class JarUtils {

   public static byte[] getResource(String jarFileName, String resourceName) {    
	   try {
		   FileInputStream fis = new FileInputStream(jarFileName);
		   BufferedInputStream bis = new BufferedInputStream(fis);
		   ZipInputStream zis = new ZipInputStream(bis);
		   ZipEntry ze = zis.getNextEntry();
		   String currRes = ze.getName();
		   while (currRes != null) {
			   if ((currRes).equals(resourceName)) {
				   int size = (int)ze.getSize();
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
			   
			   ze = zis.getNextEntry();
			   currRes = ze.getName();				   			
		   }
	   } catch (NullPointerException e) {} 
	     catch (FileNotFoundException e) {} 
	     catch (IOException e) {}
	     
	   return null;	   
   }
   
}