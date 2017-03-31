package entities.enemies;

import entities.projectiles.Bullet;

public class CircleEnemy extends Enemy {
	private static int CENEMYcount = 0;

	public CircleEnemy(float x, float y, double t) {
		super(15, "CENEMY" + CENEMYcount++, x, y, t, Bullet.CENEMY);
		setShootCooldown(20);
	}

	public void circlePlayer(float px, float py) {

		float dx = px - getPos()[0];
		float dy = py - getPos()[1];

		double distance = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));

		// Gets angle to player
		double theta = 0;
		if (dx > 0) {
			theta = Math.atan(dy / dx) - Math.PI / 2;
		}
		if (dx < 0) {
			theta = Math.PI / 2 + Math.atan(dy / dx);
		}

		// Rotates to face player
		setRotation(theta, 0.05);

		if (distance > 600) {
			double thetaD = Math.asin(600 / distance);
			setVPolar(3f, theta + thetaD + Math.PI / 2);
		} else if (distance < 500) {
			setVPolar(-5f, theta + Math.PI / 2);
		} else {
			double v = Math.sqrt(Math.pow(getVx(), 2) + Math.pow(getVy(), 2));
			setAPolar(Math.pow(v, 2) / distance, theta - Math.PI);
		}

	}

	public void update(float px, float py, float delta) {
		if (alive) {
			checkShot();
			circlePlayer(px, py);
		}
		super.update(px, py, delta);
	}

}
