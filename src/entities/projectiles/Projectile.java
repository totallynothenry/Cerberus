package entities.projectiles;

import org.newdawn.slick.opengl.Texture;

import entities.Entity;
import entities.HitBox;
import game.Game;
import graphics.Sprite;
import render.RenderUtility;

public abstract class Projectile extends Entity {
	protected Sprite sprite;

	public Projectile(String UID, float xCoord, float yCoord, double angle, int tNum, HitBox hbox) {
		super(UID, xCoord, yCoord, angle, tNum, hbox);
	}

	public void update(float px, float py, float delta) {
		super.update(px, py, delta);
		hBox.update(getPos()[0], getPos()[1]);
		checkDestroy();
	}

	public void checkDestroy() {
		if (getPos()[0] > 3000 || getPos()[1] > 3000) {
			destroy();
		}
	}

	public void render(Texture sheet, RenderUtility renderer) {
		super.render(sheet, renderer, sprite);
	}

	public void destroy() {
		Game.projectiles.remove(UID);
		super.destroy();
	}

}
