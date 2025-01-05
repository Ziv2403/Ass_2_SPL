package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

/**
 * CrashedBroadcast
 * •
 * Sent by all the sensors
 * •
 * Used for: notifying all other services that the sender service has crashed.
 */
public class CrashedBroadcast implements Broadcast{
    // --------------------- fields -------------------------
    private final String componentType;
    private final String errDescription;


    // --------------------- constructor --------------------

    /**
     * Constructs a CrashedBroadcast message.
     *
     * @param componentType The type of the component (e.g., Camera, LiDAR).
     * @param errDescription A description of the error.
     * @pre {@code componentType != null  && errDescription != null}
     * @post {@code this.componentType.equals(componentType) && this.errDescription.equals(errDescription)}
     */
    public CrashedBroadcast(String componentType, String errDescription){
        this.componentType = componentType;
        this.errDescription = errDescription;    
    }

    // --------------------- Methods ------------------------

    /**
     * @return The type of the component (e.g., Camera, LiDAR).
     */
    public String getComponentType() {return componentType;}

    /**
     * @return A description of the error.
     */
    public String getErrorDescription() {return errDescription;}

    /**
     * Generates a string representation of the CrashedBroadcast object.
     *
     * @return A string in JSON format that represents the CrashedBroadcast.
     */
    @Override
    public String toString() {
        return "{\n" +
                "  \"error\": \"" + errDescription + "\",\n" +
                "  \"componentType\": \"" + componentType + "\"\n" +
                "}";
    }
}