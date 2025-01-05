package bgu.spl.mics.application.objects;

// import java.util.ArrayList;
// import java.util.List;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an object tracked by the LiDAR.
 * This object includes information about the tracked object's ID, description, 
 * time of tracking, and coordinates in the environment.
 */
public class TrackedObject {
    
    // --------------------- fields -------------------------
    private final String Id;
    private final int time; // The time the object was tracked
    private final String description;
    private final CloudPoint[] coordinates;

    // --------------------- constructor --------------------

    /**
     * Constructs a TrackedObject with the specified ID, time, description, and coordinates.
     *
     * @param id The unique identifier of the tracked object.
     * @param time The time when the object was tracked.
     * @param description A description of the tracked object.
     * @param coordinates An array of CloudPoint objects representing the coordinates.
     * @post {@code this.Id.equals(id)}
     * @post {@code this.time == time}
     * @post {@code this.description.equals(description)}
     * @post {@code this.coordinates.length == coordinates.length}
     */
    public TrackedObject(String id, int time, String description, CloudPoint[] coordinates){
        this.Id = id;
        this.time = time;
        this.description = description;
        this.coordinates = coordinates;
    }

    // --------------------- methods --------------------

    /**
     * @return The unique identifier of the tracked object.
     */
    public String getId() {return Id;}
    
    
    /**
     * @return The time when the object was tracked.
     */
    public int getTime() {return time;}


    /**
     * @return A description of the tracked object.
     */
    public String getDescription() {return description;}


    /**
     * @return An array of CloudPoint objects representing the coordinates of the tracked object.
     */
    public CloudPoint[] getCoordinates() {return coordinates;}


    /**
     * Converts an array of CloudPoint objects to a List of CloudPoint objects.
     *
     * @param arrayPoints The array of CloudPoint objects to convert.
     * @return A list of CloudPoint objects.
     * @pre {@code arrayPoints != null}
     * @post {@code result.size() == arrayPoints.length}
     */
    public List<CloudPoint> convertToList(CloudPoint[] arrayPoints){
        List<CloudPoint> list = new ArrayList<>();
        for(CloudPoint point : arrayPoints){
            list.add(point);
        }
        return list;
    }


    /**
     * @return A string representation of the TrackedObject.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("TrackedObject{")
          .append("Id='").append(Id).append('\'')
          .append(", time=").append(time)
          .append(", description='").append(description).append('\'')
          .append(", coordinates=[");
        for (int i = 0; i < coordinates.length; i++) {
            sb.append(coordinates[i].toString());
            if (i < coordinates.length - 1) {
                sb.append(", ");
            }
        }
        sb.append("]}");
        return sb.toString();
    }

}
