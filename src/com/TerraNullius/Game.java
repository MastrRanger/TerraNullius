package com.TerraNullius;

import com.TerraNullius.appstates.GameState;
import com.TerraNullius.appstates.InventoryState;
import com.TerraNullius.appstates.MenuState;
import com.TerraNullius.entity.*;
import com.TerraNullius.physics.TNPhysicsListener;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.font.BitmapText;
import com.jme3.math.*;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.CameraNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.system.AppSettings;
import de.lessvoid.nifty.Nifty;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Game extends SimpleApplication {

    static Game instance;
    public static AppSettings settings;
    public EntityIDMapper idMap;
    
    public Vector2f cursorPos;
    public CameraNode camNode;
    public Node playerNode;
    public Node mobs;
    boolean isRunning = false;
    
    //AppStates
    public BulletAppState bulletAppState;
    public GameState gameState;
    public MenuState menuState;
    public InventoryState inventoryState;
    
    //Entities
    public Player player;
    public ArrayList<Mob> mobList = new ArrayList();
    public ArrayList<Entity> entityList = new ArrayList();
    public Geometry line; //debug
    //HUD
    public BitmapText healthText;
    public BitmapText weapText;
    public Nifty nifty;

    public static void main(String[] args) {
        Game app = new Game();
        Game.instance = app;
        app.setShowSettings(false);
        SettingsLoader sl = new SettingsLoader();
        settings = new AppSettings(true);
        settings.put("Width", sl.windowWidth);
        settings.put("Height", sl.windowHeight);
        settings.put("Title", "Terra Nullius");
        settings.put("VSync", sl.vsync);
        settings.put("Samples", sl.aasamples);
        app.setSettings(settings);
        app.start();
    }

    @Override
    public void simpleInitApp() {
        Logger.getLogger("").setLevel(Level.SEVERE);

        //AppStates
        bulletAppState = new BulletAppState();
        gameState = new GameState();
        menuState = new MenuState();
        inventoryState = new InventoryState();
        stateManager.attach(bulletAppState);
        stateManager.attach(gameState);
        stateManager.attach(menuState);
        stateManager.attach(inventoryState);
        
        //Physics
        bulletAppState.getPhysicsSpace().setGravity(new Vector3f(0f, -1f, 0f));
        bulletAppState.getPhysicsSpace().setAccuracy(0.005f);
        TNPhysicsListener pListener = new TNPhysicsListener(bulletAppState);
        
        flyCam.setEnabled(false);
        
//        //Ground
//        Box b = new Box(Vector3f.ZERO, 128f, 1f, 128f);
//        b.scaleTextureCoordinates(new Vector2f(64f, 64f));
//        Geometry ground = new Geometry("Ground", b);
//        ground.setLocalTranslation(0f, -1f, 0f);
//        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
//        mat.setColor("Color", ColorRGBA.Green);
//        Texture groundTex = assetManager.loadTexture("Textures/BlandTile.png");
//        groundTex.setWrap(Texture.WrapMode.Repeat);
//        mat.setTexture("ColorMap", groundTex);
//        ground.setMaterial(mat);
//        RigidBodyControl groundPhys = new RigidBodyControl(0);
////        groundPhys.setFriction(1f);
////        groundPhys.setKinematic(false); 
//        ground.addControl(groundPhys);
//        bulletAppState.getPhysicsSpace().add(groundPhys);
//        rootNode.attachChild(ground);

//        Mesh lineMesh = new Mesh();
//        lineMesh.setMode(Mesh.Mode.Lines);
//        lineMesh.setLineWidth(5f);
//        lineMesh.setBuffer(VertexBuffer.Type.Position, 3, new float[]{0,0,0,1,1,1});
//        lineMesh.setBuffer(VertexBuffer.Type.Index, 2, new short[]{ 0, 1 });
//        lineMesh.updateBound();
//        lineMesh.updateCounts();
//        line = new Geometry("line", lineMesh);
//        Material matL = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
//        matL.setColor("Color", ColorRGBA.Black);
//        line.setMaterial(matL);
//        rootNode.attachChild(line);

        //HUD
//        guiNode.detachAllChildren();
//        guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
//        healthText = new BitmapText(guiFont, false);
//        healthText.setSize(guiFont.getCharSet().getRenderedSize());
//        healthText.setText("Health: " + player.getCurrentHealth());
//        healthText.setLocalTranslation(300, healthText.getLineHeight(), 0);
//        guiNode.attachChild(healthText);
//        weapText = new BitmapText(guiFont, false);
//        weapText.setSize(guiFont.getCharSet().getRenderedSize());
//        weapText.setText("Weap: " + player.getWeap().toString());
//        weapText.setLocalTranslation(100, weapText.getLineHeight(), 0);
//        guiNode.attachChild(weapText);
        
        NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(getAssetManager(),getInputManager(),getAudioRenderer(),getGuiViewPort());
        nifty = niftyDisplay.getNifty();
        nifty.fromXml("Interface/GUI.xml", "MenuScreen", new MenuState());
        getGuiViewPort().addProcessor(niftyDisplay);

    }

    public AppSettings getSettings(){
        return this.settings;
    }

    @Override
    public void simpleUpdate(float tpf) {
        
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }

    public void gameOver() {
        guiNode.detachAllChildren();
//        BitmapFont font = game.getAssetManager().loadFont("Interface/Fonts/Default.fnt");
        BitmapText deathText = new BitmapText(guiFont, false);
        deathText.setSize(30);
        deathText.setText("Game Over");
        deathText.setLocalTranslation(settings.getWidth() / 2 - deathText.getLineWidth() / 2, deathText.getLineHeight() + settings.getHeight() / 2, 0);
        guiNode.attachChild(deathText);
    }
}
