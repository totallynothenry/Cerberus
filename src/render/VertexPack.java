package render;

public class VertexPack {
	// All attribute data for a 2D RGB vertex
	private float[] xy = new float[] { 0f, 0f };
	private float[] rgb = new float[] { 1f, 1f, 1f };
	private float[] st = new float[] { 0f, 0f };

	// The amount of bytes an element has
	public static final int elementBytes = 8;

	// Elements per parameter
	public static final int posCount = 2; // 2D
	public static final int colorCount = 3; // RGB
	public static final int texPosCount = 2; // Always 2 unless using 3D model

	public void setST(float s, float t) {
		st = new float[] { s, t };
	}

	public VertexPack setXY(float x, float y) {
		xy = new float[] { x, y };
		return this;
	}

	public VertexPack setRGB(float r, float g, float b) {
		rgb = new float[] { r, g, b};
		return this;
	}

	public float[] getElements() {
		float[] out = new float[posCount + colorCount + texPosCount];
		int i = 0;
		for (float f : xy) {
			out[i++] = f;
		}
		for (float f : rgb) {
			out[i++] = f;
		}
		for (float f : st) {
			out[i++] = f;
		}
		return out;
	}
}
