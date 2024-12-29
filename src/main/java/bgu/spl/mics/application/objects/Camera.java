package bgu.spl.mics.application.objects;

import java.util.List;
import java.util.ArrayList;


/**
 * Represents a camera sensor on the robot.
 * Responsible for detecting objects in the environment.
 */
public class Camera {
   // --------------------- fields --------------------
   private final int id;
   private final int frequency;
   private STATUS status;
   private List<StampedDetectedObjects> detectedObjectsList;
   private final String camera_key;

   // --------------------- constructor --------------------
    public Camera(int id, int frequency, STATUS status, String key ){
        this.id = id;
        this.frequency = frequency;
        this.status = status;
        this.detectedObjectsList = new ArrayList<>();
        this.camera_key = key;
    }

   // --------------------- methods --------------------
   //Getters
    public int getId() {return id;}
    public int getFrequency() {return frequency;}
    public STATUS getStatus() {return status;}
    public List<StampedDetectedObjects> getDetectedObjectsList() {return detectedObjectsList;}
    public String getCameraKey() {return camera_key;}

    //Setter
    public void setStatus(STATUS status) {
        this.status = status;
    }

    //otherMethods
    public void addDetectedObject(StampedDetectedObjects detectedObject) {
        this.detectedObjectsList.add(detectedObject);
    }

}


