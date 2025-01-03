package bgu.spl.mics.application.objects;

/**
 * CloudPoint represents a specific point in a 3D space as detected by the LiDAR.
 * These points are used to generate a point cloud representing objects in the environment.
 */
public class CloudPoint {

// --------------------- fields -------------------------
    private double x;
    private double y;

// --------------------- constructor --------------------

    /**
     * Constructs a CloudPoint object with specified x and y coordinates.
     *
     * @param x The x-coordinate of the point.
     * @param y The y-coordinate of the point.
     * @post {@code this.x == x}
     * @post {@code this.y == y}
     */
    public CloudPoint(double x, double y){
        this.x = x;
        this.y = y;
    }

// --------------------- methods ------------------------

    /**
     * @return The x-coordinate of the point.
     */
    public double getX() {return x;}


        /**
     * @return The y-coordinate of the point.
     */
    public double getY() {return y;}


    /**
     * Updates the current CloudPoint's coordinates to the average of its current values and a new point's values.
     *
     * @param newPoint The new CloudPoint to merge with the current point.
     * @pre {@code newPoint != null}
     * @post {@code this.x == (oldX + newPoint.getX()) / 2}
     * @post {@code this.y == (oldY + newPoint.getY()) / 2}
     */
    public void update(CloudPoint newPoint) {
        this.x = (this.x + newPoint.getX()) / 2;
        this.y = (this.y + newPoint.getY()) / 2;
    }


    /**
     * @return A string representation of the CloudPoint in JSON format.
     */
    @Override
    public String toString() {
        return "{\"x\": " + x + ", \"y\": " + y + "}";
    }


}
