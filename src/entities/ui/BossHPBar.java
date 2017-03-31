package entities.ui;

import org.newdawn.slick.opengl.Texture;

import entities.Entity;
import entities.HitBox;
import entities.enemies.Boss;
import game.Game;
import graphics.Sprite;
import render.RenderUtility;

public class BossHPBar extends Entity {
	private static int WIDTH = 1680;
	private static int HEIGHT = 60;

	private Sprite bossHPBar;
	private BossHPBarOverlay overlay;

	public BossHPBar() {
		super("BOSSHP", 0, 670, 0, -1, (HitBox) null);
		bossHPBar = new Sprite(WIDTH, HEIGHT, 0, 280, 1);
		Game.UI.put(UID, this);
		
		overlay = new BossHPBarOverlay();
	}

	public void render(Texture sheet, RenderUtility renderer) {
		super.render(sheet, renderer, bossHPBar);
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
	private class BossHPBarOverlay extends Entity {
		private Sprite bossHPBarOverlay;
		private int width;

		public BossHPBarOverlay() {
			super("BOSSHPOV", 0, 670, 0, -1, (HitBox) null);
			bossHPBarOverlay = new Sprite(WIDTH, HEIGHT, 0, 220, 1);
			width = WIDTH;
		}

		public void render(Texture sheet, RenderUtility renderer) {
			super.render(sheet, renderer, bossHPBarOverlay);
		}

		public void update(float px, float py, float delta) {
			width = WIDTH * ((Boss) Game.boss).getHealth() / 5000;
			bossHPBarOverlay = new Sprite(width, HEIGHT, 0, 220, 1);
			setPos(-840 + width / 2, getPos()[1], 0);
			super.update(px, py, delta);
		}

		public void destroy() {
			super.destroy();
		}
	}
}
