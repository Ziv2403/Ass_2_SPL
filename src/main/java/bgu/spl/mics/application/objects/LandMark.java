package bgu.spl.mics.application.objects;

/**
 * Represents a landmark in the environment map.
 * Landmarks are identified and updated by the FusionSlam service.
 */
public class LandMark {
// --------------------- fields --------------------
    private final String Id;
    private final String description;

// --------------------- constructor --------------------
    public LandMark(String id, String description){
        this.Id = id;
        this.description = description;
    }

// --------------------- methods --------------------
    public String getId() {return Id;}
    public String getDescription() {return description;}

}
