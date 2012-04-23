package com.TerraNullius.controls;

import com.TerraNullius.entity.WeaponType;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;

/**
 *
 * @author Griffin
 */
public abstract class MobControl extends AbstractControl {
    
    CharacterControl physChar;
    
    boolean firing = false;
    long shootTimer = 0;
    
    public int getHealth(){
        return (Integer)spatial.getUserData("Health");
    }
    
    public int getDamage(){
        return (Integer)spatial.getUserData("Damage");
    }
    
    public float getSpeed(){
        return (Float)spatial.getUserData("Speed");
    }
    
    public float getStrength(){
        return (Float)spatial.getUserData("Strength");
    }
    
    public WeaponType getWeapon(){
        return (WeaponType)spatial.getUserData("Weapon");
    }
    
    public boolean isAlive(){
        return (Boolean)spatial.getUserData("Alive");
    }
    
    public int getCurrentHealth(){
        return getHealth() - getDamage();
    }
    //using slight auto-aim correction
    public void shoot(Vector3f targetPos) {
//        CollisionResults results = new CollisionResults();
//        Vector3f targetPos = target.getWorldTranslation();
//        Vector3f playerPos = spatial.getWorldTranslation().add(new Vector3f(0, 1, 0));

        //increment ammo
        
        //get rotation
        //ray trace to target
        //detect hit
        //draw line to target
        //notify target if its hit

//        CollisionResults results = new CollisionResults();
//
//        Vector2f mousePosNoOff = new Vector2f();
//        mousePosNoOff.x = (game.cursorPos.x - game.settings.getWidth() / 2) / (game.settings.getWidth() / 2);
//        mousePosNoOff.y = (game.cursorPos.y - game.settings.getHeight() / 2) / (game.settings.getHeight() / 2);
//        float polarAngle = mousePosNoOff.getAngle() - FastMath.PI / 4;
//        float polarMag = FastMath.sqrt(FastMath.pow(mousePosNoOff.x, 2) + FastMath.pow(mousePosNoOff.y, 2));
//
//        Vector2f mousePos = new Vector2f(polarMag * FastMath.cos(polarAngle), polarMag * FastMath.sin(polarAngle));
//        Vector3f playerPos = this.getWorldPos().add(new Vector3f(0, 1, 0));
//        Vector3f rayCoords = new Vector3f(((mousePos.y * 300)), 1f, ((mousePos.x * 300)));

//        Ray ray = new Ray(playerPos, rayCoords);

//        //Temp line
//        Mesh lineMesh = new Mesh();
//        lineMesh.setMode(Mesh.Mode.Lines);
//        lineMesh.setLineWidth(5f);
//        lineMesh.setBuffer(VertexBuffer.Type.Position, 3, new float[]{
//            playerPos.x,
//            playerPos.y,
//            playerPos.z,
//            rayCoords.x + playerPos.x,
//            rayCoords.y + playerPos.y,
//            1f
//        });
//        lineMesh.setBuffer(VertexBuffer.Type.Index, 2, new short[]{ 0, 1 });
//        lineMesh.updateBound();
//        lineMesh.updateCounts();
//        game.line.setMesh(lineMesh);

//        game.mobs.collideWith(ray, results);
//
//        Bullet bul = new Bullet(game, playerPos, rayCoords);
//
//        if (results.size() > 0) {
//            CollisionResult col = results.getClosestCollision();
//            Spatial tempGeom = col.getGeometry();
//            if (col.getDistance() <= weap.range) {
//                System.out.println("  You shot " + tempGeom.getName() + " at " + col.getContactPoint() + ", " + col.getDistance() + " wu away.");
//                String id = tempGeom.getUserData("ID");
//                Entity e = game.idMap.getEntity(id);
//                if(e != null) e.hurt(this);
//            }
//        }
    
    }
    
    public void shoot() {
        shoot(physChar.getViewDirection());
    }
    
    public void toggleFire(boolean fire){
        this.firing = fire;
    }
    
    public void die() {
        spatial.removeFromParent();
        spatial.setUserData("Alive", false);
    }
    
    public void hurt(Spatial s){
        spatial.setUserData("Damage", getDamage() + (getStrength() * getWeapon().fireDamage));
        //knock the character down
        spatial.setUserData("Color", ColorRGBA.Red);
        
        if(getCurrentHealth() <= 0){ 
            die();
        }
    }
    
    public void jump(){
        physChar.jump();
    }
    
    @Override
    public void setSpatial(Spatial spatial){
        physChar = spatial.getControl(CharacterControl.class);
        spatial.setUserData("Alive", true);
    }

    @Override
    protected void controlUpdate(float tpf) {
        if (isAlive()) {
            physChar.setWalkDirection(((Vector3f)spatial.getUserData("WalkDirection")));//.normalize().mult(spatial.getUserData("Speed"))
            physChar.setViewDirection(((Vector3f)spatial.getUserData("ViewDirection")));
                            
            if ((System.currentTimeMillis() - shootTimer > getWeapon().fireRate * 1000) && firing) {
                shoot();
                shootTimer = System.currentTimeMillis();
            }
            //checkCollisions(pos);
        }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public Control cloneForSpatial(Spatial spatial) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
