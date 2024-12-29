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
    private List<DetectedObject> detectedObjectsList;

// --------------------- constructors --------------------
    public StampedDetectedObjects(int time){
        this.time = time;
        this.detectedObjectsList = new ArrayList<>();
    }


// --------------------- methods ------------------------
    public int getTime() {return time;}
    public List<DetectedObject> getDetectedObjectsList() {return detectedObjectsList;}

    public  void addDetectedObject(DetectedObject detectedObject) {
        this.detectedObjectsList.add(detectedObject);
    }
}
//111
