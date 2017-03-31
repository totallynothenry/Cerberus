package entities.projectiles;

import entities.HitBox;
import graphics.Sprite;

public class BossLaserBeam extends Projectile{
	private static final int hitBoxRadius = 20;

	private static int bLaserBCount = 0;

	public BossLaserBeam(String HostUID, float x, float y, double t, int team) {
		super(HostUID + "_" + "BOSSLB" + bLaserBCount++, x, y, t+Math.PI/2, team, new HitBox(x, y, hitBoxRadius));
		sprite = new Sprite(300, 100, 0, 205, 1);
	}

	public void update(float px, float py, float delta) {
		setVPolar(80, getPos()[2]+Math.PI);
		super.update(px, py, delta);
	}
}
