package graphics;

public class Sprite {
	public final int width;
	public final int height;
	public int texX;
	public final int texY;
	public final int frames;
	private int texXp;
	private final int texXinit;
	
	private int frameTime;
	private int tCount;

	public Sprite(int w, int h, int tx, int ty, int fr) {
		width = w;
		height = h;
		texX = tx;
		texY = ty;
		frames = fr;
		frameTime = 7;
		tCount = 0;
		texXp = texX;
		texXinit = texX;
	}
	
	public void setFrameTime(int ft){
		frameTime = ft;
	}

	public void update() {
		if(tCount%frameTime == 0){
			texXp = (texXp + width) % (width * frames);
			texX = texXinit + texXp;
		}
		tCount++;
	}

}
