package game;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;

import org.lwjgl.input.Keyboard;
import org.lwjgl.openal.AL;
import org.newdawn.slick.openal.Audio;
import org.newdawn.slick.openal.AudioLoader;
import org.newdawn.slick.openal.SoundStore;
import org.newdawn.slick.opengl.Texture;
import org.newdawn.slick.opengl.TextureLoader;
import org.newdawn.slick.util.ResourceLoader;

import entities.Entity;
import entities.Player;
import entities.enemies.BasicEnemy;
import entities.enemies.Boss;
import entities.enemies.CircleEnemy;
import entities.enemies.Enemy;
import entities.enemies.RunnerEnemy;
import graphics.Window;
import render.RenderUtility;

public class Game {
	// Used for maintaining semi-stable UPS (updates per second)
	private static final int TARGET_UPS = 60;
	private static final long INTERVAL = 1000000000 / TARGET_UPS;

	private long globalTime;
	private long lastLoopStart;
	private long initialTime;

	// Global resources
	private Window window;

	public static RenderUtility renderer;
	private ScreenUtility screenUtil;

	public static final int GRIDW = 1280;
	public static final int GRIDH = 720;

	// These are local resources
	public final Texture playerSheet;
	public final Texture enemySheet;
	public final Texture bossSheet;
	public final Texture explosionSheet;
	public final Texture projectileSheet;
	public final Texture UISheet;
	public final Texture background;

	public static final String playerPath = "resources/textures/playerfull.png";
	public static final String enemyPath = "resources/textures/enemyfull.png";
	public static final String bossPath = "resources/textures/bossfull.png";
	public static final String explosionPath = "resources/textures/explosionfull.png";
	public static final String projectilePath = "resources/textures/projectilefull.png";
	public static final String UIPath = "resources/textures/UIfull.png";

	public static final String backgroundPath = "resources/textures/bg.png";

	private Audio mainTheme;
	private Audio bossTheme;
	private Audio bossThemeUltra;
	private boolean stepItUp;

	// All entities
	public static ConcurrentHashMap<String, Entity> enemies;
	public static ConcurrentHashMap<String, Entity> projectiles;
	public static ConcurrentHashMap<String, Entity> explosions;
	public static ConcurrentHashMap<String, Entity> UI;
	public static Boss boss;
	public static Player player;

	// Some flag and event listening variables important in level design
	private boolean begin;
	public static boolean alive;
	public static boolean win;

	private static boolean stage1 = false;
	private static boolean stage2 = false;
	private static boolean stage3 = false;
	private static boolean bossSpawn = false;

	private static long stage1begin = Long.MAX_VALUE;
	private static long stage2begin = Long.MAX_VALUE;
	private static long stage3begin = Long.MAX_VALUE;

	private boolean shouldUpdate;

	/**
	 * Creates a new window, defines the grid size, defines the sprite sheet,
	 * and creates all entities
	 */
	public Game() {
		// Creates a new window to display the game
		window = new Window();

		// Creates a RenderUtility to render entities
		renderer = new RenderUtility();
		screenUtil = new ScreenUtility();

		projectiles = new ConcurrentHashMap<String, Entity>();
		explosions = new ConcurrentHashMap<String, Entity>();
		UI = new ConcurrentHashMap<String, Entity>();

		// Initiates all entities and places them into a HashMap
		enemies = new ConcurrentHashMap<String, Entity>();

		// Player get special treatment because IO
		player = new Player(0, -600, 0);
		alive = true;
		win = false;

		// Sets the sprite sheet
		try {
			playerSheet = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream(playerPath));
			enemySheet = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream(enemyPath));
			bossSheet = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream(bossPath));
			explosionSheet = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream(explosionPath));
			projectileSheet = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream(projectilePath));
			UISheet = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream(UIPath));

			background = TextureLoader.getTexture("PNG", ResourceLoader.getResourceAsStream(backgroundPath));
		} catch (IOException e) {
			throw new RuntimeException("Can't find texture file!");
		}

		// Creates the theme played during the main level
		try {
			mainTheme = AudioLoader.getStreamingAudio("OGG",
					ResourceLoader.getResource("resources/audio/mainTheme.ogg"));
			bossTheme = AudioLoader.getStreamingAudio("OGG",
					ResourceLoader.getResource("resources/audio/bossTheme.ogg"));
			bossThemeUltra = AudioLoader.getStreamingAudio("OGG",
					ResourceLoader.getResource("resources/audio/bossThemeUltra.ogg"));
		} catch (IOException e) {
			throw new RuntimeException("Audio file not found!");
		}
		// Creates the theme played during the boss fight

		begin = false;
		stepItUp = false;

		boss = null;

		shouldUpdate = true;
	}

	private synchronized void spawnEnemies() {
		if (!stage1 && globalTime < 20) {
			mainTheme.playAsMusic(1.0f, 1.0f, true);

			Enemy e1 = new BasicEnemy(-500, 1000, Math.PI);
			Enemy e2 = new BasicEnemy(-200, 800, Math.PI);
			Enemy e3 = new BasicEnemy(200, 800, Math.PI);
			Enemy e4 = new BasicEnemy(500, 1000, Math.PI);

			enemies.put(e1.UID, e1);
			enemies.put(e2.UID, e2);
			enemies.put(e3.UID, e3);
			enemies.put(e4.UID, e4);
			stage1 = true;
			stage1begin = globalTime;
		}
		if (stage2) {
			Enemy e1 = new CircleEnemy(800, 900, Math.PI);
			Enemy e2 = new CircleEnemy(-800, 900, Math.PI);
			Enemy e3 = new RunnerEnemy(0, 800, Math.PI);

			enemies.put(e1.UID, e1);
			enemies.put(e2.UID, e2);
			enemies.put(e3.UID, e3);
			stage2 = false;
			stage2begin = globalTime;
		}
		if (stage3 && globalTime > stage2begin) {
			Enemy e1 = new RunnerEnemy(-300, 1200, Math.PI);
			Enemy e2 = new RunnerEnemy(300, 800, Math.PI);
			Enemy e3 = new RunnerEnemy(800, 800, Math.PI);
			Enemy e4 = new RunnerEnemy(-800, 1200, Math.PI);

			enemies.put(e1.UID, e1);
			enemies.put(e2.UID, e2);
			enemies.put(e3.UID, e3);
			enemies.put(e4.UID, e4);
			stage3 = false;
			stage3begin = globalTime;
		}
		if (bossSpawn && globalTime > stage3begin) {
			bossTheme.playAsMusic(1.0f, 1.0f, true);
			// Creates a boss
			boss = new Boss(0, 1500, 0);
			bossSpawn = false;
			stage3begin = Long.MAX_VALUE;
		}
	}

	/**
	 * Begins the game
	 */
	public void start() {
		gameLoop();
	}

	/**
	 * Main loop of the game, uses variable UPS that is capped; can depend on
	 * hardware capability
	 */
	public void gameLoop() {
		lastLoopStart = System.nanoTime();

		// Main loop of the game
		while (true) {
			// Time counter, allows semi-stable UPS
			long now = System.nanoTime();
			float delta = (now - lastLoopStart) / (float) INTERVAL;
			lastLoopStart = now;

			// Current method of locking down game until player interaction
			checkBegin();
			if (begin) {
				spawnEnemies();
				globalTime = System.currentTimeMillis() - initialTime;
				player.input();
				update(delta);
			}
			try {
				if (INTERVAL - System.nanoTime() + now > 0) {
					Thread.sleep((INTERVAL - System.nanoTime() + now) / 1000000);
				}
			} catch (InterruptedException e) {
				throw new RuntimeException("Well shit");
			}
			// System.out.println("Currently: " + enemies.mappingCount() + "
			// enemies.");
			render();
			if (!begin) {
				screenUtil.renderStartScreen();
			}
			window.update();

			while (Keyboard.next()) {
				if (Keyboard.getEventKey() == Keyboard.KEY_F5) {
					terminate();
					System.exit(0);
				}
				if (Keyboard.getEventKeyState()) {
					if (Keyboard.getEventKey() == Keyboard.KEY_ESCAPE) {
						shouldUpdate = !shouldUpdate;
						System.out.println(shouldUpdate);
					}
				}
			}

		}
	}

	private void checkBegin() {
		if (Keyboard.isKeyDown(Keyboard.KEY_RETURN) && !begin) {
			begin = true;
			initialTime = System.currentTimeMillis();
		}
	}

	/**
	 * d Calls all entities to update positions, actions, and other stuff. NOTE:
	 * Fixed-time game loop still not implemented because I'm lazy.
	 * 
	 * @param delta
	 *            The change in time since the last cycle of the main game loop,
	 *            used in maintaining a reasonable frame-rate that doesn't burn
	 *            your house down
	 */
	public synchronized void update(float delta) {
		SoundStore.get().poll(0);
		if (shouldUpdate) {
			if (enemies.isEmpty() && globalTime > stage3begin) {
				bossSpawn = true;
				stage2begin = Long.MAX_VALUE;
			}
			if (enemies.isEmpty() && globalTime > stage2begin) {
				stage3 = true;
				stage1begin = Long.MAX_VALUE;
			}
			if (enemies.isEmpty() && globalTime > stage1begin) {
				stage2 = true;
			}

			if (alive) {
				player.update(player.getPos()[0], player.getPos()[1], delta);

			}
			for (String key : UI.keySet()) {
				if (UI.get(key) != null) {
					UI.get(key).update(player.getPos()[0], player.getPos()[1], delta);
				}
			}
			for (String key : enemies.keySet()) {
				if (enemies.get(key) != null) {
					enemies.get(key).update(player.getPos()[0], player.getPos()[1], delta);
				}
			}
			for (String key : explosions.keySet()) {
				if (explosions.get(key) != null) {
					explosions.get(key).update(player.getPos()[0], player.getPos()[1], delta);
				}
			}
			for (String key : projectiles.keySet()) {
				if (projectiles.get(key) != null) {
					projectiles.get(key).update(player.getPos()[0], player.getPos()[1], delta);
				}
			}
			if (boss != null && boss.shouldUpdate()) {
				boss.update(player.getPos()[0], player.getPos()[1], delta);
				if (boss.isPhase2() && !stepItUp) {
					stepItUp = true;
					bossThemeUltra.playAsMusic(1.0f, 1.0f, true);
				}
			}
			if (!alive || win) {
				if (Keyboard.isKeyDown(Keyboard.KEY_R)) {
					reset();
				}
			}
		}
	}

	/**
	 * Renders all entities
	 */
	public void render() {
		// Clears screen
		renderer.reset();

		renderBackground();

		if (alive) {
			renderPlayer();
		}
		if (!enemies.isEmpty()) {
			renderEnemies();
		}
		if (boss != null) {
			bossSheet.bind();
			renderer.begin();
			boss.render(bossSheet, renderer);
			renderer.end();
		}
		if (!explosions.isEmpty()) {
			renderExplosions();
		}
		if (!projectiles.isEmpty()) {
			renderProjectiles();
		}

		renderUI();
		screenUtil.renderMenuScreen(shouldUpdate);
		if (!alive) {
			screenUtil.renderGameOverScreen();
		}
		if (win) {
			screenUtil.renderWinScreen();
		}
	}

	private void renderPlayer() {
		playerSheet.bind();
		renderer.begin();
		player.render(playerSheet, renderer);
		renderer.end();
	}

	private void renderUI() {
		UISheet.bind();
		renderer.begin();
		for (String key : UI.keySet()) {
			if (UI.get(key) != null) {
				UI.get(key).render(UISheet, renderer);
			}
		}
		renderer.end();
	}

	private void renderEnemies() {
		enemySheet.bind();
		renderer.begin();
		for (String key : enemies.keySet()) {
			if (enemies.get(key) != null) {
				enemies.get(key).render(enemySheet, renderer);
			}
		}
		renderer.end();
	}

	private void renderProjectiles() {
		projectileSheet.bind();
		renderer.begin();
		for (String key : projectiles.keySet()) {
			if (projectiles.get(key) != null) {
				projectiles.get(key).render(projectileSheet, renderer);
			}
		}
		renderer.end();
	}

	private void renderExplosions() {
		explosionSheet.bind();
		renderer.begin();
		for (String key : explosions.keySet()) {
			if (explosions.get(key) != null) {
				explosions.get(key).render(explosionSheet, renderer);
			}
		}
		renderer.end();
	}

	/**
	 * Renders the background
	 */
	private void renderBackground() {
		background.bind();
		renderer.begin();
		renderer.bufferVertices(background, 0, 0, 0, 2560, 1440, 0, 0, GRIDW, GRIDH);
		renderer.end();
	}

	private void reset() {
		globalTime = 0;

		stepItUp = false;
		

		// Delete local resources
		for (String key : enemies.keySet()) {
			enemies.get(key).destroy();
		}
		for (String key : projectiles.keySet()) {
			projectiles.get(key).destroy();
		}
		for (String key : explosions.keySet()) {
			explosions.get(key).destroy();
		}
		if (boss != null) {
			boss.destroy();
			boss = null;
		}

		// Some flag and event listening variables important in level design
		begin = false;
		player.reset();
		alive = true;
		win = false;
		
		stage1 = false;
		stage2 = false;
		stage3 = false;
		bossSpawn = false;

		stage1begin = Long.MAX_VALUE;
		stage2begin = Long.MAX_VALUE;
		stage3begin = Long.MAX_VALUE;

		shouldUpdate = true;
	}

	/**
	 * Destroys all OpenGL objects used to free up memory
	 */
	public void terminate() {
		// Delete general resources
		window.destroy();
		AL.destroy();
		reset();
	}
}
