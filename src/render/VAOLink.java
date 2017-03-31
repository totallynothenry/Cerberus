package render;

import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class VAOLink {
    public final int id;

    /**
     * Creates a VAOLink; functions as a buffer to hold vertex information until rendering occurs (also called a Vertex Array Object)
     */
    public VAOLink() {
        id = glGenVertexArrays();
    }

    /**
     * Binds the VAOLink as the current target Vertex Array
     */
    public void bind() {
        glBindVertexArray(id);
    }

}
