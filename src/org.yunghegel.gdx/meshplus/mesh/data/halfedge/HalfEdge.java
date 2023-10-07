package org.yunghegel.gdx.meshplus.mesh.data.halfedge;

import com.badlogic.gdx.graphics.Mesh;
import org.yunghegel.gdx.meshplus.mesh.data.gdx.MeshInfo;

import java.util.HashMap;

public class HalfEdge {
    private float[] vertices;
    private float[] positions;
    private float[] normals;
    private float[] colors;
    private float[] uvs;
    private short[] indices;
    public static final double EPSILON = 0.001;
    public MeshInfo info;

    private static boolean ERROR_CHECKING = true;

    HVert[] verts;

    public static HMesh create(Mesh mesh){
        MeshInfo info = new MeshInfo(mesh);


        int num_verts = info.verts.size;
        int num_faces = info.faces.size;
        int num_edges = 0;

        HVert[] he_verts;
        HFace[] he_faces;
        HEdge[] he_H_edges;

        he_verts = new HVert[num_verts];

        for(int i = 0; i < num_verts; i++){
            MeshInfo.Vertex v = info.verts.get(i);
            HVert vert = new HVert(v.pos);
            vert.data.n = v.nor;
            vert.data.t = v.tex;
            he_verts[i] = vert;
            vert.setID(i);
        }

        he_faces = new HFace[num_faces];
        for (int i = 0; i <num_faces ; i++) {
            he_faces[i] = new HFace();
            num_edges += info.faces.get(i).idx.length;
        }

        he_H_edges = new HEdge[num_edges];
        for(int id_f = 0, id_e = 0; id_f < num_faces; id_f++){

            short[] vert_indices = info.faces.get(id_f).idx;

            int num_face_edges = vert_indices.length;
            int id_e_cur = id_e;                  // first edge index of current face
            int id_e_nxt = id_e+num_face_edges;   // first edge index of next face

            // init edges for current face
            for(int id_v = 0; id_v < num_face_edges; id_v++, id_e++){
                he_H_edges[id_e]           = new HEdge();
                he_H_edges[id_e].orig      = he_verts[vert_indices[id_v]];
                he_H_edges[id_e].face      = he_faces[id_f];
                he_H_edges[id_e].orig.edge = he_H_edges[id_e]; // link vertex with edge
                he_H_edges[id_e].face.edge = he_H_edges[id_e]; // link face with edge
                he_H_edges[id_e].next      = null; // done in next step
                he_H_edges[id_e].pair      = null; // done, after all edges/faces are created
            }

            // link edges (edge.next) for current face in CCW-Direction
            he_H_edges[id_e_nxt-1].next = he_H_edges[id_e_cur];
            while( id_e_cur < id_e_nxt-1) {
                he_H_edges[id_e_cur].next = he_H_edges[++id_e_cur];
            }
        }

        HashMap<EdgeVerts, HEdge> map = new HashMap<EdgeVerts, HEdge>(num_edges);
        for( int i = 0; i < num_edges; i++ ){
            if( map.put( EdgeVerts.AB(he_H_edges[i]), he_H_edges[i] ) != null ){
                System.err.println("HashMap-Error during map creation (hashcode collision)");
            }
        }

        for( int i = 0; i < num_edges; i++ ){
            if( he_H_edges[i].pair == null ) {
                HEdge e1 = he_H_edges[i];                // find pair for e1
                HEdge e2 = map.get( EdgeVerts.BA(e1));
                HEdge pair = new HEdge();// search map, key = swapped verts
                pair.orig=e1.next.orig;
                pair.next=e1;



                if( e2 != null ){ // on closed meshes, edge2 is never null!!
                    e1.pair = e2;
                    e2.pair = e1;
                } else {
//                    System.err.println("HashMap-Error during pair-linking (hashcode collision)");
                }
            }
        }
        for( int i = 0; ERROR_CHECKING && i < num_edges; i++ ){
            HEdge edge = he_H_edges[i];

            if( edge.pair == null ) {
//                System.err.println("WARNING: edge.pair == null  "+i);
                continue;
            }
            if( edge == edge.pair ){
                System.err.println("ERROR: edge.pair == edge  "+i);
                continue;
            }
            if( edge != edge.pair.pair) {
                System.err.println("ERROR: edge.pair.pair != edge  "+i);
                continue;
            }
            HVert edge_a = edge.orig;
            HVert edge_b = edge.next.orig;
            HVert pair_a = edge.pair.orig;
            HVert pair_b = edge.pair.next.orig;
            if( edge_a != pair_b || edge_b != pair_a){
                System.err.println("ERROR:  edge_a != pair_b || edge_b != pair_a  "+i);
                continue;
            }
        }

        HMesh he_mesh = new HMesh();
        he_mesh.edge = he_H_edges[0];
        he_mesh.data.info = info;

        he_mesh.data.edges = he_H_edges;
        he_mesh.data.faces = he_faces;
        he_mesh.data.verts = he_verts;

        System.out.println("HEMeshInfo: "+info.verts.size +" verts, "+info.faces.size +" faces, "+info.numIndices+" indices");

        return he_mesh;

    }

    public static final class EdgeVerts {

        private final HVert a, b;
        private final int hashcode;

        public static EdgeVerts AB(final HEdge e){  return new EdgeVerts( e.orig, e.next.orig ); }
        public static EdgeVerts BA(final HEdge e){  return new EdgeVerts( e.next.orig, e.orig ); }

        private EdgeVerts(final HVert a, final HVert b) {
            this.a = a;
            this.b = b;
            this.hashcode = a.hashCode() + b.hashCode()*31;
        }

        @Override
        public int hashCode() { return hashcode; }

        @Override
        public boolean equals(Object o) {
            return equals( (EdgeVerts)o );
        }
        public boolean equals(final EdgeVerts e){
            return  (a.equals(e.a) && b.equals(e.b) );
        }
    }


}
