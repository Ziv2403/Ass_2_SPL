package bgu.spl.mics.application.objects;

import java.util.List;

/**
 * Represents a landmark in the environment map.
 * Landmarks are identified and updated by the FusionSlam service.
 */
public class LandMark {
// --------------------- fields --------------------
    private final String Id;
    private final String description;
    private final List<CloudPoint> coordinates;

// --------------------- constructor --------------------
    public LandMark(String id, String description, List<CloudPoint> coordinates){
        this.Id = id;
        this.description = description;
        this.coordinates= coordinates;
    }

// --------------------- methods --------------------
    public String getId() {return Id;}
    public String getDescription() {return description;}
    public List<CloudPoint> getCoordinates() {return coordinates;}

}
