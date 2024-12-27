package bgu.spl.mics.application.objects;

import java.util.List;
import java.util.ArrayList;

/**
 * Represents a group of cloud points corresponding to a specific timestamp.
 * Used by the LiDAR system to store and process point cloud data for tracked objects.
 */
public class StampedCloudPoints {
// --------------------- fields -------------------------
    private String id;
    private int time;
    private List<List<Double>> cloudPoints;

// --------------------- constructor --------------------
    public StampedCloudPoints(String id, int time){
        this.id = id;
        this.time = time;
        this.cloudPoints = new ArrayList<>();
    }
// --------------------- methods ------------------------
    public String getId() {return id;}
    public int getTime() {return time;}
    public List<List<Double>> getCloudPoints() {return cloudPoints;}
}
