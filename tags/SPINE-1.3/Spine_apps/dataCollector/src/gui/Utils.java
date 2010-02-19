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

package gui;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JDialog;
import javax.swing.JFrame;

/**
 * This class contains useful GUI utility methods.
 * 
 * @author Raffaele Gravina
 * @author Antonio Guerrieri
 * 
 * @version 1.0
 */
public class Utils {

	public static void onScreenCentered(JFrame window) {
		int windowWidth = window.getWidth();
		int windowHeight = window.getHeight();

		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension scrnsize = toolkit.getScreenSize();
		int screenWidth = (int) scrnsize.getWidth();
		int screenHeight = (int) scrnsize.getHeight();

		window.setBounds((screenWidth - windowWidth) / 2,
				(screenHeight - windowHeight) / 2, windowWidth, windowHeight);
	}

	public static void onScreenCentered(JDialog window) {
		int windowWidth = window.getWidth();
		int windowHeight = window.getHeight();

		Toolkit toolkit = Toolkit.getDefaultToolkit();
		Dimension scrnsize = toolkit.getScreenSize();
		int screenWidth = (int) scrnsize.getWidth();
		int screenHeight = (int) scrnsize.getHeight();

		window.setBounds((screenWidth - windowWidth) / 2,
				(screenHeight - windowHeight) / 2, windowWidth, windowHeight);
	}

}
