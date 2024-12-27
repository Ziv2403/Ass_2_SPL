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
    private final STATUS status;
    private final List<TrackedObject> lastTrackedObjects;

    // --------------------- constructor --------------------
    public LiDarWorkerTracker(int id, int frequency, STATUS status ){
        this.Id = id;
        this.frequency = frequency;
        this.status = status;
        this.lastTrackedObjects = new ArrayList<>();
    }

    // --------------------- methods --------------------
    public int getId() {return Id;}
    public int getFrequency() {return frequency;}
    public STATUS getStatus() {return status;}
    public List<TrackedObject> getLastTrackedObjects() {return lastTrackedObjects;}

}
