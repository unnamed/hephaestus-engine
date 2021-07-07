package team.unnamed.hephaestus.struct;

import org.bukkit.util.EulerAngle;

public class OldQuaternion {

    private float x;
    private float y;
    private float z;
    private float w;

    public OldQuaternion(
            float x,
            float y,
            float z,
            float w
    ) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    private OldQuaternion() {}

    public float getX() {
        return x;
    }

    public void setX(float x) {
        this.x = x;
    }

    public float getY() {
        return y;
    }

    public void setY(float y) {
        this.y = y;
    }

    public float getZ() {
        return z;
    }

    public void setZ(float z) {
        this.z = z;
    }

    public float getW() {
        return w;
    }

    public void setW(float w) {
        this.w = w;
    }

    public float get(int axis) {
        switch (axis) {
            case 0:
                return x;
            case 1:
                return y;
            case 2:
                return z;
            case 3:
                return w;
            default:
                throw new IllegalArgumentException("Invalid axis: " + axis);
        }
    }

    void set(int axis, float value) {
        switch (axis) {
            case 0:
                this.x = value;
                break;
            case 1:
                this.y = value;
            case 2:
                this.z = value;
            case 3:
                this.w = value;
            default:
                throw new ArrayIndexOutOfBoundsException(axis);
        }
    }

    @Override
    public OldQuaternion clone() {
        return new OldQuaternion().copy(this);
    }

    public OldQuaternion copy(OldQuaternion other) {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
        this.w = other.w;
        return this;
    }

    public OldQuaternion set(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
        return this;
    }

    public OldQuaternion setFromEuler(EulerAngle euler, EulerOrder order) {
        double x = euler.getX();
        double y = euler.getY();
        double z = euler.getZ();

        float c1 = (float) Math.cos( x / 2 );
        float c2 = (float) Math.cos( y / 2 );
        float c3 = (float) Math.cos( z / 2 );

        float s1 = (float) Math.sin( x / 2 );
        float s2 = (float) Math.sin( y / 2 );
        float s3 = (float) Math.sin( z / 2 );

        switch (order) {

            case XYZ:
                this.x = s1 * c2 * c3 + c1 * s2 * s3;
                this.y = c1 * s2 * c3 - s1 * c2 * s3;
                this.z = c1 * c2 * s3 + s1 * s2 * c3;
                this.w = c1 * c2 * c3 - s1 * s2 * s3;
                break;

            case YXZ:
                this.x = s1 * c2 * c3 + c1 * s2 * s3;
                this.y = c1 * s2 * c3 - s1 * c2 * s3;
                this.z = c1 * c2 * s3 - s1 * s2 * c3;
                this.w = c1 * c2 * c3 + s1 * s2 * s3;
                break;

            case ZXY:
                this.x = s1 * c2 * c3 - c1 * s2 * s3;
                this.y = c1 * s2 * c3 + s1 * c2 * s3;
                this.z = c1 * c2 * s3 + s1 * s2 * c3;
                this.w = c1 * c2 * c3 - s1 * s2 * s3;
                break;

            case ZYX:
                this.x = s1 * c2 * c3 - c1 * s2 * s3;
                this.y = c1 * s2 * c3 + s1 * c2 * s3;
                this.z = c1 * c2 * s3 - s1 * s2 * c3;
                this.w = c1 * c2 * c3 + s1 * s2 * s3;
                break;

            case YZX:
                this.x = s1 * c2 * c3 + c1 * s2 * s3;
                this.y = c1 * s2 * c3 + s1 * c2 * s3;
                this.z = c1 * c2 * s3 - s1 * s2 * c3;
                this.w = c1 * c2 * c3 - s1 * s2 * s3;
                break;

            case XZY:
                this.x = s1 * c2 * c3 - c1 * s2 * s3;
                this.y = c1 * s2 * c3 - s1 * c2 * s3;
                this.z = c1 * c2 * s3 + s1 * s2 * c3;
                this.w = c1 * c2 * c3 + s1 * s2 * s3;
                break;
        }

        return this;
    }

    public OldQuaternion setFromAxisAngle(Vector3Float axis, double angle) {
        double halfAngle = angle / 2.0;
        float s = (float) Math.sin(halfAngle);

        this.x = axis.getX() * s;
        this.y = axis.getY() * s;
        this.z = axis.getZ() * s;
        this.w = (float) Math.cos(halfAngle);

        return this;
    }

    public OldQuaternion multiplyQuaternions(OldQuaternion a, OldQuaternion b) {
        float qax = a.x, qay = a.y, qaz = a.z, qaw = a.w;
        float qbx = b.x, qby = b.y, qbz = b.z, qbw = b.w;

        this.x = qax * qbw + qaw * qbx + qay * qbz - qaz * qby;
        this.y = qay * qbw + qaw * qby + qaz * qbx - qax * qbz;
        this.z = qaz * qbw + qaw * qbz + qax * qby - qay * qbx;
        this.w = qaw * qbw - qax * qbx - qay * qby - qaz * qbz;

        return this;
    }

    public OldQuaternion multiply(OldQuaternion q) {
        return this.multiplyQuaternions(this, q);
    }

    public OldQuaternion premultiply(OldQuaternion q) {
        return this.multiplyQuaternions(q, this);
    }

    public OldQuaternion conjugate() {
        this.x *= - 1;
        this.y *= - 1;
        this.z *= - 1;
        return this;
    }

    // quaternion is assumed to have unit length
    public OldQuaternion inverse() {
        return this.conjugate();
    }

    public double dot(OldQuaternion q) {
        return this.x * q.x + this.y * q.y + this.z * q.z + this.w * q.w;
    }

    public double lengthSquared() {
        return (this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w);
    }

    public double length() {
        return Math.sqrt(this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w);
    }

    public OldQuaternion normalize() {
        double len = this.length();

        if (len == 0.0) {
            this.x = 0f;
            this.y = 0f;
            this.z = 0f;
            this.w = 1f;
        } else {
            float invLen = (float) (1.0 / len);

            this.x = this.x * invLen;
            this.y = this.y * invLen;
            this.z = this.z * invLen;
            this.w = this.w * invLen;
        }

        return this;
    }

    public double angleTo(OldQuaternion q) {
        return 2.0 * Math.acos(Math.abs(clamp(this.dot(q), -1.0, 1.0)));
    }

    public OldQuaternion slerp(OldQuaternion qb, double t) {

        if ( t <= 0.0 ) {
            return this;
        }

        if ( t >= 1.0 ) {
            return this.copy( qb );
        }

        float x = this.x;
        float y = this.y;
        float z = this.z;
        float w = this.w;

        double cosHalfTheta = (double) (w * qb.w + x * qb.x + y * qb.y + z * qb.z);

        if ( cosHalfTheta < 0f ) {
            this.w = -qb.w;
            this.x = -qb.x;
            this.y = -qb.y;
            this.z = -qb.z;

            cosHalfTheta = -cosHalfTheta;
        }
        else {
            this.copy(qb);
        }

        if (cosHalfTheta >= 1.0) {
            this.w = w;
            this.x = x;
            this.y = y;
            this.z = z;
            return this;
        }

        double sqrSinHalfTheta = 1.0 - cosHalfTheta * cosHalfTheta;

        double epsilon = 0.00000001;
        if ( sqrSinHalfTheta <= epsilon) {
            float s = (float) (1.0 - t);
            float tf = (float) t;

            this.w = s * w + tf * this.w;
            this.x = s * x + tf * this.x;
            this.y = s * y + tf * this.y;
            this.z = s * z + tf * this.z;

            return this.normalize();
        }

        double sinHalfTheta = Math.sqrt( sqrSinHalfTheta );
        double halfTheta = Math.atan2( sinHalfTheta, cosHalfTheta );
        float ratioA = (float) (Math.sin( ( 1.0 - t ) * halfTheta ) / sinHalfTheta);
        float ratioB = (float) (Math.sin( t * halfTheta ) / sinHalfTheta);

        this.w = ( w * ratioA + this.w * ratioB );
        this.x = ( x * ratioA + this.x * ratioB );
        this.y = ( y * ratioA + this.y * ratioB );
        this.z = ( z * ratioA + this.z * ratioB );

        return this;
    }

    public OldQuaternion slerp(OldQuaternion qa, OldQuaternion qb, double t) {
        return this.copy(qa).slerp(qb, t);
    }

    public EulerAngle toEuler(EulerOrder order) {
        float x = this.getX();
        float y = this.getY();
        float z = this.getZ();
        float w = this.getW();

        float x2 = x + x;
        float y2 = y + y;
        float z2 = z + z;

        float xx = x * x2;
        float xy = x * y2;
        float xz = x * z2;
        float yy = y * y2;
        float yz = y * z2;
        float zz = z * z2;
        float wx = w * x2;
        float wy = w * y2;
        float wz = w * z2;

        // rotation matrix column parameters
        double e00 = 1f - (yy + zz);
        double e10 = xy + wz;
        double e20 = xz - wy;

        double e01 = xy - wz;
        double e11 = 1f - (xx + zz);
        double e21 = yz + wx;

        double e02 = xz + wy;
        double e12 = yz - wx;
        double e22 = 1f - (xx + yy);

        float ex = 0;
        float ey = 0;
        float ez = 0;

        switch (order) {
            case XYZ:
                ey = (float) Math.asin( clamp( e02, -1.0, 1.0 ) );

                if ( Math.abs( e02 ) < 0.9999999 ) {
                    ex = (float) Math.atan2( -e12, e22 );
                    ez = (float) Math.atan2( -e01, e00 );
                }
                else {
                    ex = (float) Math.atan2( e21, e11 );
                    ez = 0f;
                }
                break;
            case YXZ:
                ex = (float) Math.asin( - clamp( e12, -1.0, 1.0 ) );

                if ( Math.abs( e12 ) < 0.9999999 ) {
                    ey = (float) Math.atan2( e02, e22 );
                    ez = (float) Math.atan2( e10, e11 );
                }
                else {
                    ey = (float) Math.atan2( -e20, e00 );
                    ez = 0f;
                }
                break;
            case ZXY:
                ex = (float) Math.asin( clamp( e21, -1.0, 1.0 ) );

                if ( Math.abs( e21 ) < 0.9999999 ) {
                    ey = (float) Math.atan2( -e20, e22 );
                    ez = (float) Math.atan2( -e01, e11 );
                }
                else {
                    ey = 0f;
                    ez = (float) Math.atan2( e10, e00 );
                }
                break;

            case ZYX:
                ey = (float) Math.asin( - clamp( e20, -1.0, 1.0 ) );

                if ( Math.abs( e20 ) < 0.9999999 ) {
                    ex = (float) Math.atan2( e21, e22 );
                    ez = (float) Math.atan2( e10, e00 );
                }
                else {
                    ex = 0f;
                    ez = (float) Math.atan2( -e01, e11 );
                }
                break;

            case YZX:
                ez = (float) Math.asin( clamp( e10, -1.0, 1.0 ) );

                if ( Math.abs( e10 ) < 0.9999999 ) {
                    ex = (float) Math.atan2( -e12, e11 );
                    ey = (float) Math.atan2( -e20, e00 );
                }
                else {
                    ex = 0f;
                    ey = (float) Math.atan2( e02, e22 );
                }
                break;

            case XZY:
                ez = (float) Math.asin( - clamp( e01, -1.0, 1.0 ) );

                if ( Math.abs( e01 ) < 0.9999999 ) {
                    ex = (float) Math.atan2( e21, e11 );
                    ey = (float) Math.atan2( e02, e00 );
                }
                else {
                    ex = (float) Math.atan2( -e12, e22 );
                    ey = 0f;
                }
                break;
        }

        return new EulerAngle(ex, ey, ez);
    }

    public OldQuaternion rotateTowards(OldQuaternion q, double step) {

        double angle = this.angleTo(q);

        double epsilon = 0.00001;
        if (angle < epsilon) {
            return this.copy(q);
        }

        double t = Math.min(1.0, step / angle);

        return this.slerp(q, t);
    }

    public OldQuaternion fromFloatArray(float[] array, int offset) {
        this.x = array[offset];
        this.y = array[offset + 1];
        this.z = array[offset + 2];
        this.w = array[offset + 3];
        return this;
    }

    public float[] toFloatArray() {
        float[] array = new float[4];
        array[0] = this.x;
        array[1] = this.y;
        array[2] = this.z;
        array[3] = this.w;
        return array;
    }

    public float[] toFloatArray(float [] array, int offset) {
        array[offset] = this.x;
        array[offset + 1] = this.y;
        array[offset + 2] = this.z;
        array[offset + 3] = this.w;
        return array;
    }

    private double clamp(double v, double min, double max) {
        return Math.max(min, Math.min(max, v));
    }

    public static OldQuaternion zero() {
        return new OldQuaternion(0f, 0f, 0f, 0f);
    }

    public static OldQuaternion create() {
        return new OldQuaternion(0f, 0f, 0f, 1f);
    }

    public static OldQuaternion fromEuler(EulerAngle euler, EulerOrder order) {
        OldQuaternion oldQuaternion = new OldQuaternion(0f, 0f, 0f, 0f);
        return oldQuaternion.setFromEuler(euler, order);
    }

    public static OldQuaternion fromAxisAngle(Vector3Float axis, double angle) {
        OldQuaternion oldQuaternion = new OldQuaternion(0f, 0f, 0f, 0f);
        return oldQuaternion.setFromAxisAngle(axis, angle);
    }

    public static OldQuaternion fromVector(Vector3Float vector) {
        return new OldQuaternion(vector.getX(), vector.getY(), vector.getZ(), 0);
    }
}