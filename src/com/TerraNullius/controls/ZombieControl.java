package com.TerraNullius.controls;

import com.TerraNullius.entity.WeaponType;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;

/**
 *
 * @author Griffin
 */
public class ZombieControl extends MobControl {
    
    @Override
    public void setSpatial(Spatial spatial){
        super.setSpatial(spatial);
        spatial.setName("Player");
        spatial.setUserData("Alive", true);
        spatial.setUserData("Firing", false);
        spatial.setUserData("Weapon", WeaponType.HANDS);
        spatial.setUserData("Speed", 1f);
        spatial.setUserData("Health", 100);
        spatial.setUserData("Damage", 0);
        spatial.setUserData("Strength", 1f);
        spatial.scale(1.5f, 1.5f, 1.5f);
        spatial.setLocalTranslation(new Vector3f());
    }

    public Control cloneForSpatial(Spatial spatial) {
        MobControl control = new ZombieControl();
        control.setSpatial(spatial);
        control.setEnabled(isEnabled()); 
        return control;
    }
}
