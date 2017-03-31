package game;

import java.io.IOException;

import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import render.RenderUtility;

public class ScreenUtility {
	private Texture start;
	private Texture credits;
	private Texture menu;
	private Texture gameOver;
	private Texture win;
	private Texture restart;

	private int frameCount;

	public ScreenUtility() {
		try {
			start = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("resources/start.png"));
			credits = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("resources/credits.png"));
			menu = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("resources/menu.png"));
			gameOver = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("resources/gameOver.png"));
			// win = TextureLoader.getTexture("PNG",
			// ResourceLoader.getResourceAsStream("resources/win.png"));
			// restart = TextureLoader.getTexture("PNG",
			// ResourceLoader.getResourceAsStream("resources/textures/restart.png"));
		} catch (IOException e) {
			System.out.println("whatthefuck");
		}
		frameCount = 0;
	}

	public void renderStartScreen() {
		start.bind();
		Game.renderer.begin();
		Game.renderer.bufferVertices(start, 0, 0, 0, 2560, 1440, 0, 0, Game.GRIDW, Game.GRIDH);
		Game.renderer.end();
	}

	public void renderMenuScreen(boolean active) {
		if (!active) {
			menu.bind();
			Game.renderer.begin();
			Game.renderer.bufferVertices(menu, 0, 0, 0, 2560, 1440, 0, 0, Game.GRIDW, Game.GRIDH);
			Game.renderer.end();
		}
	}

	public void renderGameOverScreen() {
		gameOver.bind();
		Game.renderer.begin();
		Game.renderer.bufferVertices(gameOver, 0, 0, 0, 2560, 1440, 0, 0, Game.GRIDW, Game.GRIDH);
		Game.renderer.end();
	}

	public void renderWinScreen() {
		if (frameCount++ < 100) {
			win.bind();
			Game.renderer.begin();
			Game.renderer.bufferVertices(win, 0, 0, 0, 2560, 1440, 0, 0, Game.GRIDW, Game.GRIDH);
			Game.renderer.end();
		} else if (frameCount < 400) {
			credits.bind();
			Game.renderer.begin();
			Game.renderer.bufferVertices(credits, 0, 0, 0, 2560, 1440, 0, 0, Game.GRIDW, Game.GRIDH);
			Game.renderer.end();
		} else {
			restart.bind();
			Game.renderer.begin();
			Game.renderer.bufferVertices(restart, 0, 0, 0, 2560, 1440, 0, 0, Game.GRIDW, Game.GRIDH);
			Game.renderer.end();
		}
	}

	public void reset() {
		frameCount = 0;
	}
}
