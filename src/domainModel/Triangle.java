package domainModel;

public class Triangle implements Object3D {
	public Vector3D[] vertices = new Vector3D[3];
	private Vector3D u, v; // lines that make up plane
	public Vector3D n; // normal to plane
	private int currVertex = 0;
	private Material material;
	private boolean full = false;


	public Triangle(Material material) {
		this.material = material;
	}

	private void calulatePlane() {
		u = Vector3D.sub(vertices[1], vertices[0]);
		v = Vector3D.sub(vertices[2], vertices[0]);
		n = u.cross(v);
		n.normalize();
	}

	public void addNextVertex(Vector3D v) {
		if (!full) {
			vertices[currVertex] = v;
			if (currVertex<2) currVertex++;
			else {
				currVertex = 0;
				full = true;
				calulatePlane();
			}
		}
		else System.out.println("WARNING: Attempting to add a vertex to a Triangle which already has three vertices!");
	}

	public Vector3D getNextVertex() {
		// should only be called once array is populated or it will return null
		if (full) {
			Vector3D v = vertices[currVertex];
			currVertex = (currVertex<2) ? currVertex+1 : 0;
			return v;
		}
		System.out.println("WARNING: Attempting to get a vertex from a Triangle with less than three vertices!");
		return null;
	}
	
	public void setMaterial(Material m) {
		this.material = m;
	}
	
	public Material getMaterial() {
		return material;
	}

	// This function checks if a ray intersects with a triangle and returns a Hit object if an intersection occurs
	public Hit intersectsRay(Ray ray) {
		Vector3D v0 = vertices[0];
		Vector3D v1 = vertices[1];
		Vector3D v2 = vertices[2];

		// Calculate the triangle's normal
		Vector3D e1 = Vector3D.sub(v1, v0);
		Vector3D e2 = Vector3D.sub(v2, v0);
		Vector3D normal = e1.cross(e2);  // Compute the normal vector of the triangle
		normal.normalize();  // Normalize the normal vector

		Vector3D w0 = Vector3D.sub(ray.b, v0);
		float a = -normal.dot(w0);
		float b = normal.dot(ray.d);

		// Check if the ray is parallel to the triangle's plane
		if (Math.abs(b) < 0.0000001 && a != 0) {
			return null;  // Escape case: ray is parallel to the plane, no intersection
		}

		// Find the intersection point of the ray with the triangle's plane
		float t = a / b;

		if (t < 0) {
			return null;  // Escape case: ray is traveling away from the triangle
		}

		Vector3D hitPos = Vector3D.add(ray.b, Vector3D.mult(ray.d, t));  // Calculate the intersection point

		// Perform a 3D point-in-polygon test to check if the intersection point is inside the triangle
		Vector3D u = Vector3D.sub(v1, v0);
		Vector3D v = Vector3D.sub(v2, v0);
		float uu = u.dot(u);
		float uv = u.dot(v);
		float vv = v.dot(v);
		Vector3D w = Vector3D.sub(hitPos, v0);
		float wu = w.dot(u);
		float wv = w.dot(v);
		float d = uv * uv - uu * vv;
		float s = (uv * wv - vv * wu) / d;

		// Check if the intersection point is outside the triangle
		if (s > 1.000001f || s < 0) {
			return null;
		}

		float q = (uv * wu - uu * wv) / d;

		if ((s + q) > 1.000001f || q < 0) {
			return null;  // Escape case: point of intersection is outside the triangle
		}

		// Return a Hit object with information about the intersection
		return new Hit(this, t, hitPos, normal.get());
	}



	
	public String getType() {
		return "Triangle";
	}
}