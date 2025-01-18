package bgu.spl.mics.application.messages;

import bgu.spl.mics.Broadcast;

/**
 * TickBroadcast
 * •
 * Sent by: TimeService
 * •
 * Used for: Timing message publications and processing.
 */
public class TickBroadcast implements Broadcast{
    // --------------------- fields -------------------------
    private final int currentTick;
    // --------------------- constructor --------------------
    public TickBroadcast(int tick) {this.currentTick = tick;}

    public TickBroadcast() {
        this.currentTick = 0;
    }

    // --------------------- Methods ------------------------
    public int getTick() {return currentTick;}
}
