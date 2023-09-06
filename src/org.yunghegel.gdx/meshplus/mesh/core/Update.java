package org.yunghegel.gdx.meshplus.mesh.core;

import org.yunghegel.gdx.meshplus.mesh.data.halfedge.HFace;
import org.yunghegel.gdx.meshplus.mesh.data.halfedge.HMesh;
import org.yunghegel.gdx.meshplus.mesh.data.halfedge.HVert;
import org.yunghegel.gdx.meshplus.mesh.math.DwVec3;

import java.util.ArrayList;
import java.util.List;

public abstract class Update{

    public static final double EPSILON = 0.001;

    public static void faces(HMesh mesh){
        HFace[] list = Collect.faces(null, mesh);
        for(HFace item : list) Update.face(item);
    }

    /**
     * update/compute face data of a given face.<br>
     * Center (c)<br>
     * Plane: normal (n) and distance (d) to origin<br>
     *
     * @param face
     */
    public static void face(HFace face){
        List<HVert> list = new ArrayList<HVert>();
        Query.adjacentVerts(list, face);

        float[] C = new float[3];
        float[] N = new float[3];
        float D;

        // center
        for( HVert item : list ){
            DwVec3.add_ref_slf(item.data.v, C);
        }
        DwVec3.scale_ref_slf(C, 1f/list.size());

        // normal, TODO: for non-planar faces (or faces with more than 3 vertices),
        // an average normal should be generated
        final float[] E1 = DwVec3.sub_new(list.get(1).data.v, C);
        final float[] E2 = DwVec3.sub_new(list.get(0).data.v, C);
        DwVec3.cross_ref(E1, E2, N); // TODO
        DwVec3.normalize_ref_slf(N);

        // distance to origin
        D = DwVec3.dot(C, N);

        face.data.c = C;
        face.data.n = N;
        face.data.d = D;
    }

    private static final float _1_DIV_3_ = 1f/3f;


    /**
     * returns true, if the triangle is valid
     * @param face

     */
    public static boolean faceTriangle(HFace face){
        // vertices
        float[] v0 = face.edge.orig.data.v;
        float[] v1 = face.edge.next     .orig.data.v;
        float[] v2 = face.edge.next.next.orig.data.v;
        // edges, TODO orientation, left-handed/right-handed?
        float[] E1 = DwVec3.sub_new(v2, v0);
        float[] E2 = DwVec3.sub_new(v1, v0);

        ///// TRIANGLE - CENTER
        float[] C = new float[3];
        DwVec3.add_ref_slf(v0, C);
        DwVec3.add_ref_slf(v1, C);
        DwVec3.add_ref_slf(v2, C);
        DwVec3.scale_ref_slf(C, _1_DIV_3_);
        face.data.c = C;

        ///// TRIANGLE - PLANE
        // normal
        float[] N = DwVec3.cross_new(E1, E2); // TODO
        float mag_sq = DwVec3.mag_sq(N);
        if( mag_sq == 0.0 ) {
            System.err.println("faceTriangle(Face face): triangle is degenerated");
            return false; // triangle is degenerate !!!! -> delete triangle
        }
        DwVec3.scale_ref_slf(N, (float)(1d/Math.sqrt(mag_sq)));

        face.data.n = N;
        face.data.d = DwVec3.dot(C, N); // distance to origin

        return true;
    }

    /**
     * computes/updates the vertex normals of the whole mesh.<br>
     * mostly, vertex-normals are only need for rendering, or debugging.<br>
     *
     * @param mesh mesh, the vertices are update.
     */
    public static void verts(HMesh mesh){
        HVert[] list = Collect.verts((HVert[])null, mesh);
//      System.out.println("number of vertices to update: "+list.size());
        for(HVert item : list) Update.vert(item);
    }

    /**
     * computes/updates the given vertex normal of the mesh.<br>
     * mostly, vertex-normals are only need for rendering, or debugging.<br>
     * @param vert vertex to compute.
     */
    public static boolean vert(HVert vert){

        // get adjacent vertices
        List<HVert> list = new ArrayList<HVert>();
        Query.adjacentVerts(list, vert);

        final int num_verts = list.size();

        // special case
        // TODO interpolate normal? from neighbors?
        if( num_verts == 2 ){

            HFace face_L = vert.edge.face;
            HFace face_R = vert.edge.pair.face;

            float[] N = DwVec3.add_new(face_L.data.n, face_R.data.n );
            DwVec3.scale_ref_slf(N, 0.5f);
            vert.data.n = N;
            return true;
        }

        // compute edges to adjacent vertices
        final float[]   V = vert.data.v;
        final float[][] E = new float[num_verts][3]; // edges to adjacent vertices
        for( int i = 0; i < num_verts; i++ ){
            DwVec3.sub_ref(list.get(i).data.v, V, E[i]);
            DwVec3.normalize_ref_slf(E[i]);
        }
        if( num_verts == 2){
            System.out.println("_____");
            System.out.println(num_verts);
        }

        // compute vertex normal
        // sum up cross-products (normal vectors) of adjacent edges.
        // this takes the size of the face into account (if it is a triangle)
        // TODO: alternatives:
        //       sum up: cross-products of adjacent edges * angle-between-edges
        //       sup up: face-normals(normalized)*face_size
        //       sum up: face-normals(normalized)
        float[] N = new float[3];  // summed up cross-products (final normal)
        float[] CP = new float[3]; // cross-product (normal)
        float[] E1 = E[num_verts-1];
        for( int i = 0; i < num_verts; i++ ){

            DwVec3.cross_ref(E1, E[i], CP); // normal vector E1->E[i]

            float mag_sq = DwVec3.mag_sq(CP);
            if( mag_sq < EPSILON) {
                System.err.println("update vertex normal, crossproduct has length 0.0");
            }
            DwVec3.scale_ref_slf(CP, (float)(1d/Math.sqrt(mag_sq))); // normalize it

            float dot = DwVec3.dot(E1, E[i]); // cos-angle of E1->E[i], both normalized

            float s = (float)(Math.acos(dot)); //TODO?
            if( num_verts == 2){
                System.out.println(dot+", "+s);
                System.out.println("mag_sq = "+mag_sq);

            }
//      System.out.println("  dot = "+dot);
//      System.out.printf(Locale.ENGLISH, "    len E1 = %5.2f, E[i] =%5.2f\n",DwVec3.mag(E1), DwVec3.mag(E[i]));
//      System.out.printf(Locale.ENGLISH, "s =%5.2f,  n = [%5.2f, %5.2f, %5.2f], len = %5.2f\n", s, CP[0], CP[1], CP[2], DwVec3.mag(CP));
            DwVec3.scale_ref_slf(CP, s); // scale normal with by angle of E1->E[i]
            DwVec3.add_ref_slf(CP, N);
            E1=E[i];
        }
        DwVec3.normalize_ref_slf(N);

        vert.data.n = N;
        return true;
    }
}
