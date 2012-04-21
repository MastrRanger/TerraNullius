/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.TerraNullius.appstates;

import com.TerraNullius.Game;
import com.TerraNullius.controls.PlayerControl;
import com.TerraNullius.controls.ZombieControl;
import com.TerraNullius.entity.WeaponType;
import com.jme3.app.Application;
import com.jme3.app.SimpleApplication;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.asset.plugins.ZipLocator;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.input.InputManager;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.Filter;
import com.jme3.post.FilterPostProcessor;
import com.jme3.post.filters.BloomFilter;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.CameraControl.ControlDirection;
import com.jme3.scene.shape.Box;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.screen.Screen;
import de.lessvoid.nifty.screen.ScreenController;
import java.util.Random;

/**
 *
 * @author Griffin
 */
public class GameState extends AbstractAppState implements ScreenController {

    private SimpleApplication app;
    private Node rootNode;
    private AssetManager assetManager;
    private AppStateManager stateManager;
    private InputManager inputManager;
    private ViewPort viewPort;
    private BulletAppState bulletAppState;
    
    private Nifty nifty;
    private Screen screen;
    
    public Node mobs;
    public Spatial player;
    public CameraNode camNode;

    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = (Game) app; // can cast Application to something more specific
        this.rootNode = this.app.getRootNode();
        this.assetManager = this.app.getAssetManager();
        this.stateManager = this.app.getStateManager();
        this.inputManager = this.app.getInputManager();
        this.viewPort = this.app.getViewPort();
        this.bulletAppState = this.stateManager.getState(BulletAppState.class);

        // init stuff that is independent of whether state is PAUSED or RUNNING
        
        //Light
        AmbientLight al = new AmbientLight();
        al.setColor(ColorRGBA.White.mult(1.3f));
        rootNode.addLight(al);
        DirectionalLight dl = new DirectionalLight();
        dl.setColor(ColorRGBA.White);
        dl.setDirection(new Vector3f(2.8f, -2.8f, -2.8f).normalizeLocal());
        rootNode.addLight(dl);
        
        //Terrain
        assetManager.registerLocator("town.zip", ZipLocator.class.getName());
        Spatial sceneModel = assetManager.loadModel("main.scene");
        sceneModel.setLocalTranslation(0, -1f, 0);
        sceneModel.setLocalScale(1f);
        CollisionShape sceneShape = CollisionShapeFactory.createMeshShape((Node) sceneModel);
        RigidBodyControl landscape = new RigidBodyControl(sceneShape, 0);
        sceneModel.addControl(landscape);
        rootNode.attachChild(sceneModel);
        bulletAppState.getPhysicsSpace().add(landscape);
        
        //Player
        player = assetManager.loadModel("Models/Human/meHumanMale.mesh.xml");
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.White);
        player.setMaterial(mat);
        player.addControl(new PlayerControl());
        CharacterControl physChar = new CharacterControl(new CapsuleCollisionShape(.2f, 0.75f, 1), 0.1f);
        physChar.setJumpSpeed(10);
        physChar.setFallSpeed(20);
        physChar.setGravity(30);
        physChar.setUseViewDirection(true);
        player.addControl(physChar);
        bulletAppState.getPhysicsSpace().add(physChar);
        rootNode.attachChild(player);

        //Camera      
        camNode = new CameraNode("Camera Node", app.getCamera());
        camNode.setControlDir(ControlDirection.SpatialToCamera);
        camNode.setLocalTranslation(player.getWorldTranslation().add(new Vector3f(-14, 14, -14)));
        camNode.lookAt(player.getWorldTranslation(), Vector3f.UNIT_Y);
        rootNode.attachChild(camNode);
        
        //Filters
        FilterPostProcessor fpp = new FilterPostProcessor(assetManager);
        Filter bloom = new BloomFilter(BloomFilter.GlowMode.Objects);        
        fpp.addFilter(bloom);
        viewPort.addProcessor(fpp);
        
        //Mobs
        mobs = new Node("Mobs");
        rootNode.attachChild(mobs);

        createZombies(20, 30);

        //Collectables
        createTestWeaps();
    }
    
    //Creates new zombies of number amount a random distance less than maxDist away from the player
    public void createZombies(int amount, int maxDist) {
        maxDist *= 2;   //convert radius to diameter
        Spatial zombie;
        Random rand = new Random();
        Vector3f offset;
        for (int i = 0; i < amount; i++) {
            zombie = assetManager.loadModel("Models/Human/meHumanMale.mesh.xml");
            Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
            mat.setColor("Color", ColorRGBA.Black);
            zombie.setMaterial(mat);
            zombie.addControl(new ZombieControl());

            offset = new Vector3f(rand.nextInt(maxDist) - maxDist / 2, 0, rand.nextInt(maxDist) - maxDist / 2);
            zombie.setLocalTranslation(player.getWorldTranslation().add(offset));

            mobs.attachChild(zombie);
        }
    }

    public void createTestWeaps() {
        Box b = new Box(Vector3f.ZERO, 0.5f, 0.25f, 0.5f);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", ColorRGBA.Pink);
        
        Geometry weap = new Geometry("Weapon", b);
        Geometry weap1 = new Geometry("Weapon", b);
        Geometry weap2 = new Geometry("Weapon", b);
        Geometry weap3 = new Geometry("Weapon", b);
        weap.setMaterial(mat);
        weap1.setMaterial(mat);
        weap2.setMaterial(mat);
        weap3.setMaterial(mat);
        weap.setUserData("Weapon", "Hands");
        weap1.setUserData("Weapon", "Pistol");
        weap2.setUserData("Weapon", "Machinegun");
        weap3.setUserData("Weapon", "Rifle");
        weap.setLocalTranslation(player.getWorldTranslation().add(new Vector3f(5f, 0f, 5f)));
        weap1.setLocalTranslation(player.getWorldTranslation().add(new Vector3f(-5f, 0f, 5f)));
        weap2.setLocalTranslation(player.getWorldTranslation().add(new Vector3f(-5f, 0f, -5f)));
        weap3.setLocalTranslation(player.getWorldTranslation().add(new Vector3f(5f, 0f, -5f)));
        rootNode.attachChild(weap);
        rootNode.attachChild(weap1);
        rootNode.attachChild(weap2);
        rootNode.attachChild(weap3);
    }

    @Override
    public void cleanup() {
        super.cleanup();
        // unregister all my listeners, detach all my nodes, etc...
//      this.app.getRootNode().detachChild(getX()); // modify scene graph...
//      this.app.doSomethingElse();                 // call custom methods...
    }

    @Override
    public void setEnabled(boolean enabled) {
        // Pause and unpause
        super.setEnabled(enabled);
        if (enabled) {
            // init stuff that is in use while this state is RUNNING
//        this.app.getRootNode().attachChild(getX()); // modify scene graph...
//        this.app.doSomethingElse();                 // call custom methods...
        } else {
            // take away everything not needed while this state is PAUSED
        }
    }

    @Override
    public void update(float tpf) {
        if (isEnabled()) {
            // do the following while game is RUNNING
//        this.app.getRootNode().getChild("blah").scale(tpf); // modify scene graph...
//        x.setUserData(...);                                 // call some methods...
        } else {
            // do the following while game is PAUSED, e.g. play an idle animation.    
        }
    }

    public void bind(Nifty nifty, Screen screen) {
        this.nifty = nifty;
        this.screen = screen;
    }

    public void onStartScreen() {
    }

    public void onEndScreen() {
    }
}
