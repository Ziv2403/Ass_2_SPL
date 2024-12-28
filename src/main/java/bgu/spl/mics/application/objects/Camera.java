package bgu.spl.mics.application.objects;

import java.util.List;
import java.util.ArrayList;


/**
 * Represents a camera sensor on the robot.
 * Responsible for detecting objects in the environment.
 */
public class Camera {
   // --------------------- fields --------------------
   private final int Id;
   private final int frequency;
   private STATUS status;
   private List<StampedDetectedObjects> detectedObjectsList;
   private String key;

   // --------------------- constructor --------------------
    public Camera(int id, int frequency, STATUS status, String key ){
        this.Id = id;
        this.frequency = frequency;
        this.status = status;
        this.detectedObjectsList = new ArrayList<>();
        this.key = key;
    }

   // --------------------- methods --------------------
   //Getters
    public int getId() {return Id;}
    public int getFrequency() {return frequency;}
    public STATUS getStatus() {return status;}
    public List<StampedDetectedObjects> getDetectedObjectsList() {return detectedObjectsList;}
    public String getKey() {return key;}

    //Setter
    public void setStatus(STATUS status) {
        this.status = status;
    }

    //otherMethods
    public void addDetectedObject(StampedDetectedObjects detectedObject) {
        this.detectedObjectsList.add(detectedObject);
    }

}


