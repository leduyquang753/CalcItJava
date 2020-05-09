package cf.leduyquang753.calcit;

public interface AngleUnit {
	public static final AngleUnit DEGREE = new Degree();
	public static final AngleUnit RADIAN = new Radian();
	public static final AngleUnit GRADIAN = new Gradian();
	
	double convertToDegrees(double angle);
	double convertFromDegrees(double angle);
	double convertToRadians(double angle);
	double convertFromRadians(double angle);
	double convertToGradians(double angle);
	double convertFromGradians(double angle);
	
	static class Degree implements AngleUnit {
		@Override
		public double convertToDegrees(double angle) {
			return angle;
		}
		@Override
		public double convertFromDegrees(double angle) { 
			return angle;
		}
		@Override
		public double convertToRadians(double angle) {
			return Utils.degToRad(angle);
		}
		@Override
		public double convertFromRadians(double angle) {
			return Utils.radToDeg(angle);
		}
		@Override
		public double convertToGradians(double angle) {
			return Utils.degToGrad(angle);
		}
		@Override
		public double convertFromGradians(double angle) {
			return Utils.radToDeg(angle);
		}
	}

	static class Radian implements AngleUnit {
		@Override
		public double convertToDegrees(double angle) {
			return Utils.radToDeg(angle);
		}
		@Override
		public double convertFromDegrees(double angle) {
			return Utils.degToRad(angle);
		}
		@Override
		public double convertToRadians(double angle) {
			return angle;
		}
		@Override
		public double convertFromRadians(double angle) {
			return angle;
		}
		@Override
		public double convertToGradians(double angle) {
			return Utils.radToGrad(angle);
		}
		@Override
		public double convertFromGradians(double angle) {
			return Utils.gradToRad(angle);
		}
	}

	static class Gradian implements AngleUnit {
		@Override
		public double convertToDegrees(double angle) {
			return Utils.gradToDeg(angle);
		}
		@Override
		public double convertFromDegrees(double angle) {
			return Utils.degToGrad(angle);
		}
		@Override
		public double convertToRadians(double angle) {
			return Utils.gradToRad(angle);
		}
		@Override
		public double convertFromRadians(double angle) {
			return Utils.radToGrad(angle);
		}
		@Override
		public double convertToGradians(double angle) {
			return angle;
		}
		@Override
		public double convertFromGradians(double angle) {
			return angle;
		}
	}
}
