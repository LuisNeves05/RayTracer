package domainModel;

import java.util.ArrayList;
import java.util.Observable;

public class TransformationManager extends Observable {

    private final ArrayList<Transformation> transformations;

    public TransformationManager(ArrayList<Transformation> transformations) {
        this.transformations = transformations;
    }

    public void clearTransformations(){
        transformations.clear();
    }

    public Transformation addTransformation(Transformation transformation ) {
        transformations.add(transformation);
        setChanged();
        notifyObservers();
        return transformation;
    }

    public Transformation getTransformation(int index) {
        return transformations.get(index);
    }

    public ArrayList<Transformation> getTransformations() {
        return transformations;
    }
}
