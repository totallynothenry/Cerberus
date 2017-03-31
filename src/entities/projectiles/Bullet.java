package entities.projectiles;

import entities.HitBox;
import graphics.Sprite;

public class Bullet extends Projectile {
	public static final int PLAYER = 0;
	public static final int BENEMY = 1;
	public static final int CENEMY = 2;
	public static final int RENEMY = 3;

	private static final int hitBoxRadius = 15;

	private static int bCount = 0;

	public Bullet(String HostUID, float x, float y, double t, int team) {
		super(HostUID + "_" + "BULLET" + bCount++, x, y, t, team, new HitBox(x, y, hitBoxRadius));
		switch (team - 100) {
		case PLAYER:
			sprite = new Sprite(20, 42, 60, 0, 1);
			break;
		case BENEMY:
			sprite = new Sprite(20, 42, 0, 0, 1);
			break;
		case CENEMY:
			sprite = new Sprite(20, 42, 20, 0, 1);
			break;
		case RENEMY:
			sprite = new Sprite(20, 42, 40, 0, 1);
			break;
		default:
			sprite = new Sprite(20, 42, 60, 0, 1);
		}
	}

	public void update(float px, float py, float delta) {
		setVPolar(50, getPos()[2] + Math.PI / 2);
		super.update(px, py, delta);
	}
}
