package domainModel;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.RealVector;

public class Vector3D {
	public float x, y, z;
	protected float[] array;

	public Vector3D() {
		x = y = z = 0;
	}

	public Vector3D(float x, float y, float z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vector3D get() {
		return new Vector3D(x, y, z);
	}

	public float mag() {
		return (float) Math.sqrt(x*x + y*y + z*z);
	}

	public float length() {
		return (float) Math.sqrt(x * x + y * y + z * z);
	}

	public Vector3D add(Vector3D v) {
		x += v.x;
		y += v.y;
		z += v.z;

		return new Vector3D(x, y, z);
	}

	static public Vector3D add(Vector3D v1, Vector3D v2) {
		float newX = v1.x + v2.x;
		float newY = v1.y + v2.y;
		float newZ = v1.z + v2.z;
		return new Vector3D(newX, newY, newZ);
	}

	public Vector3D sub(Vector3D v) {
		return new Vector3D(x - v.x, y - v.y, z - v.z);
	}

	static public Vector3D sub(Vector3D v1, Vector3D v2) {
		float newX = v1.x - v2.x;
		float newY = v1.y - v2.y;
		float newZ = v1.z - v2.z;
		return new Vector3D(newX, newY, newZ);
	}

	public Vector3D mult(float n) {
		x *= n;
		y *= n;
		z *= n;

		return new Vector3D(x, y, z);
	}

	static public Vector3D mult(Vector3D v, float n) {
		float newX = v.x * n;
		float newY = v.y * n;
		float newZ = v.z * n;
		return new Vector3D(newX, newY, newZ);
	}

	public Vector3D mult(Vector3D v) {
		x *= v.x;
		y *= v.y;
		z *= v.z;
		return new Vector3D(x, y, z);
	}

	static public Vector3D mult(Vector3D v1, Vector3D v2) {
		float newX = v1.x * v2.x;
		float newY = v1.y * v2.y;
		float newZ = v1.z * v2.z;
		return new Vector3D(newX, newY, newZ);
	}

	public void div(float n) {
		x /= n;
		y /= n;
		z /= n;
	}

	static public Vector3D div(Vector3D v, float n) {
		float newX = v.x / n;
		float newY = v.y / n;
		float newZ = v.z / n;
		return new Vector3D(newX, newY, newZ);
	}

	public void div(Vector3D v) {
		x /= v.x;
		y /= v.y;
		z /= v.z;
	}

	static public Vector3D div(Vector3D v1, Vector3D v2) {
		float newX = v1.x / v2.x;
		float newY = v1.y / v2.y;
		float newZ = v1.z / v2.z;
		return new Vector3D(newX, newY, newZ);
	}

	static public Vector3D oppSign(Vector3D v) {
		return new Vector3D(-v.x, -v.y, -v.z);
	}

	public float dist(Vector3D v) {
		float newX = x - v.x;
		float newY = y - v.y;
		float newZ = z - v.z;
		return (float) Math.sqrt(newX*newX + newY*newY + newZ*newZ);
	}

	static public float dist(Vector3D v1, Vector3D v2) {
		float newX = v1.x - v2.x;
		float newY = v1.y - v2.y;
		float newZ = v1.z - v2.z;
		return (float) Math.sqrt(newX*newX + newY*newY + newZ*newZ);
	}

	public float dot(Vector3D v) {
		return x*v.x + y*v.y + z*v.z;
	}

	static public float dot(Vector3D v1, Vector3D v2) {
		return v1.x*v2.x + v1.y*v2.y + v1.z*v2.z;
	}

	public Vector3D cross(Vector3D v) {
		float newX = y * v.z - v.y * z;
		float newY = z * v.x - v.z * x;
		float newZ = x * v.y - v.x * y;
		return new Vector3D(newX, newY, newZ);
	}

	public void normalize() {
		float m = mag();
		if (m != 0 && m != 1) {
			div(m);
		}
	}

	public Vector3D normalize2() {
		float m = mag();
		if (m != 0 && m != 1) {
			div(m);
		}
		return new Vector3D(x,y,z);
	}

	public float get(int i) {
		if (i == 0) {
			return x;
		} else if (i == 1) {
			return y;
		} else if (i == 2) {
			return z;
		} else {
			throw new IllegalArgumentException("Index i must be 0, 1, or 2 for a 3D vector.");
		}
	}

	public void limit(float max) {
		if (mag() > max) {
			normalize();
			mult(max);
		}
	}

	public Vector3D applyTransformation(Transformation t) {
		translate(t.getT().x, t.getT().y, t.getT().z);
		rotateX(t.getRx());
		rotateY(t.getRy());
		rotateZ(t.getRz());
		scale(t.getS().x, t.getS().y, t.getS().z);
		return new Vector3D(x, y, z);
	}

	public void translate(float tx, float ty, float tz) {
		x += tx;
		y += ty;
		z += tz;
	}

	public void rotateX(float angleDegrees) {
		double radianAngle = Math.toRadians(angleDegrees);
		double tempY = y * Math.cos(radianAngle) - z * Math.sin(radianAngle);
		double tempZ = y * Math.sin(radianAngle) + z * Math.cos(radianAngle);
		y = (float) tempY;
		z = (float) tempZ;
	}

	public void rotateY(float angleDegrees) {
		double radianAngle = Math.toRadians(angleDegrees);
		double tempX = x * Math.cos(radianAngle) + z * Math.sin(radianAngle);
		double tempZ = -x * Math.sin(radianAngle) + z * Math.cos(radianAngle);
		x = (float) tempX;
		z = (float) tempZ;
	}

	public void rotateZ(float angleDegrees) {
		double radianAngle = Math.toRadians(angleDegrees);
		double tempX = x * Math.cos(radianAngle) - y * Math.sin(radianAngle);
		double tempY = x * Math.sin(radianAngle) + y * Math.cos(radianAngle);
		x = (float) tempX;
		y = (float) tempY;
	}

	public void scale(float sx, float sy, float sz) {
		x = (sx != 0) ? x * sx : x;
		y = (sy != 0) ? y * sy : y;
		z = (sz != 0) ? z * sz : z;
	}

	public Vector3D scale(float sx) {
		x = (sx != 0) ? x * sx : x;
		y = (sx != 0) ? y * sx : y;
		z = (sx != 0) ? z * sx : z;
		return new Vector3D(x,y,z);
	}



	public String toString() {
		return "<"+x+", "+y+", "+z+">";
	}

	public float[] toArray() {
		if (array == null) {
			array = new float[3];
		}
		array[0] = x;
		array[1] = y;
		array[2] = z;
		return array;
	}

	public Vector3D applyTransformationBoxCenter(Transformation t) {
		translate(t.getT().x, t.getT().y, t.getT().z);
		rotateX(t.getRx());
		rotateY(t.getRy());
		rotateZ(t.getRz());
		return new Vector3D(x, y, z);
	}

	public List<Vector3D> calculateBoxCorners(Transformation objectTransformation, Transformation cameraTransformation) {
		// Define the half-length of the box's sides (since the side length is 1 before scaling)
		float halfSideLength = 0.5f;

		// Define the relative coordinates of the box's corners
		Vector3D[] relativeCorners = {
				new Vector3D(-halfSideLength, -halfSideLength, -halfSideLength),
				new Vector3D(halfSideLength, -halfSideLength, -halfSideLength),
				new Vector3D(-halfSideLength, halfSideLength, -halfSideLength),
				new Vector3D(halfSideLength, halfSideLength, -halfSideLength),
				new Vector3D(-halfSideLength, -halfSideLength, halfSideLength),
				new Vector3D(halfSideLength, -halfSideLength, halfSideLength),
				new Vector3D(-halfSideLength, halfSideLength, halfSideLength),
				new Vector3D(halfSideLength, halfSideLength, halfSideLength)
		};

		// Apply the combined transformation to the relative coordinates to get the corners
		List<Vector3D> corners = new ArrayList<>();
		for (Vector3D relativeCorner : relativeCorners) {
			Vector3D transformedCorner = relativeCorner.applyTransformation(objectTransformation, cameraTransformation);
			corners.add(transformedCorner);
		}

		return corners;
	}

	// This function applies a sequence of transformations to a 3D point, updating its coordinates
	// The transformations include translation, rotation, and scaling for both the object and camera
	// It returns the transformed 3D point after applying all the transformations
	public Vector3D applyTransformation(Transformation objectTransformation, Transformation cameraTransformation) {
		// Create transformation matrices for the object's translation, rotation, and scaling
		RealMatrix translationMatrixObject = createTranslationMatrix(objectTransformation.getT());
		RealMatrix rotationMatrixObject = createRotationMatrix(objectTransformation.getRx(), objectTransformation.getRy(), objectTransformation.getRz());
		RealMatrix scalingMatrixObject = createScalingMatrix(objectTransformation.getS());

		// Create transformation matrices for the camera's translation, rotation, and scaling
		RealMatrix translationMatrixCamera = createTranslationMatrix(cameraTransformation.getT());
		RealMatrix rotationMatrixCamera = createRotationMatrix(cameraTransformation.getRx(), cameraTransformation.getRy(), cameraTransformation.getRz());
		RealMatrix scalingMatrixCamera = createScalingMatrix(cameraTransformation.getS());

		// Combine transformation matrices for the object and camera
		RealMatrix transformationMatrixObject = translationMatrixObject.multiply(rotationMatrixObject).multiply(scalingMatrixObject);
		RealMatrix transformationMatrixCamera = translationMatrixCamera.multiply(rotationMatrixCamera).multiply(scalingMatrixCamera);
		RealMatrix combinedMatrix = transformationMatrixCamera.multiply(transformationMatrixObject);

		// Create a vector representing the object's position
		RealVector objectPosition = new ArrayRealVector(new double[]{x, y, z, 1});

		// Apply the combined transformation to the object's position
		RealVector transformedPosition = combinedMatrix.operate(objectPosition);
		x = (float) transformedPosition.getEntry(0);
		y = (float) transformedPosition.getEntry(1);
		z = (float) transformedPosition.getEntry(2);

		// Return the transformed 3D point
		return new Vector3D(x, y, z);
	}


	// This helper function creates a translation matrix based on a 3D vector representing translation values
	// It sets the translation components in the last column of the matrix and returns the resulting translation matrix
	private RealMatrix createTranslationMatrix(Vector3D translation) {
		RealMatrix matrix = MatrixUtils.createRealIdentityMatrix(4);

		// Set the translation components in the last column of the matrix
		matrix.setEntry(0, 3, translation.getX());
		matrix.setEntry(1, 3, translation.getY());
		matrix.setEntry(2, 3, translation.getZ());

		return matrix;
	}


	// This helper function creates a rotation matrix by composing individual rotation matrices for X, Y, and Z axes
	// It takes rotation angles in degrees for each axis and returns the combined rotation matrix
	private RealMatrix createRotationMatrix(double rx, double ry, double rz) {
		// Create individual rotation matrices for each axis
		RealMatrix rotationX = MatrixUtils.createRealIdentityMatrix(4);
		RealMatrix rotationY = MatrixUtils.createRealIdentityMatrix(4);
		RealMatrix rotationZ = MatrixUtils.createRealIdentityMatrix(4);

		// Convert rotation angles to radians
		double radianAngleX = Math.toRadians(rx);
		double radianAngleY = Math.toRadians(ry);
		double radianAngleZ = Math.toRadians(rz);

		// Fill the individual rotation matrices for each axis
		rotationX.setEntry(1, 1, Math.cos(radianAngleX));
		rotationX.setEntry(1, 2, -Math.sin(radianAngleX));
		rotationX.setEntry(2, 1, Math.sin(radianAngleX));
		rotationX.setEntry(2, 2, Math.cos(radianAngleX));

		rotationY.setEntry(0, 0, Math.cos(radianAngleY));
		rotationY.setEntry(0, 2, Math.sin(radianAngleY));
		rotationY.setEntry(2, 0, -Math.sin(radianAngleY));
		rotationY.setEntry(2, 2, Math.cos(radianAngleY));

		rotationZ.setEntry(0, 0, Math.cos(radianAngleZ));
		rotationZ.setEntry(0, 1, -Math.sin(radianAngleZ));
		rotationZ.setEntry(1, 0, Math.sin(radianAngleZ));
		rotationZ.setEntry(1, 1, Math.cos(radianAngleZ));

		// Multiply the rotation matrices for X, Y, and Z axes
		return rotationX.multiply(rotationY).multiply(rotationZ);
	}

	// This helper function creates a scaling matrix based on a 3D vector representing scaling factors
	// The matrix will perform non-uniform scaling if any of the scaling factors is non-zero
	private RealMatrix createScalingMatrix(Vector3D scaling) {
		RealMatrix matrix = MatrixUtils.createRealIdentityMatrix(4);

		// Set the diagonal entries of the matrix with scaling factors, ensuring non-zero values or defaults to 1
		matrix.setEntry(0, 0, (scaling.getX() != 0) ? scaling.getX() : 1);
		matrix.setEntry(1, 1, (scaling.getY() != 0) ? scaling.getY() : 1);
		matrix.setEntry(2, 2, (scaling.getZ() != 0) ? scaling.getZ() : 1);

		return matrix;
	}
	public float getX() {
		return x;
	}

	public void setX(float x) {
		this.x = x;
	}

	public float getY() {
		return y;
	}

	public void setY(float y) {
		this.y = y;
	}

	public float getZ() {
		return z;
	}

	public void setZ(float z) {
		this.z = z;
	}
}
