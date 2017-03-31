package entities;

import java.io.IOException;
import java.util.HashMap;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.util.ResourceLoader;

import entities.explosions.Explosion;
import entities.projectiles.Bullet;
import entities.projectiles.PlayerMissile;
import entities.ui.PlayerHPBar;
import entities.ui.PlayerMissileBar;
import entities.ui.PlayerSPBar;
import game.Game;
import graphics.Sprite;
import graphics.Window;
import render.RenderUtility;

public class Player extends Entity {
	private HashMap<Integer, Sprite> sprites;

	public static final int hitBoxRadius = 108;

	private int shootCooldown;

	private int dodgeCooldown;
	private boolean dodging;
	private int rollDirection;

	private boolean boosting;

	private int missileCooldown;
	
	private int shields;
	private static final int shieldRechargeTime = 10;
	private int shieldCooldown;

	private int health;
	private boolean alive;

	private PlayerHPBar hp;
	private PlayerSPBar sp;
	private PlayerMissileBar missBar;

	private Audio laserEffect;
	private Audio missileLaunch;
	private Audio laserHit;

	private int state;
	private static final int STATIC = 0;
	private static final int SHOOTING = 1;
	private static final int DODGE = 2;
	private static final int BOOST = 3;

	public Player(float x, float y, double t) {
		super("PLAYER", x, y, t, Bullet.PLAYER, new HitBox(x, y, hitBoxRadius));
		shootCooldown = 0;

		sprites = new HashMap<Integer, Sprite>();
		sprites.put(STATIC, new Sprite(136, 143, 0, 0, 1));
		sprites.put(SHOOTING, new Sprite(136, 143, 0, 286, 1));
		sprites.put(BOOST, new Sprite(136, 143, 0, 143, 4));
		sprites.put(DODGE, new Sprite(136, 143, 0, 0, 4));

		sprites.get(BOOST).setFrameTime(20);
		sprites.get(DODGE).setFrameTime(15);

		state = STATIC;
		alive = true;
		boosting = false;

		try {
			laserEffect = AudioLoader.getAudio("OGG", ResourceLoader.getResourceAsStream("resources/audio/laser.ogg"));
			missileLaunch = AudioLoader.getAudio("OGG",
					ResourceLoader.getResourceAsStream("resources/audio/missile.ogg"));
			laserHit = AudioLoader.getAudio("OGG", ResourceLoader.getResourceAsStream("resources/audio/laserHit.ogg"));
		} catch (IOException e) {
			throw new RuntimeException("Can't find audio file!");
		}

		shields = 50;
		health = 10;
		shieldCooldown = 0;
		missileCooldown = 0;

		hp = new PlayerHPBar();
		sp = new PlayerSPBar();
		missBar = new PlayerMissileBar();
	}

	public void input() {
		state = STATIC;
		boosting = false;
		setVelocity(0, 0);
		/* Player input */

		float transS = 9f;
		if (Keyboard.isKeyDown(Keyboard.KEY_W)) {
			setVy(transS);
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_S)) {
			setVy(-transS);
		}
		if (Keyboard.isKeyDown(Keyboard.KEY_SPACE)) {
			dodging = true;
			if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
				rollDirection = -1;
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
				rollDirection = 1;
			}
		} else {
			if (Keyboard.isKeyDown(Keyboard.KEY_A)) {
				setVx(-transS);
			}
			if (Keyboard.isKeyDown(Keyboard.KEY_D)) {
				setVx(transS);
			}
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) {
			boosting = true;
			state = BOOST;
			setVPolar(20f, getPos()[2] + Math.PI / 2);
		}

		if (Keyboard.isKeyDown(Keyboard.KEY_F)) {
			launchMissiles();
		}
		// Shoots bullets if mouse is down
		if (Mouse.isButtonDown(0)) {
			shootBullet();
		}

	}

	private void shootBullet() {
		if (shootCooldown == 0 && !dodging && !boosting) {
			state = SHOOTING;
			laserEffect.playAsSoundEffect(1.0f, 0.25f, false);
			float x = getPos()[0] + 60 * (float) Math.cos(getPos()[2] + Math.PI / 2);
			float y = getPos()[1] + 60 * (float) Math.sin(getPos()[2] + Math.PI / 2);
			Bullet b = new Bullet(UID, x, y, getPos()[2], getTeam() + 100);
			Game.projectiles.put(b.UID, b);
			shootCooldown = 5;
		}

	}

	private void launchMissiles() {
		if (missileCooldown == 0) {
			String tar = autoTarget();
			// First missile
			float x1 = getPos()[0] + 60 * (float) Math.sin(getPos()[2] + Math.PI / 2);
			float y1 = getPos()[1] + 60 * (float) Math.cos(getPos()[2] + Math.PI / 2);
			PlayerMissile p1 = new PlayerMissile(tar, UID, x1, y1, getPos()[2], getTeam() + 100);
			// Second missile
			float x2 = getPos()[0] - 60 * (float) Math.sin(getPos()[2] + Math.PI / 2);
			float y2 = getPos()[1] - 60 * (float) Math.cos(getPos()[2] + Math.PI / 2);
			PlayerMissile p2 = new PlayerMissile(tar, UID, x2, y2, getPos()[2], getTeam() + 100);

			Game.projectiles.put(p1.UID, p1);
			Game.projectiles.put(p2.UID, p2);
			missileLaunch.playAsSoundEffect(1.0f, 1.0f, false);
			missileCooldown = 1200;
		}
	}

	private void checkDodgeRoll() {
		if (dodging && dodgeCooldown < 60) {
			setVelocity(getVx() + rollDirection * 15, getVy());
			state = DODGE;
			dodgeCooldown++;
		} else {
			dodging = false;
			dodgeCooldown = 0;
		}

	}

	public void render(Texture sheet, RenderUtility renderer) {
		if (alive) {
			super.render(sheet, renderer, sprites.get(state));
			// Next animation frame
			sprites.get(state).update();
		}
	}

	public void update(float pX, float pY, float delta) {
		if (alive) {
			checkShot();

			turnToMouse();

			checkDodgeRoll();

			lockInWindow();
			super.update(0, 0, delta);
			hBox.update(getPos()[0], getPos()[1]);
			shieldRegen();
			if (shootCooldown > 0) {
				shootCooldown--;
			}
			if (missileCooldown > 0) {
				missileCooldown--;
			}

		} else {
			Explosion death = new Explosion(getPos()[0], getPos()[1]);
			Game.explosions.put(death.UID, death);
			destroy();
		}
	}

	private void lockInWindow() {
		float x = getPos()[0];
		float y = getPos()[1];
		float t = getPos()[2];
		if (x > 1280) {
			setPos(1280, y, t);
		}
		if (x < -1280) {
			setPos(-1280, y, t);
		}
		if (y > 720) {
			setPos(x, 720, t);
		}
		if (y < -720) {
			setPos(x, -720, t);
		}
	}

	private String autoTarget() {
		double minDist = Double.MAX_VALUE;
		String minUID = null;
		for (String UID : Game.enemies.keySet()) {
			// Translates screen coordinates of mouse into grid coordinates
			double mx = 2 * Mouse.getX() - Window.WIDTH;
			double my = 2 * Mouse.getY() - Window.HEIGHT;

			Entity focus = Game.enemies.get(UID);
			// Calculate the angle the player should be facing
			double dx = mx - focus.getPos()[0];
			double dy = my - focus.getPos()[1];
			double distance = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
			if (distance < 300 && distance < minDist) {
				minDist = distance;
				minUID = UID;
			}
		}
		return minUID;
	}

	private void shieldRegen() {
		// System.out.println(shields);
		if (shields < 50 && shieldCooldown < 0) {
			shields += 2;
			shieldCooldown = shieldRechargeTime;
		}
		shieldCooldown--;
	}

	private void turnToMouse() {
		// Translates screen coordinates of mouse into grid coordinates
		double mx = 2 * Mouse.getX() - Window.WIDTH;
		double my = 2 * Mouse.getY() - Window.HEIGHT;

		// Calculate the angle the player should be facing
		double dx = mx - getPos()[0];
		double dy = my - getPos()[1];
		double theta = 0;
		if (dx > 0) {
			theta = (Math.atan(dy / dx) - Math.PI / 2);
		}
		if (dx < 0) {
			theta = (Math.PI / 2 + Math.atan(dy / dx));
		}

		// Rotates toward that angle
		setRotation(theta, 0.1);
	}

	private void checkShot() {
		for (String key : Game.projectiles.keySet()) {
			Entity focus = Game.projectiles.get(key);
			if (!dodging && focus != null && focus.getTeam() - 100 != 0 && hBox.checkCollide(focus.hBox)) {
				laserHit.playAsSoundEffect(1.0f, 1.0f, false);
				if (shields > 0) {
					shields -= 2;
				} else {
					health--;
				}
				focus.destroy();
			}
			alive = health > 0;
		}
	}

	public int getHealth() {
		return health;
	}

	public int getShields() {
		return shields;
	}

	public int getMissileCooldown() {
		return missileCooldown;
	}

	public boolean isAlive() {
		return alive;
	}

	public void reset() {
		setPos(0, -600, 0);
		shootCooldown = 0;
		state = STATIC;
		alive = true;
		boosting = false;
		shields = 50;
		health = 10;
		shieldCooldown = 0;
		missileCooldown = 0;
	}

	public void destroy() {
		Game.alive = false;
		super.destroy();
	}
}
