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
    public LiDarWorkerTracker() {
        this.id = 0;
        this.frequency = 0;
        this.status = STATUS.UP;
        this.lastTrackedObjects = new ArrayList<>();
    }

    public LiDarWorkerTracker(int id, int frequency){
        this.id = id;
        this.frequency = frequency;
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

    // Process DetectObjectsEvent and return a list of the corresponding TrackedObjects
    public List<TrackedObject> processDetectObjectsEvent(DetectObjectsEvent event, LiDarDataBase liDarDataBase) {
        int detectionTime = event.getDetectedObjects().getTime();
        StampedDetectedObjects detectedObjects = event.getDetectedObjects();
        // Check if the worker is active and if the event is ready to process
        if (status == STATUS.UP) {
            List<TrackedObject> output = new ArrayList<>();
            for (DetectedObject d : detectedObjects.getDetectedObjectsList()) {
                String id = d.getId();
                String description = d.getDescription();
                CloudPoint[] points = liDarDataBase.getCloudPointsOfObject(id);
                if (points != null) {
                    TrackedObject trackedObject = new TrackedObject(id, detectionTime, description, points); // CHECK TIMING AGAIN
                    addTrackedObject(trackedObject);
                    output.add(trackedObject);
                }
            }
            return output;
        }
        return new ArrayList<>(); // No object was tracked
    }

    

}
