package domainModel;

import java.util.ArrayList;
import java.util.Observable;

public class MaterialsManager extends Observable {
	public ArrayList<Material> materials;

	public MaterialsManager(ArrayList<Material> materials) {
		this.materials = materials;
	}

	public Material addMaterial(float[] rgb, float Kd, float Ka, float Ks, float p, float Krefl, boolean isDefault) {
		Material m = new Material(new RGB(rgb[0], rgb[1], rgb[2]), Kd, Ka, Ks, p, Krefl);
		materials.add(m);
		setChanged();
		notifyObservers("Collection");
		return m;
	}

	public void clearMaterial() {
		materials.clear();
	}

	public Material addMaterial(Material m) {
		materials.add(m);
		setChanged();
		notifyObservers("Collection");
		return m;
	}

	public Material getMaterial(int index) {
		return materials.get(index);
	}
	
	public Object[] getMaterialsAsArray() {
		return materials.toArray();
	}
	
	public Material removeMaterial(int index) {
		Material m = materials.get(index);
		materials.remove(index);
		setChanged();
		notifyObservers();
		return m;
	}

	public void editMaterial(Material material, RGB rgb, float kd, float ka, float ks, float kreflIndex, float krefl) {
		material.rgb = rgb;
		material.Kd = kd;
		material.Ka = ka;
		material.Ks= ks;
		material.KreflIndex = kreflIndex;
		material.Krefl = krefl;
		setChanged();
		notifyObservers();
	}
}
