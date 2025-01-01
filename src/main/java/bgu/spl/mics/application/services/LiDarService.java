package bgu.spl.mics.application.services;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.MicroService;

import java.util.ArrayList;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.MicroService;

//
/**
 * LiDarService is responsible for processing data from the LiDAR sensor and
 * sending TrackedObjectsEvents to the FusionSLAM service.
 * 
 * This service interacts with the LiDarWorkerTracker object to retrieve and process
 * cloud point data and updates the system's StatisticalFolder upon sending its
 * observations.
 * • Responsibilities:
 * o Sends TrackedObjectsEvents (can be multiple events).
 */
public class LiDarService extends MicroService {

    private final LiDarWorkerTracker liDarWorkerTracker;
    private final LiDarDataBase liDarDataBase;
    private final StatisticalFolder statisticalFolder;
    /**
     * Constructor for LiDarService.
     *
     * @param LiDarWorkerTracker A LiDAR Tracker worker object that this service will use to process data.
     */

    public LiDarService(LiDarWorkerTracker LiDarWorkerTracker, LiDarDataBase liDarDataBase, StatisticalFolder statisticalFolder) {
        super("liDar " + LiDarWorkerTracker.getId(), statisticalFolder);
        // TODO Implement this
        this.liDarWorkerTracker = LiDarWorkerTracker;
        this.liDarDataBase = liDarDataBase;
        this.statisticalFolder = statisticalFolder;
    }


    /**
     * Initializes the LiDarService.
     * Registers the service to handle DetectObjectsEvents and TickBroadcasts,
     * and sets up the necessary callbacks for processing data.
     */
    @Override
    protected void initialize() {
        // TODO Implement this
        // Subscribe to TickBroadcast
        subscribeBroadcast(TickBroadcast.class, tick -> {

        });

        // Subscribe to DetectObjectsEvent
        subscribeEvent(DetectObjectsEvent.class, event -> {

        });

        // Subscribe to CrashedBroadcast
        subscribeBroadcast(CrashedBroadcast.class, broadcast -> {

        });

        // Subscribe to TerminatedBroadcast
        subscribeBroadcast(TerminatedBroadcast.class, broadcast -> {

        });

    }
}
