package domainModel;

import java.util.ArrayList;
import java.util.List;

public class Box implements Object3D {
	public Vector3D v; // center of sphere object: x, y, z
	private Material material;
	private Vector3D[][] faces;

	public Box(Vector3D v, Material material,Vector3D[][] faces) {
		this.v = v;
		this.material = material;
		this.faces = faces;
	}

	public Vector3D getNormal(Vector3D hitPos) {
		Vector3D n = Vector3D.sub(v, hitPos);
		n.normalize();
		return n;
	}

	public void setMaterial(Material m) {
		this.material = m;
	}

	public Material getMaterial() {
		return material;
	}
	private Vector3D calculateMinCorner(List<Vector3D> corners) {
		// Calculate the minimum corner of the box
		float minX = Float.POSITIVE_INFINITY;
		float minY = Float.POSITIVE_INFINITY;
		float minZ = Float.POSITIVE_INFINITY;

		for (Vector3D corner : corners) {
			minX = Math.min(minX, corner.x);
			minY = Math.min(minY, corner.y);
			minZ = Math.min(minZ, corner.z);
		}

		return new Vector3D(minX, minY, minZ);
	}

	private Vector3D calculateMaxCorner(List<Vector3D> corners) {
		// Calculate the maximum corner of the box
		float maxX = Float.NEGATIVE_INFINITY;
		float maxY = Float.NEGATIVE_INFINITY;
		float maxZ = Float.NEGATIVE_INFINITY;

		for (Vector3D corner : corners) {
			maxX = Math.max(maxX, corner.x);
			maxY = Math.max(maxY, corner.y);
			maxZ = Math.max(maxZ, corner.z);
		}

		return new Vector3D(maxX, maxY, maxZ);
	}


	// This function checks if a ray intersects with a 3D object represented by its faces
	// It returns the closest Hit object if an intersection occurs.
	public Hit intersectsRay(Ray ray) {
		// Initialize the closest hit to null
		Hit closestHit = null;

		Vector3D rayDirection = ray.d.normalize2(); // Precompute and normalize the ray direction.

		// Iterate over each face of the object (box or other shape)
		for (Vector3D[] face : faces) {
			// Calculate the normal vector of the face (assuming it's a plane)
			Vector3D e1 = Vector3D.sub(face[1], face[0]);
			Vector3D e2 = Vector3D.sub(face[2], face[0]);
			Vector3D normal = e1.cross(e2).normalize2();

			// Calculate the distance from the ray origin to the plane of the face
			float d = Vector3D.dot(normal, face[0]);
			float t = (d - Vector3D.dot(normal, ray.b)) / Vector3D.dot(normal, rayDirection);

			// Check if the intersection point is in front of the ray origin and not too close
			if (t > 0 && (closestHit == null || t < closestHit.t)) {
				// Calculate the intersection point
				Vector3D pos = Vector3D.add(ray.b, rayDirection.scale(t));

				// Check if the intersection point is inside the face polygon
				if (isInsideFace(face, pos)) {
					// Update the closest hit
					closestHit = new Hit(this, t, pos, normal);
				}
			}
		}
		return closestHit;
	}

	// Helper function to check if a point is inside a 2D polygon defined by its vertices.
	private boolean isInsideFace(Vector3D[] face, Vector3D point) {
		int numVertices = face.length;
		boolean isInside = false;

		for (int i = 0, j = numVertices - 1; i < numVertices; j = i++) {
			Vector3D vertexI = face[i];
			Vector3D vertexJ = face[j];

			if ((vertexI.y > point.y) != (vertexJ.y > point.y) &&
					point.x < (vertexJ.x - vertexI.x) * (point.y - vertexI.y) / (vertexJ.y - vertexI.y) + vertexI.x) {
				isInside = !isInside;
			}
		}

		return isInside;
	}


	@Override
	public String toString() {
		return "Box{" +
				"V=" + v +
				", material=" + material +
				'}';
	}

	public String getType() {
		return "Box";
	}

}