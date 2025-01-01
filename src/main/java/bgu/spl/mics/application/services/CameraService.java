package bgu.spl.mics.application.services;

import java.util.List;

import bgu.spl.mics.MicroService;
import bgu.spl.mics.application.messages.CrashedBroadcast;
import bgu.spl.mics.application.messages.DetectObjectsEvent;
import bgu.spl.mics.application.messages.TerminatedBroadcast;
import bgu.spl.mics.application.messages.TickBroadcast;
import bgu.spl.mics.application.objects.Camera;
import bgu.spl.mics.application.objects.StampedDetectedObjects;
import bgu.spl.mics.application.objects.StatisticalFolder;
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
    private int lastProcessedIndex = 0;
    private final StatisticalFolder stats;
    private int currentTick = 0;

    /**
     * Constructor for CameraService.
     *
     * @param camera The Camera object that this service will use to detect objects.
     */
    public CameraService(Camera camera, StatisticalFolder stats) {
        super("CameraService");
        this.camera = camera;
        this.stats = stats;
    }

    /**
     * Initializes the CameraService.
     * Registers the service to handle TickBroadcasts and sets up callbacks for sending
     * DetectObjectsEvents.
     */
    @Override
    protected void initialize() {
        subscribeBroadcast(TickBroadcast.class, this::handleTickBroadcast);
        subscribeBroadcast(TerminatedBroadcast.class, terminated -> terminate());
    }

    //Added Methods

    private void handleTickBroadcast(TickBroadcast tick) {
        this.setCurrentTick(tick.getTick()); // Update current tick
        handleCameraStatus();
    }

    private void handleCameraStatus() {
        switch (camera.getStatus()) {
            case UP:
                //Check if this is the frequency cycle time for the camera to send detectedObject
                if (this.getCurrentTick() % camera.getFrequency() == 0){
                    //Sending all detectedObjects received within the desired time range
                     processDetectedObjects();
                }
                break;
            case DOWN:
                System.err.println(getName() + ": Camera is down, skipping processing.");
                break;
            case ERROR:
                System.err.println(getName() + ": Camera encountered an error, sending CrashedBroadcast.");
                sendBroadcast(new CrashedBroadcast(getName()));
                terminate();
                break;
        }
    }

    private void processDetectedObjects(){
        List<StampedDetectedObjects> detectedObjects = camera.getDetectedObjectsList();

        for(int i = lastProcessedIndex; i < detectedObjects.size(); i++){
            StampedDetectedObjects detected = detectedObjects.get(i);

            //Sending events for objects with the correct times in the requested range
            if (detected.getTime() >= this.getCurrentTick() && detected.getTime() < this.getCurrentTick() + camera.getFrequency()) {
                DetectObjectsEvent newEvent = new DetectObjectsEvent(detected, camera.getId(), "Detected objects at tick " + this.getCurrentTick()); //not sure about the description!!
                sendEvent(newEvent);

                //Update the number of detectedObjects in the statistics folder
                stats.incrementDetectedObjects(detected.getDetectedObjectsList().size());
            }
            else if (detected.getTime() >= this.getCurrentTick() + camera.getFrequency()){
                break;
            }
            lastProcessedIndex = i+1;
        }
    }

    public int getCurrentTick() {return currentTick;}
    public void setCurrentTick(int tick) {this.currentTick = tick; }

}
