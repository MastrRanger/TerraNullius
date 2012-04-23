/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.TerraNullius;

/**
 * Loads saved settings from file, stores them for other classes to access
 * @author Griffin
 */
public class SettingsLoader {
    //TODO: implement loading from file
    
    //Video
    public int windowHeight;
    public int windowWidth;
    public int aasamples;
    public boolean vsync;
    
    //Keybindings
    
    public SettingsLoader(){
        //Video
        this.windowHeight = 720;
        this.windowWidth = 1280;
        this.aasamples = 4;
        this.vsync = true;
    }
    
}
