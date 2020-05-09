package cf.leduyquang753.calcit;

public class GetVariableException extends Exception {
	private static final long serialVersionUID = 8058081598694498276L;

	public static enum Type {
		EMPTY_NAME,
		INVALID_NAME,
		NOT_SET
	}

	private Type type;

	public GetVariableException(Type type) {
		this.type = type;
	}
	
	public Type getType() {
		return type;
	}
}
