package entities.projectiles;

import entities.HitBox;
import entities.explosions.SmallExplosion;
import game.Game;
import graphics.Sprite;

public class BossMissile extends Projectile {
	private static final int hitBoxRadius = 50;

	private static int bmCount = 0;

	private int frameCount;

	public BossMissile(String HostUID, float x, float y, double t, int team) {
		super(HostUID + "_" + "BMISSILE" + bmCount++, x, y, t, team, new HitBox(x, y, hitBoxRadius));

		sprite = new Sprite(60, 60, 0, 145, 1);
		frameCount = 0;
	}

	public void update(float px, float py, float delta) {
		seek();
		if (frameCount++ > 600) {
			destroy();
		}
		super.update(px, py, delta);
	}

	private void seek() {
		if (Game.player.isAlive()) {
			float dx = Game.player.getPos()[0] - getPos()[0];
			float dy = Game.player.getPos()[1] - getPos()[1];

			double theta = 0;
			if (dx > 0) {
				theta = Math.atan(dy / dx) - Math.PI / 2;
			}
			if (dx < 0) {
				theta = Math.PI / 2 + Math.atan(dy / dx);
			}
			setRotation(theta, 0.05);
			setVPolar(15f, getPos()[2] + Math.PI / 2);
		} else {
			setVPolar(15f, getPos()[2] + Math.PI / 2);
		}
	}

	public void destroy() {
		SmallExplosion e = new SmallExplosion(getPos()[0], getPos()[1]);
		Game.explosions.put(e.UID, e);
		super.destroy();
	}
}
