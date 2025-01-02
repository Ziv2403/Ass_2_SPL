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
    public DetectedObject(String id, String description){
        this.id = id;
        this.description = description;
    }

// --------------------- methods ------------------------
    public String getId() {return id;}
    public String getDescription() {return description;}

}
