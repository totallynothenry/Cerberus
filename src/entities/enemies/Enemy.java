package entities.enemies;

import java.io.IOException;
import java.util.HashMap;

import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.util.ResourceLoader;

import entities.Entity;
import entities.HitBox;
import entities.explosions.Explosion;
import entities.projectiles.Bullet;
import entities.projectiles.PlayerMissile;
import game.Game;
import graphics.Sprite;
import render.RenderUtility;

public abstract class Enemy extends Entity {
	public static final int HBOXRAD = 128;
	private int shootCooldown;
	private int shootCount;

	private Audio laserEffect;
	private Audio laserHit;

	protected int health;
	protected boolean alive;

	private int state;
	private static final int STATIC = 0;
	private static final int SHOOTING = 1;

	protected HashMap<Integer, Sprite> sprites;

	public Enemy(int health, String UID, float xCoord, float yCoord, double angle, int tNum) {
		super(UID, xCoord, yCoord, angle, tNum, new HitBox(xCoord, yCoord, HBOXRAD));
		shootCount = 0;
		shootCooldown = 100;

		try {
			laserEffect = AudioLoader.getAudio("OGG",
					ResourceLoader.getResourceAsStream("resources/audio/enemyLaser.ogg"));
			laserHit = AudioLoader.getAudio("OGG", ResourceLoader.getResourceAsStream("resources/audio/laserHit.ogg"));
		} catch (IOException e) {
			throw new RuntimeException("Can't find audio file!");
		}

		alive = true;

		this.health = health;

		sprites = new HashMap<Integer, Sprite>();
		switch (tNum) {
		case Bullet.BENEMY:
			sprites.put(STATIC, new Sprite(163, 128, 0, 256, 1));
			sprites.put(SHOOTING, new Sprite(163, 128, 163, 256, 1));
			break;
		case Bullet.CENEMY:
			sprites.put(STATIC, new Sprite(163, 128, 0, 128, 1));
			sprites.put(SHOOTING, new Sprite(163, 128, 163, 128, 1));
			break;
		case Bullet.RENEMY:
			sprites.put(STATIC, new Sprite(163, 128, 0, 0, 1));
			sprites.put(SHOOTING, new Sprite(163, 128, 163, 0, 1));
			break;
		}
	}

	public void setShootCooldown(int s) {
		shootCooldown = s;
	}

	public void shoot() {
		state = STATIC;
		if (shootCount == 0) {
			state = SHOOTING;
			laserEffect.playAsSoundEffect(1.0f, 0.125f, false);
			float x = getPos()[0] + 32 * (float) Math.cos(getPos()[2] + Math.PI / 2);
			float y = getPos()[1] + 32 * (float) Math.sin(getPos()[2] + Math.PI / 2);
			Bullet b = new Bullet(UID, x, y, getPos()[2], getTeam() + 100);
			Game.projectiles.put(b.UID, b);
			shootCount = shootCooldown;
		}
		shootCount--;
	}

	public void checkShot() {
		for (String key : Game.projectiles.keySet()) {
			Entity focus = Game.projectiles.get(key);
			if (focus != null && focus.getTeam() - 100 == 0 && hBox.checkCollide(focus.hBox)) {
				if (focus instanceof Bullet) {
					laserHit.playAsSoundEffect(1.0f, 1.0f, false);
					health-=2;
				}
				if(focus instanceof PlayerMissile){
					laserHit.playAsSoundEffect(1.0f, 1.0f, false);
					health-=50;
				}
				focus.destroy();
			}
			alive = health > 0;
		}
	}

	public void render(Texture sheet, RenderUtility renderer) {
		if (alive) {
			super.render(sheet, renderer, sprites.get(state));
		}
		// System.out.println(state);
	}

	public void update(float px, float py, float delta) {
		if (alive) {
			super.update(0, 0, delta);
			hBox.update(getPos()[0], getPos()[1]);
			shoot();
		} else {
			Explosion death = new Explosion(getPos()[0], getPos()[1]);
			Game.explosions.put(death.UID, death);
			destroy();
		}
	}
	
	public int getHealth(){
		return health;
	}

	public void destroy() {
		Game.enemies.remove(UID);
	}
}
