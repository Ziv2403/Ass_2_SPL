package bgu.spl.mics.application.objects;

import bgu.spl.mics.application.messages.DetectObjectsEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * LiDarWorkerTracker is responsible for managing a LiDAR worker.
 * It processes DetectObjectsEvents and generates TrackedObjectsEvents by using data from the LiDarDataBase.
 * Each worker tracks objects and sends observations to the FusionSlam service.
 */
public class LiDarWorkerTracker {
    private final int id;
    private final int frequency;
    private  STATUS status;
    private  List<TrackedObject> lastTrackedObjects;

    // --------------------- constructor --------------------
    public LiDarWorkerTracker(int id, int frequency, STATUS status ){
        this.id = id;
        this.frequency = frequency;
        this.status = status;
        this.lastTrackedObjects = new ArrayList<>();
    }

    // --------------------- methods --------------------

    //Getters
    public int getId() {return id;}
    public int getFrequency() {return frequency;}
    public STATUS getStatus() {return status;}
    public List<TrackedObject> getLastTrackedObjects() {return lastTrackedObjects;}

    //Setter
    public void setStatus(STATUS status) {
        this.status = status;
    }
    
    //other Methods
    public void addTrackedObject(TrackedObject trackedObject) {
        this.lastTrackedObjects.add(trackedObject);
    }

    // Process DetectObjectsEvent and return a TrackedObject
    public TrackedObject processDetectObjectsEvent(DetectObjectsEvent event, LiDarDataBase liDarDataBase) {
        int detectionTime = event.getDetectedObjects().getTime();
        int scheduledTime = detectionTime + frequency;

        // Check if the worker is active and if the event is ready to process
        if (status == STATUS.UP) {
            List<CloudPoint> cloudPoints = liDarDataBase.getCloudPoints(event.getObjectId());
            if (!cloudPoints.isEmpty()) {
                TrackedObject trackedObject = new TrackedObject(event.getObjectId(), cloudPoints, scheduledTime);
                addTrackedObject(trackedObject);
                return trackedObject;
            }
        }
        return null; // No object was tracked
    }

    

}
