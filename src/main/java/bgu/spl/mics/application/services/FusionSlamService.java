package bgu.spl.mics.application.services;

import java.util.ArrayList;
import java.util.List;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.FusionSlam;
import bgu.spl.mics.application.objects.LandMark;
import bgu.spl.mics.application.objects.Pose;
import bgu.spl.mics.application.objects.StatisticalFolder;
import bgu.spl.mics.application.objects.TrackedObject;
//
/**
 * FusionSlamService integrates data from multiple sensors to build and update
 * the robot's global map.
 *
 * This service receives TrackedObjectsEvents from LiDAR workers and PoseEvents from the PoseService,
 * transforming and updating the map with new landmarks.
 */
public class FusionSlamService extends MicroService {

    private final FusionSlam fusionSlam;
    private List<LandMark> result;
    private int currentTick;

    /**
     * Constructor for FusionSlamService.
     *
     * @param fusionSlam The FusionSLAM object responsible for managing the global map.
     */
    public FusionSlamService(FusionSlam fusionSlam, StatisticalFolder statisticalFolder) {
        super("FusionSlam", statisticalFolder);
        this.fusionSlam = FusionSlam.getInstance();
        this.result = new ArrayList<>();
        this.currentTick = 0;
    }

    /**
     * Initializes the FusionSlamService.
     * Registers the service to handle TrackedObjectsEvents, PoseEvents, and TickBroadcasts,
     * and sets up callbacks for updating the global map.
     */
    @Override
    protected void initialize() {

        subscribeEvent(TrackedObjectsEvent.class, event -> {
            List<TrackedObject> trackedObjects = event.getTrackedObjects();
            int addedLandmarks = fusionSlam.processTrackedObjects(trackedObjects);

            // Update the statistical folder with new landmarks
            for (LandMark landMark : fusionSlam.getLandMarkList()) {
                statisticalFolder.addOrUpdateLandMark(landMark.getId(), landMark);
            }
            statisticalFolder.incrementLandmarks(addedLandmarks);

            System.out.println("[FusionSlamService -> initialize()]: TrackedObjectsEvent processed: " + addedLandmarks + " new landmarks added.");

        });

        subscribeEvent(PoseEvent.class, event -> {
            Pose pose = event.getPose();
            fusionSlam.addPose(pose);
            //statisticalFolder.incrementSystemRuntime();
            System.out.println("[FusionSlamService -> initialize()]: PoseEvent processed: New pose added.");
        });

        // Subscribe to CrashedBroadcast
        subscribeBroadcast(CrashedBroadcast.class, broadcast -> {
            terminate();
        });

        // CHECK AGAIN
        // Subscribe to TerminatedBroadcast
        subscribeBroadcast(TerminatedBroadcast.class, terminate -> {
            terminate();
        });

    }


    /**
     * Queries the list of landmarks from FusionSlam.
     *
     * @return A list of landmarks in the global map.
     */
    public List<LandMark> queryLandmarks() {
        return fusionSlam.getLandMarkList();
    }

    /**
     * Retrieves the number of landmarks currently in the map.
     *
     * @return The total count of landmarks.
     */
    public int getLandmarkCount() {
        return fusionSlam.getLandMarkList().size();
    }

    /**
     * Retrieves a specific landmark by ID.
     *
     * @param id The unique identifier of the landmark.
     * @return The landmark with the given ID, or null if not found.
     */
    public LandMark queryLandmarkById(String id) {
        return fusionSlam.getLandMarkList().stream()
                .filter(landmark -> landmark.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    /**
     * Returns a string representation of the FusionSlamService.
     *
     * @return A string representation of the service.
     */
    @Override
    public String toString() {
        return "FusionSlamService{" +
                "fusionSlam=" + fusionSlam +
                ", statisticalFolder=" + statisticalFolder +
                '}';
    }
}
