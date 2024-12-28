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
    private String errSource;

// --------------------- constructor --------------------
    public CrashedBroadcast(String errSource){
        this.errSource = errSource;
    }
// --------------------- Methods ------------------------
//Getters
    public String getErrorSource() {return errSource;}
}
