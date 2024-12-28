package bgu.spl.mics.application.services;

import bgu.spl.mics.MicroService;

/**
 * FusionSlamService
 * •
 * Responsibilities:
 * o
 * Does not send events. o Subscribes to TickBroadcast, TrackedObjectsEvent, PoseEvent, TerminatedBroadcast, CrashedBroadcast.
 * o
 * Manages the environmental map by processing tracked objects.
 */
public class FusionSlamService extends MicroService
{
}
