package entities.ui;

import org.newdawn.slick.opengl.Texture;

import entities.Entity;
import entities.HitBox;
import game.Game;
import graphics.Sprite;
import render.RenderUtility;

public class PlayerSPBar extends Entity {
	private static int WIDTH = 600;
	private static int HEIGHT = 40;
	
	private Sprite playerSPBar;
	private PlayerSPBarOverlay overlay;

	public PlayerSPBar() {
		super("PLAYERSP", -1260+WIDTH/2, -630, 0, -1, (HitBox) null);
		
		playerSPBar = new Sprite(WIDTH, HEIGHT, 0, 40, 1);
		overlay = new PlayerSPBarOverlay();
		Game.UI.put(UID, this);
	}

	public void render(Texture sheet, RenderUtility renderer) {
		super.render(sheet, renderer, playerSPBar);
		overlay.render(sheet, renderer);
	}
	
	public void update(float px, float py, float delta) {
		overlay.update(px, py, delta);
		super.update(px, py, delta);
	}
	
	public void destroy(){
		overlay.destroy();
		Game.UI.remove(UID);
		super.destroy();
	}

	// Defines the overlay
	private class PlayerSPBarOverlay extends Entity {
		private Sprite playerSPBarOverlay;
		private int width;

		public PlayerSPBarOverlay() {
			super("PLAYERSPOV", -1260+WIDTH/2, -630, 0, -1, (HitBox) null);
			playerSPBarOverlay = new Sprite(WIDTH, HEIGHT, 0, 0, 1);
			width = WIDTH;
		}

		public void render(Texture sheet, RenderUtility renderer) {
			super.render(sheet, renderer, playerSPBarOverlay);
		}

		public void update(float px, float py, float delta) {
			width = WIDTH * Game.player.getShields() / 50;
			playerSPBarOverlay = new Sprite(width, HEIGHT, 0, 0, 1);
			setPos(-1260 + width / 2, getPos()[1], 0);
			super.update(px, py, delta);
		}
		
		public void destroy(){
			super.destroy();
		}
	}
}
