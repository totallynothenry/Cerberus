package entities.projectiles;

import entities.HitBox;
import graphics.Sprite;

public class BossLaser extends Projectile {
	private static final int hitBoxRadius = 20;

	private static int bLaserCount = 0;

	public BossLaser(String HostUID, float x, float y, double t, int team) {
		super(HostUID + "_" + "BOSSL" + bLaserCount++, x, y, t, team, new HitBox(x, y, hitBoxRadius));
		sprite = new Sprite(30, 63, 0, 42, 1);
	}

	public void update(float px, float py, float delta) {
		setVPolar(50, getPos()[2] + Math.PI / 2);
		super.update(px, py, delta);
	}
}
