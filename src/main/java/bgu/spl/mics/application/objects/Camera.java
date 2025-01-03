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
   private final STATUS status;
   private final List<StampedDetectedObjects> detectedObjectsList;

   // --------------------- constructor --------------------
    public Camera(int id, int frequency, STATUS status ){
        this.Id = id;
        this.frequency = frequency;
        this.status = status;
        this.detectedObjectsList = new ArrayList<>();
    }

   // --------------------- methods --------------------
   public int getId() {return Id;}
   public int getFrequency() {return frequency;}
   public STATUS getStatus() {return status;}
   public List<StampedDetectedObjects> getDetectedObjectsList() {return detectedObjectsList;}
}


