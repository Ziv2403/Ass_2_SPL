package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;

/**
 * TimeService
 * • Purpose: Global system timer handling clock ticks.
 * • Responsibilities:
 * o Counts clock ticks since initialization.
 * o Sends TickBroadcast messages at each tick.
 * o Receives speed (tick duration in milliseconds) and duration (number of ticks before
 * termination) as constructor arguments.
 * o Stops sending TickBroadcast messages after duration ticks.
 * o Signals termination of the process; do not wait for all events to finish.
 * o Note: Ensure the event loop is not blocked.
 * o Note: If this service reaches `duration` ticks the system should terminate.
 */
public class TimeService extends MicroService {

    // --------------------- constructor --------------------

    // --------------------- methods --------------------
    protected void initialize() {

    }
}
