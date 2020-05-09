package cf.leduyquang753.calcit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import cf.leduyquang753.calcit.Operand.ClosingBrace;
import cf.leduyquang753.calcit.Operand.OpeningBrace;

public class CalculatorEngine {
	private static Operand.DotlessMultiplication dotlessMulOp = new Operand.DotlessMultiplication();
	private static HashMap<String, String> braceMap = new HashMap<String, String>();
	private static final int positiveInfinity = 99999;
	//private static final int negativeInfinity = -99999;

	static {
		braceMap.put("(", ")");
		braceMap.put("{", "}");
		braceMap.put("[", "]");
		braceMap.put("<", ">");
	}

	private HashMap<String, Operand> operandMap = new HashMap<String, Operand>();
	private HashMap<String, Double> variableMap = new HashMap<String, Double>();
	private HashMap<String, Function> functionMap = new HashMap<String, Function>();

	// BEGIN SETTINGS VARIABLES
	// You can set these values to change how the engine behaves.
	public boolean
		decimalDot = false,
		enforceDecimalSeparator = false,
		thousandDot = false,
		mulAsterisk = false,
		enforceMulDiv = false,
		zeroUndefinedVars = false;

	public AngleUnit angleUnit = AngleUnit.DEGREE;
	// END SETTINGS VALUES

	private double
		ans = 0,
		preAns = 0;

	public CalculatorEngine() {
		// Register every operand.
		for (Operand operand : new Operand[] {
			new Operand.Plus(),
			new Operand.Minus(),
			new Operand.Multiply(),
			new Operand.Divide(),
			new Operand.Exponentiation(),
			new Operand.Root(),
			new Operand.OpeningBrace(),
			new Operand.ClosingBrace()
		}) registerOperand(operand);

		// Register every function.
		for (Function function : new Function[] {
			new Function.Sum(),
			new Function.Sin(),
			new Function.Cos(),
			new Function.Tan(),
			new Function.Cot(),
			new Function.ArcSin(),
			new Function.ArcCos(),
			new Function.ArcTan(),
			new Function.ArcCot(),
			new Function.Floor(),
			new Function.Abs(),
			new Function.GCD(),
			new Function.LCM(),
			new Function.Fact(),
			new Function.Log(),
			new Function.Ln(),
			new Function.Permutation(),
			new Function.Combination(),
			new Function.Round(),
			new Function.DegToRad(),
			new Function.RadToDeg(),
			new Function.DegToGrad(),
			new Function.GradToDeg(),
			new Function.GradToRad(),
			new Function.RadToGrad(),
			new Function.Max(),
			new Function.Min(),
			new Function.Average(),
			new Function.RandomFunc(),
			new Function.RandomInt(),
			new Function.RandomInList(),
			new Function.IsGreater(),
			new Function.IsSmaller(),
			new Function.IsEqual(),
			new Function.If(),
			new Function.And(),
			new Function.Or(),
			new Function.Not(),
			new Function.AngleToDegrees(),
			new Function.AngleToRadians(),
			new Function.AngleToGradians(),
			new Function.AngleFromDegrees(),
			new Function.AngleFromRadians(),
			new Function.AngleFromGradians(),
			new Function.Date(),
			new Function.Year(),
			new Function.DayOfYear(),
			new Function.Month(),
			new Function.Day(),
			new Function.DecimalDay(),
			new Function.Hour(),
			new Function.Minute(),
			new Function.Second(),
			new Function.DayOfWeekMondayFirst(),
			new Function.DayOfWeekSundayFirst(),
			new Function.Time()
		}) registerFunction(function);
	}

	/**
	 * Registers an operand to the engine. If there is a registered operand with some characters overlapping the operand being added, the one being will override.
	 */
	public void registerOperand(Operand op) {
		for (String key : op.getCharacters()) operandMap.put(key, op);
	}

	/**
	/* Registers a function to the engine. If there is a registered function with some names overlapping the operand being added, the one being will override.
	*/
	public void registerFunction(Function func) {
		for (String key : func.getNames()) functionMap.put(lowercaseAndRemoveWhitespace(key), func);
	}

	private boolean isDigit(char c) {
		return c >= '0' && c <= '9';
	}

	private boolean isChar(char c) {
		return Character.isAlphabetic(c) || c == '_';
	}

	private boolean areBracesMatch(String opening, String closing) {
		return braceMap.get(opening).equals(closing);
	}

	private void performBacktrackCalculation(CalculationStatus calculationStatus, boolean shouldCalculateAll) throws ExpressionInvalidException {
		if (calculationStatus.OS.isEmpty()) return;
		Operand currentOperand = calculationStatus.OS.pop();
		double currentNumber = calculationStatus.NS.pop();
		int lastPriority = positiveInfinity;
		while (shouldCalculateAll || !(currentOperand instanceof OpeningBrace)) {
			if (shouldCalculateAll && currentOperand instanceof OpeningBrace) {
				while (!calculationStatus.TOS.isEmpty()) currentNumber = calculationStatus.TOS.pop().calculate(currentNumber, calculationStatus.TNS.pop(), this);
				lastPriority = positiveInfinity;
				if (!calculationStatus.OS.isEmpty()) currentOperand = calculationStatus.OS.pop(); else {
					while (!calculationStatus.TOS.isEmpty()) currentNumber = calculationStatus.TOS.pop().calculate(currentNumber, calculationStatus.TNS.pop(), this);
					calculationStatus.NS.push(currentNumber);
					return;
				}
			}
			if (currentOperand.getPriority() != lastPriority)
				while (!calculationStatus.TOS.isEmpty()) currentNumber = calculationStatus.TOS.pop().calculate(currentNumber, calculationStatus.TNS.pop(), this);
			if (currentOperand.isReversed()) currentNumber = currentOperand.calculate(calculationStatus.NS.pop(), currentNumber, this); else {
				calculationStatus.TNS.push(currentNumber);
				calculationStatus.TOS.push(currentOperand);
				currentNumber = calculationStatus.NS.pop();
			}
			lastPriority = currentOperand.getPriority();
			if (!calculationStatus.OS.isEmpty()) currentOperand = calculationStatus.OS.pop(); else {
				while (!calculationStatus.TOS.isEmpty()) currentNumber = calculationStatus.TOS.pop().calculate(currentNumber, calculationStatus.TNS.pop(), this);
				calculationStatus.NS.push(currentNumber);
				return;
			}
		}
		while (!calculationStatus.TOS.isEmpty()) currentNumber = calculationStatus.TOS.pop().calculate(currentNumber, calculationStatus.TNS.pop(), this);
		calculationStatus.NS.push(currentNumber);
		calculationStatus.OS.push(currentOperand);
	}

	private void performBacktrackSameLevelCalculation(CalculationStatus calculationStatus) throws ExpressionInvalidException {
		if (calculationStatus.OS.isEmpty()) return;
		Operand currentOperand = calculationStatus.OS.pop();
		double currentNumber = calculationStatus.NS.pop();
		int lastPriority = currentOperand.getPriority();
		while (!(currentOperand instanceof OpeningBrace)) {
			if (currentOperand.getPriority() != lastPriority) {
				while (!calculationStatus.TOS.isEmpty()) currentNumber = calculationStatus.TOS.pop().calculate(currentNumber, calculationStatus.TNS.pop(), this);
				calculationStatus.NS.push(currentNumber);
				calculationStatus.OS.push(currentOperand);
				return;
			}
			if (currentOperand.isReversed()) currentNumber = currentOperand.calculate(calculationStatus.NS.pop(), currentNumber, this); else {
				calculationStatus.TNS.push(currentNumber);
				calculationStatus.TOS.push(currentOperand);
				currentNumber = calculationStatus.NS.pop();
			}
			lastPriority = currentOperand.getPriority();
			if (!calculationStatus.OS.isEmpty()) currentOperand = calculationStatus.OS.pop(); else {
				while (!calculationStatus.TOS.isEmpty()) currentNumber = calculationStatus.TOS.pop().calculate(currentNumber, calculationStatus.TNS.pop(), this);
				calculationStatus.NS.push(currentNumber);
				return;
			}
		}
		while (!calculationStatus.TOS.isEmpty()) currentNumber = calculationStatus.TOS.pop().calculate(currentNumber, calculationStatus.TNS.pop(), this);
		calculationStatus.NS.push(currentNumber);
		calculationStatus.OS.push(currentOperand);
	}

	private double processNumberToken(CalculationStatus calculationStatus, int position) throws ExpressionInvalidException {
		boolean percent = false;
		if (calculationStatus.currentToken.charAt(calculationStatus.currentToken.length()-1) == '%') {
			percent = true;
			calculationStatus.currentToken = calculationStatus.currentToken.substring(0, calculationStatus.currentToken.length()-1);
		}
		double result;
		if (calculationStatus.isVariable) result = getVariableInternal(calculationStatus.currentToken, position); else {
			calculationStatus.currentToken = calculationStatus.currentToken.replace(",", ".");
			result = Double.parseDouble(calculationStatus.currentToken);
		}
		if (percent) result /= 100;
		if (calculationStatus.negativity) {
			calculationStatus.NS.push(-1d);
			calculationStatus.OS.push(dotlessMulOp);
		}
		calculationStatus.negativity = false;
		calculationStatus.hadNegation = false;
		calculationStatus.hadComma = false;
		calculationStatus.currentToken = "";
		return result;
	}

	private boolean isDecimalSeparator(char c) {
		return decimalDot ? enforceDecimalSeparator ? c == '.' : c == '.' || c == ',' : c == ',';
	}

	private double performCalculation(String input) throws ExpressionInvalidException {
		try {
			CalculationStatus calculationStatus = new CalculationStatus();
			Stack<Bracelet> BS = new Stack<Bracelet>();
			boolean
				status = false, // true: previous was number/closing brace; false: previous was operand/opening brace.
				hadClosingBrace = false,
				hadPercent = false;
			char thousandSeparator = decimalDot ? '.' : ',';
			Operand currentOperand;
			Function currentFunction;
			Bracelet currentBracelet;
			for (int i = 0; i < input.length(); i++) {
				char c = input.charAt(i);
				if (thousandDot && c == thousandSeparator) {
					if (status && !calculationStatus.isVariable) continue; else throw new ExpressionInvalidException("unexpectedThousandSeparator", i+1);
				} else if (c == '-' && !status) {
					calculationStatus.negativity = !calculationStatus.negativity;
					calculationStatus.hadNegation = true;
				} else if (c == '%') {
					if (hadPercent) throw new ExpressionInvalidException("unexpectedPercent", i + 1);
					if (hadClosingBrace) {
						calculationStatus.NS.push(calculationStatus.NS.pop() / 100d);
						hadPercent = true;
					} else if (!status || calculationStatus.currentToken.charAt(calculationStatus.currentToken.length() - 1) == '%') throw new ExpressionInvalidException("unexpectedPercent", i+1); else calculationStatus.currentToken += c;
				} else if (c == ';') {
					if (!BS.isEmpty()) {
						if (status) {
							if (calculationStatus.currentToken.length() != 0) calculationStatus.NS.push(processNumberToken(calculationStatus, i));
							performBacktrackCalculation(calculationStatus, false);
							BS.peek().addArgument(calculationStatus.NS.pop());
							status = false;
							hadClosingBrace = false;
							hadPercent = false;
						} else if (calculationStatus.OS.peek() instanceof OpeningBrace) {
							BS.peek().addArgument(0);
							status = false;
							hadClosingBrace = false;
							hadPercent = false;
						} else throw new ExpressionInvalidException("unexpectedSemicolon", i+1);
					} else throw new ExpressionInvalidException("unexpectedSemicolon", i+1);
				} else if (isDecimalSeparator(c)) {
					if (calculationStatus.currentToken.length() == 0) {
						if (hadClosingBrace) {
							while (!calculationStatus.OS.isEmpty() && dotlessMulOp.getPriority() < calculationStatus.OS.peek().getPriority()) performBacktrackSameLevelCalculation(calculationStatus);
							calculationStatus.OS.push(dotlessMulOp);
							hadClosingBrace = false;
						}
						calculationStatus.currentToken = "0,";
						status = true;
						calculationStatus.isVariable = false;
						calculationStatus.hadComma = true;
						hadPercent = false;
					} else if (status) {
						if (calculationStatus.isVariable || calculationStatus.hadComma) throw new ExpressionInvalidException("unexpectedDecimalSeparator", i+1);
						calculationStatus.currentToken += c;
						calculationStatus.hadComma = true;
						hadPercent = false;
					} else { };
				} else if (isDigit(c)) {
					if (calculationStatus.currentToken.length() == 0) {
						if (hadClosingBrace) {
							while (!calculationStatus.OS.isEmpty() && dotlessMulOp.getPriority() < calculationStatus.OS.peek().getPriority()) performBacktrackSameLevelCalculation(calculationStatus);
							calculationStatus.OS.push(dotlessMulOp);
							hadClosingBrace = false;
						}
						calculationStatus.currentToken = Character.toString(c);
						status = true;
						calculationStatus.isVariable = false;
						hadPercent = false;
					} else if (status) {
						if (calculationStatus.isVariable) calculationStatus.currentToken += c;
						else if (calculationStatus.currentToken.charAt(calculationStatus.currentToken.length() - 1) == '%') throw new ExpressionInvalidException("unexpectedDigit", i+1);
						else calculationStatus.currentToken += c;
					} else {
						calculationStatus.currentToken = Character.toString(c);
						status = true;
						calculationStatus.isVariable = false;
						hadPercent = false;
					}
				} else if (isChar(c)) {
					if (hadClosingBrace || calculationStatus.currentToken.length() != 0 && !calculationStatus.isVariable) {
						if (calculationStatus.currentToken.length() != 0 && !calculationStatus.isVariable) calculationStatus.NS.push(processNumberToken(calculationStatus, i));
						while (!calculationStatus.OS.isEmpty() && dotlessMulOp.getPriority() < calculationStatus.OS.peek().getPriority()) performBacktrackSameLevelCalculation(calculationStatus);
						calculationStatus.OS.push(dotlessMulOp);
						hadClosingBrace = false;
					}
					calculationStatus.currentToken += c;
					calculationStatus.isVariable = true;
					status = true;
					hadClosingBrace = false;
					hadPercent = false;
				} else if ((currentOperand = operandMap.get(Character.toString(c))) == null) throw new ExpressionInvalidException("unknownSymbol", i+1);
				else {
					if (currentOperand instanceof OpeningBrace) {
						if (hadClosingBrace || calculationStatus.currentToken.length() != 0 && !calculationStatus.isVariable) {
							if (calculationStatus.currentToken.length() != 0 && !calculationStatus.isVariable) calculationStatus.NS.push(processNumberToken(calculationStatus, i));
							while (!calculationStatus.OS.isEmpty() && dotlessMulOp.getPriority() < calculationStatus.OS.peek().getPriority()) performBacktrackSameLevelCalculation(calculationStatus);
							calculationStatus.OS.push(dotlessMulOp);
							hadClosingBrace = false;
						}
						if ((currentFunction = functionMap.get(calculationStatus.currentToken)) == null) throw new ExpressionInvalidException("unknownFunction", i, new String[] { calculationStatus.currentToken });
						calculationStatus.OS.push(currentOperand);
						BS.push(new Bracelet(Character.toString(c), currentFunction, this));
						status = false;
						hadPercent = false;
						calculationStatus.currentToken = "";
					} else if (currentOperand instanceof ClosingBrace) {
						if (status) if (BS.isEmpty()) throw new ExpressionInvalidException("unexpectedClosingBrace", i + 1);
							else if (areBracesMatch(BS.peek().opening, Character.toString(c))) {
								if (calculationStatus.currentToken.length() != 0) calculationStatus.NS.push(processNumberToken(calculationStatus, i));
								performBacktrackCalculation(calculationStatus, false);
								calculationStatus.OS.pop();
								(currentBracelet = BS.pop()).addArgument(calculationStatus.NS.pop());
								calculationStatus.NS.push(currentBracelet.getResult());
								status = true;
								hadClosingBrace = true;
								hadPercent = false;
							} else throw new ExpressionInvalidException("unmatchingBraces", i + 1);
						else if (calculationStatus.OS.isEmpty()) {
							calculationStatus.NS.push(0d);
							status = true;
							hadClosingBrace = true;
							hadPercent = false;
						} else if (calculationStatus.OS.peek() instanceof OpeningBrace) {
							if (!BS.isEmpty() && !areBracesMatch(BS.peek().opening, Character.toString(c))) throw new ExpressionInvalidException("unmatchingBraces", i + 1);
							calculationStatus.OS.pop();
							(currentBracelet = BS.pop()).addArgument(0);
							calculationStatus.NS.push(currentBracelet.getResult());
							status = true;
							hadClosingBrace = true;
							hadPercent = false;
						} else throw new ExpressionInvalidException("unexpectedClosingBrace", i + 1);
					} else {
						if (status) {
							if (enforceMulDiv) switch (c) {
									case '.':
									case ':':
										if (mulAsterisk) throw new ExpressionInvalidException("unknownSymbol", i+1);
										break;
									case '*':
									case '/':
										if (!mulAsterisk) throw new ExpressionInvalidException("unknownSymbol", i+1);
										break;
								}
							if (calculationStatus.currentToken.length() != 0) calculationStatus.NS.push(processNumberToken(calculationStatus, i));
							else if (calculationStatus.hadNegation) throw new ExpressionInvalidException("unexpectedOperand", i);
							while (!calculationStatus.OS.isEmpty() && currentOperand.getPriority() < calculationStatus.OS.peek().getPriority()) performBacktrackSameLevelCalculation(calculationStatus);
							calculationStatus.OS.push(currentOperand);
							status = false;
							hadClosingBrace = false;
							hadPercent = false;
						} else if (c == '+') calculationStatus.hadNegation = true; else throw new ExpressionInvalidException("unexpectedOperand", i+1);
					}
				}
			}
			if (status) {
				if (calculationStatus.currentToken.length() != 0) calculationStatus.NS.push(processNumberToken(calculationStatus, input.length()));
				else if (calculationStatus.hadNegation) throw new ExpressionInvalidException("trailingSign");
				else { };
			} else throw new ExpressionInvalidException("unexpectedEnd");
			while (!BS.isEmpty()) {
				performBacktrackCalculation(calculationStatus, false);
				currentBracelet = BS.pop();
				currentBracelet.addArgument(calculationStatus.NS.pop());
				calculationStatus.NS.push(currentBracelet.getResult());
			}
			performBacktrackCalculation(calculationStatus, true);
			return calculationStatus.NS.pop();
		} catch (ArithmeticException e) {
			throw new ExpressionInvalidException("numberOutOfRange");
		}
	}

	private String lowercaseAndRemoveWhitespace(String stringIn) {
		return stringIn.replace(" ", "").replace("\t", "").replace("\n", "").toLowerCase();
	}

	public double calculate(String expression) throws ExpressionInvalidException {
		String trimmedExpression = lowercaseAndRemoveWhitespace(expression);
		List<String> toAssign = new ArrayList<String>();
		int ps;
		int position = 0;
		mainLoop: while (true) {
			switch (ps = trimmedExpression.indexOf('=')) {
				case -1: break mainLoop;
				case 0: throw new ExpressionInvalidException("unexpectedEqual", Utils.getIndexWithWhitespace(expression, position+1));
				default:
					String s = trimmedExpression.substring(0, ps);
					if (s == "ans" || s == "preAns") throw new ExpressionInvalidException("reservedVariable", Utils.getIndexWithWhitespace(expression, position + ps));
					if (isDigit(s.charAt(0))) throw new ExpressionInvalidException("invalidVariable", Utils.getIndexWithWhitespace(expression, position + ps), new String[] { s }); 
					for (char c : s.toCharArray()) if (!isChar(c) && !isDigit(c)) throw new ExpressionInvalidException("nonAlphanumericVariableName", Utils.getIndexWithWhitespace(expression, position + ps), new String[] { s });
					toAssign.add(s);
					break;
			}
			trimmedExpression = trimmedExpression.substring(ps + 1);
			position += ps + 1;
		}
		if (trimmedExpression.length() == 0) throw new ExpressionInvalidException("nothingToCalculate", Utils.getIndexWithWhitespace(expression, position));
		if (trimmedExpression == "!") {
			for (String s : toAssign) variableMap.remove(s);
			preAns = ans;
			ans = 0;
			return 0;
		}
		double oldAns = ans;
		try {
			ans = performCalculation(trimmedExpression);
		} catch (ExpressionInvalidException e) { // Handle and rethrow the exception to properly position the error in the expression with whitespace.
			throw new ExpressionInvalidException(e.getMessage(), position + Utils.getIndexWithWhitespace(expression, position + e.getPosition()), e.getMessageArguments());
		}
		for (String s : toAssign) variableMap.put(s, ans);
		preAns = oldAns;
		return ans;
	}

	public double getVariable(String name) throws GetVariableException {
		name = lowercaseAndRemoveWhitespace(name);
		if (name.length() == 0) throw new GetVariableException(GetVariableException.Type.EMPTY_NAME);
		if (isDigit(name.charAt(0))) throw new GetVariableException(GetVariableException.Type.INVALID_NAME);
		for (char c : name.toCharArray()) if (!isDigit(c) && !isChar(c)) throw new GetVariableException(GetVariableException.Type.INVALID_NAME);
		switch (name) {
			case "ans": return ans;
			case "preans": return preAns;
		}
		Double p = variableMap.get(name);
		if (p == null && !zeroUndefinedVars) throw new GetVariableException(GetVariableException.Type.NOT_SET);
		return name == null ? 0 : p;
	}

	private double getVariableInternal(String var, int position) throws ExpressionInvalidException {
		String name = lowercaseAndRemoveWhitespace(var);
		switch (name) {
			case "ans": return ans;
			case "preans": return preAns;
		}
		Double p = variableMap.get(name);
		if (p != null) return p;
		else if (zeroUndefinedVars) return 0;
		else throw new ExpressionInvalidException("variableNotSet", position, new String[] { var });
	}
	
	private class Bracelet {
		public String opening;
		public Function functionAssigned;
		public List<Double> arguments = new ArrayList<Double>();
		private CalculatorEngine engine;

		public Bracelet(String openingIn, Function functionIn, CalculatorEngine engineIn) {
			opening = openingIn;
			functionAssigned = functionIn;
			engine = engineIn;
		}

		public void addArgument(double argumentIn) {
			arguments.add(argumentIn);
		}

		public double getResult() throws ExpressionInvalidException {
			return functionAssigned.calculate(arguments, engine);
		}
	}
	
	private class CalculationStatus {
		public Stack<Double>
			NS = new Stack<Double>(),
			TNS = new Stack<Double>();
		public Stack<Operand>
			OS = new Stack<Operand>(),
			TOS = new Stack<Operand>();
		public boolean
			negativity = false,
			hadNegation = false,
			isVariable = false,
			hadComma = false;
		String currentToken = "";
	}
}