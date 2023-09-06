package org.yunghegel.gdx.meshplus.mesh.data.halfedge;

public class HEdge {

    public HEdge next;
    public HEdge pair;
    public HVert orig;
    public HFace face;
    public Flag flag;
    public EdgeData data;

    public HEdge(){
        flag = new Flag();
        data = new EdgeData();
    }

    public static class EdgeData{
    }

}
