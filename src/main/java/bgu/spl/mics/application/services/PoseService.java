package bgu.spl.mics.application.services;
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

    /**
     * Constructor for PoseService.
     *
     * @param gpsimu The GPSIMU object that provides the robot's pose data.
     */
    public PoseService(GPSIMU gpsimu) {
        super("PoseService");
        // TODO Implement this
    }

    public PoseService(GPSIMU gpsimu, StatisticalFolder statisticalFolder) {
        super("PoseService", statisticalFolder);
        // TODO Implement this
    }


    /**
     * Initializes the PoseService.
     * Subscribes to TickBroadcast and sends PoseEvents at every tick based on the current pose.
     */
    @Override
    protected void initialize() {
        // TODO Implement this
    }
}
