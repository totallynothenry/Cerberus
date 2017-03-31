package entities.explosions;

import java.io.IOException;

import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.util.ResourceLoader;

import entities.Entity;
import entities.HitBox;
import game.Game;
import graphics.Sprite;
import render.RenderUtility;

public class SmallExplosion extends Entity {
	private static int sCount = 0;
	private Sprite sprite;
	boolean active;

	private int frameCount;

	public SmallExplosion(float x, float y) {
		super("BEXPLOSION" + sCount, x, y, 0, -1, (HitBox) null);
		frameCount = 0;
		sprite = new Sprite(18, 20, 0, 1100, 5);
		sprite.setFrameTime(6);
		
		active = true;
	}

	public void render(Texture sheet, RenderUtility renderer) {
		super.render(sheet, renderer, sprite);
	}

	public void update(float px, float py, float delta) {
		if (active) {
			sprite.update();

			frameCount++;
			active = frameCount < 24;
		} else {
			delete();
		}
		super.update(px, py, delta);
	}

	private void delete() {
		Game.explosions.remove(UID);
	}
}
