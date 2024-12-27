package bgu.spl.mics.application.messages;

/**
 * TrackedObjectsEvent
 * •
 * Sent by: a LiDar worker
 * •
 * Handled by: Fusion-SLAM
 * •
 * Details:
 * o
 * Includes a list of TrackedObjects.
 * o
 * Upon receiving this event, Fusion:
 * ▪
 * Transforms the cloud points to the charging station's coordinate system using the current pose.
 * ▪
 * Checks if the object is new or previously detected:
 * •
 * If new, add it to the map.
 * •
 * If previously detected, updates measurements by averaging with previous data.
 */
public class TrackedObjectsEvent {
}
