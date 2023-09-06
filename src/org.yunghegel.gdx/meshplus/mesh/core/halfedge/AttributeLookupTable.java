package org.yunghegel.gdx.meshplus.mesh.core.halfedge;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import org.yunghegel.gdx.meshplus.mesh.data.gdx.MeshInfo;

public class AttributeLookupTable {

    private Mesh mesh;
    private VertexAttributes attributes;
    private VertexAttribute positionAttribute, normalAttribute, colorAttribute, texCoordAttribute;

    public float[] vertices;
    public short[] indices;
    public float[] positions, normals, colors, texCoords;
    public int posOffset, norOffset, colOffset, texOffset;

    public int vertexSize;
    public int numVertices, numIndices;

    private boolean hasNormals = false, hasColors = false, hasTexCoords = false;

    MeshInfo info;

    public AttributeLookupTable(Mesh mesh){
        this.mesh = mesh;

        getAttributes();
        allocateArrays();
    }

    private void getAttributes(){
        attributes = mesh.getVertexAttributes();
        //find the offsets so we can navigate the backing array and allocate memory correctly
        positionAttribute = attributes.findByUsage(VertexAttributes.Usage.Position);
        normalAttribute = attributes.findByUsage(VertexAttributes.Usage.Normal);
        if (normalAttribute != null) {
            hasNormals = true;
            norOffset = normalAttribute.offset / 4;
        }
        colorAttribute = attributes.findByUsage(VertexAttributes.Usage.ColorPacked);
        if (colorAttribute != null) {
            colOffset = colorAttribute.offset / 4;
            hasColors = true;
        }
        texCoordAttribute = attributes.findByUsage(VertexAttributes.Usage.TextureCoordinates);
        if (texCoordAttribute != null) {
            hasTexCoords = true;
            texOffset = texCoordAttribute.offset / 4;
        }
    }

    private void allocateArrays(){
        posOffset = positionAttribute.offset / 4;
        vertexSize = mesh.getVertexSize() / 4;
        numVertices = mesh.getNumVertices();
        numIndices = mesh.getNumIndices();

        vertices = new float[numVertices * vertexSize * 4];
        indices = new short[numIndices];
        positions = new float[numVertices * 3];
        normals = new float[numVertices * 3];
        colors = new float[numVertices * 4];
        texCoords = new float[numVertices * 2];

        mesh.getVertices(vertices);
        mesh.getIndices(indices);
    }

    public Vertex.VertexInfo getVertInfo(int index){
        Vertex.VertexInfo v = new Vertex.VertexInfo();

        for (int i = 0; i < numVertices; i++) {
            if (positionAttribute != null) {
                v.pos[0] = vertices[i * vertexSize + positionAttribute.offset / 4];
                v.pos[1] = vertices[i * vertexSize + positionAttribute.offset / 4 + 1];
                v.pos[2] = vertices[i * vertexSize + positionAttribute.offset / 4 + 2];
            }
            if (normalAttribute != null) {
                v.nor[0] = vertices[i * vertexSize + normalAttribute.offset / 4];
                v.nor[1] = vertices[i * vertexSize + normalAttribute.offset / 4 + 1];
                v.nor[2] = vertices[i * vertexSize + normalAttribute.offset / 4 + 2];
            }
            if (colorAttribute != null) {
                v.col[0] = vertices[i * vertexSize + colorAttribute.offset / 4];
                v.col[1] = vertices[i * vertexSize + colorAttribute.offset / 4 + 1];
                v.col[2] = vertices[i * vertexSize + colorAttribute.offset / 4 + 2];
                v.col[3] = vertices[i * vertexSize + colorAttribute.offset / 4 + 3];
            }
            if (texCoordAttribute != null) {
                v.tex[0] = vertices[i * vertexSize + texCoordAttribute.offset / 4];
                v.tex[1] = vertices[i * vertexSize + texCoordAttribute.offset / 4 + 1];
                }
            }
        return v;
    }

    public void getAllVertexInfo(Vertex ...vertices){
        for (Vertex v:vertices) {
            Vertex.VertexInfo info = getVertInfo(v.getId());
            v.info = info;
        }
    }

    public Vertex lookupVertex(int idx){
        Vertex v = new Vertex(idx);
        Vertex.VertexInfo info = getVertInfo(idx);
        v.info = info;
        return v;
    }
}
