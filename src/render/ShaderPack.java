package render;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindFragDataLocation;

public class ShaderPack {
	public final int id;

	/**
	 * Attaches two Shaders so OpenGL will use them together; also packs them
	 * into one object
	 * 
	 * @param vShader
	 * @param fShader
	 */
	public ShaderPack(Shader vShader, Shader fShader) {
		id = glCreateProgram();
		glAttachShader(id, vShader.id);
		glAttachShader(id, fShader.id);

		glBindAttribLocation(id, 0, "position");
		// Color information will be attribute 1
		glBindAttribLocation(id, 1, "inColor");
		// Texture information will be attribute 2
		glBindAttribLocation(id, 2, "inSTcoord");

		glLinkProgram(id);
		glValidateProgram(id);
	}

	public void link() {
		glLinkProgram(id);

	}

	public int getUniformLocation(CharSequence name) {
		return glGetUniformLocation(id, name);
	}

	public void setUniform(int location, int value) {
		glUniform1i(location, value);
	}

	public int getAttribLocation(CharSequence name) {
		return glGetAttribLocation(id, name);
	}

	public void bindFragmentDataLocation(int number, CharSequence name) {
		glBindFragDataLocation(id, number, name);
	}

	public void enableVertexAttribArray(int location) {
		glEnableVertexAttribArray(location);
	}

	public void disableVertexAttribArray(int location) {
		glDisableVertexAttribArray(location);
	}

	/**
	 * Sets the vertex attribute pointer.
	 *
	 * @param location
	 *            Location of the vertex attribute
	 * @param size
	 *            Number of values per vertex
	 * @param stride
	 *            Offset between consecutive generic vertex attributes in bytes
	 * @param offset
	 *            Offset of the first component of the first generic vertex
	 *            attribute in bytes
	 */
	public void vertexAttribPointer(int location, int size, int stride,
			int offset) {
		glVertexAttribPointer(location, size, GL_FLOAT, false, stride, offset);
	}

	public void use() {
		glUseProgram(id);
	}
}
