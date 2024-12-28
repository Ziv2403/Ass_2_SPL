package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.List;

/**
 * LiDarWorkerTracker is responsible for managing a LiDAR worker.
 * It processes DetectObjectsEvents and generates TrackedObjectsEvents by using data from the LiDarDataBase.
 * Each worker tracks objects and sends observations to the FusionSlam service.
 */
public class LiDarWorkerTracker {
    private final int Id;
    private final int frequency;
    private  STATUS status;
    private  List<TrackedObject> lastTrackedObjects;

    // --------------------- constructor --------------------
    public LiDarWorkerTracker(int id, int frequency, STATUS status ){
        this.Id = id;
        this.frequency = frequency;
        this.status = status;
        this.lastTrackedObjects = new ArrayList<>();
    }

    // --------------------- methods --------------------

    //Getters
    public int getId() {return Id;}
    public int getFrequency() {return frequency;}
    public STATUS getStatus() {return status;}
    public List<TrackedObject> getLastTrackedObjects() {return lastTrackedObjects;}

    //Setter
    public void setStatus(STATUS status) {
        this.status = status;
    }
    
    //otherMethods
    public void addTrackedObject(TrackedObject trackedObject) {
        this.lastTrackedObjects.add(trackedObject);
    }
    

}
