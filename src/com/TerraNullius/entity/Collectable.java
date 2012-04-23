/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.TerraNullius.entity;

import com.jme3.collision.CollisionResults;

/**
 *
 * @author Griffin
 */
public class Collectable extends Entity{

    @Override
    public void update(){
        if(!isDead()){
            if(!pos.equals(spatial.getLocalTranslation())){
                spatial.setLocalTranslation(pos);
            }
            if(!rot.equals(spatial.getLocalRotation())){
                spatial.setLocalRotation(rot);
            }
            CollisionResults results = new CollisionResults();
            spatial.collideWith(game.player.spatial.getWorldBound(), results);
            if(results.size() > 0){
                //game.player.give(this);
                die();
            }
        }
    }
    
    
}
