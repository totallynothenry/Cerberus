package entities.enemies;

import entities.projectiles.Bullet;

public class BasicEnemy extends Enemy {

	private static int BENEMYcount = 0;

	public BasicEnemy(float x, float y, double t) {
		super(25, "BENEMY" + BENEMYcount++, x, y, t, Bullet.BENEMY);
		setShootCooldown(25);
	}

	public void seekPlayer(float px, float py) {
		float dx = px - getPos()[0];
		float dy = py - getPos()[1];
		double theta = 0;
		if (dx > 0) {
			theta = Math.atan(dy / dx) - Math.PI / 2;
		}
		if (dx < 0) {
			theta = Math.PI / 2 + Math.atan(dy / dx);
		}
		setRotation(theta, 0.025);
		if (Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2)) > 800) {
			setVPolar(2.5f, theta + Math.PI / 2);
		}
	}

	public void update(float px, float py, float delta) {
		if (alive) {
			setVPolar(0, 0);
			checkShot();
			
			//BENEMY specific movement code
			seekPlayer(px, py);
			
		}
		super.update(0, 0, delta);
	}
}
