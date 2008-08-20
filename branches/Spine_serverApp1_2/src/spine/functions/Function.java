package spine.functions;

public abstract class Function {

	public byte functionCode;
	
	public abstract void init(byte[] spec) throws BadFunctionSpecException ;
	
}
