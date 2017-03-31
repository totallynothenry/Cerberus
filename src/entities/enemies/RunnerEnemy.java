package entities.enemies;

import entities.projectiles.Bullet;

public class RunnerEnemy extends Enemy {
	private static int RENEMYcount = 0;

	public RunnerEnemy(float x, float y, double t) {
		super(20, "RENEMY" + RENEMYcount++, x, y, t, Bullet.RENEMY);
		setShootCooldown(10);
	}

	public void relocate(float px, float py) {		
		float dx = px- getPos()[0];
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
			setVPolar(5f, theta + Math.PI / 2 + 0.2);

		} 
		if(distance < 400 ){
			setVPolar(-5f, theta + Math.PI / 2 + 0.2);
		}
	}

	public void update(float px, float py, float delta) {
		if (alive) {
			setVPolar(0,0);
			checkShot();
			relocate(px, py);
		}
		super.update(px, py, delta);
	}
}
