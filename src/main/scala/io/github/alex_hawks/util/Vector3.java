package io.github.alex_hawks.util;

public class Vector3
{
    public final int x, y, z;
    
    public Vector3(int x, int y, int z)
    {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3(double x, double y, double z)
    {
        this.x = (int) Math.round(x);
        this.y = (int) Math.round(y);
        this.z = (int) Math.round(z);
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + x;
        result = prime * result + y;
        result = prime * result + z;
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Vector3 other = (Vector3) obj;
        if (x != other.x)
            return false;
        if (y != other.y)
            return false;
        if (z != other.z)
            return false;
        return true;
    }

    @Override
    public String toString()
    {
        return "Vector3 [x=" + x + ", y=" + y + ", z=" + z + "]";
    }
}
