/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.TerraNullius.appstates;

import com.TerraNullius.Game;
import com.TerraNullius.SettingsLoader;
import com.TerraNullius.controls.PlayerControl;
import com.TerraNullius.controls.ZombieControl;
import com.TerraNullius.entity.Entity;
import com.TerraNullius.entity.Mob;
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
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
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

    private Game app;
    private Node rootNode;
    private AssetManager assetManager;
    private AppStateManager stateManager;
    private InputManager inputManager;
    private ViewPort viewPort;
    private BulletAppState bulletAppState;
    private SettingsLoader sl;
    private Nifty nifty;
    private Screen screen;
    public Node mobs;
    public Spatial player;
    public CameraNode camNode;
    private Vector2f cursorPos;

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
        this.nifty = this.app.nifty;

        // init stuff that is independent of whether state is PAUSED or RUNNING

        this.sl = new SettingsLoader();

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
        CharacterControl physChar = new CharacterControl(new CapsuleCollisionShape(.2f, 0.75f, 1), 0.1f);
        physChar.setJumpSpeed(10);
        physChar.setFallSpeed(20);
        physChar.setGravity(30);
        physChar.setUseViewDirection(true);
        player.addControl(physChar);
        bulletAppState.getPhysicsSpace().add(physChar);
        player.addControl(new PlayerControl());
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

        initKeys();
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
            CharacterControl physChar = new CharacterControl(new CapsuleCollisionShape(.2f, 0.75f, 1), 0.1f);
            physChar.setJumpSpeed(10);
            physChar.setFallSpeed(20);
            physChar.setGravity(30);
            physChar.setUseViewDirection(true);
            zombie.addControl(physChar);
            bulletAppState.getPhysicsSpace().add(physChar);
            zombie.addControl(new ZombieControl());

            offset = new Vector3f(rand.nextInt(maxDist) - maxDist / 2, 0, rand.nextInt(maxDist) - maxDist / 2);
            physChar.warp(player.getWorldTranslation().add(offset));

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
        weap.setUserData("Weapon", WeaponType.HANDS);
        weap1.setUserData("Weapon", WeaponType.PISTOL);
        weap2.setUserData("Weapon", WeaponType.MACHINEGUN);
        weap3.setUserData("Weapon", WeaponType.RIFLE);
        weap.setLocalTranslation(player.getWorldTranslation().add(new Vector3f(5f, 0f, 5f)));
        weap1.setLocalTranslation(player.getWorldTranslation().add(new Vector3f(-5f, 0f, 5f)));
        weap2.setLocalTranslation(player.getWorldTranslation().add(new Vector3f(-5f, 0f, -5f)));
        weap3.setLocalTranslation(player.getWorldTranslation().add(new Vector3f(5f, 0f, -5f)));
        rootNode.attachChild(weap);
        rootNode.attachChild(weap1);
        rootNode.attachChild(weap2);
        rootNode.attachChild(weap3);
    }

    private void initKeys() {
        //inputManager.clearMappings();
        //Movement Controls
        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping("Mouse Up", new MouseAxisTrigger(MouseInput.AXIS_Y, true));
        inputManager.addMapping("Mouse Down", new MouseAxisTrigger(MouseInput.AXIS_Y, false));
        inputManager.addMapping("Mouse Right", new MouseAxisTrigger(MouseInput.AXIS_X, true));
        inputManager.addMapping("Mouse Left", new MouseAxisTrigger(MouseInput.AXIS_X, false));
        inputManager.addMapping("Left Click", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        //HUD controls
        inputManager.addMapping("Inv", new KeyTrigger(KeyInput.KEY_I));
        inputManager.addMapping("Menu", new KeyTrigger(KeyInput.KEY_M));


        inputManager.addListener(analogListener, new String[]{"Mouse Up", "Mouse Down", "Mouse Right", "Mouse Left"});
        inputManager.addListener(actionListener, new String[]{"Left", "Right", "Up", "Down", "Mouse Up", "Jump", "Left Click", "Inv", "Menu"});
    }
    private boolean left = false, right = false, up = false, down = false,
            fire = false, jump = false, invToggle = false, menuToggle = false;
    private ActionListener actionListener = new ActionListener() {

        public void onAction(String name, boolean isPressed, float tpf) {
            if (name.equals("Left")) {
                if (isPressed) {
                    left = true;
                } else {
                    left = false;
                }
            } else if (name.equals("Right")) {
                if (isPressed) {
                    right = true;
                } else {
                    right = false;
                }
            } else if (name.equals("Up")) {
                if (isPressed) {
                    up = true;
                } else {
                    up = false;
                }
            } else if (name.equals("Down")) {
                if (isPressed) {
                    down = true;
                } else {
                    down = false;
                }
            } else if (name.equals("Jump")) {
                if (isPressed) {
                    jump = true;
                } else {
                    jump = false;
                }
            } else if (name.equals("Left Click")) {
                if (isPressed) {
                    fire = true;
                } else {
                    fire = false;
                }
            } else if (name.equals("Inv")) {
                if (isPressed) {
                    invToggle = true;
                }
            } else if (name.equals("Menu")) {
                if (isPressed) {
                    menuToggle = true;
                }
            }
        }
    };
    private AnalogListener analogListener = new AnalogListener() {

        public void onAnalog(String name, float value, float tpf) {
            if (name.equals("Mouse Up") || name.equals("Mouse Down") || name.equals("Mouse Right") || name.equals("Mouse Left")) {
                cursorPos = inputManager.getCursorPosition();
                //Target Picking
                CollisionResults results = new CollisionResults();
                Vector3f cursor3d = app.getCamera().getWorldCoordinates(new Vector2f(cursorPos.x, cursorPos.y), 0f).clone();
                Vector3f dir = app.getCamera().getWorldCoordinates(new Vector2f(cursorPos.x, cursorPos.y), 1f).subtractLocal(cursor3d).normalizeLocal();
                Ray ray = new Ray(cursor3d, dir);
                mobs.collideWith(ray, results);
                if (results.size() > 0) {
                    CollisionResult col = results.getClosestCollision();
                    Spatial tempGeom = col.getGeometry();
                    System.out.println("  You selected " + tempGeom.getName() + " at " + col.getContactPoint() + ", " + col.getDistance() + " wu away.");
                    System.out.println(tempGeom.getUserDataKeys());
                    Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
                    mat.setColor("Color", ColorRGBA.Green);
                    //mat.setColor("GlowColor", ColorRGBA.Red);
                    tempGeom.setMaterial(mat);
                }

                //Player Rotation
                //float angle = (float)(Math.PI + Math.PI/4 + Math.atan2((cursorPos.y - settings.getHeight()/2),(cursorPos.x - settings.getWidth()/2)));
                //Correction for model rotation, above is normal
                float angle = (float) (Math.PI + (3 * Math.PI) / 4 + Math.atan2((cursorPos.y - sl.windowHeight / 2), (cursorPos.x - sl.windowWidth / 2)));
                //player.setRot((new Quaternion()).fromAngles(0, angle, 0));
                //BUG: this method breaks for some quadrants
                player.setUserData("ViewDirection", new Vector3f().set((float) (1 / Math.cos(angle)), 0f, (float) (1 / Math.sin(angle))));
            }
        }
    };

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
            for (Spatial s : rootNode.descendantMatches(Spatial.class)) {
                s.setUserData("WalkDirection", Vector3f.ZERO);
            }
        }
    }

    @Override
    public void update(float tpf) {
        if (menuToggle) {
            menuToggle = false;
            if (nifty.getCurrentScreen() == nifty.getScreen("MenuScreen")) {
                nifty.gotoScreen("HUDScreen");
                setEnabled(true);
            } else {
                nifty.gotoScreen("MenuScreen");
                setEnabled(false);
            }
        }
        if (invToggle) {
            invToggle = false;
            if (nifty.getCurrentScreen() == nifty.getScreen("InventoryScreen")) {
                nifty.gotoScreen("HUDScreen");
                setEnabled(true);
            } else {
                nifty.gotoScreen("InventoryScreen");
                setEnabled(false);
            }
        }
        if (isEnabled()) {
            // do the following while game is RUNNING

            Vector3f walkDirection = new Vector3f();
            float speed = player.getControl(PlayerControl.class).getSpeed() * tpf;
            if (left) {
                walkDirection.addLocal(speed, 0, -speed);
            }
            if (right) {
                walkDirection.addLocal(-speed, 0, speed);
            }
            if (up) {
                walkDirection.addLocal(speed, 0, speed);
            }
            if (down) {
                walkDirection.addLocal(-speed, 0, -speed);
            }
            player.setUserData("WalkDirection", walkDirection);

            if (jump) {
                player.getControl(PlayerControl.class).jump();
            }

            if (fire && !player.getControl(PlayerControl.class).isFiring()) {
                player.getControl(PlayerControl.class).toggleFire(true);
            } else if (!fire && player.getControl(PlayerControl.class).isFiring()) {
                player.getControl(PlayerControl.class).toggleFire(false);
            }

            camNode.setLocalTranslation(player.getWorldTranslation().add(new Vector3f(-14, 14, -14)));

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
