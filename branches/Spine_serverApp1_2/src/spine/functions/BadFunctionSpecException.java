package spine.functions;

import spine.SPINEFunctionConstants;

public class BadFunctionSpecException extends Exception {

	private static final long serialVersionUID = 0;

	public BadFunctionSpecException(byte functionCode) {
		super("Function " + SPINEFunctionConstants.functionCodeToString(functionCode) + 
				": spec cannot be decoded!");
	}
	
}
