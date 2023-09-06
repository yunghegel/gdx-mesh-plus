package org.yunghegel.gdx.meshplus.mesh.data.halfedge;

public class HVert {

    public HEdge edge;
    public VertData data;
    public Flag flag;
    public HVert(float[] v) {
        data = new VertData();
        data.v = v;
        flag = new Flag();
    }

    public HVert(float x, float y, float z) {
        data = new VertData();
        data.v = new float[]{x, y, z};
        flag = new Flag();
    }

    public void setID(int id){
        data.id = id;
    }

    public int getID(){
        return data.id;
    }

    public static class VertData{

        public float[] v; // vertex: Float3
        public float x, y, z;
        public float[] t; // texture coord: Float2 or null
        public float[] n; // normal:
        public int id;
        public int tmp_val;  // used for: slicing
        public float d; // use for: slicing[distance to plane.
        public int tmp_val_2 = 0;
    }
}
