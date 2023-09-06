package org.yunghegel.gdx.meshplus.mesh.core.ifs;

import org.yunghegel.gdx.meshplus.mesh.core.halfedge.AttributeLookupTable;
import org.yunghegel.gdx.meshplus.mesh.data.gdx.MeshInfo;

import org.yunghegel.gdx.meshplus.mesh.data.halfedge.*;
import org.yunghegel.gdx.meshplus.mesh.data.ifs.IFSBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class IFS {

    public float[][]   v, t, n;
    public IFS.Face[]  f;
    public IFS.Group[] g;
    public IFS.Mesh[]  m;

    // obj ID: "g"
    public static class Group implements Comparable<Group>{
        public IFS parent;
        public ArrayList<Mesh> m = new ArrayList<IFS.Mesh>();
        public String name;
        public Group(IFS parent, String name){
            this.parent = parent;
            this.name = name;
        }
        @Override
        public int compareTo(IFS.Group o) {
            return name.compareTo(o.name);
        }
    }
    // obj ID: "o"
    public static class Mesh implements Comparable<Mesh>{
        public IFS.Group parent;
        public ArrayList<IFS.Face> f = new ArrayList<Face>();
        public String name;
        public Mesh(IFS.Group parent, String name){
            this.parent = parent;
            this.name = name;
        }
        @Override
        public int compareTo(IFS.Mesh o) {
            return name.compareTo(o.name);
        }
    }

    // obj ID: "f"
    public static class Face{
        public IFS.Mesh parent;
        public int[] IDX_V, IDX_T, IDX_N;
        public Face(IFS.Mesh parent, int[] idx_v, int[]idx_t, int[]idx_n){
            this.parent = parent;
            this.IDX_V = idx_v;
            this.IDX_T = idx_t;
            this.IDX_N = idx_n;
        }
        public Face(int vertex_count){
            this.IDX_V = new int[vertex_count];
            this.IDX_T = new int[vertex_count];
            this.IDX_N = new int[vertex_count];
        }
    }

    public static IFS createFromGdxMesh(com.badlogic.gdx.graphics.Mesh mesh){
        AttributeLookupTable table = new AttributeLookupTable(mesh);
        MeshInfo info = new MeshInfo(mesh);

        final int num_verts = info.he_verts.length;
        final int num_faces = info.he_faces.length;
        final int num_edges = 0;

        final IFS         ifs   = new IFS();
        final IFS.Group[] ifs_g = { new IFS.Group(ifs, "group_halfedge_mesh") };
        final IFS.Mesh[]  ifs_m = { new IFS.Mesh (ifs_g[0], "org/yunghegel/gdx/meshplus/mesh") };
        final IFS.Face[]  ifs_f = new IFS.Face[info.he_faces.length];
        final float[][]   ifs_v = new float[info.he_verts.length][];
        final float[][]   ifs_t = null; // TODO
        final float[][]   ifs_n = null; // TODO

        //verts
        for(int i = 0; i < num_verts; i++){
            ifs_v[i] = info.he_verts[i].pos;
        }

        //faces
        int[] idx_v_buffer = new int[3];
        for(int i=0; i < num_faces; i++){
            MeshInfo.Face face = info.he_faces[i];

            int v_pos = 0;
            MeshInfo.HalfEdge edge = face.edge;
            do {
                if( v_pos >= idx_v_buffer.length){
                    idx_v_buffer = Arrays.copyOf(idx_v_buffer, v_pos<<1);
                }
                idx_v_buffer[v_pos++] = edge.origin.getID();
            } while( (edge=edge.next) != face.edge);

            final int[] idx_v = Arrays.copyOf(idx_v_buffer, v_pos);
            final int[] idx_n = null; // TODO
            final int[] idx_t = null; // TODO

            ifs_f[i] = new IFS.Face(ifs_m[0], idx_v, idx_n, idx_t);
        }

        ifs_g[0].m.add(ifs_m[0]);
        ifs_m[0].f.addAll(Arrays.asList(ifs_f));

        ifs.set( ifs_g, ifs_m ,ifs_f, ifs_v, ifs_t, ifs_n );
        return ifs;
    }

    static public HMesh convertFromIFS(IFS ifs){
        //TODO handle incorrect ifs-input
        int num_verts = ifs.v.length;
        int num_faces = ifs.f.length;
        int num_edges = 0; // depends of the number of vertices per face.

        HVert[] he_verts;
        HFace[] he_faces;
        HEdge[] he_edges;


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
        he_edges = new HEdge[num_edges];
        for(int id_f = 0, id_e = 0; id_f < num_faces; id_f++){

            int[] vert_indices = ifs.f[id_f].IDX_V;

            int num_face_edges = vert_indices.length;
            int id_e_cur = id_e;                  // first edge index of current face
            int id_e_nxt = id_e+num_face_edges;   // first edge index of next face

            // init edges for current face
            for(int id_v = 0; id_v < num_face_edges; id_v++, id_e++){
                he_edges[id_e]           = new HEdge();
                he_edges[id_e].orig      = he_verts[vert_indices[id_v]];
                he_edges[id_e].face      = he_faces[id_f];
                he_edges[id_e].orig.edge = he_edges[id_e]; // link vertex with edge
                he_edges[id_e].face.edge = he_edges[id_e]; // link face with edge
                he_edges[id_e].next      = null; // done in next step
                he_edges[id_e].pair      = null; // done, after all edges/faces are created
            }

            // link edges (edge.next) for current face in CCW-Direction
            he_edges[id_e_nxt-1].next = he_edges[id_e_cur];
            while( id_e_cur < id_e_nxt-1) {
                he_edges[id_e_cur].next = he_edges[++id_e_cur];
            }
        }




        // setup a hash-map of all edges, using the edge's vertices (A->B) as key.
        HashMap<HalfEdge.EdgeVerts, HEdge> map = new HashMap<HalfEdge.EdgeVerts, HEdge>(num_edges);
        for( int i = 0; i < num_edges; i++ ){
            if( map.put( HalfEdge.EdgeVerts.AB(he_edges[i]), he_edges[i] ) != null ){
//                System.err.println("HashMap-Error during map creation (hashcode collision)");
            }
        }

        // to find an edge-pair, the map is queried by using the reversed vertex order (B->A) as key.
        // this is really fast!
        for( int i = 0; i < num_edges; i++ ){
            if( he_edges[i].pair == null ) {
                HEdge e1 = he_edges[i];                // find pair for e1
                HEdge e2 = map.get( HalfEdge.EdgeVerts.BA(e1)); // search map, key = swapped verts
                if( e2 != null ){ // on closed meshes, edge2 is never null!!
                    e1.pair = e2;
                    e2.pair = e1;
                } else {
                    System.err.println("HashMap-Error during pair-linking (hashcode collision)");
                }
            }
        }
//      timer = System.currentTimeMillis()-timer;
//      System.out.println("  linking pairs: "+timer+" ms");

        //------------------------------------------------------------------------
        // 5) DEBUGGING: CHECK PAIRS
        for(int i = 0; IFSBuilder.ERROR_CHECKING && i < num_edges; i++ ){
            HEdge edge = he_edges[i];

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

        //------------------------------------------------------------------------
        // 6) INIT MESH
        HMesh he_mesh = new HMesh();
        he_mesh.edge = he_edges[0];

        return he_mesh;
    }



    public IFS set(IFS.Group[] g, IFS.Mesh[] m, IFS.Face[] f, float[][] v, float[][] t, float[][] n){
        this.g = g;
        this.m = m;
        this.f = f;
        this.v = v;
        this.t = t;
        this.n = n;
        return this;
    }

}
