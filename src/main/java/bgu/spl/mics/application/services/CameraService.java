package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.*;
import bgu.spl.mics.application.objects.*;
import bgu.spl.mics.application.utils.ErrorLogger;

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
    // --------------------- fields -------------------------
    private final Camera camera;
    private final List<StampedDetectedObjects> cameraData;
    private final Map<StampedDetectedObjects, Integer> pendingEvents = new HashMap<>();

// --------------------- constructors -------------------

    /**
     * Constructor for CameraService with an empty data list.
     *
     * @param camera The Camera object that this service will use to detect objects.
     * @param statisticalFolder The StatisticalFolder for tracking system statistics.
     */
    public CameraService(Camera camera, StatisticalFolder statisticalFolder) {
        super(camera.getCameraKey(), statisticalFolder);
        this.camera = camera;
        this.cameraData = new ArrayList<>();
    }


    /**
     * Constructor for CameraService with preloaded data.
     *
     * @param camera The Camera object that this service will use to detect objects.
     * @param cameraData A list of StampedDetectedObjects representing preloaded camera data.
     * @param statisticalFolder The StatisticalFolder for tracking system statistics.
     */
    public CameraService(Camera camera, List<StampedDetectedObjects> cameraData, StatisticalFolder statisticalFolder) {
        super(camera.getCameraKey(), statisticalFolder);
        this.camera = camera;
        this.cameraData = cameraData;
    }

    // --------------------- initialize ------------------------

    /**
     * Initializes the CameraService.
     * Registers the service to handle TickBroadcasts and sets up callbacks for sending
     * DetectObjectsEvents.
     */
    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class, tick -> {
            int currentTick = tick.getTick();
            // System.out.println("Initialize -> camera_currentTick = " + currentTick); //DEBUG

            // Process pending events
            if (!pendingEvents.isEmpty()) {
                // System.out.println("Initialize -> Process pending events");//DEBUG
                processPendingEvents(currentTick);
            }

            // Process camera data
            if (cameraData != null) {
                // System.out.println(" Initialize -> Process camera data");//DEBUG
                processCameraData(currentTick);
            }

            // Update status if no more events to process
            if (cameraData.isEmpty() && pendingEvents.isEmpty()) {
                // System.out.println("Initialize ->" + camera.getCameraKey() + "status DOWN");//DEBUG
                camera.setStatus(STATUS.DOWN);
                terminate();
            }
        });

        // Subscribe to CrashedBroadcast
        subscribeBroadcast(CrashedBroadcast.class, broadcast -> {

        });

        // Subscribe to TerminatedBroadcast
        subscribeBroadcast(TerminatedBroadcast.class, broadcast ->{
            System.out.println("TerminatedBroadcast received. Ending service...");
            terminate();
        });
    
    }



    // --------------------- Private methods ------------------------

    /**
     * Processes events that are ready for sending at the current tick.
     *
     * @param currentTick The current tick of the simulation.
     * @pre {@code currentTick >= 0}
     * @post {@code pendingEvents.size() <= pendingEvents.size()@pre}
     */
    private void processPendingEvents(int currentTick) {
        Iterator<Map.Entry<StampedDetectedObjects, Integer>> iterator = pendingEvents.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<StampedDetectedObjects, Integer> entry = iterator.next();
            if (currentTick >= entry.getValue()) { //what happend if else? need to wait and stop processing?
                StampedDetectedObjects event = entry.getKey();

                if (event.isContainError() == null) {
                    sendEvent(new DetectObjectsEvent(entry.getKey(), camera.getId()));
                    // System.out.println("ProcessPendingEvent -> send DetectedObjectEvent for: " + entry.getKey() + "cameraID:" + camera.getId()); //Debug
                    statisticalFolder.addCameraFrame(camera.getCameraKey(), event);
                    // System.out.println("ProcessPendingEvent -> lastFrame of" + camera.getCameraKey() + "is: " + event.toString() ); //Debug
                    statisticalFolder.incrementDetectedObjects(entry.getKey().getDetectedObjectsList().size());
                    iterator.remove();
                } else {
                    // System.out.println("ProcessPendingEvent -> Found Error in Stamped" + event.toString() + "detectedError is: " + event.isContainError().toString());//Debug
                    handleError(event, currentTick); // Handle error when time matches
                    iterator.remove();
                    return; // Stop further processing for this tick
                }


            }
        }
    }


    /**
     * Processes camera data for the current tick.
     *
     * @param currentTick The current tick of the simulation.
     * @pre {@code currentTick >= 0}
     * @post {@code cameraData.size() <= cameraData.size()@pre}
     */
    private void processCameraData(int currentTick) {
        Iterator<StampedDetectedObjects> iterator = cameraData.iterator();
        while (iterator.hasNext()) {
            StampedDetectedObjects event = iterator.next();
            if (event.getTime() == currentTick) {

                //not sure if need: check if need to insert into pendingEvents
                if (event.getDetectedObjectsList().isEmpty()) {
                    // System.out.println("processCameraData -> No detected objects for event at time: " + event.getTime()); //DEBUG
                    iterator.remove(); 
                    continue;
                }


                
                //When the currentTick is the desired processing time --> process immediately
                if (currentTick == event.getTime() + camera.getFrequency() && event.isContainError() == null) {
                    // System.out.println("processCameraData -> immediately processing:" + event.toString()); //DEBUG
                    sendEvent(new DetectObjectsEvent(event, camera.getId()));

                    statisticalFolder.incrementDetectedObjects(event.getDetectedObjectsList().size());
                    // System.out.println("processCameraData -> lastFrame of" + camera.getCameraKey() + "is: " + event.toString() ); //Debug
                    statisticalFolder.addCameraFrame(camera.getCameraKey(), event);
                
                //Otherwise, sent for later processing
                } else {
                    int scheduledTime = event.getTime() + camera.getFrequency();
                    // System.out.println("processCameraData -> Insert into waitingList: " + event.toString() + ", currentTime:" + currentTick + "processTime will be: " + scheduledTime); //Debug
                    pendingEvents.put(event, scheduledTime);
                }
                iterator.remove();
            }
        }
    }



    /**
     * Handles an event that contains an error.
     *
     * @param event The event containing the error.
     * @pre {@code containsError(event)}
     * @post {@code camera.getStatus() == STATUS.ERROR}
     */
    private void handleError(StampedDetectedObjects event, int currentTick) {
        camera.setStatus(STATUS.ERROR);

        DetectedObject detectedError = event.isContainError();
        // System.out.println("handlerError -> detectedError:" + detectedError.toString()); //debug

        // System.out.println("handlerError -> write report! at time:" + currentTick); //debug
        ErrorLogger.writeFormattedErrorReport("error_output.json", detectedError.getDescription(), camera.getCameraKey(), statisticalFolder);

        // Send CrashedBroadcast
        sendBroadcast(new CrashedBroadcast(camera.getCameraKey(), "Detected error in event"));

        terminate();
    }


}

