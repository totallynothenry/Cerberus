package entities.ui;

import org.newdawn.slick.opengl.Texture;

import entities.Entity;
import entities.HitBox;
import game.Game;
import graphics.Sprite;
import render.RenderUtility;

public class PlayerMissileBar extends Entity {
	private static int WIDTH = 200;
	private static int HEIGHT = 30;

	private Sprite playerMissileBar;
	private PlayerMissileBarOverlay overlay;

	public PlayerMissileBar() {
		super("PLAYERMISSILE", -1260 + WIDTH / 2, -580, 0, -1, (HitBox) null);

		playerMissileBar = new Sprite(WIDTH, HEIGHT, 0, 190, 1);
		overlay = new PlayerMissileBarOverlay();
		Game.UI.put(UID, this);
	}

	public void render(Texture sheet, RenderUtility renderer) {
		super.render(sheet, renderer, playerMissileBar);
		overlay.render(sheet, renderer);
	}

	public void update(float px, float py, float delta) {
		overlay.update(px, py, delta);
		super.update(px, py, delta);
	}

	public void destroy() {
		overlay.destroy();
		Game.UI.remove(UID);
		super.destroy();
	}

	// Defines the overlay
	private class PlayerMissileBarOverlay extends Entity {
		private Sprite playerMissileBarOverlay;
		private int width;

		public PlayerMissileBarOverlay() {
			super("PLAYERMISSILEOV", -1260 + WIDTH / 2, -580, 0, -1, (HitBox) null);
			playerMissileBarOverlay = new Sprite(WIDTH, HEIGHT, 0, 160, 1);
			width = WIDTH;
		}

		public void render(Texture sheet, RenderUtility renderer) {
			super.render(sheet, renderer, playerMissileBarOverlay);
		}

		public void update(float px, float py, float delta) {
			width = WIDTH - WIDTH * Game.player.getMissileCooldown() / 1200;
			playerMissileBarOverlay = new Sprite(width, HEIGHT, 0, 160, 1);
			setPos(-1260 + width / 2, getPos()[1], 0);
			super.update(px, py, delta);
		}

		public void destroy() {
			super.destroy();
		}
	}
}
