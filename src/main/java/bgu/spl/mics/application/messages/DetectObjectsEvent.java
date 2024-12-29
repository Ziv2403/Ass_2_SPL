package bgu.spl.mics.application.messages;

/**
 * DetectObjectsEvent
 * •
 * Sent by: Camera
 * •
 * Handled by: a LiDar Worker
 * •
 * Details:
 * o
 * Includes DetectedObjects.
 * o
 * The Camera send DetectedObjectsEvent of the Objects with time T for all the subscribed Lidar workers to this event at time T, and one of them deals with a single event.
 * 19
 * o
 * The LiDar gets the X’s,Y’s coordinates from the DataBase of them and sends a new TrackedObjectsEvent to the Fusion.
 * o
 * After the LiDar Worker completes the event, it saves the coordinates in the lastObjects variable in DataBase and sends True value to the Camera.
 */
public class DetectObjectsEvent {

}
