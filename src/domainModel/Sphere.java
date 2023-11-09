package domainModel;

public class Sphere implements Object3D {
	public Vector3D v; // center of sphere object: x, y, z
	private Material material;
	private float radius;

	public Sphere(Vector3D v, Material material, float radius) {
		this.v = v;
		this.material = material;
		this.radius = radius;
	}

	public Vector3D getNormal(Vector3D hitPos) {
		//PVector n = PVector.sub(hitPos, pos);
		Vector3D n = Vector3D.sub(v, hitPos);
		//n.div(radius);
		n.normalize();
		return n;
	}
	
	public void setMaterial(Material m) {
		this.material = m;
	}
	
	public Material getMaterial() {
		return material;
	}

	// This function checks if a ray intersects with a 3D object represented by its faces
	public Hit intersectsRay(Ray ray) {
		// Vector from the ray's starting point to the sphere's center
		Vector3D dst = Vector3D.sub(ray.b, v);
		// Coefficients of the quadratic equation
		float B = dst.dot(ray.d);
		float C = dst.dot(dst) - radius * radius; // For a sphere with radius 1

		// Discriminant of the quadratic equation
		float D = B * B - C;
		// Check for intersection
		if (D > 0) {
			// Calculate the two values of t
			float t1 = (float)(-B - Math.sqrt(D));
			float t2 = (float)(-B + Math.sqrt(D));

			if (t1 > 0 || t2 > 0) {
				// Choose the closest (smallest) positive t
				float t = (t1 > 0 && t2 > 0) ? Math.min(t1, t2) : Math.max(t1, t2);

				// Calculate the intersection point
				Vector3D hitPos = Vector3D.add(ray.b, Vector3D.mult(ray.d, t));

				// Calculate the normal at the intersection point
				Vector3D n = getNormal(hitPos);
				return new Hit(this, t, hitPos, n);
			}
		}
		// No valid intersection found
		return null;
	}

	@Override
	public String toString() {
		return "Sphere{" +
				"v=" + v +
				", material=" + material +
				'}';
	}

	public String getType() {
		return "Sphere";
	}

}