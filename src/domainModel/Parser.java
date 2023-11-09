package domainModel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Parser {
    public static Transformation cameraT;
    public static int widthImage, heightImage, fovCamera;
    public static void load(File selectedFile, RayTracer tracer) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(selectedFile));
            String line;
            while ((line = reader.readLine()) != null) {
                switch (line) {
                    case "Image":
                        parseImage(reader, tracer);
                        break;
                    case "Transformation":
                        parseTransformation(reader, tracer);
                        break;
                    case "Material":
                        parseMaterial(reader, tracer);
                        break;
                    case "Camera":
                        parseCamera(reader, tracer);
                        break;
                    case "Light":
                        parseLight(reader, tracer);
                        break;
                    case "Sphere":
                        parseSphere(reader, tracer);
                        break;
                    case "Box":
                        parseBox(reader, tracer);
                        break;
                    case "Triangles":
                        parseTriangle(reader, tracer);
                        break;
                }
            }
            tracer.alterDimensions(widthImage, heightImage, fovCamera);
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void parseTransformation(BufferedReader br, RayTracer tracer) throws IOException {
        Vector3D T = new Vector3D(0, 0, 0), S = new Vector3D(0, 0, 0);
        float rx = 0, ry = 0, rz = 0;

        String line;
        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (line.startsWith("}")) {
                break; // End of Material section
            } else {
                String[] parts = line.trim().split(" ");
                line = line.trim();
                if (parts.length > 1) {
                    if (line.startsWith("T")) {
                        T = new Vector3D(
                                Float.parseFloat(parts[1]),
                                Float.parseFloat(parts[2]),
                                Float.parseFloat(parts[3])
                        );
                    } else if (line.startsWith("S")) {
                        S = new Vector3D(
                                Float.parseFloat(parts[1]),
                                Float.parseFloat(parts[2]),
                                Float.parseFloat(parts[3])
                        );
                    } else if (line.startsWith("Rx")) {
                        rx = Float.parseFloat(parts[1]);
                    } else if (line.startsWith("Ry")) {
                        ry = Float.parseFloat(parts[1]);
                    } else if (line.startsWith("Rz")) {
                        rz = Float.parseFloat(parts[1]);
                    }
                }
            }
        }
        tracer.transformationManager.addTransformation(new Transformation(T, S, rx, ry, rz));
    }

    private static void parseImage(BufferedReader reader, RayTracer tracer) throws IOException {
        List<String> values = new ArrayList<>();
        reader.readLine();
        String line;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.startsWith("}")) {
                int width = Integer.parseInt(values.get(0));
                int height = Integer.parseInt(values.get(1));
                float r = Float.parseFloat(values.get(2));
                float g = Float.parseFloat(values.get(3));
                float b = Float.parseFloat(values.get(4));
                widthImage = width;
                heightImage = height;
                tracer.setBackground(r, g, b);
                break;
            } else {
                String[] parts = line.split(" ");
                for (String val : parts)
                    values.add(val.trim());
            }
        }
    }

    private static void parseCamera(BufferedReader reader, RayTracer tracer) throws IOException {
        List<String> values = new ArrayList<>();
        float distance = 0, fov = 0;
        int transformationIndex = 0;

        reader.readLine();
        String line;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.startsWith("}")) {
                transformationIndex = Integer.parseInt(values.get(0));
                distance = Float.parseFloat(values.get(1));
                fov = Float.parseFloat(values.get(2));

                break;
            } else {
                String[] parts = line.split(" ");
                for (String val : parts)
                    values.add(val.trim());
            }
        }
        Transformation t = tracer.transformationManager.getTransformation(transformationIndex);
        cameraT = t;
        fovCamera = (int) fov;
        tracer.cameraManager.setCamera(new Vector3D(0, 0, distance), distance, fov);
    }

    private static void parseSphere(BufferedReader reader, RayTracer tracer) throws IOException {
        List<String> values = new ArrayList<>();
        int transformationIndex = 0, materialIndex = 0;

        reader.readLine();
        String line;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.startsWith("}")) {
                transformationIndex = Integer.parseInt(values.get(0));
                materialIndex = Integer.parseInt(values.get(1));
                break;
            } else {
                String[] parts = line.split(" ");
                for (String val : parts)
                    values.add(val.trim());
            }
        }
        Transformation t = tracer.transformationManager.getTransformation(transformationIndex);
        Material m = tracer.materialsManager.getMaterial(materialIndex);

        int radius = 1;
        if(t.S.x != 0){
            radius *= t.S.x;
        }
        if(cameraT.S.x != 0){
            radius *= cameraT.S.x;
        }

        var vec = new Vector3D(0, 0, 0).applyTransformation(t,cameraT);
        tracer.shapesManager.addSphere(vec, m, radius);

    }

    private static void parseBox(BufferedReader reader, RayTracer tracer) throws IOException {
        List<String> values = new ArrayList<>();
        int transformationIndex = 0, materialIndex = 0;

        reader.readLine();
        String line;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.startsWith("}")) {
                transformationIndex = Integer.parseInt(values.get(0));
                materialIndex = Integer.parseInt(values.get(1));
                break;
            } else {
                String[] parts = line.split(" ");
                for (String val : parts)
                    values.add(val.trim());
            }
        }
        Transformation t = tracer.transformationManager.getTransformation(transformationIndex);
        Material m = tracer.materialsManager.getMaterial(materialIndex);

        Vector3D center = new Vector3D(0, 0, 0).applyTransformation(t,cameraT);
        List<Vector3D> corners = center.calculateBoxCorners(t,cameraT);

        int[][] faceIndices = {
                {0, 1, 5, 4}, // Front face
                {1, 3, 7, 5}, // Right face
                {3, 2, 6, 7}, // Back face
                {2, 0, 4, 6}, // Left face
                {0, 2, 3, 1}, // Top face
                {4, 5, 7, 6}  // Bottom face
        };

        Vector3D[][] faces = new Vector3D[6][4];
        // Fill in the faces matrix with the corner vertices
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 4; j++) {
                faces[i][j] = corners.get(faceIndices[i][j]);
            }
        }

        tracer.shapesManager.addBox(center, m,faces);

    }

    static void parseLight(BufferedReader br, RayTracer tracer) throws IOException {
        RGB rgb = null;
        int transformationIndex = 0;

        String line;
        br.readLine();
        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (line.startsWith("}")) {
                break; // End of Light section
            } else {
                String[] parts = line.trim().split(" ");
                if (parts.length > 1) {
                    if (parts.length == 3) {
                        rgb = new RGB(
                                Float.parseFloat(parts[0]),
                                Float.parseFloat(parts[1]),
                                Float.parseFloat(parts[2])
                        );
                    }
                }
                if (parts.length == 1){
                    transformationIndex = Integer.parseInt(parts[0]);
                }
            }
        }
        Transformation t = tracer.transformationManager.getTransformation(transformationIndex);
        tracer.lightsManager.addLight(new Vector3D(0, 0, 0).applyTransformation(t,cameraT), rgb);
    }

    static void parseMaterial(BufferedReader br, RayTracer tracer) throws IOException {
        RGB rgb = null;
        float KreflIndex = 0, Krefl = 0, Kd = 0, Ka = 0, Ks = 0;

        String line;
        while ((line = br.readLine()) != null) {
            line = line.trim();
            if (line.startsWith("}")) {
                break; // End of Material section
            } else {
                String[] parts = line.trim().split(" ");
                if (parts.length > 1) {
                    switch (parts.length) {
                        case 3:
                            rgb = new RGB(
                                    Float.parseFloat(parts[0]),
                                    Float.parseFloat(parts[1]),
                                    Float.parseFloat(parts[2])
                            );
                            break;
                        case 5:
                            Ka = Float.parseFloat(parts[0]);
                            Kd = Float.parseFloat(parts[1]);
                            Ks = Float.parseFloat(parts[2]);
                            Krefl = Float.parseFloat(parts[3]);
                            KreflIndex = Float.parseFloat(parts[4]);
                            break;
                    }
                }
            }
        }
        tracer.materialsManager.addMaterial(new Material(rgb, Kd, Ka, Ks, Krefl, KreflIndex));
    }

    private static void parseTriangle(BufferedReader reader, RayTracer tracer) throws IOException {
        String line;
        ArrayList<Float> values = new ArrayList<>();
        ArrayList<Vector3D> vectors = new ArrayList<>();
        reader.readLine();
        Transformation transformation = tracer.transformationManager.getTransformation(Integer.parseInt(reader.readLine().trim()));
        Material material = null;
        while ((line = reader.readLine()) != null) {
            line = line.trim();
            if (line.startsWith("}")) {
                addTriangle(tracer, vectors, material);
                break;
            } else {
                String[] parts = line.split(" ");

                if (parts.length == 1) {

                    vectors = addTriangle(tracer, vectors, material);
                    material = tracer.materialsManager.getMaterial(Integer.parseInt(parts[0].trim()));
                } else if (parts.length > 1) {
                    for (String val : parts)
                        values.add(Float.parseFloat(val.trim()));
                    vectors.add(new Vector3D(values.get(0), values.get(1), values.get(2)).applyTransformation(transformation,cameraT));
                    values = new ArrayList<>();
                }
            }
        }
        tracer.shapesManager.setChangedAndNotify();
    }

    private static ArrayList<Vector3D> addTriangle(RayTracer tracer, ArrayList<Vector3D> vectors, Material material) {
        if (!vectors.isEmpty()) {
            tracer.shapesManager.addTriangle(vectors, material);
        }
        return new ArrayList<>();
    }
}
