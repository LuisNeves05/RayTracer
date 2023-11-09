package domainModel;
public class Hit {
	public float t; // distance along ray (to make comparisons easier)
	public Vector3D pos; // position of hit
	public Object3D object3DHit;
	public Vector3D n; // surface normal
	
	public Hit(Object3D object3DHit, float t, Vector3D pos, Vector3D n) {
		this.t = t;
		this.pos = pos;
		this.object3DHit = object3DHit;
		this.n = n;
	}

	@Override
	public String toString() {
		return "Hit{" +
				"t=" + t +
				", pos=" + pos +
				", shapeHit=" + object3DHit +
				", n=" + n +
				'}';
	}
}