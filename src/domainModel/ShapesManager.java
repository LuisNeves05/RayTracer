package domainModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

public class ShapesManager extends Observable {
    public ArrayList<Object3D> object3DS;

    public ShapesManager(ArrayList<Object3D> object3DS) {
        this.object3DS = object3DS;
    }

    public void addShape(Object3D s) {
        this.object3DS.add(s);
        setChanged();
        notifyObservers();
    }

    public void clearShapes() {
        object3DS.clear();
    }

    public void addSphere(Vector3D v, Material material, float radius) {
        Sphere s = new Sphere(v, material, radius);
        object3DS.add(s);
        setChanged();
        notifyObservers();
    }

    public void addBox(Vector3D v, Material m, Vector3D[][] faces) {
        Box b = new Box(v, m, faces);
        object3DS.add(b);
        setChanged();
        notifyObservers();
    }

    public Triangle addTriangle(ArrayList<Vector3D> vectors, Material material) {
        Triangle t = new Triangle(material);

        for (Vector3D vec : vectors) {
            t.addNextVertex(vec);
        }
        object3DS.add(t);
        return t;
    }

    public void setChangedAndNotify() {
        setChanged();
        notifyObservers();
    }

    public void editTriangle(Triangle triangle, float[] vertices, Material material) {
        triangle.setMaterial(material);
        int coord = 0;
        for (int i = 0; i < 3; i++) {
            triangle.vertices[i].x = vertices[coord++];
            triangle.vertices[i].y = vertices[coord++];
            triangle.vertices[i].z = vertices[coord++];
        }
        setChanged();
        notifyObservers();
    }

    public Object3D getShape(int index) {
        return object3DS.get(index);
    }

    public Object3D removeShape(int index) {
        Object3D s = object3DS.get(index);
        object3DS.remove(index);
        setChanged();
        notifyObservers();
        return s;
    }

    public List<Vector3D> bootstrapBox() {
        List<Vector3D> corners = new ArrayList<>(8);

        // Define the 8 corner points based on a box with center (0, 0, 0) and edge at 1
        corners.add(new Vector3D(0.5f, 0.5f, 0.5f));
        corners.add(new Vector3D(0.5f, 0.5f, -0.5f));
        corners.add(new Vector3D(0.5f, -0.5f, 0.5f));
        corners.add(new Vector3D(0.5f, -0.5f, -0.5f));
        corners.add(new Vector3D(-0.5f, 0.5f, 0.5f));
        corners.add(new Vector3D(-0.5f, 0.5f, -0.5f));
        corners.add(new Vector3D(-0.5f, -0.5f, 0.5f));
        corners.add(new Vector3D(-0.5f, -0.5f, -0.5f));

        return corners;
    }
}
