package org.yunghegel.gdx.meshplus.mesh.data.ifs;

import org.yunghegel.gdx.meshplus.mesh.core.Collect;
import org.yunghegel.gdx.meshplus.mesh.data.gdx.MeshInfo;
import org.yunghegel.gdx.meshplus.mesh.data.halfedge.HEdge;
import org.yunghegel.gdx.meshplus.mesh.data.halfedge.HFace;
import org.yunghegel.gdx.meshplus.mesh.data.halfedge.HMesh;
import org.yunghegel.gdx.meshplus.mesh.data.halfedge.HVert;

import java.util.Arrays;
import java.util.HashMap;

public class IFSBuilder {

    public static boolean ERROR_CHECKING = true;

    static public IFS convertToIFS(HMesh mesh){

        // collecting half-edge stuff
        final HVert[] verts = Collect.verts(null, mesh);
        final HFace[] faces = Collect.faces(null, mesh);

        final int num_verts = verts.length;
        final int num_faces = faces.length;

        // creating IFS-structure
        final IFS         ifs   = new IFS();
        final IFS.Group[] ifs_g = { new IFS.Group(ifs, "group_halfedge_mesh") };
        final IFS.Mesh[]  ifs_m = { new IFS.Mesh (ifs_g[0], mesh.data.name) };
        final IFS.Face[]  ifs_f = new IFS.Face[num_faces];
        final float[][]   ifs_v = new float[num_verts][];
        final float[][]   ifs_t = null; // TODO
        final float[][]   ifs_n = null; // TODO

        // vertices
        for(int i = 0; i < num_verts; i++){
            ifs_v[i] = verts[i].data.v;
        }

        // faces
        int[] idx_v_buffer = new int[3];
        for(int i = 0; i < num_faces; i++){
            HFace face = faces[i];

            // get face vertices
            int v_pos = 0;
            HEdge edge = face.edge;
            do {
                if( v_pos >= idx_v_buffer.length){
                    idx_v_buffer = Arrays.copyOf(idx_v_buffer, v_pos<<1);
                }
                idx_v_buffer[v_pos++] = edge.orig.data.id;
            } while( (edge = edge.next) != face.edge);

            final int[] idx_v = Arrays.copyOf(idx_v_buffer, v_pos);
            final int[] idx_n = null; // TODO
            final int[] idx_t = null; // TODO

            ifs_f[i] = new IFS.Face(ifs_m[0], idx_v, idx_n, idx_t);
        }

        ifs_g[0].m.add(ifs_m[0]);
        ifs_m[0].f.addAll(Arrays.asList(ifs_f));

        return ifs.set( ifs_g, ifs_m ,ifs_f, ifs_v, ifs_t, ifs_n );
    }



    static public HMesh convertFromIFS(IFS ifs){
        //TODO handle incorrect ifs-input
        int num_verts = ifs.v.length;
        int num_faces = ifs.f.length;
        int num_edges = 0; // depends of the number of vertices per face.

        HVert[] he_verts;
        HFace[] he_faces;
        HEdge[] he_H_edges;


        //------------------------------------------------------------------------
        // 1) INIT VERTICES
        he_verts = new HVert[num_verts];
        for(int i = 0; i < num_verts; i++){
            he_verts[i]         = new HVert(ifs.v[i]);
//        he_verts[i].data.id = i;
//        he_verts[i].edge    = null; // done in step 3
        }


        //------------------------------------------------------------------------
        // 2) INIT FACES ... and compute number of edges
        he_faces = new HFace[num_faces];
        for(int i = 0; i < num_faces; i++){
            he_faces[i]      = new HFace();
//        he_faces[i].edge = null; // done in step 3
            num_edges += ifs.f[i].IDX_V.length;
        }


        //------------------------------------------------------------------------
        // 3) INIT EDGES ... link to face and edge.next
        he_H_edges = new HEdge[num_edges];
        for(int id_f = 0, id_e = 0; id_f < num_faces; id_f++){

            int[] vert_indices = ifs.f[id_f].IDX_V;

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
                HEdge e2 = map.get( EdgeVerts.BA(e1)); // search map, key = swapped verts
                if( e2 != null ){ // on closed meshes, edge2 is never null!!
                    e1.pair = e2;
                    e2.pair = e1;
                } else {
                    System.err.println("HashMap-Error during pair-linking (hashcode collision)");
                }
            }
        }

        for( int i = 0; ERROR_CHECKING && i < num_edges; i++ ){
            HEdge edge = he_H_edges[i];

            if( edge.pair == null ) {
                System.err.println("WARNING: edge.pair == null  "+i);
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

        return he_mesh;
    }

    public static IFS gdxMeshToIfs(com.badlogic.gdx.graphics.Mesh mesh){
        MeshInfo info = new MeshInfo(mesh);
        MeshInfo.Vertex[] verts = info.verts.items;
        MeshInfo.Face[] faces = info.faces.items;

        final int num_verts = verts.length;
        final int num_faces = faces.length;

        // creating IFS-structure
        final IFS         ifs   = new IFS();
        final IFS.Group[] ifs_g = { new IFS.Group(ifs, "group_halfedge_mesh") };
        final IFS.Mesh[]  ifs_m = { new IFS.Mesh (ifs_g[0], "org/yunghegel/gdx/meshplus/mesh") };
        final IFS.Face[]  ifs_f = new IFS.Face[num_faces];
        final float[][]   ifs_v = new float[num_verts][];
        final float[][]   ifs_t = new float[num_verts][];
        final float[][]   ifs_n = new float[num_verts][];


        for(int i = 0; i < num_verts; i++){
            ifs_v[i] = verts[i].pos;
        }

        // faces
        int[] idx_v_buffer = new int[3];
        for(int i = 0; i < num_faces; i++){
            MeshInfo.Face face = faces[i];
            short a = face.a;
            short b = face.b;
            short c = face.c;

            MeshInfo.Vertex v_a = verts[a];
            MeshInfo.Vertex v_b = verts[b];
            MeshInfo.Vertex v_c = verts[c];




        }

        return ifs;


    }

    private static final class EdgeVerts {

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
