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
    /**
    * Default constructor for the Camera class.
    * Initializes the camera with default values.
    * 
    * @post {@code id == 0}
    * @post {@code frequency == 0}
    * @post {@code status == STATUS.UP}
    * @post {@code detectedObjectsList.isEmpty() == true}
    * @post {@code camera_key.isEmpty() == true}
    */
   public Camera(){
       this.id = 0;
       this.frequency = 0;
       this.status = STATUS.UP;
       this.detectedObjectsList = new ArrayList<>();
       this.camera_key = "";
   }
    /**
    * Parameterized constructor for the Camera class.
    * Initializes the camera with specific values.
    *
    * @param id The unique identifier for the camera.
    * @param frequency The time interval at which the camera sends new events.
    * @param status The initial status of the camera.
    * @param key The unique key associated with the camera.
    * @post {@code this.id == id}
    * @post {@code this.frequency == frequency}
    * @post {@code this.status == status}
    * @post {@code this.camera_key.equals(key)}
    */
    public Camera(int id, int frequency, STATUS status, String key ){
        this.id = id;
        this.frequency = frequency;
        this.status = status;
        this.detectedObjectsList = new ArrayList<>();
        this.camera_key = key;
    }

   // --------------------- methods --------------------

   /**
    * @return The unique identifier of the camera.
    */   
    public int getId() {return id;}


   /**
    * @return The frequency at which the camera sends events.
    */
    public int getFrequency() {return frequency;}


   /**
    * @return The current status of the camera.
    */    
    public STATUS getStatus() {return status;}


    /**
    * @return A list of detected objects by the camera, each with a timestamp.
    */
    public List<StampedDetectedObjects> getDetectedObjectsList() {return detectedObjectsList;}


    /**
    * @return The unique key associated with the camera.
    */
    public String getCameraKey() {return camera_key;}


   /**
    * Sets the status of the camera.
    *
    * @param status The new status to set for the camera.
    * @pre {@code status != null}
    * @post {@code this.status == status}
    */
    public void setStatus(STATUS status) {
        this.status = status;
    }


   /**
    * Adds a detected object to the list of detected objects.
    *
    * @param detectedObject The object to add, with its associated timestamp.
    * @pre {@code detectedObject != null}
    * @post {@code detectedObjectsList.contains(detectedObject)}
    */
    public void addDetectedObject(StampedDetectedObjects detectedObject) {
        this.detectedObjectsList.add(detectedObject);
    }

    /**
     * @return A string representation of the Camera.
     */
    @Override
    public String toString() {
        return "Camera{id=" + id + ", frequency=" + frequency + ", status=" + status + ", camera_key='" + camera_key + '\'' + ", detectedObjectsList=" + detectedObjectsList + '}';
    }
    

}


