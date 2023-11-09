package domainModel;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class RayTracer extends Observable implements Observer {
    public int Hres, Vres, fov;
    public int maxDepth; // maximum number of recursive reflection rays
    public float[] pixels;
    public RGB bgColor;
    public ArrayList<Object3D> object3DS;
    public ShapesManager shapesManager;
    public ArrayList<Light> lights;
    public LightsManager lightsManager;
    public CameraManager cameraManager;
    public Camera camera;
    public ArrayList<Material> materials;
    public MaterialsManager materialsManager;
    public ArrayList<Transformation> transformations;
    public TransformationManager transformationManager;
    public Ray[] camRays;

    // Clear all scene components
    public void clearScene() {
        transformationManager.clearTransformations();
        materialsManager.clearMaterial();
        cameraManager.clearCamera();
        lightsManager.clearLights();
        shapesManager.clearShapes();
    }

    public RayTracer(int Hres, int Vres, int fov, int maxDepth, float bgR, float bgG, float bgB, Camera camera) {
        this.Hres = Hres;
        this.Vres = Vres;
        this.camera = camera;
        this.maxDepth = maxDepth;
        this.bgColor = new RGB(bgR, bgG, bgB);
        // Create collections
        object3DS = new ArrayList<>();
        shapesManager = new ShapesManager(object3DS);
        shapesManager.addObserver(this);

        cameraManager = new CameraManager(camera);
        cameraManager.addObserver(this);

        lights = new ArrayList<>();
        lightsManager = new LightsManager(lights);
        lightsManager.addObserver(this);

        materials = new ArrayList<>();
        materialsManager = new MaterialsManager(materials);
        materialsManager.addObserver(this);

        transformations = new ArrayList<>();
        transformationManager = new TransformationManager(transformations);
        transformationManager.addObserver(this);
        alterDimensions(Hres, Vres, fov);
    }

    // This function updates the dimensions and properties of the rendering setup, such as resolution and field of view.
    public void alterDimensions(int Hres, int Vres, int fovT) {
        this.Hres = Hres;  // Update horizontal resolution
        this.Vres = Hres;  // Update vertical resolution (note: check if this should be Vres = Vres instead)

        this.fov = fovT;  // Update the field of view

        camera.setFov(fovT);  // Update the camera's field of view
        double fov = camera.fov * Math.PI / 180.0;  // Convert the field of view to radians

        // Calculate the height of the image plane
        double height = 2.0 * camera.distance * Math.tan(fov / 2.0);

        // Calculate the width of the image plane
        double width = height * Hres / Vres;

        // Calculate the dimensions of each pixel (assuming square pixels)
        float s = (float) (height / Vres);

        // Define the origin for all primary rays
        Vector3D origin = new Vector3D(0.0f, 0.0f, camera.distance);

        // Initialize arrays to store pixel colors and camera rays
        this.pixels = new float[Hres * Vres * 3]; // 96-bit pixels
        this.camRays = new Ray[Hres * Vres];

        for (int j = 0; j < Vres; j++) {
            for (int i = 0; i < Hres; i++) {
                // Calculate the coordinates of the center of the pixel (i, j)
                float pixelX = (float) ((i + 0.5) * s - width / 2.0);
                float pixelY = (float) (height / 2.0 - (j + 0.5) * s);

                // Calculate the direction vector for the primary ray
                Vector3D direction = new Vector3D(pixelX, pixelY, -camera.distance); // direction = (P.x, P.y, -distance)
                direction.normalize();

                // Create the ray with the calculated direction
                Ray ray = new Ray(origin, direction);

                camRays[i + j * Hres] = ray;  // Store the ray in the array
            }
        }

        setChanged();  // Notify observers that dimensions have been updated
        notifyObservers("Dimensions");

        // Start the ray tracing process to render the scene with the new dimensions
        rayTraceScene();
    }


    public void setBackground(float r, float g, float b) {
        bgColor.r = r;
        bgColor.g = g;
        bgColor.b = b;
        rayTraceScene();
    }

    public void setBackgroundRGB(RGB rgb) {
        bgColor.r = rgb.r;
        bgColor.g = rgb.g;
        bgColor.b = rgb.b;
        rayTraceScene();
    }

    public void update(Observable o, Object arg) {
        if (arg != "Collection") rayTraceScene();
    }

    /*
     **************************** Ray Tracing functions ********************************
     */

    // This function performs ray tracing to render the scene and store the pixel colors in the "pixels" array.
    public void rayTraceScene() {
        int pixelNum = 0;  // Initialize a counter for pixel positions
        RGB color;  // Initialize a variable to store the computed color

        // Iterate over camera rays (ray casting through pixels)
        for (Ray ray : camRays) {
            pixelNum += 3;  // Increment the pixel counter by 3 for RGB channels
            color = traceRay(ray, maxDepth);  // Trace the ray through the scene and compute the color

            // Ensure the color values are within the valid range
            // color.checkRange();

            // Store the RGB color values as integers in the "pixels" array
            pixels[pixelNum - 3] = (int) (color.r);  // Red channel
            pixels[pixelNum - 2] = (int) (color.g);  // Green channel
            pixels[pixelNum - 1] = (int) (color.b);  // Blue channel
        }

        setChanged();  // Notify observers that rendering is complete
        notifyObservers("Render");
    }


    // This function traces a ray through the scene and computes the color of the intersection point.
    // It recursively handles reflection and refraction.
    RGB traceRay(Ray ray, int rec) {
        Hit hit = null;  // Initialize hit to null
        float tmin = Float.POSITIVE_INFINITY;  // Initialize tmin to a very high value (positive infinity)

        // Iterate through all objects in the scene to find the closest intersection
        for (Object3D object : shapesManager.object3DS) {
            Hit objectHit = object.intersectsRay(ray);
            if (objectHit != null && objectHit.t < tmin) {
                hit = objectHit;  // Update hit if this intersection is closer
                tmin = objectHit.t;
            }
        }

        float cosTheta = 0;  // Initialize the cosine of the angle between the normal and light direction

        if (hit != null) {
            RGB color = new RGB();  // Initialize the final color at the intersection point
            Vector3D epsilonDisplacement;

            // Compute the lighting contribution from all light sources
            for (Light light : lights) {
                // Diffuse reflection and shading
                color = color.add(light.rgb.multiply(hit.object3DHit.getMaterial().rgb).mult(hit.object3DHit.getMaterial().Kd));

                Vector3D l = light.v.sub(hit.pos);
                float tLight = l.length();
                l.normalize();
                cosTheta = hit.n.dot(l);

                if (cosTheta > 0.0) {
                    // Cast a shadow ray to check for shadows
                    Vector3D shadowRayOrigin = hit.pos.add(l.mult(0.001f));
                    Ray shadowRay = new Ray(shadowRayOrigin, l);
                    Hit hitS = null;

                    for (Object3D object : shapesManager.object3DS) {
                        hitS = object.intersectsRay(shadowRay);
                        if (hitS != null && tLight > 0.001) {
                            break;
                        }
                    }

                    if (hitS == null) {
                        // No shadow, add the diffuse reflection to the color
                        color = color.add(light.rgb.multiply(hit.object3DHit.getMaterial().rgb).mult(hit.object3DHit.getMaterial().Kd * cosTheta));
                    }
                }
            }

            float cosThetaV = -(ray.d.dot(hit.n));

            if (rec > 0) {
                if (hit.object3DHit.getMaterial().Ks > 0.0) {
                    // Compute specular reflection using the Schlick approximation
                    Vector3D r = ray.d.add(hit.n.mult(2.0f * cosThetaV));
                    r.normalize();
                    epsilonDisplacement = hit.n.mult(0.001f);
                    Ray reflectedRay = new Ray(hit.pos.add(epsilonDisplacement), r);
                    RGB specularReflection = traceRay(reflectedRay, rec - 1);

                    float R0 = hit.object3DHit.getMaterial().Ks;
                    float R = (float) (R0 + (1.0f - R0) * Math.pow(1.0f - cosTheta, 5));
                    color = color.add(hit.object3DHit.getMaterial().rgb
                            .mult(R)
                            .multiply(specularReflection));
                }

                if(hit.object3DHit.getMaterial().Krefl > 0){
                    // Refraction - Compute the refracted ray and its color
                    float eta = 1 / hit.object3DHit.getMaterial().Krefl;
                    float cosThetaR = (float) Math.sqrt(1.0 - eta * eta * (1.0 - cosThetaV * cosThetaV));

                    if(cosThetaV < 0){
                        eta = hit.object3DHit.getMaterial().Krefl;
                        cosThetaR = - cosThetaR;
                    }

                    Vector3D rRefr = ray.d.mult(eta).add(hit.n.mult(eta * cosThetaV - cosThetaR));
                    rRefr.normalize();

                    epsilonDisplacement = hit.n.mult(-0.001f);
                    Ray refractedRay = new Ray(hit.pos.add(rRefr.mult(epsilonDisplacement)), rRefr);

                    RGB refractionColor  = traceRay(refractedRay, rec - 1);
                    color = color.add(hit.object3DHit.getMaterial().rgb.mult(hit.object3DHit.getMaterial().Krefl).multiply(refractionColor));
                }
            }

            return color.divide(lights.size());  // Return the final color divided by the number of lights
        } else {
            return bgColor;  // No intersection, return the background color
        }
    }


/*	public void rayTraceScene() {
		int ray_depth = 0;
		int pixelNum = 0;
		RGB color;
		for (Ray ray : camRays) {
			pixelNum += 3;
			Hit h = rayIntersectScene(ray);
			color = h != null ? h.object3DHit.getMaterial().rgb : bgColor.get()  ;

			// Limitação das componentes primárias (R, G e B) das cores obtidas
			//color.checkRange();
			// Conversão das componentes primárias (R, G e B) das cores obtidas e coloração dos píxeis
			pixels[pixelNum - 3] = (int) (color.r);
			pixels[pixelNum - 2] = (int) (color.g);
			pixels[pixelNum - 1] = (int) (color.b);
		}
		setChanged();
		notifyObservers("Render");
	}

	private Hit rayIntersectScene(Ray ray) {
		// returns nearest hit object
		Hit hit = null;
		if (!object3DS.isEmpty()) hit = object3DS.get(0).intersectsRay(ray);
		// brute force all shapes in the scene
		for (int i=1; i<object3DS.size(); i++) {
			Object3D s = object3DS.get(i);
			Hit nextHit = s.intersectsRay(ray);
			// check if the hit is closer than the last hit
			if (hit == null && nextHit != null) hit = nextHit;
			else if (hit != null && nextHit != null) {
				// compare which one is closer
				hit = (nextHit.t < hit.t) ? nextHit : hit;
			}
		}
		return hit;
	}*/

    public void setMaxRecursionDepth(int maxRecursionDepth) {
        this.maxDepth = maxRecursionDepth;
        rayTraceScene(); // Re-trigger ray tracing with the new depth
    }
}