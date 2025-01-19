package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

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
    public void addCloudPoint(CloudPoint cloudPoint) {
        cloudPoints.add(cloudPoint);
    }


    /**
     * Converts the LandMark object to a JSON object.
     * @return A JsonObject representation of the LandMark.
     */
    public JsonObject toJsonTree() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("id",Id);
        jsonObject.addProperty("description",description);

        JsonArray coordinatesArray = new JsonArray();
        for (CloudPoint point : cloudPoints) {
            JsonObject pointJson = new JsonObject();
            pointJson.addProperty("x", point.getX());
            pointJson.addProperty("y", point.getY());
            coordinatesArray.add(pointJson);
        }
        jsonObject.add("coordinates",coordinatesArray);

        return jsonObject;
    }


    /**
    * Updates the list of cloud points for this LandMark.
    *
    * @param updatedPoints The new list of cloud points to set.
    * @pre {@code updatedPoints != null}
    * @post {@code this.cloudPoints.equals(updatedPoints)}
    */
    public void setCloudPoints(List<CloudPoint> updatedPoints) {
        if (updatedPoints == null) {
            throw new IllegalArgumentException("Updated points cannot be null");
        }
        this.cloudPoints = updatedPoints;
    }


    /**
    * Updates the list of cloud points for this LandMark.
    *
    * @param newPoints The new list of cloud points to set.
    * @pre {@code newPoints != null}
    * @post {@code this.cloudPoints.equals(newPoints)}
    */

    public void updateCloudPoints(List<CloudPoint> newPoints) {
        if (newPoints == null || newPoints.isEmpty()) {
            return;
        }

        Set<String> existingPointsSet = new HashSet<>();
        for (CloudPoint point : cloudPoints) {
            existingPointsSet.add(point.getX() + "," + point.getY());
        }

        for (CloudPoint newPoint : newPoints) {
            String pointKey = newPoint.getX() + "," + newPoint.getY();
            if (!existingPointsSet.contains(pointKey)) {
                cloudPoints.add(newPoint);
                existingPointsSet.add(pointKey);
            } 
        }
    }
    
}
