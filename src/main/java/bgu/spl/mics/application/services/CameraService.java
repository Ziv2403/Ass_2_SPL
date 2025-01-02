package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.*;

import java.util.*;

//
/**
 * CameraService is responsible for processing data from the camera and
 * sending DetectObjectsEvents to LiDAR workers.
 *
 * This service interacts with the Camera object to detect objects and updates
 * the system's StatisticalFolder upon sending its observations.
 *
 * • Responsibilities:
 * o Sends DetectObjectsEvents only.
 * o Subscribe to TickBroadcast, TerminatedBroadcast, CrashedBroadcast.
 */
public class CameraService extends MicroService {
    private final Camera camera;
    private final List<StampedDetectedObjects> cameraData;
    private final Map<StampedDetectedObjects, Integer> pendingEvents = new HashMap<>();


    /**
     * Constructor for CameraService.
     *
     * @param camera The Camera object that this service will use to detect objects.
     */
    public CameraService(Camera camera, StatisticalFolder statisticalFolder) {
        super(camera.getCameraKey(), statisticalFolder);
        this.camera = camera;
        this.cameraData = new ArrayList<>();
    }

    public CameraService(Camera camera, List<StampedDetectedObjects> cameraData, StatisticalFolder statisticalFolder) {
        super(camera.getCameraKey(), statisticalFolder);
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
            int currentTick = tick.getTick();
            processPendingEvents(currentTick);

            if (cameraData != null) {
                for (StampedDetectedObjects event : cameraData) {
                    if (event.getTime() == currentTick) {
                        int scheduledTime = event.getTime() + camera.getFrequency();
                        if (scheduledTime == currentTick) {
                            sendEvent(new DetectObjectsEvent(event, camera.getId()));
                            statisticalFolder.incrementDetectedObjects(event.getDetectedObjectsList().size());
                            break;
                        } else {
                            pendingEvents.putIfAbsent(event, scheduledTime); // Store the event for later processing
                            break; // CHECK AGAIN IF NEEDED FOR OPTIMIZATION PURPOSES
                        }
                    }
                }
            }
        });

        // Subscribe to CrashedBroadcast
        subscribeBroadcast(CrashedBroadcast.class, broadcast -> {
            terminate();
        });

        // CHECK AGAIN
        // Subscribe to TerminatedBroadcast
        subscribeBroadcast(TerminatedBroadcast.class, terminate -> {
            System.out.println(getName() + " received TerminatedBroadcast. Terminating...");
            terminate();
        });

    }

    /**
     * Processes events that are ready for sending at the current tick.
     *
     * @param currentTick The current tick of the simulation.
     */
    private void processPendingEvents(int currentTick) {
        Iterator<Map.Entry<StampedDetectedObjects, Integer>> iterator = pendingEvents.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<StampedDetectedObjects, Integer> entry = iterator.next();
            if (currentTick >= entry.getValue()) {
                sendEvent(new DetectObjectsEvent(entry.getKey(), camera.getId()));
                statisticalFolder.incrementDetectedObjects(entry.getKey().getDetectedObjectsList().size());
                iterator.remove();
            }
        }
    }
}

