package spine.functions;

import java.util.Vector;

import spine.SPINEFunctionConstants;

public class FeatureFunction extends Function {

	private Vector functionalities = null;
	
	public FeatureFunction() {		
	}
	
	public void init(byte[] spec) throws BadFunctionSpecException {
		functionalities = new Vector();
		
		for (int i = 0; i < spec.length; i++)
			functionalities.addElement(SPINEFunctionConstants.functionalityCodeToString(SPINEFunctionConstants.FEATURE, spec[i]));
		
	}
	
	
	public String toString() {
		return SPINEFunctionConstants.FEATURE_LABEL + " : " + functionalities;
	}
}