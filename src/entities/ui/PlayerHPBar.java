package entities.ui;

import org.newdawn.slick.opengl.Texture;

import entities.Entity;
import entities.HitBox;
import game.Game;
import graphics.Sprite;
import render.RenderUtility;

public class PlayerHPBar extends Entity {
	private static int WIDTH = 120;
	private static int HEIGHT = 40;

	private Sprite playerHPBar;
	private PlayerHPBarOverlay overlay;

	public PlayerHPBar() {
		super("PLAYERHP", -1260 + WIDTH / 2, -680, 0, -1, (HitBox) null);

		playerHPBar = new Sprite(WIDTH, HEIGHT, 0, 120, 1);
		overlay = new PlayerHPBarOverlay();
		Game.UI.put(UID, this);
	}

	public void render(Texture sheet, RenderUtility renderer) {
		super.render(sheet, renderer, playerHPBar);
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
	private class PlayerHPBarOverlay extends Entity {
		private Sprite playerHPBarOverlay;
		private int width;

		public PlayerHPBarOverlay() {
			super("PLAYERHPOV", -1260 + WIDTH / 2, -680, 0, -1, (HitBox) null);
			playerHPBarOverlay = new Sprite(WIDTH, HEIGHT, 0, 80, 1);
			width = WIDTH;
		}

		public void render(Texture sheet, RenderUtility renderer) {
			super.render(sheet, renderer, playerHPBarOverlay);
		}

		public void update(float px, float py, float delta) {
			width = WIDTH * Game.player.getHealth() / 10;
			playerHPBarOverlay = new Sprite(width, HEIGHT, 0, 80, 1);
			setPos(-1260 + width / 2, getPos()[1], 0);
			super.update(px, py, delta);
		}

		public void destroy() {
			super.destroy();
		}
	}
}
