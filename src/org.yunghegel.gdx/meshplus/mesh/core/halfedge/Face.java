package org.yunghegel.gdx.meshplus.mesh.core.halfedge;

public class Face {

    public HalfEdge edge;
    public int id;
    public int[] indices = new int[3];

    public Face(int id){
        this.id = id;
    }

    public Face(int id, int[] indices){
        this.id = id;
        this.indices = indices;
    }

    public Face(int id, HalfEdge edge){
        this.id = id;
        this.edge = edge;
    }

    public Face(int id, int[] indices, HalfEdge edge){
        this.id = id;
        this.indices = indices;
        this.edge = edge;
    }



}
