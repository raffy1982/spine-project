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

package logic;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;

/**
 * Csv File manager.
 * 
 * @author Luigi Buondonno : luigi.buondonno@gmail.com
 * @author Antonio Giordano : antoniogior@hotmail.com
 * 
 * @version 1.0
 */

public class CsvFile {

	File f;

	PrintWriter pw;

	BufferedReader br;

	int numLine = 0;

	boolean fileCreated = false;

	/**
	 * Create new empty file.
	 * 
	 */
	public CsvFile(String path) {
		f = new File(path);
		try {
			pw = new PrintWriter(new FileWriter(f));
			br = new BufferedReader(new FileReader(f));
			fileCreated = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create new tmp empty file.
	 * 
	 */
	public CsvFile() {
		f = new File("tmpFile.csv");
		try {
			pw = new PrintWriter(new FileWriter(f));
			fileCreated = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Save file.
	 * 
	 */
	public void saveAs(String path) {
		this.copia(f.getAbsolutePath(), path);
		return;
	}

	/**
	 * Copy file "from" in "to".
	 * 
	 */
	private void copia(String from, String to) {
		try {
			FileInputStream source = new FileInputStream(from);
			FileOutputStream dest = new FileOutputStream(to);
			int dato;
			for (;;) {
				dato = source.read();
				if (dato == -1)
					break; // end of file di f1
				dest.write(dato);
			}// for
			source.close();
			dest.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Write a line.
	 * 
	 */
	public void writeLine(LinkedList ll) {
		if (fileCreated) {
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < ll.size(); i++) {
				sb.append(ll.get(i) + "; ");
			}
			pw.println(sb.toString());
			numLine++;
			System.out.println("scritto: " + sb.toString());
		} else {
			System.out.println("file not created");
		}
		return;
	}

	/**
	 * Flush file content.
	 * 
	 */
	public void commitNow() {
		pw.flush();
	}

	/**
	 * Get number of line.
	 * 
	 */
	public int getNumberOfLine() {
		return numLine;
	}

	/**
	 * Get the line in lineNumber position.
	 * 
	 */
	public String getLine(int lineNumber) {
		String s = "";
		for (int i = 0; i < lineNumber; i++) {
		}
		try {
			s = br.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return s;
	}

	/**
	 * Close file.
	 * 
	 */
	public void close() {

		try {
			pw.close();
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
