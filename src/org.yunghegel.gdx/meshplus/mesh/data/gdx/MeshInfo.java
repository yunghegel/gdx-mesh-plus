package org.yunghegel.gdx.meshplus.mesh.data.gdx;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.utils.Array;

public class MeshInfo {

    private VertexAttributes attributes;
    private VertexAttribute positionAttribute, normalAttribute, colorAttribute, texCoordAttribute;

    public float[] vertices;
    public short[] indices;

    public float[] positions, normals, colors, texCoords;

    public int posOffset, norOffset, colOffset, texOffset;

    public int vertexSize;

    public int numVertices, numIndices;

    public int numEdges = 0;

    public Mesh mesh;

    private boolean hasNormals = false, hasColors = false, hasTexCoords = false;

    public Array<Vertex> verts = new Array<>();
    public Array<Face> faces = new Array<>();
    public Array<HalfEdge> edges = new Array<>();

    public Vertex[] he_verts;
    public Face[] he_faces;
    public HalfEdge[] he_edges;

    public static class Vertex {

        //we only need to store one of the half edges that points to this vertex I think
        public HalfEdge edge;
        public float[] pos = new float[3];
        public float[] nor = new float[3];
        public float[] col = new float[4];
        public float[] tex = new float[2];
        int id;

        public Vertex(float x, float y, float z) {
            pos = new float[]{x, y, z};
        }

        public void setID(int id) {
            this.id = id;
        }

        public int getID() {
            return id;
        }

        @Override
        public String toString() {
            return "Vertex{" +
                    +id + ", " +
                    "pos=" + pos[0] + ", " + pos[1] + ", " + pos[2] +
                    ", nor=" + nor[0] + ", " + nor[1] + ", " + nor[2] +
                    ", col=" + col[0] + ", " + col[1] + ", " + col[2] + ", " + col[3] +
                    ", tex=" + tex[0] + ", " + tex[1] +
                    '}';
        }
    }

    public static class Face {

        //a single halfedge with its origin as the first vertex of the face
        //this is enough to define the entire face
        /**
         * i.e halfedge.next -> next.next -> next.next.next -> halfedge
         * with pointer to vertex in each halfedge (edge.origin)
         * pairs link adjacent faces for traversal across topology
         */
        public HalfEdge edge;

        public short a, b, c;
        public short[] idx = new short[3];

        public Face(short a, short b, short c) {
            this.a = a;
            this.b = b;
            this.c = c;
            idx = new short[]{a, b, c};
        }

        @Override
        public String toString() {
            return "Face{" +
                    "a=" + a +
                    ", b=" + b +
                    ", c=" + c +
                    '}';
        }
    }

    public static class HalfEdge {

        public Vertex origin;
        public HalfEdge next;
        public HalfEdge pair;
        public Face face;

        @Override
        public String toString() {
            return ("Edge origin: " + origin + "\n");
        }
    }

    public MeshInfo(Mesh mesh) {
        this.mesh = mesh;
        collect();
        allocate();
        traverse();
    }

    private void traverse() {
        //traverse vertices, collecting position and normal, color, texCoord if available
        he_verts = new Vertex[numVertices];
        for (int i = 0; i < numVertices; i++) {
            Vertex v = new Vertex(0, 0, 0);
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
            v.setID(i);
            verts.add(v);
            he_verts[i] = v;
        }

        //traverse indices, collecting faces
        //triangles only - quads are split into two triangles
        he_faces = new Face[numIndices / 3];

        for (int i = 0; i < numIndices; i += 3) {
            Face f = new Face(indices[i], indices[i + 1], indices[i + 2]);
            faces.add(f);
            numEdges += f.idx.length;
            he_faces[i / 3] = f;
        }

        //traverse faces, collecting edges and pointers
        he_edges = new HalfEdge[numEdges];
        int num_faces = faces.size;
        // link to face and edge.next
        for (int id_f = 0, id_e = 0; id_f < num_faces; id_f++) {
            short[] vert_indices = he_faces[id_f].idx;

            int num_face_edges = vert_indices.length;
            int id_e_cur = id_e;                  // first edge index of current face
            int id_e_nxt = id_e + num_face_edges;   // first edge index of next face

            for (int id_v = 0; id_v < num_face_edges; id_v++, id_e++) {
                he_edges[id_e] = new HalfEdge();
                he_edges[id_e].origin = he_verts[vert_indices[id_v]];
                he_edges[id_e].face = he_faces[id_f];
                he_edges[id_e].origin.edge = he_edges[id_e]; // link vertex with edge pointer
                he_edges[id_e].face.edge = he_edges[id_e]; // link face with edge pointer
                he_edges[id_e].next = null;
                he_edges[id_e].pair = null; // done last, only when edges/faces are populated with pointers
            }
        }

        // populate next pointers
        for (int id_e = 0; id_e < numEdges; id_e++) {
            HalfEdge e = he_edges[id_e];
            e.next = he_edges[(id_e + 1) % numEdges];
        }

        //finally we can find the edge pairs
        for (int id_e = 0; id_e < numEdges; id_e++) {
            HalfEdge e = he_edges[id_e];
            HalfEdge e_pair = null;
            Vertex v1 = e.origin;
            Vertex v2 = e.next.origin;
            for (int id_e2 = 0; id_e2 < numEdges; id_e2++) {
                HalfEdge e2 = he_edges[id_e2];
                Vertex v3 = e2.origin;
                Vertex v4 = e2.next.origin;
                if (v1 == v4 && v2 == v3) {
                    e_pair = e2;
                    break;
                }
            }
            e.pair = e_pair;
            System.out.println("id_e: " + id_e + " e_pair: " + e_pair);
        }
    }

    private void allocate() {
        //allocate memory for the arrays
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

    private void collect() {
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
        posOffset = positionAttribute.offset / 4;
        vertexSize = mesh.getVertexSize() / 4;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("MeshInfo: ").append(mesh).append("\n");
        sb.append("Vertices: ").append(numVertices).append("\n");
        sb.append("Indices: ").append(numIndices).append("\n");
        sb.append("Vertex Size: ").append(vertexSize).append("\n");
        sb.append("Position Offset: ").append(posOffset).append("\n");
        sb.append("Normal Offset: ").append(norOffset).append("\n");
        sb.append("Color Offset: ").append(colOffset).append("\n");
        sb.append("TexCoord Offset: ").append(texOffset).append("\n");
        sb.append("Has Normals: ").append(hasNormals).append("\n");
        sb.append("Has Colors: ").append(hasColors).append("\n");
        sb.append("Has TexCoords: ").append(hasTexCoords).append("\n");
        return sb.toString();
    }
}

