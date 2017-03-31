package entities.enemies;

import java.io.IOException;
import java.util.HashMap;

import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.util.ResourceLoader;

import entities.BossHitBox;
import entities.Entity;
import entities.explosions.BossExplosion;
import entities.projectiles.BossLaser;
import entities.projectiles.BossLaserBeam;
import entities.projectiles.BossMissile;
import entities.projectiles.Bullet;
import entities.projectiles.PlayerMissile;
import entities.ui.BossHPBar;
import game.Game;
import graphics.Sprite;
import render.RenderUtility;

public class Boss extends Entity {
	// Constant for the team
	public static final int BOSS = 4;

	private int state;
	// Constants for all the states
	private static final int STATIC = 0;
	private static final int LASER = 1;
	private static final int BEAM = 2;
	private static final int MISSILE = 3;
	private static final int STATIC2 = 4;
	private static final int LASER2 = 5;
	private static final int BEAM2 = 6;
	private static final int MISSILE2 = 7;

	private static final int INITIAL = 10;

	// Constants for the phases
	private static final int PHASE1 = 0;
	private static final int PHASE2 = 1;

	// Audio files
	private Audio laser;
	private Audio laserBeam;
	private Audio missiles;
	private Audio roar;
	private Audio laserHit;

	// Sprites
	private HashMap<Integer, Sprite> sprites;

	// Last player coords
	private float pxLast;
	private float pyLast;

	// Timing variables
	private float laserCooldown;
	private float missileCooldown;
	private long frameCount;

	private boolean arrivedA;
	private boolean arrivedB;
	private boolean isInvincible;

	private boolean inPhase2;

	private BossHPBar bhp;
	private int health;
	private boolean alive;
	private boolean shouldUpdate;
	
	public Boss(float x, float y, double t) {
		super("BOSS", x, y, t, BOSS, new BossHitBox(x, y, t));

		sprites = new HashMap<Integer, Sprite>();
		createSprites();

		bhp = new BossHPBar();

		try {
			laser = AudioLoader.getAudio("OGG", ResourceLoader.getResourceAsStream("resources/audio/bossLaser.ogg"));
			laserBeam = AudioLoader.getAudio("OGG",
					ResourceLoader.getResourceAsStream("resources/audio/bossLaserBeam.ogg"));
			missiles = AudioLoader.getAudio("OGG", ResourceLoader.getResourceAsStream("resources/audio/bossMissile.ogg"));
			roar = AudioLoader.getAudio("OGG", ResourceLoader.getResourceAsStream("resources/audio/roar.ogg"));
			laserHit = AudioLoader.getAudio("OGG", ResourceLoader.getResourceAsStream("resources/audio/laserHit.ogg"));
		} catch (IOException e) {
			throw new RuntimeException("Boss audio file missing!");
		}

		laserCooldown = 0;
		missileCooldown = 0;
		frameCount = 0;

		arrivedA = false;
		arrivedB = false;
		isInvincible = false;
		inPhase2 = false;

		health = 5000;
		alive = true;
		shouldUpdate=true;
	}

	private void createSprites() {
		Sprite initial = new Sprite(440, 680, 0, 0, 4);
		sprites.put(INITIAL, initial);

		Sprite stat = new Sprite(440, 680, 0, 680, 4);
		Sprite laser = new Sprite(440, 680, 0, 1360, 4);
		Sprite laserb = new Sprite(440, 680, 0, 2040, 4);
		Sprite missile = new Sprite(440, 680, 0, 2720, 4);

		Sprite stat2 = new Sprite(440, 680, 1760, 680, 4);
		Sprite laser2 = new Sprite(440, 680, 1760, 1360, 4);
		Sprite laserb2 = new Sprite(440, 680, 1760, 2040, 4);
		Sprite missile2 = new Sprite(440, 680, 1760, 2720, 4);

		sprites.put(STATIC, stat);
		sprites.put(LASER, laser);
		sprites.put(BEAM, laserb);
		sprites.put(MISSILE, missile);

		sprites.put(STATIC2, stat2);
		sprites.put(LASER2, laser2);
		sprites.put(BEAM2, laserb2);
		sprites.put(MISSILE2, missile2);
	}

	private void phase1(float px, float py) {
		int delay = 1400;
		int lasersT = 400;
		int beamT = 400;
		int missilesT = 600;
		if (frameCount < delay) {
			if (distanceTo(0, 250) > 20) {
				moveToPoint(0.8f, 0, 300);
			} else {
				setVPolar(0, 0);
			}
			isInvincible = true;
			state = INITIAL;
		} else if (frameCount == delay) {
			isInvincible = false;
			roar.playAsSoundEffect(1.0f, 1.0f, false);
			setVelocity(0, 0);
			state = STATIC;
		} else if (frameCount < delay + lasersT) {
			if (distanceTo(-1000, 300) > 20 && !arrivedA) {
				moveToPoint(5f, -1000, 300);
			} else {
				arrivedA = true;
			}
			if (distanceTo(1000, 300) > 20 && arrivedA) {
				moveToPoint(5f, 1000, 300);
			} else {
				arrivedA = false;
			}
			shootLaser(px, py, PHASE1);
		} else if (frameCount < delay + lasersT + beamT) {
			if (distanceTo(-1000, 450) > 20 && !arrivedA) {
				moveToPoint(10f, -1000, 450);
			} else {
				arrivedA = true;
			}
			if (distanceTo(1000, 450) > 20 && arrivedA) {
				moveToPoint(10f, 1000, 450);
			} else {
				arrivedA = false;
			}
			fireBeam(PHASE1);
		} else if (frameCount < delay + lasersT + beamT + missilesT) {
			if (distanceTo(0, 300) > 20) {
				moveToPoint(5f, 0, 300);
			} else {
				setVelocity(0, 0);
				launchMissiles(PHASE1);
			}
		} else {
			frameCount = delay - 1;
		}
		frameCount++;
		// System.out.println(" : [" + frameCount + "]");
	}

	private void phase2(float px, float py) {
		inPhase2 = true;
		if (frameCount == 0) {
			roar.playAsSoundEffect(1.0f, 1.0f, false);
		}
		if (frameCount < 600) {
			moveToPoint(7f, px, py);
			setRotation(getPos()[2] + Math.PI / 2, 0.05f);
			shootLaser(px, py, PHASE2);
		} else if (frameCount < 1000) {
			moveToPoint(2f, px, py);

			float dx = px - getPos()[0];
			float dy = py - getPos()[1];

			double theta = 0;

			if (dx > 0) {
				theta = Math.atan(dy / dx) - Math.PI / 2;
			}
			if (dx < 0) {
				theta = Math.PI / 2 + Math.atan(dy / dx);
			}
			System.out.println(theta);
			setRotation(theta+Math.PI, 0.02f);
			fireBeam(PHASE2);
		} else if (frameCount < 1200) {
			setRotation(0,0.1f);
			moveToPoint(5f, 0, 0);
			launchMissiles(PHASE2);
			health++;
		} else if (frameCount < 1400) {
			moveToPoint(2f, px, py);

			float dx = px - getPos()[0];
			float dy = py - getPos()[1];

			double theta = 0;

			if (dx > 0) {
				theta = Math.atan(dy / dx) - Math.PI / 2;
			}
			if (dx < 0) {
				theta = Math.PI / 2 + Math.atan(dy / dx);
			}
			setRotation(theta+Math.PI, 0.02f);
			fireBeam(PHASE2);
		} else{
			state = STATIC2;
			frameCount = 0;
		}
		frameCount++;
	}

	private void shootLaser(float px, float py, int phase) {
		if (laserCooldown == 0) {
			float dx = 0;
			float dy = 0;
			if (phase == PHASE1) {
				dx = px;
				dy = py;
				laserCooldown = 25;
				state = LASER;
			} else if (phase == PHASE2) {
				dx = 2 * px - pxLast;
				dy = 2 * py - pyLast;
				pxLast = px;
				pyLast = py;
				laserCooldown = 5;
				state = LASER2;
				System.out.println("printing");
			}

			// Spawns first laser
			double theta1 = 0;
			float x1 = getPos()[0] + 75 * (float) Math.cos(getPos()[2] + Math.PI / 2)
					+ 90 * (float) Math.sin(getPos()[2] + Math.PI / 2);
			float y1 = getPos()[1] + 75 * (float) Math.sin(getPos()[2] + Math.PI / 2)
					+ 90 * (float) Math.cos(getPos()[2] + Math.PI / 2);
			float dx1 = dx - x1;
			float dy1 = dy - y1;
			if (dx1 > 0) {
				theta1 = Math.atan(dy1 / dx1) - Math.PI / 2;
			}
			if (dx1 < 0) {
				theta1 = Math.PI / 2 + Math.atan(dy1 / dx1);
			}
			BossLaser bl1 = new BossLaser(UID, x1, y1, theta1, getTeam());
			Game.projectiles.put(bl1.UID, bl1);

			// Spawns second laser
			double theta2 = 0;
			float x2 = getPos()[0] + 85 * (float) Math.cos(getPos()[2] + Math.PI / 2)
					- 65 * (float) Math.sin(getPos()[2] + Math.PI / 2);
			float y2 = getPos()[1] + 85 * (float) Math.sin(getPos()[2] + Math.PI / 2)
					- 65 * (float) Math.cos(getPos()[2] + Math.PI / 2);
			float dx2 = dx - x2;
			float dy2 = dy - y2;
			if (dx2 > 0) {
				theta2 = Math.atan(dy2 / dx2) - Math.PI / 2;
			}
			if (dx2 < 0) {
				theta2 = Math.PI / 2 + Math.atan(dy2 / dx2);
			}
			BossLaser bl2 = new BossLaser(UID, x2, y2, theta2, getTeam());
			Game.projectiles.put(bl2.UID, bl2);

			laser.playAsSoundEffect(1.0f, 1.0f, false);
		}
	}

	private void fireBeam(int phase) {
		if (phase == PHASE1) {
			state = BEAM;
		}
		if (phase == PHASE2) {
			state = BEAM2;
		}
		laserBeam.playAsSoundEffect(1.0f, 0.1f, false);
		float x = getPos()[0] - 240 * (float) Math.cos(getPos()[2] + Math.PI / 2);
		float y = getPos()[1] - 240 * (float) Math.sin(getPos()[2] + Math.PI / 2);

		BossLaserBeam bLB = new BossLaserBeam(UID, x, y, getPos()[2], getTeam());
		Game.projectiles.put(bLB.UID, bLB);
	}

	private void launchMissiles(int phase) {
		if (missileCooldown == 0) {
			if (phase == PHASE1) {
				missileCooldown = 30;
				state = MISSILE;
			} else if (phase == PHASE2) {
				missileCooldown = 5;
				state = MISSILE2;
			}
			float x = getPos()[0] + 170 * (float) Math.cos(getPos()[2] + Math.PI / 2)
					+ 15 * (float) Math.sin(getPos()[2] + Math.PI / 2);
			float y = getPos()[1] + 170 * (float) Math.sin(getPos()[2] + Math.PI / 2)
					+ 15 * (float) Math.cos(getPos()[2] + Math.PI / 2);
			BossMissile bm = new BossMissile(UID, x, y, getPos()[2] - Math.PI, getTeam());
			Game.projectiles.put(bm.UID, bm);

			missiles.playAsSoundEffect(1.0f, 1.0f, false);

		}
	}

	/**
	 * Moves Mecha-Young at a fixed pace to the point specified at the speed
	 * given
	 * 
	 * @param speed
	 *            Speed of movement
	 * @param x
	 *            destination x coordinate
	 * @param y
	 *            destination y coordinate
	 */
	private void moveToPoint(float speed, float x, float y) {
		float dx = x - getPos()[0];
		float dy = y - getPos()[1];
		double theta = 0;
		if (dx > 0) {
			theta = Math.atan(dy / dx);
		}
		if (dx < 0) {
			theta = Math.PI + Math.atan(dy / dx);
		}
		setVPolar(speed, theta);
	}

	/**
	 * Finds the distance between Mecha-Young an a point
	 * 
	 * @param x
	 *            x coordinate of the point
	 * @param y
	 *            y coordinate of the point
	 * @return distance as a double
	 */
	private double distanceTo(float x, float y) {
		float dx = x - getPos()[0];
		float dy = y - getPos()[1];
		return Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
	}

	public void render(Texture sheet, RenderUtility renderer) {
		if (alive) {
			super.render(sheet, renderer, sprites.get(state));
		}
	}

	public void update(float px, float py, float delta) {
		if (alive) {
			if (health > 2000) {
				phase1(px, py);
			} else {
				phase2(px, py);
			}

			if (laserCooldown > 0) {
				laserCooldown--;
			}
			if (missileCooldown > 0) {
				missileCooldown--;
			}

			checkShot();

			if ((health < 2000 && !inPhase2)) {
				frameCount = 0;
			}

			sprites.get(state).update();

			super.update(0, 0, delta);
			((BossHitBox) hBox).update(getPos());
		} else {
			roar.playAsSoundEffect(1.0f, 3.0f, false);
			BossExplosion death = new BossExplosion(getPos()[0], getPos()[1]);
			Game.explosions.put(death.UID, death);
			destroy();
		}
	}

	public void checkShot() {
		for (String key : Game.projectiles.keySet()) {
			Entity focus = Game.projectiles.get(key);
			if (!isInvincible && focus != null && focus.getTeam() - 100 == 0 && hBox.checkCollide(focus.hBox)) {
				if (focus instanceof Bullet) {
					laserHit.playAsSoundEffect(1.0f, 1.0f, false);
					health -= 10;
					health -= 10;
				}
				if (focus instanceof PlayerMissile) {
					laserHit.playAsSoundEffect(1.0f, 1.0f, false);
					health -= 500;
				}
				focus.destroy();
			}
			alive = health > 0;
		}
	}

	public int getHealth() {
		return health;
	}
	
	public boolean isPhase2(){
		return inPhase2;
	}
	
	public boolean shouldUpdate(){
		return shouldUpdate;
	}
	
	public void destroy(){
		bhp.destroy();
		super.destroy();
		shouldUpdate = false;
	}
}
