package bgu.spl.mics.application.objects;

import java.util.List;

// import java.util.ArrayList;
// import java.util.List;

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
    private final List<CloudPoint> coordinates;

    // --------------------- constructor --------------------
    public TrackedObject(String id, int time, String description, List<CloudPoint> cloudPoints){
        this.Id = id;
        this.time = time;
        this.description = description;
        this.coordinates = cloudPoints;
    }

    //ZIV - ADDED
    public TrackedObject(String objectId, List<CloudPoint> cloudPoints, int scheduledTime) {
        this.Id = String.valueOf(objectId);
        this.time = scheduledTime;
        this.description = "TrackedObject" + String.valueOf(objectId);
        this.coordinates = cloudPoints;
    }

    // --------------------- methods --------------------
    public String getId() {return Id;}
    public int getTime() {return time;}
    public String getDescription() {return description;}
    public List<CloudPoint> getCoordinates() {return coordinates;}
}
