package bgu.spl.mics.application.objects;

import java.util.List;
import java.util.ArrayList;

/**
 * Represents objects detected by the camera at a specific timestamp.
 * Includes the time of detection and a list of detected objects.
 */
public class StampedDetectedObjects {
// --------------------- fields -------------------------
    private int time;
    private List<DetectedObject> detectedObjects;

// --------------------- constructors --------------------

    /**
     * Constructs a StampedDetectedObjects object with the specified timestamp.
     * Initializes an empty list of detected objects.
     *
     * @param time The timestamp of detection.
     * @post {@code this.time == time}
     * @post {@code this.detectedObjects.isEmpty() == true}
     */
    public StampedDetectedObjects(int time){
        this.time = time;
        this.detectedObjects = new ArrayList<>();
    }


    /**
     * Constructs a StampedDetectedObjects object with the specified timestamp and detected objects.
     *
     * @param time The timestamp of detection.
     * @param detectedObjectsList The list of detected objects.
     * @post {@code this.time == time}
     * @post {@code this.detectedObjects.equals(detectedObjectsList)}
     */
    public StampedDetectedObjects(int time, List<DetectedObject> detectedObjectsList) {
        this.time = time;
        this.detectedObjects = detectedObjectsList;
    }


// --------------------- methods ------------------------

    /**
     * @return The timestamp when the objects were detected.
     */
    public int getTime() {return time;}
    
    
    /**
     * @return The list of objects detected at the specified timestamp.
     */
    public List<DetectedObject> getDetectedObjectsList() {return detectedObjects;}
    
    
    /**
     * Sets the list of detected objects.
     *
     * @param detectedObjectsList The new list of detected objects.
     * @pre {@code detectedObjectsList != null}
     * @post {@code this.detectedObjects.equals(detectedObjectsList)}
     */    
    public void setDetectedObjectsList(List<DetectedObject> detectedObjectsList) {
        this.detectedObjects = detectedObjectsList;
    }
    
    
    /**
     * Adds a detected object to the list of detected objects.
     *
     * @param detectedObject The detected object to add.
     * @pre {@code detectedObject != null}
     * @post {@code this.detectedObjects.contains(detectedObject)}
     */
    public  void addDetectedObject(DetectedObject detectedObject) {
        this.detectedObjects.add(detectedObject);
    }


    /**
     * @return A string representation of the StampedDetectedObjects object.
     */
    @Override
    public String toString() {
        return "StampedDetectedObjects{" +
                "time=" + time +
                ", detectedObjects=" + detectedObjects +
                '}';
    }
}

