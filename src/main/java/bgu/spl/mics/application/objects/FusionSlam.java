package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import static java.lang.Math.*;

/**
 * Manages the fusion of sensor data for simultaneous localization and mapping (SLAM).
 * Combines data from multiple sensors (e.g., LiDAR, camera) to build and update a global map.
 * Implements the Singleton pattern to ensure a single instance of FusionSlam exists.
 */
public class FusionSlam {

// --------------------- fields -------------------------
    private final List<LandMark> landmarks;
    private final List<Pose> poses;

// --------------------- SingletonImplemment -------------------------

    // Singleton instance holder
    private static class FusionSlamHolder {
        private static final FusionSlam INSTANCE = new FusionSlam();
    }

    public FusionSlam(){ //SHOULDN'T BE PRIVATE???
            this.landmarks = new ArrayList<>();
            this.poses = new ArrayList<>();
    }

    public static FusionSlam getInstance(){
        return FusionSlamHolder.INSTANCE;
    }

// --------------------- methods --------------------
//Getters
    public List<LandMark> getLandMarkList() { return landmarks;}
    public List<Pose> getPoseList() {return poses;}

//Adders
    public void addLandmark (LandMark landmark){
        this.landmarks.add(landmark);
    }

    public void addPose( Pose pose){
        this.poses.add(pose);
    }

//Compute Global
    // //Method that convert only one point local coordinates to a global system:
    // public CloudPoint transformToGlobal(CloudPoint localPoint, Pose pose) {
    //     double yawRad = toRadians(pose.getYaw());
    //     double xGlobal = cos(yawRad) * localPoint.getX() - sin(yawRad) * localPoint.getY() + pose.getX();
    //     double yGlobal = sin(yawRad) * localPoint.getX() + cos(yawRad) * localPoint.getY() + pose.getY();
    //     return new CloudPoint((int) xGlobal, (int) yGlobal);
    // }
    
    // //Function to update and refine the map or add a new Landmark
    // public void updateLandmark(String id, List<CloudPoint> localCoordinates, Pose currentPose) {
    //     // Step 1: Convert coordinates
    //     List<CloudPoint> globalCoordinates = convertToGlobalCoordinates(localCoordinates, currentPose);
    
    //     // Step 2: Search Landmark
    //     LandMark existingLandmark = findLandmarkById(id);
    
    //     // Step 3: Update or Add Landmark
    //     if (existingLandmark != null) {
    //         updateExistingLandmark(existingLandmark, globalCoordinates);
    //     } else {
    //         createNewLandmark(id, "New Landmark", globalCoordinates);
    //     }
    // }
    
    // //Mapping a list of local points to a global system
    // private List<CloudPoint> convertToGlobalCoordinates(List<CloudPoint> localCoordinates, Pose currentPose) {
    //     List<CloudPoint> globalCoordinates = new ArrayList<>();
    //     for (CloudPoint point : localCoordinates) {
    //         globalCoordinates.add(transformToGlobal(point, currentPose));
    //     }
    //     return globalCoordinates;
    // }
    
    // //Is there an identical Landmark by ID?
    // private LandMark findLandmarkById(String id) {
    //     for (LandMark landmark : landmarks) {
    //         if (landmark.getId().equals(id)) {
    //             return landmark;
    //         }
    //     }
    //     return null; 
    // }

    // //Update for existing Landmark
    // private void updateExistingLandmark(LandMark landmark, List<CloudPoint> newCoordinates) {
    //     List<CloudPoint> existingCoordinates = landmark.getCoordinates();
    //     for (int i = 0; i < existingCoordinates.size(); i++) {
    //         existingCoordinates.get(i).update(newCoordinates.get(i)); 
    //     }
    // }

    // //Creating a new Landmark
    // private void createNewLandmark(String id, String description, List<CloudPoint> globalCoordinates) {
    //     landmarks.add(new LandMark(id, description, globalCoordinates));
    // }
    
    
}
