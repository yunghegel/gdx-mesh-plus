package org.yunghegel.gdx.meshplus.mesh.math;

import java.util.Arrays;

public class Matrix4f extends com.badlogic.gdx.math.Matrix4{
    public static final Matrix4f ZERO = new Matrix4f(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
    public static final Matrix4f UNIT = new Matrix4f(1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1, 0, 0, 0, 0, 1);
    public static final Matrix4f ONE = new Matrix4f(1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1);

    public static final int M00 = 0;
    public static final int M01 = 1;
    public static final int M02 = 2;
    public static final int M03 = 3;

    public static final int M10 = 4;
    public static final int M11 = 5;
    public static final int M12 = 6;
    public static final int M13 = 7;

    public static final int M20 = 8;
    public static final int M21 = 9;
    public static final int M22 = 10;
    public static final int M23 = 11;

    public static final int M30 = 12;
    public static final int M31 = 13;
    public static final int M32 = 14;
    public static final int M33 = 15;

    private float[] values;

    public Matrix4f() {
        values = new float[16];
    }

    public Matrix4f(Matrix4f m) {
        values = new float[16];
        System.arraycopy(m.values, 0, this.values, 0, m.values.length);
    }

    public float getValueAt(int index) {
        return values[index];
    }

    public Matrix4f(float m00, float m01, float m02, float m03, float m10, float m11, float m12, float m13, float m20,
                    float m21, float m22, float m23, float m30, float m31, float m32, float m33) {
        values = new float[16];

        values[M00] = m00;
        values[M01] = m01;
        values[M02] = m02;
        values[M03] = m03;

        values[M10] = m10;
        values[M11] = m11;
        values[M12] = m12;
        values[M13] = m13;

        values[M20] = m20;
        values[M21] = m21;
        values[M22] = m22;
        values[M23] = m23;

        values[M30] = m30;
        values[M31] = m31;
        values[M32] = m32;
        values[M33] = m33;
    }

    public Matrix4f transpose() {
        Matrix4f tranpose = new Matrix4f(values[M00], values[M10], values[M20], values[M30],

                values[M01], values[M11], values[M21], values[M31],

                values[M02], values[M12], values[M22], values[M32],

                values[M03], values[M13], values[M23], values[M33]);

        return tranpose;
    }

    public Matrix4f add(Matrix4f m) {
        Matrix4f result = new Matrix4f();
        float[] a = values;
        float[] b = m.values;
        float[] c = result.values;

        c[M00] = a[M00] + b[M00];
        c[M01] = a[M01] + b[M01];
        c[M02] = a[M02] + b[M02];
        c[M03] = a[M03] + b[M03];

        c[M10] = a[M10] + b[M10];
        c[M11] = a[M11] + b[M11];
        c[M12] = a[M12] + b[M12];
        c[M13] = a[M13] + b[M13];

        c[M20] = a[M20] + b[M20];
        c[M21] = a[M21] + b[M21];
        c[M22] = a[M22] + b[M22];
        c[M23] = a[M23] + b[M23];

        c[M30] = a[M30] + b[M30];
        c[M31] = a[M31] + b[M31];
        c[M32] = a[M32] + b[M32];
        c[M33] = a[M33] + b[M33];

        return result;
    }

    public Matrix4f addLocal(Matrix4f m) {
        values[M00] += m.values[M00];
        values[M01] += m.values[M01];
        values[M02] += m.values[M02];
        values[M03] += m.values[M03];

        values[M10] += m.values[M10];
        values[M11] += m.values[M11];
        values[M12] += m.values[M12];
        values[M13] += m.values[M13];

        values[M20] += m.values[M20];
        values[M21] += m.values[M21];
        values[M22] += m.values[M22];
        values[M23] += m.values[M23];

        values[M30] += m.values[M30];
        values[M31] += m.values[M31];
        values[M32] += m.values[M32];
        values[M33] += m.values[M33];

        return this;
    }

    public Matrix4f multLocal(float scalar) {
        values[M00] *= scalar;
        values[M01] *= scalar;
        values[M02] *= scalar;
        values[M03] *= scalar;

        values[M10] *= scalar;
        values[M11] *= scalar;
        values[M12] *= scalar;
        values[M13] *= scalar;

        values[M20] *= scalar;
        values[M21] *= scalar;
        values[M22] *= scalar;
        values[M23] *= scalar;

        values[M30] *= scalar;
        values[M31] *= scalar;
        values[M32] *= scalar;
        values[M33] *= scalar;

        return this;
    }

    public Matrix4f mult(Matrix4f other) {
        Matrix4f store = new Matrix4f();
        float[] m = new float[16];

        m[0] = values[M00] * other.values[M00] + values[M01] * other.values[M10] + values[M02] * other.values[M20]
                + values[M03] * other.values[M30];
        m[1] = values[M00] * other.values[M01] + values[M01] * other.values[M11] + values[M02] * other.values[M21]
                + values[M03] * other.values[M31];
        m[2] = values[M00] * other.values[M02] + values[M01] * other.values[M12] + values[M02] * other.values[M22]
                + values[M03] * other.values[M32];
        m[3] = values[M00] * other.values[M03] + values[M01] * other.values[M13] + values[M02] * other.values[M23]
                + values[M03] * other.values[M33];

        m[4] = values[M10] * other.values[M00] + values[M11] * other.values[M10] + values[M12] * other.values[M20]
                + values[M13] * other.values[M30];
        m[5] = values[M10] * other.values[M01] + values[M11] * other.values[M11] + values[M12] * other.values[M21]
                + values[M13] * other.values[M31];
        m[6] = values[M10] * other.values[M02] + values[M11] * other.values[M12] + values[M12] * other.values[M22]
                + values[M13] * other.values[M32];
        m[7] = values[M10] * other.values[M03] + values[M11] * other.values[M13] + values[M12] * other.values[M23]
                + values[M13] * other.values[M33];

        m[8] = values[M20] * other.values[M00] + values[M21] * other.values[M10] + values[M22] * other.values[M20]
                + values[M23] * other.values[M30];
        m[9] = values[M20] * other.values[M01] + values[M21] * other.values[M11] + values[M22] * other.values[M21]
                + values[M23] * other.values[M31];
        m[10] = values[M20] * other.values[M02] + values[M21] * other.values[M12] + values[M22] * other.values[M22]
                + values[M23] * other.values[M32];
        m[11] = values[M20] * other.values[M03] + values[M21] * other.values[M13] + values[M22] * other.values[M23]
                + values[M23] * other.values[M33];

        m[12] = values[M30] * other.values[M00] + values[M31] * other.values[M10] + values[M32] * other.values[M20]
                + values[M33] * other.values[M30];
        m[13] = values[M30] * other.values[M01] + values[M31] * other.values[M11] + values[M32] * other.values[M21]
                + values[M33] * other.values[M31];
        m[14] = values[M30] * other.values[M02] + values[M31] * other.values[M12] + values[M32] * other.values[M22]
                + values[M33] * other.values[M32];
        m[15] = values[M30] * other.values[M03] + values[M31] * other.values[M13] + values[M32] * other.values[M23]
                + values[M33] * other.values[M33];

        store.values[M00] = m[0];
        store.values[M01] = m[1];
        store.values[M02] = m[2];
        store.values[M03] = m[3];
        store.values[M10] = m[4];
        store.values[M11] = m[5];
        store.values[M12] = m[6];
        store.values[M13] = m[7];
        store.values[M20] = m[8];
        store.values[M21] = m[9];
        store.values[M22] = m[10];
        store.values[M23] = m[11];
        store.values[M30] = m[12];
        store.values[M31] = m[13];
        store.values[M32] = m[14];
        store.values[M33] = m[15];

        return store;
    }

    public Vector3f mult(Vector3f v) {
        Vector3f result = new Vector3f();
        float vx = v.getX(), vy = v.getY(), vz = v.getZ();
        float x = values[M00] * vx + values[M01] * vy + values[M02] * vz + values[M03];
        float y = values[M10] * vx + values[M11] * vy + values[M12] * vz + values[M13];
        float z = values[M20] * vx + values[M21] * vy + values[M22] * vz + values[M23];
        result.set(x, y, z);
        return result;
    }

    /**
     * Look At, right-handed coordinate system.
     *
     * @param from
     * @param to
     * @param up
     * @return the resulting view matrix
     */
    public static Matrix4f lookAtRH(Vector3f eye, Vector3f target, Vector3f up) {
        Vector3f zaxis = eye.subtract(target).normalize();
        Vector3f xaxis = up.cross(zaxis).normalize();
        Vector3f yaxis = zaxis.cross(xaxis);

        Matrix4f viewMatrix = new Matrix4f(xaxis.getX(), yaxis.getX(), zaxis.getZ(), 0, xaxis.getY(), yaxis.getY(),
                zaxis.getY(), 0, xaxis.getZ(), yaxis.getZ(), zaxis.getZ(), 0, -xaxis.dot(eye), -yaxis.dot(eye),
                -zaxis.dot(eye), 1);

        return viewMatrix;
    }

    /**
     * FPS camera, right-handed coordinate system.
     *
     * @param eye
     * @param pitch in radians
     * @param yaw   in radians
     * @return the resulting view matrix
     */
    public static Matrix4f fpsViewRH(Vector3f eye, float pitch, float yaw) {
        float cosPitch = Mathf.cos(pitch);
        float sinPitch = Mathf.sin(pitch);
        float cosYaw = Mathf.cos(yaw);
        float sinYaw = Mathf.sin(yaw);

        Vector3f xaxis = new Vector3f(cosYaw, 0, -sinYaw);
        Vector3f yaxis = new Vector3f(sinYaw * sinPitch, cosPitch, cosYaw * sinPitch);
        Vector3f zaxis = new Vector3f(sinYaw * cosPitch, -sinPitch, cosPitch * cosYaw);

        Matrix4f viewMatrix = new Matrix4f(xaxis.getX(), yaxis.getX(), zaxis.getX(), 0, xaxis.getY(), yaxis.getY(),
                zaxis.getY(), 0, xaxis.getZ(), yaxis.getZ(), zaxis.getZ(), 0, -xaxis.dot(eye), -yaxis.dot(eye),
                -zaxis.dot(eye), 1);

        return viewMatrix;
    }

    public float[] getValues() {
        return values;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(values);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Matrix4f other = (Matrix4f) obj;
        return Arrays.equals(values, other.values);
    }
}
