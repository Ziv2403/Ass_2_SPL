package bgu.spl.mics.application.services;

import java.util.ArrayList;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.PoseEvent;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.messages.TrackedObjectsEvent;
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

    private FusionSlam fusionSlam;
    private final StatisticalFolder stats;
    private ArrayList<LandMark> result;
    private int currentTick = 0;
    /**
     * Constructor for FusionSlamService.
     *
     * @param fusionSlam The FusionSLAM object responsible for managing the global map.
     */
    public FusionSlamService(FusionSlam fusionSlam, StatisticalFolder stats) {
        super("FusionSlam");
        this.fusionSlam = fusionSlam;
        this.stats = stats;
        this.result = new ArrayList<LandMark>();

    }

    /**
     * Initializes the FusionSlamService.
     * Registers the service to handle TrackedObjectsEvents, PoseEvents, and TickBroadcasts,
     * and sets up callbacks for updating the global map.
     */
    @Override
    protected void initialize() {
        //     subscribeBroadcast(TickBroadcast.class, tick -> {
        //         currentTick = tick.getTick();
        //     });

        //     subscribeEvent(TrackedObjectsEvent.class, event -> {
        //         fusionSlam.add

        //     });

        //     subscribeEvent(PoseEvent.class, event -> {
        //         fusionSlam.addPose(event.getPose());
        //     });

        //     subscribeBroadcast(, null);
    }
}
