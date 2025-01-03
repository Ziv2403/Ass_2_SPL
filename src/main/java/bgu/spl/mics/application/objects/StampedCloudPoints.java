package bgu.spl.mics.application.objects;

import java.util.List;
import java.util.ArrayList;

/**
 * Represents a group of cloud points corresponding to a specific timestamp.
 * Used by the LiDAR system to store and process point cloud data for tracked objects.
 */
public class StampedCloudPoints {
// --------------------- fields -------------------------
    private final String id;
    private final int time;
    private final List<List<Double>> cloudPoints;

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

    public CloudPoint[] cloudPointsToArray() { // FROM LIST OF LISTS TO AN ARRAY OF CLOUDPOINTS

    }
}
