package entities.projectiles;

import entities.Entity;
import entities.HitBox;
import entities.explosions.SmallExplosion;
import game.Game;
import graphics.Sprite;

public class PlayerMissile extends Projectile {
	private static final int hitBoxRadius = 30;

	private static int pmCount = 0;
	private String tar;

	public PlayerMissile(String enemyUID, String HostUID, float x, float y, double t, int team) {
		super(HostUID + "_" + "PMISSILE" + pmCount++, x, y, t, team, new HitBox(x, y, hitBoxRadius));
		sprite = new Sprite(40, 40, 0, 105, 1);
		tar = enemyUID;
	}

	public void update(float px, float py, float delta) {
		seek();
		super.update(px, py, delta);
	}

	private void seek() {
		Entity target = null;
		if (tar != null) {
			target = Game.enemies.get(tar);
		}
		if (target != null) {
			float dx = target.getPos()[0] - getPos()[0];
			float dy = target.getPos()[1] - getPos()[1];

			double theta = 0;
			if (dx > 0) {
				theta = Math.atan(dy / dx) - Math.PI / 2;
			}
			if (dx < 0) {
				theta = Math.PI / 2 + Math.atan(dy / dx);
			}
			setRotation(theta, 0.01);
			setVPolar(15f, getPos()[2] + Math.PI / 2);
		} else {
			setVPolar(15f, getPos()[2] + Math.PI / 2);
		}
	}
	public void destroy(){
		SmallExplosion e = new SmallExplosion(getPos()[0],getPos()[1]);
		Game.explosions.put(e.UID, e);
		super.destroy();
	}
}
