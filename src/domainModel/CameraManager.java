package domainModel;

import java.util.ArrayList;
import java.util.Observable;

public class CameraManager extends Observable {
	private Camera camera;

	public CameraManager(Camera camera) {
		this.camera = camera;
	}

	public void clearCamera() {
		this.camera = null;
	}

	public Camera setCamera(Vector3D v, float distance, float fov) {
		camera = new Camera(v, distance, fov);
		setChanged();
		notifyObservers("Collection");
		return camera;
	}


	public Camera getCamera(int index) {
		return camera;
	}

}
