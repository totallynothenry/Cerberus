package render;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.opengl.GL20.*;

public class Shader {
	public final int id;

	// Should only be vertex of fragment
	private Shader(int type, String source) {
		//New glShader made from source
		id = glCreateShader(type);
		glShaderSource(id, source);
		glCompileShader(id);
	}

	public void delete() {
		glDeleteShader(id);
	}

	public static Shader loadShader(int type, String path) {
		//Converts the file into a String
		StringBuilder builder = new StringBuilder();
		try (InputStream in = new FileInputStream(path);
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(in))) {
			String line;
			while ((line = reader.readLine()) != null) {
				builder.append(line).append("\n");
			}
		} catch (IOException ex) {
			throw new RuntimeException("No shader file at path location.");
		}
		//Constructs a new Shader
		return new Shader(type, builder.toString());
	}
}