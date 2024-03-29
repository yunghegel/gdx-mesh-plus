package org.yunghegel.gdx.meshplus.mesh.util;

import org.yunghegel.gdx.meshplus.mesh.math.Mathf;
import org.yunghegel.gdx.meshplus.mesh.math.Matrix3f;
import org.yunghegel.gdx.meshplus.mesh.math.Vector3f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Mesh3DUtil {
    public static Mesh3D pushToSphere(Mesh3D mesh, float radius) {
        return pushToSphere(mesh, new Vector3f(), radius);
    }

    // CLEAN UP -> Move to modifier class
    public static Mesh3D pushToSphere(Mesh3D mesh, Vector3f center, float radius) {
        Vector3f origin = new Vector3f(center);
        for (Vector3f v : mesh.vertices) {
            Vector3f v0 = new Vector3f(v.subtract(origin)).normalizeLocal();
            v.set(v0.mult(radius).add(origin));
        }
        return mesh;
    }

    // CLEAN UP -> Move to modifier class
    public static Mesh3D pushToSphere(Mesh3D mesh, Face3D face, float radius) {
        Vector3f origin = mesh.calculateFaceCenter(face);
        for (int i = 0; i < face.indices.length; i++) {
            Vector3f v = mesh.getVertexAt(face.indices[i]);
            Vector3f v0 = new Vector3f(v.subtract(origin)).normalizeLocal();
            v.set(v0.mult(radius).add(origin));
        }
        return mesh;
    }

    public static void flipDirection(Mesh3D mesh, Face3D face) {
        int[] copy = Arrays.copyOf(face.indices, face.indices.length);
        for (int i = 0; i < face.indices.length; i++) {
            face.indices[i] = copy[face.indices.length - 1 - i];
        }
    }

    public static void flipDirection(Mesh3D mesh) {
        for (Face3D face : mesh.faces) {
            flipDirection(mesh, face);
        }
    }

    public static float perimeter(Mesh3D mesh, Face3D face) {
        float perimeter = 0;
        for (int i = 0; i < face.indices.length - 2; i++) {
            i++;
            Vector3f v0 = mesh.getVertexAt(face.indices[i]);
            Vector3f v1 = mesh.getVertexAt(face.indices[i + 1]);
            perimeter += v0.distance(v1);
        }
        return perimeter;
    }

    public static void scaleFace(Mesh3D mesh, Face3D face, float scale) {
        Vector3f center = mesh.calculateFaceCenter(face);
        for (int i = 0; i < face.indices.length; i++) {
            Vector3f v = mesh.vertices.get(face.indices[i]);
            v.subtractLocal(center).multLocal(scale).addLocal(center);
        }
    }

    public static void scaleFaceAt(Mesh3D mesh, int index, float scale) {
        Face3D f = mesh.faces.get(index);
        scaleFace(mesh, f, scale);
    }

    public static void rotateFaceX(Mesh3D mesh, Face3D face, float a) {
        Matrix3f m = new Matrix3f(1, 0, 0, 0, Mathf.cos(a), -Mathf.sin(a), 0, Mathf.sin(a), Mathf.cos(a));

        for (int i = 0; i < face.indices.length; i++) {
            Vector3f v = mesh.vertices.get(face.indices[i]);
            Vector3f v0 = v.mult(m);
            v.set(v.getX(), v0.getY(), v0.getZ());
        }
    }

    public static void rotateFaceY(Mesh3D mesh, Face3D face, float a) {
        Matrix3f m = new Matrix3f(Mathf.cos(a), 0, Mathf.sin(a), 0, 1, 0, -Mathf.sin(a), 0, Mathf.cos(a));

        for (int i = 0; i < face.indices.length; i++) {
            Vector3f v = mesh.vertices.get(face.indices[i]);
            Vector3f v0 = v.mult(m);
            v.set(v0.getX(), v.getY(), v0.getZ());
        }
    }

    public static void rotateFaceZ(Mesh3D mesh, Face3D face, float a) {
        Matrix3f m = new Matrix3f(Mathf.cos(a), -Mathf.sin(a), 0, Mathf.sin(a), Mathf.cos(a), 0, 0, 0, 1);

        for (int i = 0; i < face.indices.length; i++) {
            Vector3f v = mesh.vertices.get(face.indices[i]);
            Vector3f v0 = v.mult(m);
            v.set(v0.getX(), v0.getY(), v.getZ());
        }
    }

    public static void bridge(Mesh3D mesh, Vector3f v0, Vector3f v1, Vector3f v2, Vector3f v3) {
        int idx0 = mesh.vertices.indexOf(v0);
        int idx1 = mesh.vertices.indexOf(v1);
        int idx2 = mesh.vertices.indexOf(v2);
        int idx3 = mesh.vertices.indexOf(v3);
        Face3D face = new Face3D(idx0, idx1, idx3, idx2);
        mesh.faces.add(face);
    }

    public static void bridge(Mesh3D mesh, Face3D f0, Face3D f1) {
        Face3D f2 = new Face3D(f0.indices[0], f0.indices[1], f1.indices[1], f1.indices[0]);
        Face3D f3 = new Face3D(f0.indices[1], f0.indices[2], f1.indices[2], f1.indices[1]);
        Face3D f4 = new Face3D(f0.indices[2], f0.indices[3], f1.indices[3], f1.indices[2]);
        Face3D f5 = new Face3D(f0.indices[3], f0.indices[0], f1.indices[0], f1.indices[3]);
        mesh.faces.add(f2);
        mesh.faces.add(f3);
        mesh.faces.add(f4);
        mesh.faces.add(f5);
    }

    public static void extrudeFace(Mesh3D mesh, Face3D face, float scale, float amount) {
        int n = face.indices.length;
        int idx = mesh.vertices.size();
        Vector3f normal = mesh.calculateFaceNormal(face);
        Vector3f center = mesh.calculateFaceCenter(face);

        normal.multLocal(amount);

        for (int i = 0; i < n; i++) {
            Vector3f v0 = mesh.vertices.get(face.indices[i]);
            Vector3f v1 = new Vector3f(v0).subtract(center).mult(scale).add(center);

            v1.addLocal(normal);
            mesh.vertices.add(v1);
        }

        for (int i = 0; i < n; i++) {
            Face3D f0 = new Face3D(face.indices[i], face.indices[(i + 1) % n], idx + ((i + 1) % n), idx + i);
            mesh.add(f0);
        }

        for (int i = 0; i < n; i++) {
            face.indices[i] = idx + i;
        }
    }

    public static void extrudeFace(Mesh3D mesh, Face3D face, float scale, float amount, boolean remove) {
        extrudeFace(mesh, face, scale, amount);
        if (remove)
            mesh.removeFace(face);
    }

    public static void insetFace(Mesh3D mesh, Face3D f, float thickness) {
        int n = f.indices.length;
        int idx = mesh.vertices.size();

        List<Vector3f> verts = new ArrayList<Vector3f>();

        for (int i = 0; i < n; i++) {
            Vector3f v0 = mesh.vertices.get(f.indices[i]);
            Vector3f v1 = mesh.vertices.get(f.indices[(i + 1) % f.indices.length]);

            float distance = v1.subtract(v0).length();
            float a = (1f / distance) * thickness;

            Vector3f v4 = v1.subtract(v0).mult(a).add(v0);
            Vector3f v5 = v1.add(v1.subtract(v0).mult(-a));

            verts.add(v4);
            verts.add(v5);
        }

        for (int i = 1; i < verts.size(); i += 2) {
            int a = verts.size() - 2 + i;
            Vector3f v0 = verts.get(a % verts.size());
            Vector3f v1 = verts.get((a + 1) % verts.size());
            Vector3f v = v1.add(v0).mult(0.5f);
            mesh.add(v);
        }

        for (int i = 0; i < n; i++) {
            int index0 = f.indices[i];
            int index1 = f.indices[(i + 1) % n];
            int index2 = idx + ((i + 1) % n);
            int index3 = idx + i;
            mesh.addFace(index0, index1, index2, index3);
        }

        for (int i = 0; i < n; i++) {
            f.indices[i] = idx + i;
        }
    }
}
