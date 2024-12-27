package bgu.spl.mics.application.objects;

import java.util.List;
import java.util.ArrayList;


/**
 * Represents a camera sensor on the robot.
 * Responsible for detecting objects in the environment.
 */
public class Camera {
   // --------------------- fields --------------------
   private int Id;
   private int frequence;
   private STATUS status;
   private List<StampedDetectedObjects> detectedObjectsList;

   // --------------------- constructor --------------------
    public Camera(int id, int frequence, STATUS status ){
        this.Id = id;
        this.frequence = frequence;
        this.status = status;
        this.detectedObjectsList = new ArrayList<>();
    }

   // --------------------- methods --------------------
   public int getId() {return Id;}
   public int getFrequence() {return frequence;}
   public STATUS getStatus() {return status;}
   public List<StampedDetectedObjects> getDetectedObjectsList() {return detectedObjectsList;}
}
// --------------------- fields --------------------
// --------------------- constructor --------------------
// --------------------- methods --------------------


