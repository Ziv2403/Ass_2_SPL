package bgu.spl.mics.application.objects;

/**
 * CloudPoint represents a specific point in a 3D space as detected by the LiDAR.
 * These points are used to generate a point cloud representing objects in the environment.
 */
public class CloudPoint {

// --------------------- fields -------------------------
    private int x;
    private int y;

// --------------------- constructor --------------------
    public CloudPoint(int x, int y){
        this.x = x;
        this.y = y;
    }

// --------------------- methods ------------------------
    public int getX() {return x;}
    public int getY() {return y;}
}
