package com.drakotrogdor.command2velocity;

public class Config {
    private int spawnX;
    private int spawnY;
    private int spawnZ;
    private boolean forceSpawn;
    public int getSpawnX(){
        return this.spawnX;
    }
    public void setSpawnX(int spawnX){
        this.spawnX = spawnX;
    }
    public int getSpawnY(){
        return this.spawnY;
    }
    public void setSpawnY(int spawnY){
        this.spawnY = spawnY;
    }
    public int getSpawnZ(){
        return this.spawnZ;
    }
    public void setSpawnZ(int spawnZ){
        this.spawnZ = spawnZ;
    }
    public boolean getForceSpawn(){
        return this.forceSpawn;
    }
    public void setForceSpawn(boolean forceSpawn){
        this.forceSpawn = forceSpawn;
    }

}
