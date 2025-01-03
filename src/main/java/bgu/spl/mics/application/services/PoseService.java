package bgu.spl.mics.application.services;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.MicroService;
//
/**
 * PoseService is responsible for maintaining the robot's current pose (position and orientation)
 * and broadcasting PoseEvents at every tick.
 * •
 * Responsibilities:
 * o
 * Holds the robot's coordinates at each tick.
 * o
 * Sends PoseEvents.
 * Subscribes to TickBroadcast, CrashedBroadcast, TerminatedBroadcast.
 */
public class PoseService extends MicroService {
    // --------------------- fields -------------------------
    private final GPSIMU gpsimu;

    // --------------------- constructors -------------------

    /**
     * Constructor for PoseService.
     *
     * @param gpsimu The GPSIMU object that provides the robot's pose data.
     */
    public PoseService(GPSIMU gpsimu) {
        super("PoseService");
        this.gpsimu = gpsimu;
    }

    /**
     * Constructor for PoseService.
     *
     * @param gpsimu The GPSIMU object that provides the robot's pose data.
     * @param statisticalFolder The StatisticalFolder for tracking system statistics.
     */
    public PoseService(GPSIMU gpsimu, StatisticalFolder statisticalFolder) {
        super("PoseService", statisticalFolder);
        this.gpsimu = gpsimu;
    }

    // --------------------- initialize ------------------------

    /**
     * Initializes the PoseService.
     * Subscribes to TickBroadcast and sends PoseEvents at every tick based on the current pose.
     */
    @Override
    protected void initialize() {
        // Subscribe to TickBroadcast
        subscribeBroadcast(TickBroadcast.class, tick -> {
            gpsimu.setCurrentTick(tick.getTick());
            Pose pose = gpsimu.getPoseAtTick(); // Get pose from GPSIMU
            if (pose != null) {
                // Send PoseEvent
                sendEvent(new PoseEvent(pose));
            }
        });

        // Subscribe to CrashedBroadcast
        subscribeBroadcast(CrashedBroadcast.class, broadcast -> {
            terminate();
        });

        // Subscribe to CrashedBroadcast
        subscribeBroadcast(TerminatedBroadcast.class, broadcast -> {
            terminate();
        });

    }
}
