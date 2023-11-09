package domainModel;

public class Transformation {

    Vector3D T, S;
    float Rx, Ry, Rz;

    public Transformation(Vector3D t, Vector3D s, float rx, float ry, float rz) {
        T = t;
        S = s;
        Rx = rx;
        Ry = ry;
        Rz = rz;
    }

    public Transformation multiply(Transformation transformation) {
        this.inverse();
        // Multiply translation components
        Vector3D newTranslation = new Vector3D(T.x + transformation.getT().x, T.y + transformation.getT().y, T.z + transformation.getT().z);

        // Combine scaling components
        Vector3D newScaling = new Vector3D(S.x * transformation.getS().x, S.y * transformation.getS().y, S.z * transformation.getS().z);

        // Combine rotation components (assuming rotation order is Rx, Ry, Rz)
        float newRz = Rz + transformation.getRz();
        float newRy = Ry + transformation.getRy();
        float newRx = Rx + transformation.getRx();

        return new Transformation(newTranslation, newScaling, newRx, newRy, newRz);
    }

    public Transformation inverse() {
        // Inverse of translation
        Vector3D inverseTranslation = new Vector3D(-T.x, -T.y, -T.z);

        // Inverse of scaling
        Vector3D inverseScaling = new Vector3D(1.0f / S.x, 1.0f / S.y, 1.0f / S.z);

        // Inverse of rotation (assuming reverse order Rx, Ry, Rz)
        float inverseRx = -Rx;
        float inverseRy = -Ry;
        float inverseRz = -Rz;

        return new Transformation(inverseTranslation, inverseScaling, inverseRx, inverseRy, inverseRz);
    }

    public Transformation transpose() {
        // Transpose of the rotation matrix (Rx, Ry, Rz)
        // Since the rotation matrix is orthogonal, its transpose is the same
        float transposedRx = Rx;
        float transposedRy = Ry;
        float transposedRz = Rz;

        // Transpose of translation and scaling components
        Vector3D transposedTranslation = T;
        Vector3D transposedScaling = S;

        return new Transformation(transposedTranslation, transposedScaling, transposedRx, transposedRy, transposedRz);
    }

    @Override
    public String toString() {
        return "Transformation{" +
                "T=" + T +
                ", S=" + S +
                ", Rx=" + Rx +
                ", Ry=" + Ry +
                ", Rz=" + Rz +
                '}';
    }

    public Vector3D getT() {
        return T;
    }

    public Vector3D getS() {
        return S;
    }

    public float getRx() {
        return Rx;
    }

    public float getRy() {
        return Ry;
    }

    public float getRz() {
        return Rz;
    }
}
