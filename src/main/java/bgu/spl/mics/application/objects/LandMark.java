package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a landmark in the environment map.
 * Landmarks are identified and updated by the FusionSlam service.
 */
public class LandMark {
// --------------------- fields --------------------
    private final String Id;
    private final String description;
    private List<CloudPoint> cloudPoints;

// --------------------- constructor --------------------
    public LandMark(String id, String description){
        this.Id = id;
        this.description = description;
        this.cloudPoints = new ArrayList<>();
    }

    public LandMark(String id, String description, List<CloudPoint> cloudPoints){
        this.Id = id;
        this.description = description;
        this.cloudPoints = cloudPoints;
    }


    // --------------------- methods --------------------
    public String getId() {return Id;}
    public String getDescription() {return description;}
    public List<CloudPoint> getCloudPoints() {return  cloudPoints;}
    public void addCloudPoint(CloudPoint cloudPoint) {cloudPoints.add(cloudPoint);}

    /**
    * Updates the cloud points of the landmark.
    *
    * @param cloudPoints The new list of cloud points.
    * @pre {@code cloudPoints != null}
    * @post The landmark's cloud points are replaced with the new list.
    */
    public void setCloudPoints(List<CloudPoint> cloudPoints) {
        if (cloudPoints != null) {
            this.cloudPoints = new ArrayList<>(cloudPoints);
        }
    }


}
