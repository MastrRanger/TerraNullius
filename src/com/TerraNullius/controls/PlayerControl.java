package com.TerraNullius.controls;

import com.TerraNullius.entity.WeaponType;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;

/**
 *
 * @author Griffin
 */
public class PlayerControl extends MobControl {

    @Override
    public void setSpatial(Spatial spatial){
        super.setSpatial(spatial);
        spatial.setName("Player");
        
        spatial.setUserData("Weapon", WeaponType.PISTOL);
        spatial.setUserData("Speed", 1f);
        spatial.setUserData("Health", 100);
        spatial.setUserData("Damage", 0);
        spatial.setUserData("Strength", 1f);
        
        spatial.scale(1.5f, 1.5f, 1.5f);
        spatial.setLocalTranslation(new Vector3f());
    }

    @Override
    public Control cloneForSpatial(Spatial spatial) {
        MobControl control = new PlayerControl();
        control.setSpatial(spatial);
        control.setEnabled(isEnabled()); 
        return control;
    }
    
}
