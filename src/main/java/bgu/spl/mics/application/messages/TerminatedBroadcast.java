package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

/**
 * TerminatedBroadcast
 * •
 * Sent by all the sensors
 * •
 * Used for: notifying all other services that the service sending the broadcast will terminate.
 */
public class TerminatedBroadcast implements Broadcast{
// --------------------- fields --------------------
    private String microServiceName;

// --------------------- constructor --------------------
    public TerminatedBroadcast(String microServiceName){
        this.microServiceName = microServiceName;
    }

// --------------------- Methods --------------------
//Getters
    public String getTerminatedName() {return microServiceName;}
}
