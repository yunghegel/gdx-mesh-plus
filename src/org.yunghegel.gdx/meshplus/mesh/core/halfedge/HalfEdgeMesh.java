package org.yunghegel.gdx.meshplus.mesh.core.halfedge;

import com.badlogic.gdx.graphics.Mesh;

public class HalfEdgeMesh {

    public Face[] faces;
    public Vertex[] verts;
    public HalfEdge[] edges;

    public HalfEdgeMap edgeTable;
    public AttributeLookupTable vertexTable;
    private Mesh mesh;

    public void create(Mesh mesh) {
        this.mesh = mesh;
        vertexTable = new AttributeLookupTable(mesh);

        short[] indices = new short[mesh.getNumIndices()];
        mesh.getIndices(indices);

        int faceCount = indices.length / 3;

        // Allocate all pieces of the half-edge data structure
        edges = new HalfEdge[faceCount * 3];
        faces = new Face[faceCount];
        verts = new Vertex[vertexTable.numVertices];

        for (int i = 0; i < edges.length; i++)
            edges[i] = new HalfEdge();

        // Declare a map to help build the half-edge structure:
        // - Keys are pairs of vertex indices
        // - Values are half-edges
        edgeTable = new HalfEdgeMap(edges.length);

        int srcIdx = 0;
        int edgeIdx = 0;
        for (int faceIndex = 0; faceIndex < faceCount; faceIndex++) {
            int A = indices[srcIdx++];

            int B = indices[srcIdx++];
            int C = indices[srcIdx++];

            //edge
            HalfEdge edge = edges[edgeIdx];

            //verts
            Vertex vertA = new Vertex(A);
            Vertex vertB = new Vertex(B);
            Vertex vertC = new Vertex(C);
            verts[A] = vertA;
            verts[B] = vertB;
            verts[C] = vertC;

            //face
            Face face = new Face(faceIndex, new int[]{A, B, C});
            faces[faceIndex] = face;

            face.edge = edge;

            // Create the half-edge that goes from C to A:
            edge.id = C | ((long) A << 32L);
            edgeTable.put(edge);
            edge.vert = A;
            edge.next = edges[1 + edgeIdx];
            edge = edges[++edgeIdx];

            // Create the half-edge that goes from A to B:
            edge.id = A | ((long) B << 32L);
            edge.vertex = vertA;
            edgeTable.put(edge);

            edge.vert = B;
            edge.vertex = vertB;
            edge.next = edges[1 + edgeIdx];
            edge = edges[++edgeIdx];

            // Create the half-edge that goes from B to C:
            edge.id = B | ((long) C << 32L);
            edgeTable.put(edge);
            edge.vert = C;
            edge.vertex = vertC;
            edge.next = edges[edgeIdx - 2];
            ++edgeIdx;


        }
        int numEntries = edgeTable.size;
        if (numEntries != faceCount * 3) {
            throw new IllegalArgumentException("Bad mesh: duplicated edges or inconsistent winding.");
        }

        int boundaryCount = 0;
        long UINT_MASK = 0xFFFFFFFFL;
        for (int i = 0; i < edges.length; i++) {
            HalfEdge edge = edges[i];
            long edgeIndex = edge.id;
            long twinIndex = ((edgeIndex & UINT_MASK) << 32L) | (edgeIndex >>> 32L);
            HalfEdge twinEdge = edgeTable.get(twinIndex);
            if (twinEdge != null) {
                twinEdge.twin = edge;
                edge.twin = twinEdge;
                System.out.println("edge: " + edge.vert + " twin: " + twinEdge.vert);
            } else {
                boundaryCount++;
                System.out.println(null + "twin");
            }

        }

    }
}
