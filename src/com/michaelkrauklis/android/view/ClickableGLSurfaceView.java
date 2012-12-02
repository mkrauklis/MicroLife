package com.michaelkrauklis.android.view;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class ClickableGLSurfaceView extends GLSurfaceView {
	
	public static interface IClickable{
		boolean onTouchEvent(MotionEvent event, int width, int height);
	}
	
	private IClickable clickable;

	public ClickableGLSurfaceView(Context context, IClickable clickable) {
		super(context);
		this.clickable = clickable;
	}
	
	public boolean onTouchEvent(final MotionEvent event) {
		return clickable.onTouchEvent(event, getWidth(), getHeight());
	}
}
