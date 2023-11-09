package domainModel;

public class Camera {
    Vector3D v;
    float distance, fov;

    public Camera(Vector3D v, float distance, float fov) {
        this.v = v;
        this.distance = distance;
        this.fov = fov;
    }

    public float getFov() {
        return fov;
    }

    public void setFov(float fov) {
        this.fov = fov;
    }

    @Override
    public String toString() {
        return "Camera{" +
                "V=" + v +
                ", distance=" + distance +
                ", fov=" + fov +
                '}';
    }
}
