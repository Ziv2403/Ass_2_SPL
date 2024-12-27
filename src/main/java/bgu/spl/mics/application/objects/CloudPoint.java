package bgu.spl.mics.application.objects;

/**
 * CloudPoint represents a specific point in a 3D space as detected by the LiDAR.
 * These points are used to generate a point cloud representing objects in the environment.
 */
public class CloudPoint {

// --------------------- fields -------------------------
    private final double x;
    private final double y;

// --------------------- constructor --------------------
    public CloudPoint(double x, double y){
        this.x = x;
        this.y = y;
    }

// --------------------- methods ------------------------
    public double getX() {return x;}
    public double getY() {return y;}
}
