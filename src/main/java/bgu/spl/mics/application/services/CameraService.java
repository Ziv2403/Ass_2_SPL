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
    private List<TickBroadcast> processedTicks = new ArrayList<>();

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
        // Subscribe to TickBroadcast
        subscribeBroadcast(TickBroadcast.class, tick -> {
            System.out.println(getName() + " processing TickBroadcast for tick: " + tick.getTick());
            processedTicks.add(tick);
            int currentTick = tick.getTick();
            processPendingEvents(currentTick);

            if (cameraData != null) {
                for (StampedDetectedObjects event : cameraData) {
                    if (event.getTime() == currentTick) {
                        String description = checkForError(event);
                        if (!description.isEmpty()) {
                            sendBroadcast(new CrashedBroadcast(Thread.currentThread().getName(), description));
                            camera.setStatus(STATUS.ERROR);
                            terminate();
                            sendBroadcast(new TerminatedBroadcast(getName()));
                            return;
                        }

                        int scheduledTime = event.getTime() + camera.getFrequency();
                        if (scheduledTime == currentTick) {
                            sendEvent(new DetectObjectsEvent(event, camera.getId()));
                            statisticalFolder.incrementDetectedObjects(event.getDetectedObjectsList().size());
                        } else {
                            pendingEvents.putIfAbsent(event, scheduledTime); // Store the event for later processing
                        }
                    }
                }
            }
        });

        // Subscribe to CrashedBroadcast
        subscribeBroadcast(CrashedBroadcast.class, broadcast -> {
            if (camera.getStatus() != STATUS.ERROR) { camera.setStatus(STATUS.DOWN); }
            terminate();
            System.out.println(getName() + " received CrashedBroadcast and is terminating.");

        });

        // CHECK AGAIN
        // Subscribe to TerminatedBroadcast
        subscribeBroadcast(TerminatedBroadcast.class, terminate -> {
            camera.setStatus(STATUS.DOWN);
            terminate();
            System.out.println(getName() + " received TerminatedBroadcast. Terminating...");
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

    private String checkForError(StampedDetectedObjects event) {
        List<DetectedObject> list = event.getDetectedObjectsList();
        for (DetectedObject obj : list) {
            if (obj.getId().equals("ERROR")) {
                return obj.getDescription();
            }
        }
        return "";
    }
}

