package domainModel;

import java.util.ArrayList;
import java.util.Observable;

public class LightsManager extends Observable {
	private ArrayList<Light> lights;
	
	public LightsManager(ArrayList<Light> lights) {
		this.lights = lights;
	}
	
	public Light addLight(Vector3D v, RGB rgb) {
		Light l = new Light(v, rgb);
		lights.add(l);
		setChanged();
		notifyObservers();
		return l;
	}
	
	public Light getLight(int index) {
		return lights.get(index);
	}

	public void clearLights() {
		lights.clear();
	}

	public Light removeLight(int index) {
		Light l = lights.get(index);
		lights.remove(index);
		setChanged();
		notifyObservers();
		return l;
	}

	public Light addLight(Light light ) {
		lights.add(light);
		setChanged();
		notifyObservers();
		return light;
	}
}