package com.michaelkrauklis.android.thread;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.content.Context;
import android.opengl.GLSurfaceView.Renderer;
import android.opengl.GLU;
import android.util.Log;

import com.michaelkrauklis.android.microlife.run.MicroLifeRunThread;

public abstract class RunThread extends Thread implements Renderer {
	private static enum ThreadState {
		Initial, Lose, Pause, Ready, Running, Win, Killed
	}

	public void onSurfaceChanged(GL10 gl, int width, int height) {
		Log.d(TAG, "onSurfaceChanged");

		if (height == 0) { // Prevent A Divide By Zero By
			height = 1; // Making Height Equal One
		}

		gl.glViewport(0, 0, width, height); // Reset The Current Viewport
		gl.glMatrixMode(GL10.GL_PROJECTION); // Select The Projection Matrix
		gl.glLoadIdentity(); // Reset The Projection Matrix

		// Calculate The Aspect Ratio Of The Window
		GLU.gluPerspective(gl, 45.0f, (float) width / (float) height, 0.1f,
				200.0f);

		gl.glMatrixMode(GL10.GL_MODELVIEW); // Select The Modelview Matrix
		gl.glLoadIdentity(); // Reset The Modelview Matrix
	}

	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// TODO
	}

	private static final String TAG = RunThread.class.getName();

	protected Context context;
	private ThreadState state = ThreadState.Initial;
	private long lastModelUpdate = 0;
	private long threadThrottleMS = -1;

	public RunThread(Context context) {
		this(context, -1);
	}

	public RunThread(Context context, long threadThrottleMS) {
		setName("RunThread - " + getClass().getSimpleName());
		this.context = context;
		this.threadThrottleMS = threadThrottleMS;
	}

	public void setThreadThrottleMS(long threadThrottleMS) {
		this.threadThrottleMS = threadThrottleMS;
	}

	public void pause() {
		synchronized (this) {
			onPause();
		}
		setState(ThreadState.Pause);
	}

	public void unPause() {
		if (ThreadState.Initial == state) {
			Log.d(TAG, "Starting Thread");
			start();
		}
		synchronized (this) {
			Log.d(TAG, "UnPausing Thread, State: " + state);
			onUnPause();
		}
		setState(ThreadState.Running);
	}

	private void setState(ThreadState state) {
		if (state == ThreadState.Running && this.state != ThreadState.Running) {
			lastModelUpdate = System.currentTimeMillis();
		}
		this.state = state;
	}

	public void kill() {
		setState(ThreadState.Killed);
	}

	public boolean isKilled() {
		return ThreadState.Killed == state;
	}

	public boolean isInitial() {
		return ThreadState.Initial == state;
	}

	public boolean isRunning() {
		return state == ThreadState.Running;
	}

	@Override
	public void run() {
		while (true) {
			long now = System.currentTimeMillis();
			if (!isRunning()) {
				try {
					sleep(250);
				} catch (InterruptedException e) {
				}
			} else {
				if (lastModelUpdate + threadThrottleMS > now) {
					try {
						Thread.sleep((threadThrottleMS - now + lastModelUpdate) / 2);
					} catch (InterruptedException e) {
					}
				} else {
					synchronized (this) {
						if (isRunning()) {
							long timeElapsedSinceLastUpdate = System
									.currentTimeMillis() - lastModelUpdate;

							lastModelUpdate = System.currentTimeMillis();

							updateModel(timeElapsedSinceLastUpdate,
									lastModelUpdate);
						}
					}
				}
			}
		}
	}

	public void reinit() {
		synchronized (this) {
			pause();
			initialize();
			unPause();
		}
	}

	public abstract void initialize();

	public abstract void onDrawFrame(GL10 gl);

	protected abstract boolean updateModel(long timeElapsedSinceLastUpdate,
			long currentTime);

	protected abstract void onPause();

	protected abstract void onUnPause();
}