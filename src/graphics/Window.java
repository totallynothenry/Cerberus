package graphics;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.opengl.Texture;

public class Window {
	public static final int WIDTH = 1280;
	public static final int HEIGHT = 720;
	Texture ctex;

	public Window() {
		try {
			Display.setDisplayMode(new DisplayMode(WIDTH, HEIGHT));
			Display.setTitle("Y.O.U.N.G Invaders II");
			Display.create();

		} catch (LWJGLException e) {
			e.printStackTrace();
			System.exit(0);
		}
		GL11.glEnable(GL11.GL_TEXTURE_2D);

		GL11.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);

		// enable alpha blending
		GL11.glEnable(GL11.GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		GL11.glViewport(0, 0, WIDTH, HEIGHT);

//		createCursor();
	}

	private void createCursor() {
//		try {
//			ctex = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream("resources/textures/cursor.png"));
//		} catch (IOException e) {
//			throw new RuntimeException("Custom cursor image not found!");
//		}
//
//		try {
//			ctex.getTextureData()
//			Mouse.create();
//			Cursor reticule = new Cursor();
//		} catch (LWJGLException e) {
//			e.printStackTrace();
//			System.exit(0);
//		}
	}

	public void update() {
		Display.update();
		Display.sync(100);
	}

	public boolean isClosing() {
		return Display.isCloseRequested();
	}

	public void destroy() {
		Display.destroy();
		Mouse.destroy();
	}
}
