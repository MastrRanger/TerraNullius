/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.TerraNullius.entity;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;
import java.io.IOException;

/**
 *
 * @author Griffin
 */
public enum WeaponType implements Savable {
    HANDS(0.5, 10, 2, 0),
    MACHINEGUN(0.05, 8, 50, 1),
    PISTOL(0.33, 20, 50, 2),
    RIFLE(1, 50, 100, 3);
    
    public double fireRate;
    public int fireDamage;
    public int range;
    public int modelNum;

    private WeaponType(double fireRate, int fireDamage, int range, int modelNum){
        this.fireRate = fireRate;
        this.fireDamage = fireDamage;
        this.range = range;
        this.modelNum = modelNum;
    }
    
//    public static WeaponType fromString(String s){
//        if(s.equalsIgnoreCase("Pistol")){
//            return PISTOL;
//        }else if(s.equalsIgnoreCase("Machinegun")){
//            return MACHINEGUN;
//        }else if(s.equalsIgnoreCase("Rifle")){
//            return RIFLE;
//        }else{
//            return HANDS;
//        }
//    }

    public void write(JmeExporter ex) throws IOException {
        OutputCapsule capsule = ex.getCapsule(this);
        capsule.write(fireRate, "fireRate", 0);
        capsule.write(fireDamage, "fireDamage", 0);
        capsule.write(range, "range", 0);
        capsule.write(modelNum, "modelNum", 0);
        
    }

    public void read(JmeImporter im) throws IOException {
        InputCapsule capsule = im.getCapsule(this);
        fireRate = capsule.readDouble("fireRate", 0);
        fireDamage = capsule.readInt("fireDamage", 0);
        range = capsule.readInt("range", 0);
        modelNum = capsule.readInt("modelNum", 0);
    }
        
}

