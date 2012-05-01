package com.TerraNullius.terrain;

import com.jme3.app.SimpleApplication;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.system.AppSettings;
import java.util.Random;

public class TerrainGenerator extends SimpleApplication {

    int worldZ = 512;
    int worldX = 512;
    int gridSize = 16;
    int streetSize = 4;
    int gridX = worldX / gridSize;
    int gridZ = worldZ / gridSize;
    int buildingGrid[][] = new int[gridX][gridZ];
    double noiseGrid[][] = new double[gridX][gridZ];
    PerlinNoise pn;
    public static AppSettings settings;

    public static void main(String[] args) {
        TerrainGenerator app = new TerrainGenerator();
        app.setShowSettings(false);
        settings = new AppSettings(true);
        settings.put("Width", 1024);
        settings.put("Height", 768);
        settings.put("Title", "TNTerrainGen");
        settings.put("VSync", true);
        settings.put("Samples", 4);
        app.setSettings(settings);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        flyCam.setMoveSpeed(50);
        if (worldX % gridSize != 0 || worldZ % gridSize != 0) {
            throw new IllegalArgumentException();
        }

        initMap();

        Random rand = new Random(9979);

//        for (int x = 0; x < gridX; x++) {
//            for (int z = 0; z < gridZ; z++) {
//                if (rand.nextInt() > .3) {
//                    buildingGrid[x][z] = 2;//building
//                } else {
//                    buildingGrid[x][z] = 1;//park
//                }
//            }
//        }
        pn = new PerlinNoise();
        double noise;
        for (int x = 0; x < gridX; x++) {
            for (int z = 0; z < gridZ; z++) {
                noise = pn.noise(x + 0.5, z + 0.5) + 0.5;
                noiseGrid[x][z] = noise;
                System.out.println(noise);
                if (noise > .4) {
                    buildingGrid[x][z] = 2;//building
                } else {
                    buildingGrid[x][z] = 1;//park
                }
            }
        }

        int gridType = 0;
        for (int x = 0; x < gridX; x++) {
            for (int z = 0; z < gridZ; z++) {
                gridType = buildingGrid[x][z];
                if (gridType == 0) {  //Nothing
                } else if (gridType == 1) {//Park
                    genPark(x, z);
                    if (x + 1 != buildingGrid.length && z + 1 != buildingGrid[0].length) {
                        if (buildingGrid[x + 1][z] == 1) {
                            genPark(x, z, streetSize, 0);
                        }
                        if (buildingGrid[x][z + 1] == 1) {
                            genPark(x, z, 0, streetSize);
                        }
                    }
                } else if (gridType == 2) {//Building
                    genBuilding(x, z);
                }
            }
        }
    }

    public void initMap() {
        //Light
        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(1.3f));
        rootNode.addLight(al);
        DirectionalLight dl = new DirectionalLight();
        dl.setColor(ColorRGBA.White);
        dl.setDirection(new Vector3f(2.8f, -2.8f, -2.8f).normalizeLocal());
        rootNode.addLight(dl);

        //Debug origin line
        Geometry line = new Geometry("Box", new Box(Vector3f.ZERO, 0.1f, 100, 0.1f));
        Material matL = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matL.setColor("Color", ColorRGBA.White);
        line.setMaterial(matL);
        rootNode.attachChild(line);

        //Basic floor
        Geometry floor = new Geometry("Box", new Box(Vector3f.ZERO, worldX / 2, 0.5f, worldZ / 2));
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Gray);
        floor.setMaterial(mat);
        floor.setLocalTranslation(new Vector3f(worldX / 2, -0.5f, worldZ / 2));
        rootNode.attachChild(floor);

        cam.lookAt(floor.getLocalTranslation(), Vector3f.UNIT_Y);
    }

    public void genBuildings() {
        //Determine how many grid spaces we have
        int buildingSize = (gridSize - streetSize);
        Geometry building;
        for (int i = 0; i < gridX; i++) {
            for (int j = 0; j < gridZ; j++) {
                building = new Geometry("Box", new Box(Vector3f.ZERO, buildingSize / 2, 10, buildingSize / 2));
                Material matB = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                matB.setColor("Color", ColorRGBA.randomColor());
                building.setMaterial(matB);
                building.setLocalTranslation(buildingSize / 2 + (gridSize * i), 10, buildingSize / 2 + (gridSize * j));
                rootNode.attachChild(building);
            }
        }
    }

    public void genBuilding(int gridX, int gridZ) {
        double noise = noiseGrid[gridX][gridZ];
        float height = (float)(gridSize * 2 * noise);
        int buildingSize = (gridSize - streetSize);
        Geometry building = new Geometry("Building", new Box(Vector3f.ZERO, buildingSize / 2, height, buildingSize / 2));
        Material matB = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matB.setColor("Color", noiseColor(noise));
        building.setMaterial(matB);
        building.setLocalTranslation(buildingSize / 2 + (gridSize * gridX), height, buildingSize / 2 + (gridSize * gridZ));
        rootNode.attachChild(building);
    }

    public void genPark(int gridX, int gridZ, int offsetX, int offsetZ) {
        int buildingSize = (gridSize - streetSize);
        Geometry park = new Geometry("Park", new Box(Vector3f.ZERO, buildingSize / 2, 0.1f, buildingSize / 2));
        Material matB = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        matB.setColor("Color", ColorRGBA.Green);
        park.setMaterial(matB);
        park.setLocalTranslation(buildingSize / 2 + (gridSize * gridX), 0.1f, buildingSize / 2 + (gridSize * gridZ));
        rootNode.attachChild(park);
    }

    public void genPark(int gridX, int gridZ) {
        genPark(gridX, gridZ, 0, 0);
    }
    
    public ColorRGBA noiseColor(double noise){
        return ColorRGBA.randomColor().mult((float)noise);
    }

    @Override
    public void simpleUpdate(float tpf) {
        //TODO: add update code
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }
}
