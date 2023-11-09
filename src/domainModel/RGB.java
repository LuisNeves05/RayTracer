package domainModel;

import java.awt.Color;

public class RGB {
	public float r, g, b;
	
	public RGB() {
		r=g=b=0;
	}
	
	public RGB(int r, int g, int b) {
		// assumes range of 0-255
		this.r = r;
		this.g = g;
		this.b = b;
	}
	
	public RGB(float r, float g, float b) {
		setFromFloats(r, g, b);
	}
	
	public RGB(Vector3D c) {
		// assumes the input vector contains color information
		setFromFloats(c.x, c.y, c.z);
	}
	
	public void setFromFloats(float r, float g, float b) {
		// assumes range of 0-1;
		this.r = (r < 1.0f) ? (int)(r*255) : 255;
		this.g = (g < 1.0f) ? (int)(g*255) : 255;
		this.b = (b < 1.0f) ? (int)(b*255) : 255;
	}

	public RGB add(RGB other) {
		this.r = clamp(this.r + other.r, 0, 255);
		this.g = clamp(this.g + other.g, 0, 255);
		this.b = clamp(this.b + other.b, 0, 255);
		return this;
	}

	public RGB mult(float scalar) {
		int newR = (int) clamp(this.r * scalar, 0, 255);
		int newG = (int) clamp(this.g * scalar, 0, 255);
		int newB = (int) clamp(this.b * scalar, 0, 255);
		return new RGB(newR, newG, newB);
	}

	public RGB multiply(RGB other) {
		int newR = (int) clamp(this.r * other.r, 0, 255);
		int newG = (int) clamp(this.g * other.g, 0, 255);
		int newB = (int) clamp(this.b * other.b, 0, 255);
		return new RGB(newR, newG, newB);
	}

	private float clamp(float value, float min, float max) {
		return Math.max(min, Math.min(max, value));
	}

	public RGB divide(int divisor) {
		if (divisor != 0) {
			r /= divisor;
			g /= divisor;
			b /= divisor;
		}

		return this;
	}

	public void checkRange() {
		r = Math.max(0.0f, Math.min(1.0f, r));
		g = Math.max(0.0f, Math.min(1.0f, g));
		b = Math.max(0.0f, Math.min(1.0f, b));
	}
	
	public RGB get() {
		return new RGB(r, g, b);
	}
	
	public Color toColor() {
		return new Color(r, g, b);
	}

	@Override
	public String toString() {
		return "RGB{" +
				"r=" + r +
				", g=" + g +
				", b=" + b +
				'}';
	}
}
