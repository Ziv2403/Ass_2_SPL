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

    /**
     * Constructs a StampedCloudPoints object with the specified ID and timestamp.
     * 
     * @param id The unique identifier of the tracked object.
     * @param time The timestamp when the cloud points were recorded.
     * @post {@code this.id.equals(id)}
     * @post {@code this.time == time}
     * @post {@code this.cloudPoints.isEmpty() == true}
     */
    public StampedCloudPoints(String id, int time){
        this.id = id;
        this.time = time;
        this.cloudPoints = new ArrayList<>();
    }
// --------------------- methods ------------------------

    /**
     * @return The unique identifier of the tracked object.
     */
    public String getId() {return id;}


    /**
     * @return The timestamp when the cloud points were recorded.
     */
    public int getTime() {return time;}


    /**
     * @return The list of cloud points, where each point is represented as a list of doubles [x, y].
     */
    public List<List<Double>> getCloudPoints() {return cloudPoints;}


    /**
     * Converts the list of cloud points to an array of CloudPoint objects.
     * 
     * @return An array of CloudPoint objects representing the cloud points.
     * @post {@code result.length == cloudPoints.size()}
     */
    public CloudPoint[] toCloudPointObj() {
        CloudPoint[] points = new CloudPoint[cloudPoints.size()];
        int i = 0;
        for (List<Double> d : cloudPoints) {
            points[i] = new CloudPoint(d.get(0), d.get(1));
            i++;
        }
        return points;
    }


    /**
     * @return A string representation of the StampedCloudPoints object.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("StampedCloudPoints{")
          .append("id='").append(id).append('\'')
          .append(", time=").append(time)
          .append(", cloudPoints=").append(cloudPoints)
          .append('}');
        return sb.toString();
    }
}
