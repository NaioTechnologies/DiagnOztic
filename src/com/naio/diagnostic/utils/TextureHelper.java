package com.naio.diagnostic.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.opengl.GLES20;
import android.opengl.GLUtils;
import android.util.Log;

public class TextureHelper {
	public static int idx = 0;
	private static int[] textures = new int[1];
	public static int loadTexture(final Context context, final int resourceId) {
		GLES20.glGenTextures(1, textures, 0);
		if (textures[0] != 0) {
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inScaled = false; // No pre-scaling
			// Read in the resource
			final Bitmap bitmap = BitmapFactory.decodeResource(
					context.getResources(), resourceId, options);
			// Bind to the texture in OpenGL
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);
			// Set filtering
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
					GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
			GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
					GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
			// Load the bitmap into the bound texture.
			GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
			// Recycle the bitmap, since its data has been loaded into OpenGL.
			bitmap.recycle();
		}
		if (textures[0] == 0) {
			throw new RuntimeException("Error loading texture.");
		}
		return textures[0];
	}
	
	public static int loadTextureBitmap(final Context context,final Bitmap bitmap) {
		//GLES20.glDeleteTextures(1, textures, 1);
		if(bitmap == null)
			return textures[0];
		GLES20.glGenTextures(1, textures, 0);
		if (textures[0] != 0) {
			// Bind to the texture in OpenGL
			GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0]);
			// Set filtering
						GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
								GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_NEAREST);
						GLES20.glTexParameteri(GLES20.GL_TEXTURE_2D,
								GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_NEAREST);
			// Load the bitmap into the bound texture.
			GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0);
			// Recycle the bitmap, since its data has been loaded into OpenGL.
			//bitmap.recycle();
			
		}
		if (textures[0] == 0) {
			
			throw new RuntimeException("Error loading texture. " +GLUtils.getEGLErrorString(GLES20.glGetError()));
		}
		return textures[0];
	}
}