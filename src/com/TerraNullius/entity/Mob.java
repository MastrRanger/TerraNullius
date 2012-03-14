/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.TerraNullius.entity;

import com.TerraNullius.entity.Weapon.WeaponType;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;


/**
 * Defines a type of entity that can attack.
 * 
 * @author Griffin
 */
public class Mob extends Entity{
    
    CharacterControl physChar;
    WeaponType weap;
    boolean isFireing;
    float speed = 1f;
    float turnSpeed = 1f; //Higher is slower (turn interpolation time)
    
    public void update(float tpf){}
    
    public float getSpeed(){return speed;}
    
    public float getTurnSpeed(){return turnSpeed;}
    
    public void fireOn(){isFireing = true;}
    
    public void fireOff(){isFireing = false;}
    
    public boolean isFireing(){return isFireing;}
    
    public void setWeap(WeaponType weap){this.weap = weap;}
    
    public WeaponType getWeap(){return this.weap;}
    
    public void setPos(Vector3f pos){
        this.pos = pos;
        physChar.warp(pos);
    }
    
    @Override
    public void die(){
        game.mobs.detachChild(geom);
        game.mobList.remove(this);
        game.bulletAppState.getPhysicsSpace().remove(physChar);
    }
    
    public void shoot(Entity e){
        CollisionResults results = new CollisionResults();
        
        Vector3f thisPos = this.getWorldPos();
        
        Vector3f rayCoords = new Vector3f(e.pos.x, e.pos.y, 1f);

        Ray ray = new Ray(thisPos, rayCoords);

        e.geom.collideWith(ray, results);

        if (results.size() > 0) {
            CollisionResult col = results.getCollision(0);
            Geometry tempGeom = col.getGeometry();
            if(col.getDistance() <= weap.range){
                System.out.println(this.geom.getName() + "  hit " + tempGeom.getName() + " at " + col.getContactPoint() + ", " + col.getDistance() + " wu away.");
                e.hurt(this);
            }
        }
          
    }


}