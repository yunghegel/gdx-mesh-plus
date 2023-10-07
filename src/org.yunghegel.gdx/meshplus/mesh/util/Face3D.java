package org.yunghegel.gdx.meshplus.mesh.util;

import com.badlogic.gdx.graphics.Color;
import org.yunghegel.gdx.meshplus.mesh.math.Vector3f;
import java.util.Arrays;
public class Face3D {

    public Color color;
    public int[] indices;
    public Vector3f normal;
    public String tag;

    public Face3D() {
        this(new int[0]);
    }

    public Face3D(int... indices) {
        this.color = new Color();
        this.indices = new int[indices.length];
        this.normal = new Vector3f();
        this.tag = "";
        System.arraycopy(indices, 0, this.indices, 0, indices.length);
    }

    public boolean sharesSameIndices(Face3D face) {
        int[] indices0 = Arrays.copyOf(face.indices, face.indices.length);
        int[] indices1 = Arrays.copyOf(indices, indices.length);
        Arrays.sort(indices0);
        Arrays.sort(indices1);
        return Arrays.equals(indices0, indices1);
    }

    public Face3D(Face3D f) {
        this(f.indices);
        this.tag = f.tag;
    }

    @Override
    public String toString() {
        return "Face3D [indices=" + Arrays.toString(indices) + "]";
    }
}
