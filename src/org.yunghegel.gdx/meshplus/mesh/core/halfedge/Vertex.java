package org.yunghegel.gdx.meshplus.mesh.core.halfedge;

public class Vertex {

    int id;
    public HalfEdge hEdge;
    public VertexInfo info;

    public Vertex(int id){
        this.id = id;
    }

    public Vertex (int id, HalfEdge hEdge){
        this.id = id;
        this.hEdge = hEdge;
    }

    public int getId(){
        return id;
    }

    public static class VertexInfo {
        public float[] pos = new float[3];
        public float[] nor = new float[3];
        public float[] col = new float[4];
        public float[] tex = new float[2];
    }

}
