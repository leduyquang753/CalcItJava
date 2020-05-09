package cf.leduyquang753.calcit;

public class ExpressionInvalidException extends Exception {
	private static final long serialVersionUID = -8453470294521018709L;
	private int position = -1;
	private Object[] messageArguments = null;

	public ExpressionInvalidException(String key, int position, Object[] messageArguments) {
		super(key);
		this.position = position;
		this.messageArguments = messageArguments;
	}
	
	public ExpressionInvalidException(String key, Object[] messageArguments) {
		super(key);
		this.messageArguments = messageArguments;
	}
	
	public ExpressionInvalidException(String key, int position) {
		super(key);
		this.position = position;
	}
	
	public ExpressionInvalidException(String key) {
		super(key);
	}
	
	public int getPosition() {
		return position;
	}
	
	public Object[] getMessageArguments() {
		return messageArguments;
	}
}
