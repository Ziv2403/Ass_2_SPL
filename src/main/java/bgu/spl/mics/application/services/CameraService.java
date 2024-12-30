package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.*;

import java.util.ArrayList;
import java.util.List;

//
/**
 * CameraService is responsible for processing data from the camera and
 * sending DetectObjectsEvents to LiDAR workers.
 *
 * This service interacts with the Camera object to detect objects and updates
 * the system's StatisticalFolder upon sending its observations.
 */
public class CameraService extends MicroService {
    private final Camera camera;
    private final List<StampedDetectedObjects> cameraData;

    /**
     * Constructor for CameraService.
     *
     * @param camera The Camera object that this service will use to detect objects.
     */
    public CameraService(Camera camera) {
        super(camera.getCameraKey());
        this.camera = camera;
        this.cameraData = new ArrayList<>();
    }

    public CameraService(Camera camera, List<StampedDetectedObjects> cameraData) {
        super(camera.getCameraKey());
        this.camera = camera;
        this.cameraData = cameraData;
    }

    /**
     * Initializes the CameraService.
     * Registers the service to handle TickBroadcasts and sets up callbacks for sending
     * DetectObjectsEvents.
     */
    @Override
    protected void initialize() {
        // TODO Implement this
        // Subscribe to TickBroadcast
        subscribeBroadcast(TickBroadcast.class, tick -> {

            if (cameraData != null) {
                // Check for objects to detect at the current tick
                for (StampedDetectedObjects event : cameraData) {
                    if (event.getTime() == tick.getTick()) {
                        // Publish DetectObjectsEvent for the detected objects
                        sendEvent(new DetectObjectsEvent(event, camera.getId()));
                        //System.out.println(getName() + " sent DetectObjectsEvent for time " + event.getTime());
                    }
                }
            }
        });

        // CHECK AGAIN
        // Subscribe to TerminatedBroadcast
        subscribeBroadcast(TerminatedBroadcast.class, terminate -> {
            System.out.println(getName() + " received TerminatedBroadcast. Terminating...");
            terminate();
        });

        System.out.println(getName() + " initialized with camera: " + camera.getCameraKey());
    }
}

