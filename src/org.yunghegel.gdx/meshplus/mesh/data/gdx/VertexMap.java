package org.yunghegel.gdx.meshplus.mesh.data.gdx;

import java.util.HashMap;
import java.util.Map;

public class VertexMap {

    private MeshInfo meshInfo;

    public Map<Integer,VertexMap> vertexMap;

    public VertexMap(MeshInfo meshInfo) {
        this.meshInfo = meshInfo;

        vertexMap = new HashMap<>();

        for (int i = 0; i < meshInfo.numIndices; i++) {
            //map index to vertex
            int index = meshInfo.indices[i];
            MeshInfo.Vertex vert = meshInfo.verts.get(index);
            vertexMap.put(index, this);
        }
    }

    public MeshInfo.Vertex getVertex(int index) {
        MeshInfo.Vertex vert = meshInfo.verts.get(index);
        return vert;
    }

    public MeshInfo.Vertex getVertex(short index) {
        MeshInfo.Vertex vert = meshInfo.verts.get(index);
        return vert;
    }

    public float[] getPos(int index) {
        MeshInfo.Vertex vert = meshInfo.verts.get(index);
        return vert.pos;
    }

    public float[] getPos(short index) {
        MeshInfo.Vertex vert = meshInfo.verts.get(index);
        return vert.pos;
    }

    public float[] getNor(int index) {
        MeshInfo.Vertex vert = meshInfo.verts.get(index);
        return vert.nor;
    }

    public float[] getNor(short index) {
        MeshInfo.Vertex vert = meshInfo.verts.get(index);
        return vert.nor;
    }

    public float[] getCol(int index) {
        MeshInfo.Vertex vert = meshInfo.verts.get(index);
        return vert.col;
    }

    public float[] getCol(short index) {
        MeshInfo.Vertex vert = meshInfo.verts.get(index);
        return vert.col;
    }

    public float[] getTex(int index) {
        MeshInfo.Vertex vert = meshInfo.verts.get(index);
        return vert.tex;
    }

    public float[] getTex(short index) {
        MeshInfo.Vertex vert = meshInfo.verts.get(index);
        return vert.tex;
    }

    //supply a vertex and get the index of the vertex







}
