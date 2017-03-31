package entities;

public class BossHitBox extends HitBox {
	private float semiWidth;
	private float semiHeight;

	private double theta;

	public BossHitBox(float bx, float by, double theta) {
		super(bx, by, 0);
		semiWidth = 180;
		semiHeight = 250;

		this.theta = theta;
	}

	public void update(float[] arr) {
		theta = arr[2];
		double x = arr[0] + 75 * Math.cos(theta + Math.PI / 2);
		double y = arr[1] + 75 * Math.sin(theta + Math.PI / 2);
		super.update((float) x, (float) y);
	}

	public boolean checkCollide(HitBox h) {
		double uLx = getX() + semiHeight * Math.cos(theta + Math.PI / 2) - semiWidth * Math.sin(theta + Math.PI / 2);
		double uRx = getX() + semiHeight * Math.cos(theta + Math.PI / 2) + semiWidth * Math.sin(theta + Math.PI / 2);
		double dLx = getX() - semiHeight * Math.cos(theta + Math.PI / 2) - semiWidth * Math.sin(theta + Math.PI / 2);
		double dRx = getX() - semiHeight * Math.cos(theta + Math.PI / 2) + semiWidth * Math.sin(theta + Math.PI / 2);

		double uLy = getY() + semiHeight * Math.sin(theta + Math.PI / 2) - semiWidth * Math.cos(theta + Math.PI / 2);
		double uRy = getY() + semiHeight * Math.sin(theta + Math.PI / 2) + semiWidth * Math.cos(theta + Math.PI / 2);
		double dLy = getY() - semiHeight * Math.sin(theta + Math.PI / 2) - semiWidth * Math.cos(theta + Math.PI / 2);
		double dRy = getY() - semiHeight * Math.sin(theta + Math.PI / 2) + semiWidth * Math.cos(theta + Math.PI / 2);

		float hcx = h.getX();
		float hcy = h.getY();
		float hr = h.getRadius();

		// Estimates if the other hBox's center is even close to intersection
		boolean l = hcx + hr < uLx && h.getX() < uRx && h.getX() < dLx && h.getX() < dRx;
		boolean r = hcx - hr > uLx && h.getX() > uRx && h.getX() > dLx && h.getX() > dRx;
		boolean d = hcy + hr < uLy && h.getY() < uRy && h.getY() < dLy && h.getY() < dRy;
		boolean u = hcy - hr > uLy && h.getY() > uRy && h.getY() > dLy && h.getY() > dRy;

		if (l || r || d || u) {
			return false;
		}

		/*
		 * Checks if the two circular HitBoxes overlap
		 */
		if (findDistancePL(uLx, uLy, dLx, dLy, hcx, hcy) + findDistancePL(uRx, uRy, dRx, dRy, hcx, hcy) >= 2
				* semiWidth) {
			return true;
		}
		return false;
	}

	private double findDistancePL(double u0, double v0, double u1, double v1, double x, double y) {
		double A = -(v1 - v0);
		double B = u1 - u0;
		double C = -u0 * A - v0 * B;

		return Math.abs(A * x + B * y + C) / magnitude(A, B);
	}

	private double magnitude(double a, double b) {
		return Math.sqrt(a * a + b * b);
	}
}
