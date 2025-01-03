package bgu.spl.mics.application.services;

import java.util.ArrayList;
import java.util.List;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.FusionSlam;
import bgu.spl.mics.application.objects.LandMark;
import bgu.spl.mics.application.objects.StatisticalFolder;
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
        this.fusionSlam = fusionSlam;
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
        subscribeBroadcast(TickBroadcast.class, tick -> {
            currentTick = tick.getTick();
        });

        subscribeEvent(TrackedObjectsEvent.class, event -> {
            int numOfLandMarks = fusionSlam.getLandMarkList().size();
            fusionSlam.addTrackedObjects(event.getTrackedObjects());

            int addedLandmarks = fusionSlam.getLandMarkList().size() - numOfLandMarks;
            statisticalFolder.incrementLandmarks(addedLandmarks);
        });

        subscribeEvent(PoseEvent.class, event -> {
            fusionSlam.addPose(event.getPose());
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
}
