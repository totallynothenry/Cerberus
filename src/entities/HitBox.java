package entities;

public class HitBox {
	private float x;
	private float y;

	private float radius;

	public HitBox(float ex, float ey, float rad) {
		radius = rad;
		update(ex, ey);
	}

	public void update(float x, float y) {
		this.x = x;
		this.y = y;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public float getRadius() {
		return radius;
	}

	public boolean checkCollide(HitBox h) {
		/*
		 * Checks if the two circular HitBoxes overlap
		 */
		if (Math.sqrt(Math.pow(h.getX() - x, 2) + Math.pow(h.getY() - y, 2)) < radius
				+ h.getRadius()) {
			return true;
		}
		return false;
	}

}
