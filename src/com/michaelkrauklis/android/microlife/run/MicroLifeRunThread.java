package com.michaelkrauklis.android.microlife.run;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.MotionEvent;

import com.michaelkrauklis.android.microlife.MicroLife;
import com.michaelkrauklis.android.microlife.preferences.Preferences;
import com.michaelkrauklis.android.thread.RunThread;
import com.michaelkrauklis.android.view.ClickableGLSurfaceView.IClickable;

public class MicroLifeRunThread extends RunThread implements IClickable {
	public static class Quadrangle {

		private FloatBuffer vertexBuffer; // buffer holding the vertices

		private float vertices[] = new float[12];

		public Quadrangle() {
			// a float has 4 bytes so we allocate for each coordinate 4 bytes
			ByteBuffer vertexByteBuffer = ByteBuffer.allocateDirect(12 * 4);

			vertexByteBuffer.order(ByteOrder.nativeOrder());

			// allocates the memory from the byte buffer
			vertexBuffer = vertexByteBuffer.asFloatBuffer();
		}

		public void setVertices(float x1, float y1, float x2, float y2,
				float x3, float y3, float x4, float y4) {
			vertices[0] = x1;
			vertices[1] = y1;
			vertices[3] = x2;
			vertices[4] = y2;
			vertices[6] = x3;
			vertices[7] = y3;
			vertices[9] = x4;
			vertices[10] = y4;
		}

		/** The draw method for the triangle with the GL context */
		public void draw(GL10 gl, int r, int g, int b) {
			vertexBuffer.position(0);
			vertexBuffer.put(vertices);
			vertexBuffer.position(0);

			gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
			// set the colour for the background
			// gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);

			// to show the color (paint the screen) we need to clear the color
			// buffer
			// gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

			// set the colour for the triangle
			gl.glColor4f(r / 255f, g / 255f, b / 255f, 1);

			// Point to our vertex buffer
			gl.glVertexPointer(3, GL10.GL_FLOAT, 0, vertexBuffer);

			// Draw the vertices as triangle strip
			gl.glDrawArrays(GL10.GL_TRIANGLE_STRIP, 0, vertices.length / 3);

			// Disable the client state before leaving
			gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
		}
	}

	private static final String TAG = MicroLifeRunThread.class.getName();

	public static enum DrawType {
		BACTERIA, PHAGE, FOOD, QUARANTINE
	}

	final float stateMax = 255;
	final float stateMin = 0;
	final int stateMaxInt = (int) stateMax;
	final int stateMinInt = (int) stateMin;

	int microbeSize = 3;
	int microbesHeight = 140 / microbeSize;
	int microbesWidth = 230 / microbeSize;
	float teraStateDelta = 0.11f;
	float microbeVoracity = 0.15f;
	float matingThreshold = 0.39f;
	float costOfLife = 0.15f;
	float phageVoracity = 0.33f;
	float infectionDeathThreshold = 0.5f;
	float phageDurability = 0.74f;
	float newInfectionStrength = 0.25f;
	float microbeDurability = 0.5f;
	boolean isRandomMicrobeReproduction = true;
	long updateSpeedMax = 400;
	float updateSpeed = 0.25f;

	int[][] teraState;
	int[][] microbeState;
	int[][] phageState;
	int[][] deltaPhageState;
	int[][] quarantineState;
	Quadrangle[][] quadrangles;

	private DrawType drawType = DrawType.BACTERIA;

	public void setDrawType(DrawType drawType) {
		this.drawType = drawType;
	}

	public MicroLifeRunThread(Context context) {
		super(context, 100);
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();
		gl.glTranslatef(-1 * microbeState[0].length * microbeSize / 2, -1
				* microbeState.length * microbeSize / 2, -165f);
		// gl.glScalef(0.75f, 0.75f, 1f);

		// DRAW HERE
		// Log.d(TAG, "Starting Draw");

		for (int i = 0; i < teraState.length; i++) {
			for (int j = 0; j < teraState[i].length; j++) {
				int r = phageState[i][j];
				int g = teraState[i][j];
				int b = microbeState[i][j];

				if (quarantineState[i][j] > stateMinInt) {
					r = g = b = 155;
				}

				drawRect(i, j, gl, j * microbeSize, i * microbeSize, (j + 1)
						* microbeSize, (i + 1) * microbeSize, r, g, b);
			}
		}

		// Log.d(TAG, "0,0 teraState: " + teraState[0][0]);
	}

	private void drawRect(int i, int j, GL10 gl, int x1, int y1, int x2,
			int y2, int r, int g, int b) {
		quadrangles[i][j].setVertices(x1, y1, x1, y2, x2, y1, x2, y2);
		quadrangles[i][j].draw(gl, r, g, b);
	}

	@Override
	public void initialize() {
		initializeProperties();

		Log.d(TAG, "Microbe Size " + microbeSize);
		Log.d(TAG, "Microbes " + microbesWidth + "x" + microbesHeight);

		teraState = new int[microbesHeight][microbesWidth];
		microbeState = new int[microbesHeight][microbesWidth];
		phageState = new int[microbesHeight][microbesWidth];
		deltaPhageState = new int[microbesHeight][microbesWidth];
		quarantineState = new int[microbesHeight][microbesWidth];
		quadrangles = new Quadrangle[microbesHeight][microbesWidth];
		for (int i = 0; i < microbesHeight; i++) {
			for (int j = 0; j < microbesWidth; j++) {
				quadrangles[i][j] = new Quadrangle();
			}
		}
	}

	private void initializeProperties() {
		SharedPreferences sharedPreferences = context.getSharedPreferences(
				MicroLife.class.getName(), Context.MODE_PRIVATE);

		microbeSize = sharedPreferences.getInt(Preferences.MICROBE_SIZE, 3);
		microbesHeight = 140 / microbeSize;
		microbesWidth = 230 / microbeSize;
		teraStateDelta = sharedPreferences.getFloat(Preferences.TERA_DELTA,
				0.03f);
		microbeVoracity = sharedPreferences.getFloat(
				Preferences.MICROBE_VORACITY, 0.1f);
		microbeDurability = sharedPreferences.getFloat(
				Preferences.MICROBE_DURABILITY, 0.5f);
		matingThreshold = sharedPreferences.getFloat(
				Preferences.MATING_THRESHOLD, 0.7f);
		costOfLife = sharedPreferences
				.getFloat(Preferences.COST_OF_LIFE, 0.25f);
		phageVoracity = sharedPreferences.getFloat(Preferences.PHAGE_VORACITY,
				0.25f);
		infectionDeathThreshold = sharedPreferences.getFloat(
				Preferences.INFECTION_DEATH_THRESHOLD, 0.5f);
		phageDurability = sharedPreferences.getFloat(
				Preferences.PHAGE_DURABILITY, 0.9f);
		newInfectionStrength = sharedPreferences.getFloat(
				Preferences.NEW_INFECTION_STRENGTH, 0.25f);
		updateSpeed = sharedPreferences.getFloat(Preferences.UPDATE_SPEED,
				0.98f);
		isRandomMicrobeReproduction = sharedPreferences.getBoolean(
				Preferences.IS_MICROBE_MATING_RANDOM, true);

		long throttle = Math.max(
				(long) ((1 - updateSpeed) * updateSpeedMax * 10), 50);

		setThreadThrottleMS(throttle);

		// if (Log.isLoggable(TAG, Log.DEBUG)) {
		Log.d(TAG, "[microbeSize:" + microbeSize + "]");
		Log.d(TAG, "[microbesHeight:" + microbesHeight + "]");
		Log.d(TAG, "[microbesWidth:" + microbesWidth + "]");
		Log.d(TAG, "[teraStateDelta:" + teraStateDelta + "]");
		Log.d(TAG, "[microbeVoracity:" + microbeVoracity + "]");
		Log.d(TAG, "[microbeDurability:" + microbeDurability + "]");
		Log.d(TAG, "[matingThreshold:" + matingThreshold + "]");
		Log.d(TAG, "[costOfLife:" + costOfLife + "]");
		Log.d(TAG, "[phageVoracity:" + phageVoracity + "]");
		Log.d(TAG, "[infectionDeathThreshold:" + infectionDeathThreshold + "]");
		Log.d(TAG, "[phageDurability:" + phageDurability + "]");
		Log.d(TAG, "[newInfectionStrength:" + newInfectionStrength + "]");
		Log.d(TAG, "[updateSpeed:" + updateSpeed + "]");
		Log.d(TAG, "[throttle:" + throttle + "ms]");
		Log.d(TAG, "[isRandomMicrobeReproduction:"
				+ isRandomMicrobeReproduction + "]");
		// }
	}

	@Override
	protected void onPause() {
	}

	@Override
	protected void onUnPause() {
		initializeProperties();
	}

	@Override
	protected boolean updateModel(long timeElapsedSinceLastUpdate,
			long currentTime) {
		// if update speed is not 0 update the model (0 = paused)
		if (updateSpeed != 0) {
			for (int i = 0; i < teraState.length; i++) {
				for (int j = 0; j < teraState[i].length; j++) {
					phageState[i][j] += deltaPhageState[i][j];
					deltaPhageState[i][j] = stateMinInt;
					if (phageState[i][j] > stateMaxInt) {
						phageState[i][j] = stateMaxInt;
					}
					if (phageState[i][j] < stateMinInt) {
						phageState[i][j] = stateMinInt;
					}
				}
			}
			for (int i = 0; i < teraState.length; i++) {
				for (int j = 0; j < teraState[i].length; j++) {
					updateModelIndex(i, j);
				}
			}
		}

		return true;
	}

	private void updateModelIndex(int i, int j) {
		// grow
		teraState[i][j] += teraStateDelta * stateMax;
		if (teraState[i][j] > stateMaxInt) {
			teraState[i][j] = stateMaxInt;
		}

		// eat
		if (microbeState[i][j] > stateMinInt) {
			if (teraState[i][j] < microbeVoracity * stateMax) {
				microbeState[i][j] += teraState[i][j];
				teraState[i][j] = stateMinInt;
			} else {
				microbeState[i][j] += microbeVoracity * stateMax;
				teraState[i][j] -= microbeVoracity * stateMax;
			}
			if (teraState[i][j] < stateMinInt) {
				teraState[i][j] = stateMinInt;
			}
			if (microbeState[i][j] > stateMaxInt) {
				microbeState[i][j] = stateMaxInt;
			}

			// live
			microbeState[i][j] -= (1 - microbeDurability) * microbeVoracity
					* stateMax;
			if (microbeState[i][j] < stateMinInt) {
				microbeState[i][j] = stateMinInt;
			}
		}

		// mate
		if (microbeState[i][j] > stateMinInt) {
			if (microbeState[i][j] >= costOfLife * stateMax) {
				int lifeScore = 0;
				if (i > 0) {
					if (j > 0) {
						lifeScore += microbeState[i - 1][j - 1];
					}
					if (j + 1 < microbeState[i].length) {
						lifeScore += microbeState[i - 1][j + 1];
					}
				}
				if (i + 1 < microbeState.length) {
					if (j > 0) {
						lifeScore += microbeState[i + 1][j - 1];
					}
					if (j + 1 < microbeState[i].length) {
						lifeScore += microbeState[i + 1][j + 1];
					}
				}

				// we have partners and food, mate
				if (lifeScore >= matingThreshold * stateMax) {
					if (isRandomMicrobeReproduction) {
						int direction = (int) (Math.random() * 4);
						if (direction == 0) {
							newMicrobeNorth(i, j);
						}
						if (direction == 1) {
							newMicrobeSouth(i, j);
						}
						if (direction == 2) {
							newMicrobeEast(i, j);
						}
						if (direction == 3) {
							newMicrobeWest(i, j);
						}
					} else {
						newMicrobeNorth(i, j);
						newMicrobeSouth(i, j);
						newMicrobeEast(i, j);
						newMicrobeWest(i, j);
					}
					microbeState[i][j] -= costOfLife * stateMax;
					if (microbeState[i][j] < stateMinInt) {
						microbeState[i][j] = stateMinInt;
					}
				}
			}
		}

		if (phageState[i][j] > stateMinInt) {
			if (microbeState[i][j] > stateMinInt) {
				deltaPhageState[i][j] += phageVoracity * stateMax;
				microbeState[i][j] -= phageVoracity * stateMax;
				if (microbeState[i][j] < stateMinInt) {
					microbeState[i][j] = stateMinInt;
				} else if (phageState[i][j] >= stateMaxInt
						* infectionDeathThreshold) {
					microbeState[i][j] = stateMinInt;

					int newInfectionState = (int) (stateMaxInt * newInfectionStrength);

					// north
					if (i + 1 < microbeState.length) {
						deltaPhageState[i + 1][j] = newInfectionState;
					}
					// south
					if (i > 0) {
						deltaPhageState[i - 1][j] = newInfectionState;
					}
					// east
					if (j > 0) {
						deltaPhageState[i][j - 1] = newInfectionState;
					}
					// west
					if (j + 1 < microbeState[i].length) {
						deltaPhageState[i][j + 1] = newInfectionState;
					}
				}
			} else {
				deltaPhageState[i][j] -= (1 - phageDurability) * phageVoracity
						* stateMax;
			}
		}
	}

	private void newMicrobeWest(int i, int j) {
		if (j + 1 < microbeState[i].length
				&& microbeState[i][j + 1] == stateMinInt
				&& quarantineState[i][j + 1] == stateMinInt) {
			microbeState[i][j + 1] = stateMaxInt;
		}
	}

	private void newMicrobeEast(int i, int j) {
		if (j > 0 && microbeState[i][j - 1] == stateMinInt
				&& quarantineState[i][j - 1] == stateMinInt) {
			microbeState[i][j - 1] = stateMaxInt;
		}
	}

	private void newMicrobeSouth(int i, int j) {
		if (i > 0 && microbeState[i - 1][j] == stateMinInt
				&& quarantineState[i - 1][j] == stateMinInt) {
			microbeState[i - 1][j] = stateMaxInt;
		}
	}

	private void newMicrobeNorth(int i, int j) {
		if (i + 1 < microbeState.length
				&& microbeState[i + 1][j] == stateMinInt
				&& quarantineState[i + 1][j] == stateMinInt) {
			microbeState[i + 1][j] = stateMaxInt;
		}
	}

	public boolean onTouchEvent(MotionEvent event, int width, int height) {
		// if (event.getAction() != MotionEvent.ACTION_DOWN) {
		// return false;
		// }
		int[][] theArray = microbeState;

		int y = theArray.length - 1
				- (int) (event.getY() / height * theArray.length);
		int x = (int) (event.getX() / width * theArray[0].length);

		if (y >= 0 && x >= 0 && y < theArray.length && x < theArray[y].length) {
			int originalTeraState = teraState[y][x];

			switch (drawType) {
			case BACTERIA:
				theArray = microbeState;
				break;
			case FOOD:
				theArray = teraState;
				break;
			case PHAGE:
				theArray = phageState;
				break;
			case QUARANTINE:
				theArray = quarantineState;
				break;
			}
			microbeState[y][x] = teraState[y][x] = phageState[y][x] = deltaPhageState[y][x] = quarantineState[y][x] = stateMinInt;
			theArray[y][x] = stateMaxInt;

			if (drawType == DrawType.BACTERIA) {
				teraState[y][x] = originalTeraState;
			}
		}

		return true;
	}
}
