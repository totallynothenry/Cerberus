package render;

import static org.lwjgl.opengl.GL15.glBindBuffer;
import static org.lwjgl.opengl.GL15.glBufferData;
import static org.lwjgl.opengl.GL15.glBufferSubData;
import static org.lwjgl.opengl.GL15.glDeleteBuffers;
import static org.lwjgl.opengl.GL15.glGenBuffers;

import java.nio.FloatBuffer;

public class VBO {
    public final int id;

    public VBO() {
        id = glGenBuffers();
    }

    public void bind(int gltarget) {
        glBindBuffer(gltarget, id);
    }

    public void uploadData(int gltarget, FloatBuffer data, int usage) {
        glBufferData(gltarget, data, usage);
    }

    public void uploadData(int gltarget, long size, int usage) {
        glBufferData(gltarget, size, usage);
    }

    public void uploadSubData(int gltarget, long offset, FloatBuffer data) {
        glBufferSubData(gltarget, offset, data);
    }
}
