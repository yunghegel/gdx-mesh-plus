package org.yunghegel.gdx.meshplus.mesh.data.halfedge;

public class HFace {

    public HEdge edge;
    public FaceData data;
    public Flag flag;
    public HFace(){
        data = new FaceData();
        flag = new Flag();
    }

    public static class FaceData{
        public float[] n;
        public float d; // plane: n=normal, d=distance to origin
        public float[] c;    // center
        public int tmp_val;  // tmp_val can be used for anything
        public int tmp_val_2 = -1;  // tmp_val can be used for anything
//    int tmp_val_3 = 0;  // tmp_val can be used for anything
//    int tmp_val_4 = 0;  // tmp_val can be used for anything
//    TODO: number of vertices?
//    ArrayList<float[]> points;

        public int id = -1;



    }

}
