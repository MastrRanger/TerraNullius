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
    
    float speed = 1f;
    int health = 100;
    int damage = 0;
    double strength = 1.0;
    
    public void shoot(Spatial target) {
//        CollisionResults results = new CollisionResults();
//        Vector3f targetPos = target.getWorldTranslation();
//        Vector3f playerPos = spatial.getWorldTranslation().add(new Vector3f(0, 1, 0));

        //increment ammo
    }
    
    public void die() {
        spatial.removeFromParent();
        spatial.setUserData("Alive", false);
    }
    
    public boolean isDead(){
        return spatial.getUserData("Alive");
    }
    
    public int getCurrentHealth(){
        return health - damage;
    }
    
    public void hurt(Spatial s){
        damage += (strength * (WeaponType.fromString((String)spatial.getUserData("Weapon")).fireDamage));
        //knock the character down
        spatial.setUserData("Color", ColorRGBA.Red);
        
        if(damage >= health) die();
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
        if (!isDead()) {
            physChar.setWalkDirection(((Vector3f)spatial.getUserData("WalkDirection")));//.normalize().mult(spatial.getUserData("Speed"))
            physChar.setViewDirection(((Vector3f)spatial.getUserData("ViewDirection")));
            
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
