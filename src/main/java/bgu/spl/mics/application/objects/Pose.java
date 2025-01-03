package bgu.spl.mics.application.objects;

/**
 * Represents the robot's pose (position and orientation) in the environment.
 * Includes x, y coordinates and the yaw angle relative to a global coordinate system.
 */
public class Pose {
// --------------------- fields -------------------------
    private final float x;
    private final float y;
    private final float yaw;
    private final int time;
// --------------------- constructor --------------------

    /**
     * Constructs a Pose object with the specified position, orientation, and time.
     *
     * @param x The x-coordinate of the position.
     * @param y The y-coordinate of the position.
     * @param yaw The yaw angle (orientation) in degrees.
     * @param time The time at which the pose is recorded.
     * @post {@code this.x == x}
     * @post {@code this.y == y}
     * @post {@code this.yaw == yaw}
     * @post {@code this.time == time}
     */
    public Pose(float x, float y, float yaw, int time) {
        this.x = x;
        this.y = y;
        this.yaw = yaw;
        this.time = time;
}
// --------------------- methods ------------------------

    /**
     * @return The x-coordinate of the robot's position.
     */
    public float getX() {return x;}
    
    
    /**
     * @return The y-coordinate of the robot's position.
     */
    public float getY() {return y;}
    
    
    /**
     * @return The yaw angle (orientation) of the robot in degrees.
     */
    public float getYaw() {return yaw;}
    
    
    /**
     * @return The time at which the pose is recorded.
     */
    public int getTime() {return time;}



    /**
     * @return A string representation of the Pose object.
     */
    @Override
    public String toString() {
        return "Pose{" +
                "x=" + x +
                ", y=" + y +
                ", yaw=" + yaw +
                ", time=" + time +
                '}';
    }
}

