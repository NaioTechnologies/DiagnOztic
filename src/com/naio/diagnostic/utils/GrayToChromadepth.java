package com.naio.diagnostic.utils;

public class GrayToChromadepth {
	int[][] lookupTable = new int[255][3];

	public void compute_lut() {
		float value = 0.0f;
		float[] rgb = new float[3];
		for (int i = 0; i < 255; ++i) {

			value = i / 255.0f;
			
			gray_to_rgb(value, rgb);
			lookupTable[i][2] = (int) (255.0 * rgb[0]);
			lookupTable[i][1] = (int) (255.0 * rgb[1]);
			lookupTable[i][0] = (int) (255.0 * rgb[2]);
		}
	}

	private void gray_to_rgb(float gray, float[] rgb) {
		float gr = 1.0f - gray;
		float r = 1.0f;
		float g = 0.0f;
		float b = 1.0f - 6.0f * (gr - (5.0f / 6.0f));

		if (gr <= (5.0f / 6.0f)) {
			r = 6.0f * (gr - (4.0f / 6.0f));
			g = 0.0f;
			b = 1.0f;
		}

		if (gr <= (4.0f / 6.0f)) {
			r = 0.0f;
			g = 1.0f - 6.0f * (gr - (3.0f / 6.0f));
			b = 1.0f;
		}

		if (gr <= (3.0 / 6.0)) {
			r = 0.0f;
			g = 1.0f;
			b = 6.0f * (gr - (2.0f / 6.0f));
		}

		if (gr <= (2.0 / 6.0)) {
			r = 1.0f - 6.0f * (gr - (1.0f / 6.0f));
			g = 1.0f;
			b = 0.0f;
		}

		if (gr <= (1.0 / 6.0)) {
			r = 1.0f;
			g = 6.0f * gr;

		}

		rgb[0] = r;
		rgb[1] = g;
		rgb[2] = b;

	}

	public void getRGBFromZ(float z, int[] rgb) {
		rgb[0] = lookupTable[(int) z][0];
		rgb[1] = lookupTable[(int) z][1];
		rgb[2] = lookupTable[(int) z][2];

	}
}
