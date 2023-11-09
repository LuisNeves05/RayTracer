package domainModel;
public interface Object3D {
	Hit intersectsRay(Ray ray); // test for intersection with a ray
	Material getMaterial(); // returns the material of the object
	void setMaterial(Material m); // sets the material of the object
	String getType(); // returns a String of the class type
}