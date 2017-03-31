package render;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.GL_COLOR_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_DEPTH_BUFFER_BIT;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glClear;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL11.glEnable;
import static org.lwjgl.opengl.GL15.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;
import static org.lwjgl.opengl.GL20.GL_FRAGMENT_SHADER;
import static org.lwjgl.opengl.GL20.GL_VERTEX_SHADER;

import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.newdawn.slick.opengl.Texture;

public class RenderUtility {
	private VAOLink link;
	private VBO vbo;
	private ShaderPack pack;

	private int vertexNum;
	private FloatBuffer vertices;

	private boolean busy;

	public RenderUtility() {
		// Creates a new RenderBuffer
		link = new VAOLink();
		link.bind();

		busy = false;
		vertexNum = 0;

		vertices = BufferUtils.createFloatBuffer(4096);

		vbo = new VBO();
		vbo.bind(GL_ARRAY_BUFFER);
		vbo.uploadData(GL_ARRAY_BUFFER, vertices.capacity() * Float.BYTES,
				GL_DYNAMIC_DRAW);

		pack = new ShaderPack(Shader.loadShader(GL_VERTEX_SHADER,
				"Resources/shaders/vShader.vert"), Shader.loadShader(
				GL_FRAGMENT_SHADER, "Resources/shaders/fShader.frag"));
		pack.bindFragmentDataLocation(0, "fragColor");
		pack.link();
		pack.use();
		enableVertexAttributes();
		int uniTex = pack.getUniformLocation("texImage");
		pack.setUniform(uniTex, 0);

		// Enables use of alpha channels, not too necessary but good to have
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

	}

	/**
	 * Tells OpenGL to link the arrangement of the attribute data in the VBO to
	 * the input variables in the shaders
	 */
	private void enableVertexAttributes() {
		int pos = pack.getAttribLocation("position");
		pack.enableVertexAttribArray(pos);
		pack.vertexAttribPointer(pos, 2, 7 * Float.BYTES, 0);

		int col = pack.getAttribLocation("inColor");
		pack.enableVertexAttribArray(col);
		pack.vertexAttribPointer(col, 3, 7 * Float.BYTES, 2 * Float.BYTES);

		int ST = pack.getAttribLocation("inSTcoord");
		pack.enableVertexAttribArray(ST);
		pack.vertexAttribPointer(ST, 2, 7 * Float.BYTES, 5 * Float.BYTES);
	}

	/**
	 * Begins processing vertices
	 */
	public void begin() {
		if (busy) {
			throw new IllegalStateException(
					"Can't begin because RenderUtility is busy");
		}
		busy = true;
		vertexNum = 0;
	}

	/**
	 * Processes the texture into vertices representing a bounding box
	 * 
	 * @param texture
	 *            Texture object
	 * @param c
	 *            Color object storing red, blue, and green
	 * @param width
	 *            Width of the region
	 * @param height
	 *            Height of the region
	 * @param x
	 *            x coordinate of the texture
	 * @param y
	 *            y coordinate of the texture
	 * @param regionX
	 *            x coordinate of the region
	 * @param regionY
	 *            y coordinate of the region
	 */
	public void bufferVertices(Texture texture, float x, float y, float theta,
			float width, float height, float texX, float texY, int gridX,
			int gridY) {
		if (vertices.remaining() < 7 * 4) {
			// No more space in VBO, flush necessary
			flush();
		}

		// Calculate Vertex positions, centering the image
		float left = (-1) * width / 2;
		float down = (-1) * height / 2;
		float right = width / 2;
		float up = height / 2;

		// Rotates coordinates
		float[][] rotC = new float[4][];
		rotC[0] = rotate(x, y, left, up, theta);
		rotC[1] = rotate(x, y, right, up, theta);
		rotC[2] = rotate(x, y, right, down, theta);
		rotC[3] = rotate(x, y, left, down, theta);

		/*
		 * Converts XY coordinates into ST coordinates specifying the portion of
		 * the texture to be rendered
		 */
		float s0 = texX / texture.getTextureWidth();
		float t0 = texY / texture.getTextureHeight();
		float s1 = (texX + width) / texture.getTextureWidth();
		float t1 = (texY + height) / texture.getTextureHeight();

		VertexPack v0 = new VertexPack();
		VertexPack v1 = new VertexPack();
		VertexPack v2 = new VertexPack();
		VertexPack v3 = new VertexPack();

		v0.setXY(rotC[0][0] / gridX, rotC[0][1] / gridY).setRGB(0.5f, 0f, 1f)
				.setST(s0, t0);
		v1.setXY(rotC[1][0] / gridX, rotC[1][1] / gridY).setRGB(0.5f, 0f, 1f)
				.setST(s1, t0);
		v2.setXY(rotC[2][0] / gridX, rotC[2][1] / gridY).setRGB(0.5f, 0f, 1f)
				.setST(s1, t1);
		v3.setXY(rotC[3][0] / gridX, rotC[3][1] / gridY).setRGB(0.5f, 0f, 1f)
				.setST(s0, t1);

		// Put data into buffer for flushing later
		vertices.put(v0.getElements()).put(v1.getElements())
				.put(v2.getElements()).put(v3.getElements());
		vertexNum += 4;
	}

	/**
	 * Performs axis rotation on the coordinate given relative to image center
	 * XY
	 * 
	 * @param tX
	 *            Original relative X coordinate
	 * @param y
	 *            Original relative Y coordinate
	 * @param theta
	 *            Angle of rotation
	 * @return Rotated X and Y in a float array
	 */
	private float[] rotate(float x, float y, float tX, float tY, float theta) {
		float rX = x + tX * (float) Math.cos(theta) - tY
				* (float) Math.sin(theta);
		float rY = y + tX * (float) Math.sin(theta) + tY
				* (float) Math.cos(theta);
		return new float[] { rX, rY };
	}

	public void end() {
		if (!busy) {
			throw new IllegalStateException("No RenderUtility operation to end");
		}
		busy = false;
		flush();
	}

	public void flush() {
		if (vertexNum > 0) {
			vertices.flip();
			// Binds VAO and enables vertex attributes
			link.bind();
			pack.use();

			vbo.bind(GL_ARRAY_BUFFER);
			vbo.uploadSubData(GL_ARRAY_BUFFER, 0, vertices);

			glDrawArrays(GL_QUADS, 0, vertexNum);

			// Clears vertices
			vertexNum = 0;
			vertices.clear();
		}
	}


	public void reset() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	}
}
