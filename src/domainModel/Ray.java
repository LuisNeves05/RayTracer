package domainModel;
public class Ray {
	public Vector3D b; // origin: x0, y0, z0
	public Vector3D d = new Vector3D(0,0,0); // dx, dy, dz

	public Ray(Vector3D b, Vector3D d) {
		this.b = b;
		this.d = d;
	}

	public Ray(Vector3D b, float x, float y, float z) {
		this.b = b;
		setDeltaFromPt(x, y, z);
	}

	public void setDeltaFromPt(float x, float y, float z) {
		d.x = x - b.x;
		d.y = y - b.y;
		d.z = z - b.z;
		d.normalize();
	}

	public Vector3D getPointAtParameter(float t) {
		float x = b.x + t * d.x;
		float y = b.y + t * d.y;
		float z = b.z + t * d.z;
		return new Vector3D(x, y, z);
	}


	@Override
	public String toString() {
		return "Ray{" +
				"b=" + b +
				", d=" + d +
				'}';
	}
}