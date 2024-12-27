package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an object tracked by the LiDAR.
 * This object includes information about the tracked object's ID, description, 
 * time of tracking, and coordinates in the environment.
 */
public class TrackedObject {
    private final String Id;
    private final int time; // The time the object was tracked
    private final String description;
    private final CloudPoint[] coordinates;

    // --------------------- constructor --------------------
    public TrackedObject(String id, int time, String description, CloudPoint[] coordinates){
        this.Id = id;
        this.time = time;
        this.description = description;
        this.coordinates = coordinates;
    }

    // --------------------- methods --------------------
    public String getId() {return Id;}
    public int getTime() {return time;}
    public String getDescription() {return description;}
    public CloudPoint[] getCoordinates() {return coordinates;}
}
