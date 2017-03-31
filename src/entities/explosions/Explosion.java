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

public class Explosion extends Entity {
	private static int eCount = 0;
	private Sprite sprite;
	boolean active;

	private int frameCount;

	private Audio explosion;

	public Explosion(float x, float y) {
		super("EXPLOSION" + eCount, x, y, 0, -1, (HitBox) null);
		frameCount = 0;
		sprite = new Sprite(178, 200, 0, 900, 5);
		active = true;

		try {
			explosion = AudioLoader.getAudio("OGG",
					ResourceLoader.getResourceAsStream("resources/audio/explosion.ogg"));
		} catch (IOException e) {
			throw new RuntimeException("Can't find audio file!");
		}
	}

	public void render(Texture sheet, RenderUtility renderer) {
		super.render(sheet, renderer, sprite);
	}

	public void update(float px, float py, float delta) {
		if (active) {
			explosion.playAsSoundEffect(1.0f, 1.0f, false);
			sprite.update();

			frameCount++;
			active = frameCount < 28;
		} else {
			delete();
		}
		super.update(px, py, delta);
	}

	private void delete() {
		Game.explosions.remove(UID);
	}
}
