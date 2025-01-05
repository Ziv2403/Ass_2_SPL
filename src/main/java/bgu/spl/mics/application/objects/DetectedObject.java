package bgu.spl.mics.application.objects;

/**
 * DetectedObject represents an object detected by the camera.
 * It contains information such as the object's ID and description.
 */
public class DetectedObject {

// --------------------- fields -------------------------
    private final String id;
    private final String description;

// --------------------- constructor --------------------

    /**
     * Constructs a DetectedObject with the specified ID and description.
     *
     * @param id The unique identifier for the detected object.
     * @param description A textual description of the detected object.
     * @pre {@code id != null && !id.isEmpty()}
     * @pre {@code description != null && !description.isEmpty()}
     * @post {@code this.id.equals(id)}
     * @post {@code this.description.equals(description)}
     */
    public DetectedObject(String id, String description){
        this.id = id;
        this.description = description;
    }

// --------------------- methods ------------------------

    /**
     * @return The unique identifier of the detected object.
     */
    public String getId() {return id;}


    /**
     * @return The textual description of the detected object.
     */
    public String getDescription() {return description;}


    /**
     * @return A string representation of the DetectedObject.
     */
    @Override
    public String toString() {
        return "DetectedObject{id='" + id + "', description='" + description + "'}";
    }

}
