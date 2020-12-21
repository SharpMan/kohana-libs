package koh.maths;

/**
 * Created by Melancholia on 1/15/16.
 */
public class Point3D {

    private double z;
    private double y;
    private double x;

    public Point3D(double x , double y , double z){
        this.x = x;
        this.y = y;
        this.z = z;

    }

    public final double getX() {
        return x;
    }

    public final double getY() {
        return y;
    }

    public final double getZ() {
        return z;
    }


    public void setX(double x){
        this.x = x;
    }

    public void setY(double x){
        this.y = y;
    }

    public void setZ(double z){
        this.z = z;
    }




}
