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
    public StampedDetectedObjects(int time){
        this.time = time;
        this.detectedObjects = new ArrayList<>();
    }

    public StampedDetectedObjects(int time, List<DetectedObject> detectedObjectsList) {
        this.time = time;
        this.detectedObjects = detectedObjectsList;
    }


// --------------------- methods ------------------------
    public int getTime() {return time;}
    public List<DetectedObject> getDetectedObjectsList() {return detectedObjects;}
    public void setDetectedObjectsList(List<DetectedObject> detectedObjectsList) {
        this.detectedObjects = detectedObjectsList;
    }
    public  void addDetectedObject(DetectedObject detectedObject) {
        this.detectedObjects.add(detectedObject);
    }
}
//111
