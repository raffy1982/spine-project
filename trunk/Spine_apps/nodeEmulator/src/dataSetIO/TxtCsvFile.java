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

package dataSetIO;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

/**
 * TXT-CSV manager: read and write operation.
 * 
 * @author Alessia Salmeri : alessia.salmeri@telecomitalia.it
 * 
 * @version 1.0
 */

public class TxtCsvFile {

	private String comment = "";

	private List<String> attributeNames;

	private Hashtable<String, String> attributeTypes;

	private Hashtable<String, String[]> attributeData;

	private List<Object[]> dataCollection;

	private final int COMMENT_INFO = 1;

	private final int RELATDESCR_INFO = 2;

	private final int DATA_INFO = 3;

	// infoType: Comment, RelationDescr or Data
	private int infoType = COMMENT_INFO;

	private int lineNumber;

	private final String LINE_SEPARATOR = System.getProperty("line.separator");

	private static String regExpSeparator;

	private static String separator;

	private static String headerData;

	/** Load an txt or cvs file. */
	public static TxtCsvFile load(String dataSetFile) throws FileNotFoundException, IOException, TxtCsvFileParseException {

		TxtCsvFile txtCsvFile = new TxtCsvFile();

		int indEst = dataSetFile.indexOf(".");
		String dataSetType = dataSetFile.substring(indEst + 1, dataSetFile.length());

		// Set char column separator CSV =";" TXT=" "
		separator = (dataSetType.equals("csv")) ? ";" : " ";
		regExpSeparator = (dataSetType.equals("csv")) ? "\\s*;\\s*" : "\\s";

		String dataSetCommentFile = dataSetFile.substring(0, indEst) + "Comment" + "." + dataSetType;

		BufferedReader sezData = new BufferedReader(new FileReader(dataSetFile));

		BufferedReader sezComment = new BufferedReader(new FileReader(dataSetCommentFile));

		headerData = "CLASS_LABEL" + separator + "featureDataId" + separator + "featureId" + separator + "sensorCode_featureCode_chNum" + separator + "featureValue";

		txtCsvFile.parse(sezComment, sezData);

		return txtCsvFile;
	}

	/** Construct an TxtCsvFile. */
	public TxtCsvFile() {
		comment = null;
		attributeNames = new ArrayList<String>();
		attributeTypes = new Hashtable<String, String>();
		attributeData = new Hashtable<String, String[]>();
		dataCollection = new ArrayList<Object[]>();
	}

	/** Parse an Txt or Csv (type BufferedReader). */
	public void parse(BufferedReader comment, BufferedReader data) throws IOException {
		String line;
		lineNumber = 1;
		while ((line = comment.readLine()) != null) {
			try {
				parseLineContent(line);
			} catch (TxtCsvFileParseException e) {
				System.err.println("Exception in parseLineContent (lineNumber=" + lineNumber + ")");
			}
			lineNumber = lineNumber + 1;
		}
		while ((line = data.readLine()) != null) {
			try {
				parseLineContent(line);
			} catch (TxtCsvFileParseException e) {
				System.err.println("Exception in parseLineContent (lineNumber=" + lineNumber + ")");
			}
			lineNumber = lineNumber + 1;
		}
	}

	// Parse a line.
	private void parseLineContent(String contentLine) throws TxtCsvFileParseException {
		switch (infoType) {
		case COMMENT_INFO:
			if (!contentLine.isEmpty() && contentLine.charAt(0) == '%') {
				// Case Node, Sensor_Setup, Feature_Setup and Feature_Activation
				if (contentLine.length() >= 2)
					comment = comment + contentLine.substring(1) + LINE_SEPARATOR;
			} else {
				// Case relation and attribute
				infoType = RELATDESCR_INFO;
				parseLineContent(contentLine);
			}
			break;
		case RELATDESCR_INFO:
			if (contentLine.startsWith("@attribute")) {
				try {
					parseAttribute(contentLine);
				} catch (TxtCsvFileParseException e) {
					e.printStackTrace();
				}
			} else if (contentLine.equals(headerData)) {
				infoType = DATA_INFO;
			}
			break;
		case DATA_INFO:
			if (!contentLine.isEmpty() && contentLine.charAt(0) != '%')
				parseData(contentLine);
			break;
		}
	}

	/**
	 * Define a new attribute (case "numeric", "string")".
	 */
	public void defineAttribute(String name, String type) {
		attributeNames.add(name);
		attributeTypes.put(name, type);
	}

	/**
	 * Define a new attribute (case "nominal": Data contains nominal values).
	 */
	public void defineAttribute(String name, String type, String[] data) {
		attributeNames.add(name);
		attributeTypes.put(name, type);
		attributeData.put(name, data);
	}

	// Parse attribute line.
	private void parseAttribute(String attribLine) throws TxtCsvFileParseException {

		Pattern pattern = Pattern.compile("[a-zA-Z_][a-zA-Z0-9_]*|\\{[^\\}]+\\}");

		Scanner scanner = new Scanner(attribLine);
		scanner.findInLine(pattern);
		String attribName = scanner.findInLine(pattern);
		String attribType = scanner.findInLine(pattern);

		if (attribName == null || attribType == null) {
			throw new TxtCsvFileParseException(lineNumber, "Error: attribute definition cannot be parsed");
		}

		if (attribType.equals("numeric") || attribType.equals("string")) {
			defineAttribute(attribName, attribType);
		} else if (attribType.startsWith("{") && attribType.endsWith("}")) {
			attribType = (attribType.substring(1, attribType.length() - 1)).trim();
			defineAttribute(attribName, "nominal", attribType.split("\\s*,\\s*"));
		} else {
			throw new TxtCsvFileParseException(lineNumber, "Error: attribute type \"" + attribType + "\" not supported.");
		}
	}

	// Parse Data line
	private void parseData(String dataLine) throws TxtCsvFileParseException {
		int numAttributes = attributeNames.size();

		// String[] tokens = line.split("\\s*,\\s*"); -- ARFF
		// String[] tokens = line.split("\\s*;\\s*"); -- CSV
		// String[] tokens = line.split("\\s"); -- TXT
		String[] tokensMatch = dataLine.split(regExpSeparator);
		if (tokensMatch.length != numAttributes) {
			throw new TxtCsvFileParseException(lineNumber, "Error: line " + lineNumber + " does not contain the right " + "number of elements (should be " + numAttributes + ").");
		}

		Object[] dataDetail = new Object[numAttributes];
		for (int i = 0; i < numAttributes; i++) {
			String name = attributeNames.get(i);
			String at = attributeTypes.get(name);
			if (at.equals("string")) {
				// Case "string"
				dataDetail[i] = tokensMatch[i];
			} else if (at.equals("numeric")) {
				// Case "numeric"
				dataDetail[i] = Double.parseDouble(tokensMatch[i]);
			} else if (at.equals("nominal")) {
				// Case "nominal"
				if (checkNominalValue(name, tokensMatch[i]).equalsIgnoreCase("correct")) {
					dataDetail[i] = tokensMatch[i];
				} else {
					throw new TxtCsvFileParseException(lineNumber, "Error: undefined nominal value \"" + tokensMatch[i] + "\" for " + name + ".");
				}
			}
		}

		dataCollection.add(dataDetail);

	}

	private String checkNominalValue(String name, String value) throws TxtCsvFileParseException {
		String result = "wrong";
		String[] values = attributeData.get(name);
		for (int i = 0; i < values.length; i++) {
			if (values[i].equals(value)) {
				result = "correct";
			}
		}
		return result;
	}

	/**
	 * Get the comment.
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * Get the number of attributes.
	 */
	public int getNumberOfAttributes() {
		return attributeNames.size();
	}

	/**
	 * Get the name of an attribute.
	 */
	public String getAttributeName(int index) {
		return attributeNames.get(index);
	}

	/**
	 * Get attributes list.
	 */
	public List<String> getAttributesList() {
		return attributeNames;
	}

	/**
	 * Get the type ("numeric", "string" or "nominal") of an attribute; for
	 * nominal attributes, use getAttributeData() to retrieve the values.
	 */
	public String getAttributeType(String name) {
		return attributeTypes.get(name);
	}

	/**
	 * Get the values of nominal attributes.
	 */
	public String[] getAttributeData(String name) {
		return attributeData.get(name);
	}

	/**
	 * Get the data.
	 */
	public List<Object[]> getData() {
		return dataCollection;
	}
}