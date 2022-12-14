package Experiment_1;

class Point2D {
    private int x,y;
    public Point2D(){}
    public Point2D(int x,int y)
    {
        this.x=x;
        this.y=y;
    }
    public void offset(int a,int b)
    {
        x=x+a;
        y=y+b;
    }
    public int getX()
    {
        return x;
    }
    public int getY()
    {
        return y;
    }
    public double GetDistance(Point2D p)
    {
        double X=x-p.getX();
        double Y=y-p.getY();
        double distance=Math.sqrt(X*X+Y*Y);
        return distance;
    }
    public void show()
    {
        System.out.println("x="+x+"  y="+y);
    }
}
public class Point3D extends Point2D
{
    int x,y,z;
    public Point3D(int x,int y,int z)
    {
        this.x=x;
        this.y=y;
        this.z=z;
    }
    public Point3D(Point2D p,int z)
    {
        this.x=p.getX();
        this.y=p.getY();
        this.z=z;
    }
    public int getX()
    {
        return x;
    }
    public int getY()
    {
        return y;
    }
    public int getZ()
    {
        return z;
    }
    public void offset(int a, int b, int c)
    {
        x=x+a;
        y=y+b;
        z=z+c;
    }
    public double GetDistance(Point3D p)
    {
        double X=x-p.getX();
        double Y=y-p.getY();
        double Z=z-p.getZ();
        double distance=Math.sqrt(X*X+Y*Y+Z*Z);
        return distance;
    }
    public void show()
    {
        System.out.println("x="+x+"  y="+y+"  z="+z);
    }

    public static void main(String[] args) {
        Point2D p2d1;
        Point2D p2d2;
        Point3D p3d1;
        Point3D p3d2;
        double Distance2D;
        double Distance3D;

        p2d1=new Point2D(2,2);
        p2d2=new Point2D(5,6);
        p2d1.show();
        p2d2.show();
        Distance2D=p2d1.GetDistance(p2d2);
        System.out.println("Distance2D: "+Distance2D);

        p3d1=new Point3D(1,1,1);
        p3d2=new Point3D(p2d1,2);
        p3d1.show();
        p3d2.show();
        Distance3D=p3d1.GetDistance(p3d2);
        System.out.println("Distance3D: "+Distance3D);


    }
}

