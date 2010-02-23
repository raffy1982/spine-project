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

import java.io.IOException;
import java.io.StringReader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;

/**
 * Arff File manager: read and write file in arff format.
 * 
 * @author Alessia Salmeri : alessia.salmeri@telecomitalia.it
 * 
 * @version 1.0
 */
public class ArffFile {

	private List<String> attributeNames;

	private Hashtable<String, String> attributeTypes;

	private Hashtable<String, String[]> attributeData;

	private String relation = "";

	private String comment = "";

	private List<Object[]> dataCollection;

	private final int COMMENT_INFO = 1;

	private final int RELATDESCR_INFO = 2;

	private final int DATA_INFO = 3;

	// infoType: Comment, RelationDescr or Data
	private int infoType = COMMENT_INFO;

	private int lineNumber;

	private final String LINE_SEPARATOR = System.getProperty("line.separator");

	/** Construct an Arff File. */
	public ArffFile() {
		attributeNames = new ArrayList<String>();
		attributeTypes = new Hashtable<String, String>();
		attributeData = new Hashtable<String, String[]>();
		dataCollection = new ArrayList<Object[]>();
	}

	/** Load an Arff File. */
	public static ArffFile load(String filename) throws FileNotFoundException, IOException, ArffFileParseException {
		ArffFile arffFile = new ArffFile();
		arffFile.parse(new BufferedReader(new FileReader(filename)));
		return arffFile;
	}

	/** Parse an Arff File (type String). */
	public static ArffFile parse(String content) throws IOException, ArffFileParseException {
		ArffFile arffFile = new ArffFile();
		arffFile.parse(new BufferedReader(new StringReader(content)));
		return arffFile;
	}

	/** Parse an Arff File (type BufferedReader). */
	public void parse(BufferedReader content) throws IOException {
		String lineContent;
		lineNumber = 1;
		while ((lineContent = content.readLine()) != null) {
			try {
				parseLineContent(lineContent);
			} catch (ArffFileParseException e) {
				System.err.println("Exception in parseLineContent (lineNumber=" + lineNumber + ")");
			}
			lineNumber = lineNumber + 1;
		}
	}

	// Parse a line.
	private void parseLineContent(String contentLine) throws ArffFileParseException {
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
				} catch (ArffFileParseException e) {
					System.err.println("Exception in parseAttribute");
				}
			} else if (contentLine.startsWith("@data")) {
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
	 * Define a new attribute (case "nominal"): Data contains nominal values.
	 */
	public void defineAttribute(String name, String type, String[] value) {
		attributeNames.add(name);
		attributeTypes.put(name, type);
		attributeData.put(name, value);
	}

	// Parse attribute line
	private void parseAttribute(String attribLine) throws ArffFileParseException {

		Pattern pattern = Pattern.compile("[a-zA-Z_][a-zA-Z0-9_]*|\\{[^\\}]+\\}");

		Scanner scanner = new Scanner(attribLine);
		scanner.findInLine(pattern);
		String attribName = scanner.findInLine(pattern);
		String attribType = scanner.findInLine(pattern);

		if (attribName == null || attribType == null) {
			throw new ArffFileParseException(lineNumber, "Error in attribute definition");
		}

		if (attribType.equals("numeric") || attribType.equals("string")) {
			defineAttribute(attribName, attribType);
		} else if (attribType.startsWith("{") && attribType.endsWith("}")) {
			attribType = (attribType.substring(1, attribType.length() - 1)).trim();
			defineAttribute(attribName, "nominal", attribType.split("\\s*,\\s*"));
		} else {
			throw new ArffFileParseException(lineNumber, "Error: attribute type not supported (" + attribType + ")");
		}
	}

	// Parse data line
	private void parseData(String dataLine) throws ArffFileParseException {
		int numAttributes = attributeNames.size();

		String[] tokensMatch = dataLine.split("\\s*,\\s*");
		if (tokensMatch.length != numAttributes) {
			throw new ArffFileParseException(lineNumber, "Error: wrong number of elements in line " + lineNumber);
		}

		Object[] dataDetail = new Object[numAttributes];
		for (int k = 0; k < numAttributes; k++) {
			String name = attributeNames.get(k);
			String at = attributeTypes.get(name);
			if (at.equals("string")) {
				// Case "string"
				dataDetail[k] = tokensMatch[k];
			} else if (at.equals("numeric")) {
				// Case "numeric"
				dataDetail[k] = Double.parseDouble(tokensMatch[k]);
			} else if (at.equals("nominal")) {
				// Case "nominal"
				if (checkNominalValue(name, tokensMatch[k]).equalsIgnoreCase("correct")) {
					dataDetail[k] = tokensMatch[k];
				} else {
					throw new ArffFileParseException(lineNumber, "Error: wrong nominal value for " + name + " (" + tokensMatch[k] + ")");
				}
			}
		}
		dataCollection.add(dataDetail);

	}

	private String checkNominalValue(String name, String value) throws ArffFileParseException {
		String result = "wrong";
		String[] values = attributeData.get(name);
		for (int i = 0; i < values.length; i++) {
			if (values[i].equals(value)) {
				result = "correct";
			}
		}
		return result;
	}

	private String append(String string, Object[] objects, String chrDel) {
		int objNum = 1;
		for (Object o : objects) {
			if (objNum > 1) {
				string = string + chrDel;
			}
			objNum++;
			string = string + o.toString();
		}
		return string;
	}

	/** Write the Arff File to a string. */
	public String write() {
		String arffContent = "";
		if (!comment.equals("")) {

			// COMMENT
			arffContent = arffContent + "% " + comment.replaceAll(LINE_SEPARATOR, LINE_SEPARATOR + "% ") + LINE_SEPARATOR;

			// RELATION
			arffContent = arffContent + "@relation " + relation + LINE_SEPARATOR;

			for (String name : attributeNames) {
				arffContent = arffContent + "@attribute " + name + " ";

				String type = attributeTypes.get(name);
				if (type.equals("numeric") || type.equals("string")) {
					arffContent = arffContent + type;
				} else if (type.equals("nominal")) {
					arffContent = arffContent + "{";
					arffContent = append(arffContent, attributeData.get(name), ",");
					arffContent = arffContent + "}";
				}
				arffContent = arffContent + LINE_SEPARATOR;
			}

			// DATA
			arffContent = arffContent + "@data" + LINE_SEPARATOR;

			for (Object[] dataDetail : dataCollection) {
				arffContent = append(arffContent, dataDetail, ",");
				arffContent = arffContent + LINE_SEPARATOR;
			}
		}
		return arffContent;
	}

	/** Save the data into a file. */
	public void save(String filename) throws IOException {
		Writer w = new FileWriter(filename);
		w.write(write());
		w.flush();
		System.out.println("File saved...");
	}

	/**
	 * Get the name of the relation.
	 */
	public String getRelation() {
		return relation;
	}

	/**
	 * Set the name of the relation.
	 */
	public void setRelation(String relation) {
		this.relation = relation;
	}

	/**
	 * Get the comment.
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * Set the comment.
	 */
	public void setComment(String comment) {
		this.comment = comment;
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

	/** Add a dataDetail. */
	public void addData(Object[] dataDetail) {
		dataCollection.add(dataDetail);
	}

	/**
	 * Get the data.
	 */
	public List<Object[]> getData() {
		return dataCollection;
	}
}