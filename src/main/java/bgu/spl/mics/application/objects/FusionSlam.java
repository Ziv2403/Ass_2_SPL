package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.List;
//import java.util.function.Function;

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

    /**
     * Private constructor to enforce Singleton pattern.
     */
    private FusionSlam(){
        this.landmarks =new ArrayList<>();
        this.poses = new ArrayList<>();
    }

    /**
     * Provides access to the single instance of FusionSlam.
     *
     * @return The singleton instance of FusionSlam.
     */
    public static FusionSlam getInstance(){
        return FusionSlamHolder.INSTANCE;
    }

    // --------------------- methods --------------------
//Getters
    public List<LandMark> getLandMarkList() { return landmarks;}
    public List<Pose> getPoseList() {return poses;}

//Adders
    // public void addLandmark (LandMark landmark){
    //     this.landmarks.add(landmark);
    // }

    /**
     * Adds a new pose to the list of robot poses.
     *
     * @param pose The pose to add.
     */
    public void addPose(Pose pose){
        this.poses.add(pose);
    }


    /**
     * Converts a single point from the local coordinate system to the global coordinate system.
     *
     * @param localPoint The point in the local coordinate system.
     * @param pose The robot's pose at the time the point was detected.
     * @return The point in the global coordinate system.
     */
    public CloudPoint convertPoint(CloudPoint localPoint, Pose pose) {
        double yawRad = toRadians(pose.getYaw());
        double xGlobal = cos(yawRad) * localPoint.getX() - sin(yawRad) * localPoint.getY() + pose.getX();
        double yGlobal = sin(yawRad) * localPoint.getX() + cos(yawRad) * localPoint.getY() + pose.getY();
        return new CloudPoint( xGlobal, yGlobal);
    }



    public void addTrackedObjects(List<TrackedObject> trackedObjects) {
        for (TrackedObject tracked : trackedObjects) {
            Pose matchingPose = poses.stream()
                                      .filter(p -> p.getTime() == tracked.getTime())
                                      .findFirst()
                                      .orElse(null);
            if (matchingPose != null) {
                handleLandmark(tracked, matchingPose);
            }
        }
    }
    /**
     * Updates an existing landmark or creates a new one if it doesn't exist.
     * Converts the given local coordinates to global coordinates using the current pose.
     *
     * @param currentLandMark Landmark to be updated.
     * @param currentPose The robot's pose at the time of detection.
     */
    private void handleLandmark(TrackedObject tracked, Pose pose) {
        List<CloudPoint> globalCoordinates = convertToGlobalCoordinates(tracked.convertToList(tracked.getCoordinates()), pose);
        LandMark existingLandmark = landmarks.stream()
                                             .filter(l -> l.getId().equals(tracked.getId()))
                                             .findFirst()
                                             .orElse(null);

        if (existingLandmark == null) {
            // Create a new landmark
            landmarks.add(new LandMark(tracked.getId(), tracked.getDescription(), globalCoordinates));
        } else {
            // Update the existing landmark
            updateExistingLandmark(existingLandmark, globalCoordinates);
        }
    }


    /**
     * Converts a list of points from the local coordinate system to the global coordinate system.
     *
     * @param localCoordinates List of points in the local coordinate system.
     * @param currentPose The robot's pose at the time the points were detected.
     * @return List of points in the global coordinate system.
     */
    private List<CloudPoint> convertToGlobalCoordinates(List<CloudPoint> localCoordinates, Pose currentPose) {
        List<CloudPoint> globalCoordinates = new ArrayList<>();
        for (CloudPoint point : localCoordinates) {
            globalCoordinates.add(convertPoint(point, currentPose));
        }
        return globalCoordinates;
    }



    /**
     * Updates an existing landmark with new global coordinates by averaging them.
     *
     * @param landmark The existing landmark to update.
     * @param newCoordinates The new global coordinates to integrate.
     */
    private void updateExistingLandmark(LandMark landmark, List<CloudPoint> newCoordinates) {
        List<CloudPoint> existingCoordinates = landmark.getCloudPoints();
        for (int i = 0; i < existingCoordinates.size(); i++) {
            existingCoordinates.get(i).update(newCoordinates.get(i));
        }
    }

}





    /**
     * Creates a new landmark with the given ID, description, and global coordinates.
     *
     * @param id The ID of the new landmark.
     * @param description The description of the landmark.
     * @param globalCoordinates List of global coordinates for the landmark.
     */
    // private void createNewLandmark(TrackedObject tracked) {
    //     LandMark newLandMark = new LandMark(tracked.getId(), tracked.getDescription(), tracked.getCoordinates().co);
    //     landmarks.add(newLandMark);
    // }

    // public void addTrackedObject(List<TrackedObject> trackedObjects) {
    //     for(TrackedObject tracked : trackedObjects){
    //         createNewLandmark(tracked);
    //     }
        
    // }


        // private void handleLandmark(TrackedObject tracked, Pose currentPose) {

    //     // Step 1: Convert coordinates
    //     List<CloudPoint> globalCoordinates = convertToGlobalCoordinates(tracked.convertToList(tracked.getCoordinates()), currentPose);

    //     // Step 2: Search Landmark
    //     boolean isAlreadyExist = landmarks.contains(tracked);


    //     // Step 3: Update or Add Landmark
    //     if ( ) {
    //         LandMark existingLandmark = landmarks.get(indexLandmark);
    //         updateExistingLandmark(existingLandmark, globalCoordinates);
    //     } else {
            
    //         // LandMark newLandMark = new LandMark(null, null, globalCoordinates)
    //         // createNewLandmark(currentLandMark.getId(), "New Landmark", globalCoordinates);//NEED TO CHECK ABOUT THE DESCRIPTION!!!
    //     }
    // }




//}
