package domainModel;
public class Light {
	public Vector3D v;
	public RGB rgb;

	public Light(Vector3D v, RGB rgb) {
		this.v = v;
		this.rgb = rgb;
	}

	public Vector3D getV() {
		return v;
	}

	public RGB getRgb() {
		return rgb;
	}

	@Override
	public String toString() {
		return "Light{" +
				"V=" + v +
				", rgb=" + rgb +
				'}';
	}
}