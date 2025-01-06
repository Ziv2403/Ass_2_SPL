package bgu.spl.mics.application.objects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    /** 
    * Singleton instance holder
    */
    private static class FusionSlamHolder {
        private static final FusionSlam INSTANCE = new FusionSlam();
    }

    /**
     * Private constructor to enforce Singleton pattern.
     *
     * @post {@code landmarks.isEmpty() == true}
     * @post {@code poses.isEmpty() == true}
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

    /**
     * @return The list of global landmarks.
     */
    public List<LandMark> getLandMarkList() { return landmarks;}

    /**
     * @return The list of robot poses.
     */
    public List<Pose> getPoseList() {return poses;}

    //           --------- Pose Management ---------
   
    /**
     * Adds a new pose to the list of robot poses.
     *
     * @param pose The pose to add.
     * @pre {@code pose != null}
     * @post {@code poses.contains(pose)}
     */
    public void addPose(Pose pose){
        this.poses.add(pose);
    }

    //         --------- Coordinate Transformation ---------
    /**
     * Converts a single point from the local coordinate system to the global coordinate system.
     *
     * @param localPoint The point in the local coordinate system.
     * @param pose The robot's pose at the time the point was detected.
     * @return The point in the global coordinate system.
     * @pre {@code localPoint != null && pose != null}
     */
    public CloudPoint convertPoint(CloudPoint localPoint, Pose pose) {
        double yawRad = toRadians(pose.getYaw());
        double xGlobal = cos(yawRad) * localPoint.getX() - sin(yawRad) * localPoint.getY() + pose.getX();
        double yGlobal = sin(yawRad) * localPoint.getX() + cos(yawRad) * localPoint.getY() + pose.getY();
        return new CloudPoint(xGlobal, yGlobal);
    }

    /**
     * Converts a list of points from the local coordinate system to the global coordinate system.
     *
     * @param localCoordinates List of points in the local coordinate system.
     * @param currentPose The robot's pose at the time the points were detected.
     * @return List of points in the global coordinate system.
     * @pre {@code localCoordinates != null && currentPose != null}
     * @post {@code globalCoordinates.size() == localCoordinates.size()}
     */
    private List<CloudPoint> convertToGlobalCoordinates(List<CloudPoint> localCoordinates, Pose currentPose) {
        List<CloudPoint> globalCoordinates = new ArrayList<>();
        for (CloudPoint point : localCoordinates) {
            globalCoordinates.add(convertPoint(point, currentPose));
        }
        return globalCoordinates;
    }
    
    //             --------- Landmark Management ---------
    /**
    * Processes a list of tracked objects and integrates them into the global map.
    * For each tracked object, it finds the corresponding robot pose at the time the object was tracked,
    * transforms the object's coordinates into the global coordinate system, and either updates
    * an existing landmark or creates a new one.
    *
    * @param trackedObjects A list of tracked objects to process.
    * @return The number of new landmarks added to the global map.
    * @pre {@code trackedObjects != null}
    */
    public int createLandMarks(List<TrackedObject> trackedObjects) {
        int newLandmarks = 0; //landMarks counter
        for (TrackedObject tracked : trackedObjects) {
            Pose matchingPose = poses.stream() 
                    .filter(p -> p.getTime() == tracked.getTime())
                    .findFirst()
                    .orElse(null);
            if (matchingPose != null) {
                boolean isNewLandmark = handleLandmark(tracked, matchingPose);
                if (isNewLandmark) {
                    newLandmarks++;
                }
            }
        }
        return newLandmarks;
    }

    /**
     * Updates an existing landmark or creates a new one if it doesn't exist.
     * Converts the given local coordinates to global coordinates using the current pose.
     *
     * @param tracked TrackedObject to be process into LandMark.
     * @param pose The robot's pose at the time of detection.
     * @return True if a new landmark was added; false if an existing landmark was updated.
     * @pre {@code tracked != null && pose != null}
     */
    private boolean handleLandmark(TrackedObject tracked, Pose pose) {
        List<CloudPoint> globalCoordinates = convertToGlobalCoordinates(tracked.convertToList(tracked.getCoordinates()), pose);
        LandMark existingLandmark = landmarks.stream()
                .filter(l -> l.getId().equals(tracked.getId()))
                .findFirst()
                .orElse(null);

        if (existingLandmark == null) {
            landmarks.add(new LandMark(tracked.getId(), tracked.getDescription(), globalCoordinates));
            return true; // New landmark added
        } else {
            updateExistingLandmark(existingLandmark, globalCoordinates);
            return false; // Landmark already exists
        }
    }


    /**
     * Updates an existing landmark with new global coordinates by averaging them.
     *
     * @param landmark The existing landmark to update.
     * @param newCoordinates The new global coordinates to integrate.
     * @pre {@code landmark != null && newCoordinates != null}
     */
    private void updateExistingLandmark(LandMark landmark, List<CloudPoint> newCoordinates) {
//        System.out.println("[FusionSlam - updateExistingLandmark] Updating LandMark: " + landmark.getId()); //DEBUG
//        System.out.println("[FusionSlam - updateExistingLandmark] Existing points: " + landmark.getCloudPoints());//DEBUG
//        System.out.println("[FusionSlam - updateExistingLandmark] New points: " + newCoordinates);//DEBUG
    
        landmark.updateCloudPoints(newCoordinates);

//        System.out.println("[FusionSlam - updateExistingLandmark] Updated points: " + landmark.getCloudPoints());//DEBUG
        // List<CloudPoint> existingCoordinates = landmark.getCloudPoints();

        // // Loop over the existing and new points to calculate an average
        // int minSize = Math.min(existingCoordinates.size(), newCoordinates.size());

        // for (int i = 0; i <  minSize; i++) {
        //     CloudPoint oldPoint = existingCoordinates.get(i);
        //     CloudPoint newPoint = newCoordinates.get(i);

        //     double avgX = (oldPoint.getX() + newPoint.getX()) / 2.0;
        //     double avgY = (oldPoint.getY() + newPoint.getY()) / 2.0;
        //     oldPoint.setCloudPoint(new CloudPoint(avgX, avgY));
        // }

        // for (int i = minSize; i < newCoordinates.size(); i++) {
        //     existingCoordinates.add(newCoordinates.get(i));
        // }

        // landmark.setCloudPoints(existingCoordinates);
    }


    /**
     * Generates a map of landmarks in a format ready for JSON output.
     *
     * @return A map containing landmarks with their details.
     */
    public Map<String, Object> generateGlobalMap() {
        Map<String, Object> globalMap = new HashMap<>();
        for (LandMark landmark : landmarks) {
            Map<String, Object> landmarkDetails = new HashMap<>();
            landmarkDetails.put("id", landmark.getId());
            landmarkDetails.put("description", landmark.getDescription());
            List<Map<String, Double>> coordinates = new ArrayList<>();
            for (CloudPoint point : landmark.getCloudPoints()) {
                Map<String, Double> pointMap = new HashMap<>();
                pointMap.put("x", point.getX());
                pointMap.put("y", point.getY());
                coordinates.add(pointMap);
            }
            landmarkDetails.put("coordinates", coordinates);
            globalMap.put(landmark.getId(), landmarkDetails);
        }
        return globalMap;
    }

    @Override
    public String toString() {
        return "FusionSlam{" +
                "landmarks=" + landmarks +
                ", poses=" + poses +
                '}';
    }



    // /**
    //  * Generates a map containing simulation output data, including runtime statistics, 
    //  * detected and tracked object counts, and landmark details.
    //  *
    //  * @param systemRuntime The total runtime of the system in ticks.
    //  * @param numDetectedObjects The total number of detected objects.
    //  * @param numTrackedObjects The total number of tracked objects.
    //  * @return A map representing the output data with statistics and landmarks.
    //  */
    // public List<String> generateOutput() {
    //     // Map<String, Object> output = new HashMap<>();

    //     // output.put("systemRuntime", systemRuntime);
    //     // output.put("numDetectedObjects", numDetectedObjects);
    //     // output.put("numTrackedObjects", numTrackedObjects);
    //     // output.put("numLandmarks", landmarks.size());

    //     List<String> landmarksStringList = new ArrayList<>();
    //     for (LandMark landmark : landmarks) {
    //         System.out.println("landMarks incloud:" + landmark.toString()); //DEBUGGGGGGGG
    //         // String landmarkDetails = landmark.toString();
    //         landmarksStringList.add(landmark.toString());
    //     //     landmarkDetails.put("description", landmark.getDescription());
    //     //     landmarkDetails.put("coordinates", landmark.getCloudPoints());
    //     //     landmarksMap.put(landmark.getId(), landmarkDetails);
    //     }
    //     // output.put("landMarks", landmarksMap);

    //     return landmarksStringList;
    // }
    
}






