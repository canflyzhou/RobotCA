package com.robotca.ControlApp.Core;

import android.graphics.Color;
import android.location.Location;

import org.ros.android.view.visualization.Vertices;
import org.ros.rosjava_geometry.Quaternion;
import org.ros.rosjava_geometry.Vector3;

import java.nio.FloatBuffer;

import javax.microedition.khronos.opengles.GL10;

import sensor_msgs.NavSatFix;

/**
 * Various useful functions.
 *
 * Created by Michael Brunson on 3/4/16.
 */
public class Utils {

    // Temporary float buffer
    private static final FloatBuffer fb = Vertices.allocateBuffer(3 + 4); //xyz + color (rgba)

    /**
     * Returns a heading from the specified Quaternion in radians.
     * @param quaternion The Quaternion
     * @return The heading from the Quaternion
     */
    public static double getHeading(Quaternion quaternion) {
        Vector3 xAxis = Vector3.xAxis();
        Vector3 rotatedAxis = quaternion.rotateAndScaleVector(xAxis);
        rotatedAxis = new Vector3(rotatedAxis.getX(),rotatedAxis.getY(),0);
        rotatedAxis = rotatedAxis.normalize();

        return (double) (float)Math.atan2(rotatedAxis.getY(), rotatedAxis.getX());
    }

    public static Location navSatToLocation(NavSatFix navSatFix){
        Location location = new Location(navSatFix.getHeader().getFrameId());

        location.setLatitude(navSatFix.getLatitude());
        location.setLongitude(navSatFix.getLongitude());
        location.setAltitude(navSatFix.getAltitude());

        return location;
    }

    public static Vector3 rotateVector(Vector3 originalVector, double radians){
        return new Vector3(originalVector.getX()*Math.cos(radians)-originalVector.getY()*Math.sin(radians),
                originalVector.getX()*Math.sin(radians)+originalVector.getY()*Math.cos(radians), 0);
    }

    /**
     * Calculates the difference between two angles, in radians.
     * @param angle1 The first angle
     * @param angle2 The second angle
     * @return The difference between the two angles
     */
    public static double angleDifference(double angle1, double angle2)
    {
        return ((((angle1 - angle2) % (Math.PI * 2.0)) + (Math.PI * 3)) % (Math.PI * 2)) - Math.PI;
    }

    /**
     * Calculates the direction in radians from one point to another.
     * @param x1 x coordinate of the first point
     * @param y1 y coordinate of the first point
     * @param x2 x coordinate of the second point
     * @param y2 y coordinate of the second point
     * @return Direction from the first point to the second
     */
    public static double pointDirection(double x1, double y1, double x2, double y2)
    {
        return Math.atan2(y2 - y1, x2 - x1);
    }

    /**
     * Calculates the distance between two points.
     * @param x1 x coordinate of the first point
     * @param y1 y coordinate of the first point
     * @param x2 x coordinate of the second point
     * @param y2 y coordinate of the second point
     * @return Distance from the first point to the second
     */
    public static double distance(double x1, double y1, double x2, double y2) {
        return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }

    /**
     * Calculates the square of the distance between two points.
     * @param x1 x coordinate of the first point
     * @param y1 y coordinate of the first point
     * @param x2 x coordinate of the second point
     * @param y2 y coordinate of the second point
     * @return Square distance from the first point to the second
     */
    public static double distanceSquared(double x1, double y1, double x2, double y2) {
        return (x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2);
    }

    /**
     * Draws a point.
     * @param gl  The GL10 object for drawing
     * @param x The point's x coordinate
     * @param y The point's y coordinate
     * @param color The color in the form 0xAARRGGBB
     */
    public static void drawPoint(GL10 gl, float x, float y, float size, int color) {
        fb.rewind();

        fb.put(x); // x
        fb.put(y); // y
        fb.put(0.0f); // z

        fb.put(Color.red(color) / 255.0f);   // r
        fb.put(Color.green(color) / 255.0f); // g
        fb.put(Color.blue(color) / 255.0f);  // b
        fb.put(Color.alpha(color) / 255.0f); // a

        fb.rewind();

        gl.glPointSize(size);
        gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glEnableClientState(GL10.GL_COLOR_ARRAY);

        gl.glVertexPointer(3, GL10.GL_FLOAT, (3 + 4) * 4, fb);

        FloatBuffer colors = fb.duplicate();
        colors.position(3);
        gl.glColorPointer(4, GL10.GL_FLOAT, (3 + 4) * 4, colors);

        gl.glDrawArrays(GL10.GL_POINTS, 0, 1);

        gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
        gl.glDisableClientState(GL10.GL_COLOR_ARRAY);
    }
}