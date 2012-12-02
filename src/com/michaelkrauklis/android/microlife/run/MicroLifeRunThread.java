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
	public static class TriangleStrip {

		private FloatBuffer vertexBuffer; // buffer holding the vertices

		private float vertices[];

		public TriangleStrip(float vertices[]) {
			this.vertices = vertices;

			// a float has 4 bytes so we allocate for each coordinate 4 bytes
			ByteBuffer vertexByteBuffer = ByteBuffer
					.allocateDirect(vertices.length * 4);
			vertexByteBuffer.order(ByteOrder.nativeOrder());

			// allocates the memory from the byte buffer
			vertexBuffer = vertexByteBuffer.asFloatBuffer();

			// fill the vertexBuffer with the vertices
			vertexBuffer.put(vertices);

			// set the cursor position to the beginning of the buffer
			vertexBuffer.position(0);
		}

		/** The draw method for the triangle with the GL context */
		public void draw(GL10 gl, int r, int g, int b) {
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
	float teraStateDelta = 0.03f;
	float microbeVoracity = 0.1f;
	float matingThreshold = 1f;
	float costOfLife = 0.25f;
	float phageVoracity = 0.25f;
	float infectionDeathThreshold = 0.5f;
	float phageDurability = 0.9f;
	float newInfectionStrength = 0.25f;

	int[][] teraState;
	int[][] microbeState;
	int[][] phageState;
	int[][] quarantineState;

	private DrawType drawType = DrawType.BACTERIA;

	public void setDrawType(DrawType drawType) {
		this.drawType = drawType;
	}

	public MicroLifeRunThread(Context context) {
		super(context, 100);
	}

	@Override
	public synchronized void onDrawFrame(GL10 gl) {
		gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();
		gl.glTranslatef(-1 * microbeState[0].length * microbeSize / 2, -1
				* microbeState.length * microbeSize / 2, -165f);
		// gl.glScalef(0.75f, 0.75f, 1f);

		// DRAW HERE
		Log.d(TAG, "Starting Draw");

		for (int i = 0; i < teraState.length; i++) {
			for (int j = 0; j < teraState[i].length; j++) {
				int r = phageState[i][j];
				int g = teraState[i][j];
				int b = microbeState[i][j];

				if (quarantineState[i][j] > stateMinInt) {
					r = g = b = 155;
				}

				drawRect(gl, j * microbeSize, i * microbeSize, (j + 1)
						* microbeSize, (i + 1) * microbeSize, r, g, b);
			}
		}

		Log.d(TAG, "0,0 teraState: " + teraState[0][0]);
	}

	private void drawRect(GL10 gl, int x1, int y1, int x2, int y2, int r,
			int g, int b) {
		new TriangleStrip(new float[] { x1, y1, 0.0f, x1, y2, 0.0f, x2, y1,
				0.0f, x2, y2, 0.0f }).draw(gl, r, g, b);
	}

	@Override
	public void initialize() {
		initializeProperties();

		Log.d(TAG, "Microbe Size " + microbeSize);
		Log.d(TAG, "Microbes " + microbesWidth + "x" + microbesHeight);

		teraState = new int[microbesHeight][microbesWidth];
		microbeState = new int[microbesHeight][microbesWidth];
		phageState = new int[microbesHeight][microbesWidth];
		quarantineState = new int[microbesHeight][microbesWidth];
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
	}

	@Override
	protected void onPause() {
		initializeProperties();
	}

	@Override
	protected void onUnPause() {
	}

	@Override
	protected synchronized boolean updateModel(long timeElapsedSinceLastUpdate,
			long currentTime) {
		for (int i = 0; i < teraState.length; i++) {
			for (int j = 0; j < teraState[i].length; j++) {
				updateModelIndex(i, j);
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
			microbeState[i][j] -= microbeVoracity * stateMax;
			if (microbeState[i][j] < stateMinInt) {
				microbeState[i][j] = stateMinInt;
			}
		}

		// mate
		if (microbeState[i][j] > stateMinInt) {
			if (teraState[i][j] >= costOfLife * stateMax) {
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
					int direction = (int) (Math.random() * 4);
					switch (direction) {
					case 0:
						// north
						if (i + 1 < microbeState.length
								&& microbeState[i + 1][j] == stateMinInt
								&& quarantineState[i + 1][j] == stateMinInt) {
							microbeState[i + 1][j] = stateMaxInt;
						}
						break;
					case 1:
						// south
						if (i > 0 && microbeState[i - 1][j] == stateMinInt
								&& quarantineState[i - 1][j] == stateMinInt) {
							microbeState[i - 1][j] = stateMaxInt;
						}
						break;
					case 2:
						// east
						if (j > 0 && microbeState[i][j - 1] == stateMinInt
								&& quarantineState[i][j - 1] == stateMinInt) {
							microbeState[i][j - 1] = stateMaxInt;
						}
						break;
					case 3:
						// west
						if (j + 1 < microbeState[i].length
								&& microbeState[i][j + 1] == stateMinInt
								&& quarantineState[i][j + 1] == stateMinInt) {
							microbeState[i][j + 1] = stateMaxInt;
						}
						break;
					default:
						// do nothing
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
				phageState[i][j] += phageVoracity * stateMax;
				microbeState[i][j] -= phageVoracity * stateMax;
				if (phageState[i][j] > stateMaxInt) {
					phageState[i][j] = stateMaxInt;
				}
				if (microbeState[i][j] < stateMinInt) {
					microbeState[i][j] = stateMinInt;
				}
			} else {
				phageState[i][j] -= (1 - phageDurability) * phageVoracity
						* stateMax;
				if (phageState[i][j] < stateMinInt) {
					phageState[i][j] = stateMinInt;
				}
			}

			if (phageState[i][j] >= stateMaxInt * infectionDeathThreshold) {
				microbeState[i][j] = stateMinInt;

				int newInfectionState = (int) (stateMaxInt * newInfectionStrength);

				// north
				if (i + 1 < microbeState.length) {
					phageState[i + 1][j] = newInfectionState;
				}
				// south
				if (i > 0) {
					phageState[i - 1][j] = newInfectionState;
				}
				// east
				if (j > 0) {
					phageState[i][j - 1] = newInfectionState;
				}
				// west
				if (j + 1 < microbeState[i].length) {
					phageState[i][j + 1] = newInfectionState;
				}
			}
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
		microbeState[y][x] = teraState[y][x] = phageState[y][x] = quarantineState[y][x] = stateMinInt;
		theArray[y][x] = stateMaxInt;

		if (drawType == DrawType.BACTERIA) {
			teraState[y][x] = originalTeraState;
		}

		return true;
	}
}
