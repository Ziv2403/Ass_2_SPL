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

    //ZIV CHANGE: Convert a list of points (List<List<Double>>) to CloudPoint
    public List<CloudPoint> getCloudPoints() {
        List<CloudPoint> cloudPoints = new ArrayList<>();
        for (List<Double> point : this.cloudPoints) {
            cloudPoints.add(new CloudPoint(point.get(0), point.get(1)));
        }
        return cloudPoints;
    }
}
