package cf.leduyquang753.calcit;

import java.util.List;
import java.util.Random;

public abstract class Function {
	private String[] names;
	private static Random random = new Random();
	
	public Function(String[] names) {
		this.names = names;
	}
	
	public abstract double calculate(List<Double> arguments, CalculatorEngine engine) throws ExpressionInvalidException;
	
	protected static double total(List<Double> arguments) {
		double total = 0;
		for (double d : arguments) total += d;
		return total;
	}
	
	public String[] getNames() {
		return names;
	}
	
	protected Random getRandom() {
		return random;
	}
	
	public static class Sum extends Function {
		public Sum() {
			super(new String[] { "", "sum", "total" });
		}
		@Override
		public double calculate(List<Double> arguments, CalculatorEngine engine) throws ExpressionInvalidException {
			return total(arguments);
		}
	}

	public static class Sin extends Function {
		public Sin() {
			super(new String[] { "sin", "sine" });
		}
		@Override
		public double calculate(List<Double> arguments, CalculatorEngine engine) throws ExpressionInvalidException {
			return Math.sin(engine.angleUnit.convertToRadians(total(arguments)));
		}
	}

	public static class Cos extends Function {
		public Cos() {
			super(new String[] { "cos", "cosine" });
		}
		@Override
		public double calculate(List<Double> arguments, CalculatorEngine engine) throws ExpressionInvalidException {
			return Math.cos(engine.angleUnit.convertToRadians(total(arguments)));
		}
	}

	public static class Tan extends Function {
		public Tan() {
			super(new String[] { "tan", "tangent", "tang", "tg" });
		}
		@Override
		public double calculate(List<Double> arguments, CalculatorEngine engine) throws ExpressionInvalidException {
			double tot = total(arguments);
			if (Math.cos(tot) == 0) throw new ExpressionInvalidException("divisionByZero");
			return Math.tan(engine.angleUnit.convertToRadians(tot));
		}
	}

	public static class Cot extends Function {
		public Cot() {
			super(new String[] { "cot", "cotangent", "cotang", "cotg" });
		}
		@Override
		public double calculate(List<Double> arguments, CalculatorEngine engine) throws ExpressionInvalidException {
			double tot = total(arguments);
			if (Math.sin(tot) == 0) throw new ExpressionInvalidException("divisionByZero");
			return 1 / Math.tan(engine.angleUnit.convertToRadians(tot));
		}
	}

	public static class ArcSin extends Function {
		public ArcSin() {
			super(new String[] { "arcsin", "arcsine", "sin_1", "sine_1", "asin" });
		}
		@Override
		public double calculate(List<Double> arguments, CalculatorEngine engine) throws ExpressionInvalidException {
			double tot = total(arguments);
			if (tot < -1 || tot > 1) throw new ExpressionInvalidException("invalidArcsinArg"); // TODO extends Add the number.
			return engine.angleUnit.convertFromRadians(Math.asin(tot));
		}
	}

	public static class ArcCos extends Function {
		public ArcCos() {
			super(new String[] { "arccos", "arccosine", "cos_1", "cosine_1", "acos" });
		}
		@Override
		public double calculate(List<Double> arguments, CalculatorEngine engine) throws ExpressionInvalidException {
			double tot = total(arguments);
			if (tot < -1 || tot > 1) throw new ExpressionInvalidException("invalidArccosArg"); // TODO extends Add the number.
			return engine.angleUnit.convertFromRadians(Math.acos(tot));
		}
	}

	public static class ArcTan extends Function {
		public ArcTan() {
			super(new String[] { "arctan", "arctangent", "arctang", "arctg", "tan_1", "tangent_1", "tang_1", "tg_1", "atan", "atg" });
		}
		@Override
		public double calculate(List<Double> arguments, CalculatorEngine engine) throws ExpressionInvalidException {
			return engine.angleUnit.convertFromRadians(Math.atan(total(arguments)));
		}
	}

	public static class ArcCot extends Function {
		public ArcCot() {
			super(new String[] { "arccot", "arccotangent", "arccotang", "arccotg", "cot_1", "cotangent_1", "cotang_1", "cotg_1", "acot", "acotg" });
		}
		@Override
		public double calculate(List<Double> arguments, CalculatorEngine engine) throws ExpressionInvalidException {
			double tot = total(arguments);
			if (tot == 0) return engine.angleUnit.convertFromDegrees(90);
			return engine.angleUnit.convertFromRadians(Math.atan(1 / tot));
		}
	}

	public static class Floor extends Function {
		public Floor() {
			super(new String[] { "floor", "flr" });
		}
		@Override
		public double calculate(List<Double> arguments, CalculatorEngine engine) throws ExpressionInvalidException {
			return Math.floor(total(arguments));
		}
	}

	public static class Abs extends Function {
		public Abs() {
			super(new String[] { "abs", "absolute" });
		}
		@Override
		public double calculate(List<Double> arguments, CalculatorEngine engine) throws ExpressionInvalidException {
			return Math.abs(total(arguments));
		}
	}

	public static class GCD extends Function {
		public GCD() {
			super(new String[] { "gcd", "greatestCommonDivisor", "greatest_common_divisor" });
		}
		@Override
		public double calculate(List<Double> arguments, CalculatorEngine engine) throws ExpressionInvalidException {
			if (arguments.size() == 1) {
				double r = Math.floor(Math.abs(arguments.get(0)));
				return r == 0 ? 1 : r;
			}
			double res = Math.floor(Math.abs(arguments.get(0)));
			for (int i = 1; i < arguments.size(); i++) {
				double n = Math.floor(Math.abs(arguments.get(i)));
				while (n != 0) {
					double temp = n;
					n = Utils.mod(res, n);
					res = temp;
				}
			}
			return res;
		}
	}

	public static class LCM extends Function {
		public LCM() {
			super(new String[] { "lcm", "lowestCommonMultiplier", "lowest_common_multiplier" });
		}
		@Override
		public double calculate(List<Double> arguments, CalculatorEngine engine) throws ExpressionInvalidException {
			if (arguments.size() == 1) return Math.floor(Math.abs(arguments.get(0)));
			double res = Math.floor(Math.abs(arguments.get(0)));
			for (int i = 1; i < arguments.size(); i++) {
				double n = Math.floor(Math.abs(arguments.get(i)));
				double t = n;
				double t2 = res;
				while (t2 != 0) {
					double temp = t2;
					t2 = Utils.mod(n, t2);
					n = temp;
				}
				res = Utils.div(res * t, n);
			}
			return res;
		}
	}

	public static class Fact extends Function {
		public Fact() {
			super(new String[] { "fact", "factorial" });
		}
		@Override
		public double calculate(List<Double> arguments, CalculatorEngine engine) throws ExpressionInvalidException {
			double total = 0;
			for (double argument : arguments) total += argument;
			double n = Math.floor(total);
			if (n < 0) throw new ExpressionInvalidException("invalidFactorialArg");
			total = 1;
			for (double i = 1; i <= n; i += 1) total *= i;
			return total;
		}
	}

	public static class Log extends Function {
		public Log() {
			super(new String[] { "log", "logarithm", "logarid" });
		}
		@Override
		public double calculate(List<Double> arguments, CalculatorEngine engine) throws ExpressionInvalidException {
			if (arguments.size() == 1) {
				if (arguments.get(0) <= 0) throw new ExpressionInvalidException("invalidLogInput");
				return Math.log10(arguments.get(0));
			} else {
				if (arguments.get(0) <= 0 || arguments.get(0) == 1) throw new ExpressionInvalidException("invalidLogBase");
				double total = 0;
				for (int i = 1; i < arguments.size(); i++) total += arguments.get(i);
				if (total <= 0) throw new ExpressionInvalidException("invalidLogInput");
				return Math.log(total)/Math.log(arguments.get(0));
			}
		}
	}

	public static class Ln extends Function {
		public Ln() {
			super(new String[] { "logn", "loge", "natural_algorithm", "natural_logarid" });
		}
		@Override
		public double calculate(List<Double> arguments, CalculatorEngine engine) throws ExpressionInvalidException {
			double tot = total(arguments);
			if (tot <= 0) throw new ExpressionInvalidException("invalidLogInput");
			return Math.log(tot);
		}
	}

	public static class Permutation extends Function {
		public Permutation() {
			super(new String[] { "p", "permutation", "permut" });
		}
		@Override
		public double calculate(List<Double> arguments, CalculatorEngine engine) throws ExpressionInvalidException {
			if (arguments.size() != 2) throw new ExpressionInvalidException("invalidPermutationNumArgs");
			double n = Math.floor(arguments.get(0));
			double k = Math.floor(arguments.get(1));
			if (n < 0 || k < 0) throw new ExpressionInvalidException("invalidPermutationNegativeArgs");
			if (k > n) return 0;
			k = n - k;
			double res = 1;
			while (k < n) res *= k++;
			return res;
		}
	}

	public static class Combination extends Function {
		public Combination() {
			super(new String[] { "c", "combination", "combin" });
		}
		@Override
		public double calculate(List<Double> arguments, CalculatorEngine engine) throws ExpressionInvalidException {
			if (arguments.size() != 2) throw new ExpressionInvalidException("invalidCombinationNumArgs");
			double n = Math.floor(arguments.get(0));
			double k = Math.floor(arguments.get(1));
			if (n < 0 || k < 0) throw new ExpressionInvalidException("invalidCombinationNegativeArgs");
			if (k > n) return 0;
			double i = n - k;
			double res = 1;
			while (i < n) res *= ++i;
			i = 0;
			while (i < k) res /= ++i;
			return res;
		}
	}

	public static class Round extends Function {
		public Round() {
			super(new String[] { "round", "rnd" });
		}
		@Override
		public double calculate(List<Double> arguments, CalculatorEngine engine) throws ExpressionInvalidException {
			double total = 0;
			for (double argument : arguments) total += argument;
			return Math.round(total);
		}
	}

	public static class DegToRad extends Function {
		public DegToRad() {
			super(new String[] { "dtr", "degToRad", "deg_to_rad", "degreesToRadians", "degrees_to_radians" });
		}
		@Override
		public double calculate(List<Double> arguments, CalculatorEngine engine) throws ExpressionInvalidException {
			return Utils.degToRad(total(arguments));
		}
	}

	public static class RadToDeg extends Function {
		public RadToDeg() {
			super(new String[] { "rtd", "radToDeg", "rad_to_deg", "radiansToDegrees", "radians_to_degrees" });
		}
		@Override
		public double calculate(List<Double> arguments, CalculatorEngine engine) throws ExpressionInvalidException {
			return Utils.radToDeg(total(arguments));
		}
	}

	public static class DegToGrad extends Function {
		public DegToGrad() {
			super(new String[] { "dtg", "degToGrad", "deg_to_grad", "degreesToGradians", "degrees_to_gradians" });
		}
		@Override
		public double calculate(List<Double> arguments, CalculatorEngine engine) throws ExpressionInvalidException {
			return Utils.degToGrad(total(arguments));
		}
	}

	public static class GradToDeg extends Function {
		public GradToDeg() {
			super(new String[] { "gtd", "gradToDeg", "grad_to_deg", "gradiansToDegrees", "gradians_to_degrees" });
		}
		@Override
		public double calculate(List<Double> arguments, CalculatorEngine engine) throws ExpressionInvalidException {
			return Utils.gradToDeg(total(arguments));
		}
	}

	public static class GradToRad extends Function {
		public GradToRad() {
			super(new String[] { "gtr", "gradToRad", "grad_to_rad", "gradiansToRadians", "gradians_to_radians" });
		}
		@Override
		public double calculate(List<Double> arguments, CalculatorEngine engine) throws ExpressionInvalidException {
			return Utils.gradToRad(total(arguments));
		}
	}

	public static class RadToGrad extends Function {
		public RadToGrad() {
			super(new String[] { "rtg", "radToGrad", "rad_to_grad", "radiansToGradians", "radians_to_gradians" });
		}
		@Override
		public double calculate(List<Double> arguments, CalculatorEngine engine) throws ExpressionInvalidException {
			return Utils.radToGrad(total(arguments));
		}
	}

	public static class Max extends Function {
		public Max() {
			super(new String[] { "max", "maximum" });
		}
		@Override
		public double calculate(List<Double> arguments, CalculatorEngine engine) throws ExpressionInvalidException {
			double max = 0;
			boolean isFirst = true;
			for (double num : arguments)
				if (isFirst) {
					isFirst = false;
					max = num;
				} else if (num > max) max = num;
			return max;
		}
	}

	public static class Min extends Function {
		public Min() {
			super(new String[] { "min", "minimum" });
		}
		@Override
		public double calculate(List<Double> arguments, CalculatorEngine engine) throws ExpressionInvalidException {
			double min = 0;
			boolean isFirst = true;
			for (double num : arguments)
				if (isFirst) {
					isFirst = false;
					min = num;
				} else if (num < min) min = num;
			return min;
		}
	}

	public static class Average extends Function {
		public Average() {
			super(new String[] { "avg", "average" });
		}
		@Override
		public double calculate(List<Double> arguments, CalculatorEngine engine) throws ExpressionInvalidException {
			return total(arguments) / arguments.size();
		} // No need to worry about division by zero, there can never be zero arguments.
	}

	public static class RandomFunc extends Function {
		public RandomFunc() {
			super(new String[] { "random", "rand" });
		}
		@Override
		public double calculate(List<Double> arguments, CalculatorEngine engine) throws ExpressionInvalidException {
			switch (arguments.size()) {
				case 1: return random.nextDouble() * arguments.get(0);
				case 2: return arguments.get(0) + (arguments.get(1) - arguments.get(0)) * random.nextDouble();
				default: throw new ExpressionInvalidException("invalidRandomNumArgs");
			}
		}
	}

	public static class RandomInt extends Function {
		private static final double aLittleBitMoreThanOne = 1 + 1E-10;
		public RandomInt() {
			super(new String[] { "randomInt", "randInt", "randomInteger", "random_integer" });
		}
		@Override
		public double calculate(List<Double> arguments, CalculatorEngine engine) throws ExpressionInvalidException {
			double lower, higher;
			switch (arguments.size()) {
				case 1: lower = 0; higher = arguments.get(0); break;
				case 2: lower = arguments.get(0); higher = arguments.get(1); break;
				default: throw new ExpressionInvalidException("invalidRandomNumArgs");
			}
			if (lower > higher) {
				double temp = lower;
				lower = higher;
				higher = temp;
			}
			lower = Utils.roundUp(lower);
			higher = Utils.roundDown(higher);
			if (lower > higher) throw new ExpressionInvalidException("invalidRandomNoIntegerBetween");
			return Utils.roundDown(lower + random.nextDouble() * (higher - lower + aLittleBitMoreThanOne));
		}
	}

	public static class RandomInList extends Function {
		public RandomInList() {
			super(new String[] { "randomInList", "random_in_list", "randInList" });
		}
		@Override
		public double calculate(List<Double> arguments, CalculatorEngine engine) throws ExpressionInvalidException {
			return arguments.get(random.nextInt(arguments.size()));
		}
	}

	public static class IsGreater extends Function {
		public IsGreater() {
			super(new String[] { "isGreater" });
		}
		@Override
		public double calculate(List<Double> arguments, CalculatorEngine engine) throws ExpressionInvalidException {
			if (arguments.size() < 2) throw new ExpressionInvalidException("invalidComparisonNumArgs");
			for (int i = 1; i < arguments.size(); i++)
				if (arguments.get(i) >= arguments.get(i - 1)) return 0;
			return 1;
		}
	}

	public static class IsSmaller extends Function {
		public IsSmaller() {
			super(new String[] { "isSmaller" });
		}
		@Override
		public double calculate(List<Double> arguments, CalculatorEngine engine) throws ExpressionInvalidException {
			if (arguments.size() < 2) throw new ExpressionInvalidException("invalidComparisonNumArgs");
			for (int i = 1; i < arguments.size(); i++)
				if (arguments.get(i) <= arguments.get(i - 1)) return 0;
			return 1;
		}
	}

	public static class IsEqual extends Function {
		public IsEqual() {
			super(new String[] { "isEqual" });
		}
		@Override
		public double calculate(List<Double> arguments, CalculatorEngine engine) throws ExpressionInvalidException {
			if (arguments.size() < 2) throw new ExpressionInvalidException("invalidComparisonNumArgs");
			for (int i = 1; i < arguments.size(); i++)
				if (arguments.get(i) != arguments.get(0)) return 0;
			return 1;
		}
	}

	public static class If extends Function {
		public If() {
			super(new String[] { "if" });
		}
		@Override
		public double calculate(List<Double> arguments, CalculatorEngine engine) throws ExpressionInvalidException {
			if (arguments.size() > 3) throw new ExpressionInvalidException("invalidIfNumArgs");
			while (arguments.size() < 3) arguments.add(0d);
			return arguments.get(0) > 0 ? arguments.get(1): arguments.get(2);
		}
	}

	public static class And extends Function {
		public And() {
			super(new String[] { "and" });
		}
		@Override
		public double calculate(List<Double> arguments, CalculatorEngine engine) throws ExpressionInvalidException {
			for (double num : arguments)
				if (num <= 0) return 0;
			return 1;
		}
	}

	public static class Or extends Function {
		public Or() {
			super(new String[] { "or" });
		}
		@Override
		public double calculate(List<Double> arguments, CalculatorEngine engine) throws ExpressionInvalidException {
			for (double num : arguments)
				if (num > 0) return 1;
			return 0;
		}
	}

	public static class Not extends Function {
		public Not() {
			super(new String[] { "not" });
		}
		@Override
		public double calculate(List<Double> arguments, CalculatorEngine engine) throws ExpressionInvalidException {
			if (arguments.size() != 1) throw new ExpressionInvalidException("invalidNotNumArgs");
			return arguments.get(0) > 0 ? 0 : 1;
		}
	}

	public static class AngleToDegrees extends Function {
		public AngleToDegrees() {
			super(new String[] { "angle to degrees", "angle_to_degrees", "to degrees", "to_degrees", "to deg" });
		}
		@Override
		public double calculate(List<Double> arguments, CalculatorEngine engine) throws ExpressionInvalidException {
			return engine.angleUnit.convertToDegrees(total(arguments));
		}
	}

	public static class AngleToRadians extends Function {
		public AngleToRadians() {
			super(new String[] { "angle to radians", "angle_to_radians", "to radians", "to_radians", "to rad" });
		}
		@Override
		public double calculate(List<Double> arguments, CalculatorEngine engine) throws ExpressionInvalidException {
			return engine.angleUnit.convertToRadians(total(arguments));
		}
	}

	public static class AngleToGradians extends Function {
		public AngleToGradians() {
			super(new String[] { "angle to gradians", "angle_to_gradians", "to gradians", "to_gradians", "to grad" });
		}
		@Override
		public double calculate(List<Double> arguments, CalculatorEngine engine) throws ExpressionInvalidException {
			return engine.angleUnit.convertToGradians(total(arguments));
		}
	}

	public static class AngleFromDegrees extends Function {
		public AngleFromDegrees() {
			super(new String[] { "angle from degrees", "angle_from_degrees", "from degrees", "from_degrees", "from deg" });
		}
		@Override
		public double calculate(List<Double> arguments, CalculatorEngine engine) throws ExpressionInvalidException {
			return engine.angleUnit.convertFromDegrees(total(arguments));
		}
	}

	public static class AngleFromRadians extends Function {
		public AngleFromRadians() {
			super(new String[] { "angle from radians", "angle_from_radians", "from radians", "from_radians", "from rad" });
		}
		@Override
		public double calculate(List<Double> arguments, CalculatorEngine engine) throws ExpressionInvalidException {
			return engine.angleUnit.convertFromRadians(total(arguments));
		}
	}

	public static class AngleFromGradians extends Function {
		public AngleFromGradians() {
			super(new String[] { "angle from gradians", "angle_from_gradians", "from gradians", "from_gradians", "from grad" });
		}
		@Override
		public double calculate(List<Double> arguments, CalculatorEngine engine) throws ExpressionInvalidException {
			return engine.angleUnit.convertFromGradians(total(arguments));
		}
	}

	public static class Date extends Function {
		public Date() {
			super(new String[] { "date" });
		}
		@Override
		public double calculate(List<Double> arguments, CalculatorEngine engine) throws ExpressionInvalidException {
			if (arguments.size() > 6) throw new ExpressionInvalidException("invalidDateNumOfArgs");
			while (arguments.size() < 3) arguments.add(1d);
			while (arguments.size() < 6) arguments.add(0d);
			for (int i = 0; i < 2; i++) arguments.set(i, (double)Math.round(arguments.get(i)));
			if (arguments.get(1) < 0.5 || arguments.get(1) > 12.49999 /* Accounting for rounding errors */) throw new ExpressionInvalidException("invalidDateMonthOutOfRange");
			arguments.set(2, arguments.get(2) - 1 + arguments.get(3) / 24 + arguments.get(4) / 1440 + arguments.get(5) / 86400);
			if (arguments.get(2) < 0 || arguments.get(2) >= Utils.getMonthDays(arguments.get(0), arguments.get(1).intValue())) throw new ExpressionInvalidException("invalidDateDayOutOfRange");
			return (arguments.get(0) - 1) * 365 + Utils.div(arguments.get(0), 4) - Utils.div(arguments.get(0), 100) + Utils.div(arguments.get(0), 400) - (Utils.isLeapYear(arguments.get(0)) && arguments.get(1) < 2.5 ? 1 : 0) + Utils.monthPos[arguments.get(1).intValue()] + arguments.get(2);
		}
	}

	public static class Year extends Function {
		public Year() {
			super(new String[] { "year", "yr" });
		}
		@Override
		public double calculate(List<Double> arguments, CalculatorEngine engine) throws ExpressionInvalidException {
			return Utils.getYearAndDayOfYearFromIndex(total(arguments))[0];
		}
	}

	public static class DayOfYear extends Function {
		public DayOfYear() {
			super(new String[] { "day of year", "day_of_year" });
		}
		@Override
		public double calculate(List<Double> arguments, CalculatorEngine engine) throws ExpressionInvalidException {
			return Utils.getYearAndDayOfYearFromIndex(total(arguments))[1]+1;
		}
	}

	public static class Month extends Function {
		public Month() {
			super(new String[] { "month", "mth" });
		}
		@Override
		public double calculate(List<Double> arguments, CalculatorEngine engine) throws ExpressionInvalidException {
			return Utils.getMonthAndDayOfMonthFromIndex(total(arguments))[0];
		}
	}

	public static class Day extends Function {
		public Day() {
			super(new String[] { "day" });
		}
		@Override
		public double calculate(List<Double> arguments, CalculatorEngine engine) throws ExpressionInvalidException {
			return Math.floor(Utils.getMonthAndDayOfMonthFromIndex(total(arguments))[1]);
		}
	}

	public static class DecimalDay extends Function {
		public DecimalDay() {
			super(new String[] { "decimal day", "decimal_day" });
		}
		@Override
		public double calculate(List<Double> arguments, CalculatorEngine engine) throws ExpressionInvalidException {
			return Utils.getMonthAndDayOfMonthFromIndex(total(arguments))[1];
		}
	}

	public static class Hour extends Function {
		public Hour() {
			super(new String[] { "hour", "hr" });
		}
		@Override
		public double calculate(List<Double> arguments, CalculatorEngine engine) throws ExpressionInvalidException {
			return Utils.getHourFromIndex(total(arguments));
		}
	}

	public static class Minute extends Function {
		public Minute() {
			super(new String[] { "minute", "min" });
		}
		@Override
		public double calculate(List<Double> arguments, CalculatorEngine engine) throws ExpressionInvalidException {
			double index = total(arguments);
			return Math.floor(1440 * (Utils.mod(index, 1) - Utils.getHourFromIndex(index) / 24));
		}
	}

	public static class Second extends Function {
		public Second() {
			super(new String[] { "second", "sec" });
		}
		@Override
		public double calculate(List<Double> arguments, CalculatorEngine engine) throws ExpressionInvalidException {
			double dec = Utils.mod(total(arguments), 1);
			return 86400 * (dec - Math.floor(1440 * dec) / 1440);
		}
	}

	public static class DayOfWeekMondayFirst extends Function {
		public DayOfWeekMondayFirst() {
			super(new String[] { "day of week Monday first", "day_of_week_Monday_first" });
		}
		@Override
		public double calculate(List<Double> arguments, CalculatorEngine engine) throws ExpressionInvalidException {
			double date = Math.floor(total(arguments));
			return date - Math.floor(date / 7) * 7 + 1;
		}
	}

	public static class DayOfWeekSundayFirst extends Function {
		public DayOfWeekSundayFirst() {
			super(new String[] { "day of week Sunday first", "day_of_week_Sunday_first" });
		}
		@Override
		public double calculate(List<Double> arguments, CalculatorEngine engine) throws ExpressionInvalidException {
			double date = Math.floor(total(arguments)) + 1;
			return date - Math.floor(date / 7) * 7 + 1;
		}
	}

	public static class Time extends Function {
		public Time() {
			super(new String[] { "time" });
		}
		@Override
		public double calculate(List<Double> arguments, CalculatorEngine engine) throws ExpressionInvalidException {
			if (arguments.size() > 4) throw new ExpressionInvalidException("invalidTimeNumArgs");
			while (arguments.size() < 4) arguments.add(0d);
			return arguments.get(0) + arguments.get(1) / 24 + arguments.get(2) / 1440 + arguments.get(3) / 86400;
		}
	}
}
