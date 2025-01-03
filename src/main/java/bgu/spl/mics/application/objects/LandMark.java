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

    /**
     * Constructs a LandMark with a specified ID and description.
     *
     * @param id The unique identifier for the landmark.
     * @param description A textual description of the landmark.
     * @post {@code this.Id.equals(id)}
     * @post {@code this.description.equals(description)}
     * @post {@code this.cloudPoints.isEmpty() == true}
     */
    public LandMark(String id, String description){
        this.Id = id;
        this.description = description;
        this.cloudPoints = new ArrayList<>();
    }


    /**
     * Constructs a LandMark with a specified ID, description, and list of cloud points.
     *
     * @param id The unique identifier for the landmark.
     * @param description A textual description of the landmark.
     * @param cloudPoints The list of cloud points associated with the landmark.
     * @post {@code this.Id.equals(id)}
     * @post {@code this.description.equals(description)}
     * @post {@code this.cloudPoints.equals(cloudPoints)}
     */
    public LandMark(String id, String description, List<CloudPoint> cloudPoints){
        this.Id = id;
        this.description = description;
        this.cloudPoints = cloudPoints;
    }


    // --------------------- methods --------------------
    /**
     * @return The unique identifier of the landmark.
     */
    public String getId() {return Id;}


    /**
     * @return The textual description of the landmark.
     */
    public String getDescription() {return description;}


    /**
     * @return The list of cloud points associated with the landmark.
     */
    public List<CloudPoint> getCloudPoints() {return  cloudPoints;}


    /**
     * Adds a cloud point to the list of cloud points associated with the landmark.
     *
     * @param cloudPoint The cloud point to add.
     * @pre {@code cloudPoint != null}
     * @post {@code cloudPoints.contains(cloudPoint)}
     */
    public void addCloudPoint(CloudPoint cloudPoint) {cloudPoints.add(cloudPoint);}

    /**
     * @return A string representation of the LandMark in JSON format.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{\"id\": \"").append(Id).append("\", ");
        sb.append("\"description\": \"").append(description).append("\", ");
        sb.append("\"coordinates\": [");

        for (int i = 0; i < cloudPoints.size(); i++) {
            CloudPoint point = cloudPoints.get(i);
            sb.append(point.toString());
            if (i < cloudPoints.size() - 1) {
                sb.append(", ");
            }
        }

        sb.append("]}");
        return sb.toString();
    }

}
