package domainModel;

public class Material {
	public RGB rgb;
	public float Kd, Ka, Ks; // diffuse, ambient, and specular coefficients (red, green, blue)
	public float KreflIndex;
	public float Krefl; // reflection coefficient (0 = no reflection, 1 = perfect mirror)
	// Usually, 0 <= Kd,Ka,Ks,Krefl <= 1

	public Material(RGB rgb, float Kd, float Ka, float Ks, float Krefl, float KreflIndex) {
		this.rgb = rgb;
		this.Kd = Kd;
		this.Ka = Ka;
		this.Ks = Ks;
		this.KreflIndex = KreflIndex;
		this.Krefl = Krefl;
	}

	@Override
	public String toString() {
		return "Material{" +
				"rgb=" + rgb +
				", Kd=" + Kd +
				", Ka=" + Ka +
				", Ks=" + Ks +
				", KreflIndex=" + KreflIndex +
				", Krefl=" + Krefl +
				'}';
	}
}