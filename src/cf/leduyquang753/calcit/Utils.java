package cf.leduyquang753.calcit;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

public class Utils {
	private static final double
		degInRad = Math.PI / 180,
		radInDeg = 180 / Math.PI,
		degInGrad = 10 / 9,
		gradInDeg = 0.9,
		gradInRad = Math.PI / 200,
		radInGrad = 200 / Math.PI;
	
	/**
	 * Power function.
	 * @throws ExpressionInvalidException 
	 */
	public static double power(double baseNum, double exponent, CalculatorEngine engine) throws ExpressionInvalidException {
		if (baseNum == 0) if (exponent > 0) return 0; else throw new ExpressionInvalidException("divisionByZero");
		if (exponent < 0) return 1 / Math.pow(baseNum, -exponent);
		double roundedExponent = Math.round(exponent);
		if (Math.abs(roundedExponent - exponent) < 1E-11)
			return baseNum > 0 || mod(roundedExponent, 2) == 0 ? Math.pow(baseNum, roundedExponent) : -Math.pow(-baseNum, roundedExponent);
		else if (baseNum > 0) return Math.pow(baseNum, exponent); else throw new ExpressionInvalidException("unsupportedExponentiation", new Object[] { formatNumber(baseNum, engine), formatNumber(exponent, engine) });
	}
	
	public static double degToRad(double degs) {
		return degInRad * degs;
	}

	public static double radToDeg(double rads) {
		return radInDeg * rads;
	}

	public static double degToGrad(double degs) {
		return degInGrad * degs;
	}

	public static double gradToDeg(double grads) {
		return gradInDeg * grads;
	}

	public static double radToGrad(double rads) {
		return radInGrad * rads;
	}

	public static double gradToRad(double grads) {
		return gradInRad * grads;
	}

	public static double div(double dividend, double divisor) {
		return Math.floor(dividend / divisor);
	}

	public static double mod(double dividend, double divisor) {
		return dividend - Math.floor(dividend / divisor) * divisor;
	}

	public static double roundUp(double num) {
		return num >= 0 ? Math.ceil(num) : Math.floor(num);
	}

	public static double roundDown(double num) {
		return num >= 0 ? Math.floor(num) : Math.ceil(num);
	}
	
	private static final DecimalFormat internalNumberFormat = new DecimalFormat("#,##0.##########", DecimalFormatSymbols.getInstance(Locale.ENGLISH));	
	
	private static String getFormattedNumberInternal(double number, CalculatorEngine engine, char mulSign) {
		String toReturn = internalNumberFormat.format(number).replace(",", " ").replace("E", mulSign + "10^");
		if (!engine.decimalDot) toReturn = toReturn.replace(".", ",");
		if (engine.thousandDot) toReturn = toReturn.replace(" ", engine.decimalDot ? "," : ".");
		return toReturn;
	}
	
	public static String formatNumber(double number, CalculatorEngine engine) {
		try {
			char mulSign = engine.mulAsterisk || engine.decimalDot || (!engine.decimalDot && engine.thousandDot) ? '*' : '.';
			String toReturn;
			double log = Math.log10(Math.abs(number));
			if (number != 0 && (log <= -7 || log >= 18))
			{
				int exponent = (int)Math.floor(Math.log10(Math.abs(number)) / 3) * 3;
				double displayedNumber = number * Math.pow(10, -exponent);
				if (Double.isNaN(displayedNumber)) return null;
				toReturn = getFormattedNumberInternal(displayedNumber, engine, mulSign);
				return exponent == 0 ? toReturn : toReturn + mulSign + "10^" + exponent;
			}
			String formatted = getFormattedNumberInternal(number, engine, mulSign);
			toReturn = "";
			int digitCount = -1;
			char decimalSeparator = engine.decimalDot ? '.' : ',';
			for (char c : formatted.toCharArray()) {
				if (c == decimalSeparator) {
					toReturn += c;
					digitCount = 0;
				}
				else if (c == mulSign) {
					toReturn += c;
					digitCount = -1;
				} else {
					if (digitCount == -1) {
						toReturn += c;
						continue;
					}
					else if (digitCount == 10) continue;
					else {
						toReturn += c;
						digitCount++;
					}
				}
			}
			return toReturn;
		} catch (Exception e) {
			return null;
		}
	}
	
	public static int getIndexWithWhitespace(String text, int indexWithoutWhitespace) {
		int position = -1, oldPosition = -1;
		for (char c : text.toCharArray()) {
			position++;
			if (c != ' ' && ++oldPosition == indexWithoutWhitespace-1) return position+1;
		}
		return text.length();
	}

	public static final int[] days = new int[] { 0, 31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31 };
	public static double getMonthDays(double year, int month) {
		return month == 2 ? isLeapYear(year) ? 29 : 28 : days[month];
	}
	public static boolean divisible(double dividend, double divisor) {
		return mod(dividend, divisor) < 0.5;
	}
	public static boolean isLeapYear(double year) {
		return divisible(year, 4) && (!divisible(year, 100) || divisible(year, 400));
	}
	public static final int[] monthPos     = new int[] { 0, 0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334, 365 },
	                          monthPosLeap = new int[] { 0, 0, 31, 60, 91, 121, 152, 182, 213, 244, 274, 305, 335, 366 };

	public static double[] getYearAndDayOfYearFromIndex(double index) {
		int cycles;
		double temp;
		double year = (temp = Math.floor(index / 146097)) * 400; // Gregorian calendar repeats every 146 097 days, or 400 years.
		if ((int)(index -= temp * 146097) == 146096) return new double[] { year + 400, 365 }; // Handle the last day of the cycle, which is the 366th day of the 400th year.
		return new double[] { year	+ (cycles = (int)Math.floor(index / 36524)) * 100 // In each repeat cycle, it repeats every 100 years, or 36 524 days; the only irregular year is the 400th year which is a leap year.
		                    + (cycles = (int)Math.floor((index -= cycles * 36524) / 1461)) * 4 // In that sub-cycle, it also repeats every 4 years or 1461 days, except the 100th which is not a leap year.
			                + (cycles = (int)Math.floor((index -= cycles * 1461) / 365)) // In that sub-sub-cycle, it also repeats every year, or 365 days, except the 4th which is a leap year.
			                + (cycles == 4 ? 0 : 1), // Handle the last day of the 4-year cycle.
			cycles == 4 ? 365 : index - cycles * 365
		};
	}

	public static double[] getMonthAndDayOfMonthFromIndex(double index) {
		double[] res = getYearAndDayOfYearFromIndex(index);
		int[] table = isLeapYear(res[0]) ? monthPosLeap : monthPos;
		int i;
		for (i = 0; i < 12; i++) if ((int)Math.floor(res[1]) < table[i+1]) break;
		return new double[] { i, res[1] - table[i] + 1 };
	}

	public static double getHourFromIndex(double index) {
		return Math.floor(24 * mod(index, 1));
	}
}
