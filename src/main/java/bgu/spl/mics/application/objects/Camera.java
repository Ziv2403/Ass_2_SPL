package bgu.spl.mics.application.objects;

import java.util.List;

/**
 * Represents a camera sensor on the robot.
 * Responsible for detecting objects in the environment.
 */
public class Camera {
    // TODO: Define fields and methods.
   // --------------------- fields --------------------
   private int Id;
   private int frequence;
   private enum Status{UP, DOWN, ERROR};
   private List<StampedDetectedObjects> detectedObjectsList;

   // --------------------- constructor --------------------
    public Camera(int id, int frequence, ){

    }

   // --------------------- methods --------------------
}
