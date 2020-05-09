package cf.leduyquang753.calcit;

public abstract class Operand {
	private String[] characters;
	private boolean reversed;
	private int priority;

	public Operand(String[] characters, int priority, boolean reversed) {
		this.characters = characters;
		this.reversed = reversed;
		this.priority = priority;
	}
	
	public Operand(String[] characters, int priority) {
		this.characters = characters;
		this.reversed = false;
		this.priority = priority;
	}

	public abstract double calculate(double val1, double val2, CalculatorEngine engine) throws ExpressionInvalidException;
	
	public String[] getCharacters() {
		return characters;
	}
	
	public boolean isReversed() {
		return reversed;
	}
	
	public int getPriority() {
		return priority;
	}
	
	public static class Plus extends Operand {
		public Plus() {
			super(new String[] { "+" }, 1);
		}
		@Override
		public double calculate(double val1, double val2, CalculatorEngine engine) throws ExpressionInvalidException {
			return val1 + val2;
		}
	}

	public static class Minus extends Operand {
		public Minus() {
			super(new String[] { "-", "–" }, 1);
		}
		@Override
		public double calculate(double val1, double val2, CalculatorEngine engine) throws ExpressionInvalidException {
			return val1 - val2;
		}
	}

	public static class Multiply extends Operand {
		public Multiply() {
			super(new String[] { ".", "*", "·", "×" }, 2);
		}
		@Override
		public double calculate(double val1, double val2, CalculatorEngine engine) throws ExpressionInvalidException {
			return val1 * val2;
		}
	}

	public static class Divide extends Operand {
		public Divide() {
			super(new String[] { ":", "/", "÷" }, 2);
		}
		@Override
		public double calculate(double val1, double val2, CalculatorEngine engine) throws ExpressionInvalidException {
			if (val2 == 0) throw new ExpressionInvalidException("divisionByZero");
			return val1 / val2;
		}
	}

	public static class Exponentiation extends Operand {
		public Exponentiation() {
			super(new String[] { "^" }, 4, true);
		}
		@Override
		public double calculate(double val1, double val2, CalculatorEngine engine) throws ExpressionInvalidException {
			return Utils.power(val1, val2, engine);
		}
	}

	public static class Root extends Operand {
		public Root() {
			super(new String[] { "#" }, 4);
		}
		@Override
		public double calculate(double val1, double val2, CalculatorEngine engine) throws ExpressionInvalidException {
			if (val1 == 0) throw new ExpressionInvalidException("level0Root");
			return Utils.power(val2, 1 / val1, engine);
		}
	}

	public static class OpeningBrace extends Operand {
		public OpeningBrace() {
			super(new String[] { "(", "[", "{", "<" }, -2);
		}
		@Override
		public double calculate(double val1, double val2, CalculatorEngine engine) throws ExpressionInvalidException {
			throw new ExpressionInvalidException("braceInvolved");
		}
	}

	public static class ClosingBrace extends Operand {
		public ClosingBrace() {
			super(new String[] { ")", "]", "}", ">" }, -2);
		}
		@Override
		public double calculate(double val1, double val2, CalculatorEngine engine) throws ExpressionInvalidException {
			throw new ExpressionInvalidException("braceInvolved");
		}
	}

	public static class DotlessMultiplication extends Operand {
		public DotlessMultiplication() {
			super(new String[] { "." }, 3);
		}
		@Override
		public double calculate(double val1, double val2, CalculatorEngine engine) throws ExpressionInvalidException {
			return val1 * val2;
		}
	}
}
