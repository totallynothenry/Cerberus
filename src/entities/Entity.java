package entities;

import org.newdawn.slick.opengl.Texture;

import game.Game;
import graphics.Sprite;
import render.RenderUtility;


public abstract class Entity {
	// Grid coordinates for the entity;
	private float[] position;

	// Velocity of the entity;
	private float[] velocity;

	// Acceleration of the entity;
	private float[] acceleration;

	// Rate of rotation
	private float dTheta;

	// Rate of acceleration of rotation
	private float d2Theta;

	// Team number of
	private int teamNum;

	public final HitBox hBox;

	public final String UID;

	public Entity(String UID, float xCoord, float yCoord, double angle, int tNum, HitBox hbox) {
		this.UID = UID;

		position = new float[] { xCoord, yCoord, (float) angle };
		velocity = new float[] { 0, 0 };
		acceleration = new float[] { 0, 0 };

		dTheta = 0;

		teamNum = tNum;

		hBox = hbox;
	}

	// Setters
	/**
	 * Sets the position of the entity
	 * 
	 * @param xCoord
	 *            x coordinate defined in relation to the game grid (default: 0
	 *            to 1080)
	 * @param yCoord
	 *            y coordinate defined in relation to the game grid (default: 0
	 *            to 720)
	 * @param theta
	 *            The angle at which the entity is rotated, with 0 being right
	 *            pi/2 being up;
	 */
	public void setPos(double xCoord, double yCoord, double theta) {
		position[0] = (float) xCoord;
		position[1] = (float) yCoord;
		position[2] = (float) theta;
	}

	/**
	 * Just sets the angle
	 * 
	 * @param t
	 *            Angle in radians from 0 to 2Pi
	 */
	public void setTheta(double t) {
		position[2] = (float) t;
	}

	/**
	 * Sets the velocity using a resolved vector <Vx,Vy>.
	 * 
	 * @param Vx
	 *            The change in x; positive is to the right
	 * @param Vy
	 *            The change in y; positive is up
	 */
	public void setVelocity(double Vx, double Vy) {
		velocity[0] = (float) Vx;
		velocity[1] = (float) Vy;
	}

	/**
	 * Resolves a polar vector to a Cartesian vector and then calls
	 * setVelocity();
	 * 
	 * @param r
	 *            The change in distance
	 * @param theta
	 *            The angle of the change; 0 is to the right
	 */
	public void setVPolar(double r, double theta) {
		setVelocity((float) (r * Math.cos(theta)), (float) (r * Math.sin(theta)));
	}

	public void setVx(float Vx) {
		velocity[0] = Vx;
	}

	public void setVy(float Vy) {
		velocity[1] = Vy;
	}

	public void setAcceleration(double Ax, double Ay) {
		acceleration[0] = (float) Ax;
		acceleration[1] = (float) Ay;
	}

	public void setAPolar(double r, double theta) {
		acceleration[0] = (float) (r * Math.cos(theta));
		acceleration[1] = (float) (r * Math.sin(theta));
	}

	public void setD2Theta(double d2t) {
		d2Theta = (float) d2t;
	}

	/**
	 * Sets the change in theta (a.k.a position[2]) per update; positive is
	 * counter clockwise
	 * 
	 * @param dt
	 *            The change in theta from 0 to 2Pi
	 */
	public void setRotation(double theta, double dt) {
		double diff = theta - position[2];
		if (diff > Math.PI) {
			diff -= 2 * Math.PI;
		}
		if (diff < -Math.PI) {
			diff += 2 * Math.PI;
		}
		if (diff > 0) {
			dTheta = (float) dt;
		} else if (diff < 0) {
			dTheta = (float) -dt;
		}
		if (diff != 0 && Math.abs(diff) < dt * 2) {
			position[2] = (float) theta;
			dTheta = 0;
		}
		if (diff == 0) {
			dTheta = 0;
		}
	}

	public void setRotationA(double theta, double d2t) {
		double diff = theta - position[2];
		if (diff > Math.PI) {
			diff -= 2 * Math.PI;
		}
		if (diff < -Math.PI) {
			diff += 2 * Math.PI;
		}
		if (diff > 0) {
			d2Theta = (float) d2t;
		} else if (diff < 0) {
			d2Theta = (float) -d2t;
		}
		if (diff != 0 && Math.abs(diff) < d2t * 2) {
			position[2] = (float) theta;
			dTheta = 0;
		}
		if (diff == 0) {
			dTheta = 0;
		}
	}

	// Getters
	/**
	 * Gets the position array
	 * 
	 * @return float array with x in the 0 position, y in the 1 position, and
	 *         the angle in radians in the 2 position
	 */
	public float[] getPos() {
		return position;
	}

	public float getVx() {
		return velocity[0];
	}

	public float getVy() {
		return velocity[1];
	}

	public float getAx() {
		return acceleration[0];
	}

	public float getAy() {
		return acceleration[1];
	}

	public float getd2Theta() {
		return d2Theta;
	}

	/**
	 * Returns rate of rotation
	 * 
	 * @return Change in angle in radians
	 */
	public float getRot() {
		return dTheta;
	}

	public int getTeam() {
		return teamNum;
	}

	/**
	 * Updates the state of the Entity; all changes to position, direction
	 * should be done here along with actions like shooting missiles and shit
	 */
	public void update(float px, float py, float delta) {
		velocity[0] += acceleration[0]*delta;
		velocity[1] += acceleration[1]*delta;
		dTheta += d2Theta*delta;

		position[0] += velocity[0]*delta;
		position[1] += velocity[1]*delta;
		position[2] += dTheta*delta;
	}

	/**
	 * Renders the correct texture transformed to be in the same state of the
	 * Entity; all texture transformations should be done here
	 */
	public abstract void render(Texture sheet, RenderUtility renderer);

	public void render(Texture sheet, RenderUtility renderer, Sprite s) {
		renderer.bufferVertices(sheet, position[0], position[1], position[2], s.width, s.height, s.texX, s.texY,
				Game.GRIDW, Game.GRIDH);
	}

	/**
	 * Should destroy the Entity, freeing memory space used
	 */
	public void destroy() {
	}
}
