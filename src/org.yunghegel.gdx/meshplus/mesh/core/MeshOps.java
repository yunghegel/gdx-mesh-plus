package org.yunghegel.gdx.meshplus.mesh.core;

import org.yunghegel.gdx.meshplus.mesh.data.halfedge.HEdge;
import org.yunghegel.gdx.meshplus.mesh.data.halfedge.HFace;
import org.yunghegel.gdx.meshplus.mesh.data.halfedge.HMesh;
import org.yunghegel.gdx.meshplus.mesh.data.halfedge.HVert;
import org.yunghegel.gdx.meshplus.mesh.math.DwVec3;

import java.util.List;

public abstract class MeshOps {

    /**
     * test all mesh faces for planarity. those being not planar are triangulated.<br>
     * using the face-center as new point.<br>
     *
     * @param mesh
     * @return number of new new faces
     */
    public static int trianguleNonPlanarFaces(HMesh mesh){
        int num_new_faces = 0;
        List<HFace> faces_non_planar = Query.nonPlanarFaces(mesh);
        for( HFace item : faces_non_planar ){
            num_new_faces += MeshOps.triangulateFace(mesh, item, (HFace[])null);
        }
        Update.verts(mesh);
        return num_new_faces;
    }


    /**
     * triangulate given face using its face-center as a new mesh-vertex. <br>
     * @param mesh
     * @param face
     * @param faces_new
     * @return number of new faces
     */
    public static int triangulateFace(HMesh mesh, HFace face, List<HFace> faces_new){
        return triangulateFace(mesh, face, face.data.c, faces_new);
    }
    public static int triangulateFace(HMesh mesh, HFace face, HFace[] faces_new){
        return triangulateFace(mesh, face, face.data.c, faces_new);
    }
    /**
     * triangulate given face using "point" as a new mesh-vertex. <br>
     *
     * "face" fill be replaced by new faces.<br>
     * <br>
     * mesh.edge.orig = point<br>
     * <br>
     * @param mesh
     * @param face face that gets triangulated, and replaced by new faces.
     * @param point new mesh-point
     * @param faces_new list, to save the new faces to
     * @return number of new faces
     */
    public static int triangulateFace(HMesh mesh, HFace face, float[] point, List<HFace> faces_new){

        HVert vert__ = new HVert(point);

        HEdge edge_start = face.edge;
        HEdge edge_cur = edge_start;

        edge_cur.orig.edge = edge_cur;

        do{
            HFace face__ = new HFace();
            HEdge edge_2 = new HEdge();
            HEdge edge_1 = new HEdge();
            HEdge edge_0 = edge_cur;

            edge_cur = edge_cur.next;

            // linking: FACE <-> EDGES
            face__.edge = edge_0;
            edge_0.face = face__;
            edge_1.face = face__;
            edge_2.face = face__;

            // linking: EDGE ORIGINS
            // !!!! in case the same vertex appears multiple times in an edge-ring edge-ring
            // which must NOT happen, but can (during convex-hull creation due to precision errors)
            // some created faces will be skipped in the next loop. therefore the saving and of
            // the faces is done in the next loop.
            // edit: during convex hull the horizon is granted to be "nice".
            edge_0.orig.edge = edge_0;  // -->
            edge_1.orig = edge_cur.orig;
            edge_2.orig = vert__;

            // linking: NEXT EDGES
            edge_0.next = edge_1;
            edge_1.next = edge_2;
            edge_2.next = edge_0;

            // update new created face
            if( !Update.faceTriangle(face__) ){
                //TODO
                System.err.println( "TRIANGLE IS DEGENERATE, cant comput normal");
            }


        } while( edge_cur != edge_start);

        int num_new_faces = 0;
        do{

            if(faces_new!=null) faces_new.add(edge_cur.face);
            num_new_faces++;

            HEdge edge_1_cur = edge_cur.next;
            HEdge edge_2_nxt = edge_cur.next.orig.edge.next.next;

            edge_1_cur.pair = edge_2_nxt;
            edge_2_nxt.pair = edge_1_cur;

//        edge_cur = edge_2_nxt.next;
            edge_cur = edge_cur.next.orig.edge;

        } while( edge_cur != edge_start);

        vert__.edge = edge_start.next.pair;
        mesh.edge = vert__.edge;

        //TODO: update everything that was in the neighborhood (vertices, faces)
        return num_new_faces;
    }


    /**
     * triangulate given face using "point" as a new mesh-vertex. <br>
     *
     * "face" fill be replaced by new faces.<br>
     * <br>
     * mesh.edge.orig = point<br>
     * <br>
     * @param mesh
     * @param face face that gets triangulated, and replaced by new faces.
     * @param point new mesh-point
     * @param faces_new list, to save the new faces to
     * @return number of new faces
     */
    public static int triangulateFace(HMesh mesh, HFace face, float[] point, HFace[] faces_new){

        HVert vert__ = new HVert(point);

        HEdge edge_start = face.edge;
        HEdge edge_cur = edge_start;

        edge_cur.orig.edge = edge_cur;

        do{
            HFace face__ = new HFace();
            HEdge edge_2 = new HEdge();
            HEdge edge_1 = new HEdge();
            HEdge edge_0 = edge_cur;

            edge_cur = edge_cur.next;

            // linking: FACE <-> EDGES
            face__.edge = edge_0;
            edge_0.face = face__;
            edge_1.face = face__;
            edge_2.face = face__;

            // linking: EDGE ORIGINS
            // !!!! in case the same vertex appears multiple times in an edge-ring edge-ring
            // which must NOT happen, but can (during convex-hull creation due to precision errors)
            // some created faces will be skipped in the next loop. therefore the saving and of
            // the faces is done in the next loop.
            // edit: during convex hull the horizon is granted to be "nice".
            edge_0.orig.edge = edge_0;  // -->
            edge_1.orig = edge_cur.orig;
            edge_2.orig = vert__;

            // linking: NEXT EDGES
            edge_0.next = edge_1;
            edge_1.next = edge_2;
            edge_2.next = edge_0;

            // update new created face
            if( !Update.faceTriangle(face__) ){
                //TODO
                System.err.println( "TRIANGLE IS DEGENERATE, cant comput normal");
            }


        } while( edge_cur != edge_start);

        int num_new_faces = 0;
        do{

            if(faces_new!=null) faces_new[num_new_faces] = edge_cur.face;
            num_new_faces++;

            HEdge edge_1_cur = edge_cur.next;
            HEdge edge_2_nxt = edge_cur.next.orig.edge.next.next;

            edge_1_cur.pair = edge_2_nxt;
            edge_2_nxt.pair = edge_1_cur;

//        edge_cur = edge_2_nxt.next;
            edge_cur = edge_cur.next.orig.edge;

        } while( edge_cur != edge_start);

        vert__.edge = edge_start.next.pair;
        mesh.edge = vert__.edge;

        //TODO: update everything that was in the neighborhood (vertices, faces)
        return num_new_faces;
    }


//    //TODO: delete
//    public static int triangulateFace(Mesh mesh, Face face, float[] point, FaceList faces_new){
//
//      int num_new_faces = 0;
//
//      Vert vert__ = new Vert(point);
//
//      Edge edge_start = face.edge;
//      Edge edge_cur   = edge_start;
//
//      do{
//        Face face__ = new Face();
//        Edge edge_2 = new Edge();
//        Edge edge_1 = new Edge();
//        Edge edge_0 = edge_cur;
//
//        edge_cur = edge_cur.next;
//
//        // linking: FACE <-> EDGES
//        face__.edge = edge_0;
//        edge_0.face = face__;
//        edge_1.face = face__;
//        edge_2.face = face__;
//
//        // linking: EDGE ORIGINS
//        edge_0.orig.edge  = edge_0; // !!!!
//        edge_1.orig = edge_cur.orig;
//        edge_2.orig = vert__;
//
//        // linking: NEXT EDGES
//        edge_0.next = edge_1;
//        edge_1.next = edge_2;
//        edge_2.next = edge_0;
//
//        // update new created face
//        if( !Update.faceTriangle(face__) ){
//          //TODO
//          System.err.println( "TRIANGLE IS DEGENERATE, cant comput normal");
//        }
//
//        // add to face list
//        if( faces_new != null ) faces_new.add(face__);
//
//        num_new_faces++;
//
//      } while( edge_cur != edge_start);
//
//      do{
//        Edge edge_1_cur = edge_cur.next;
//        Edge edge_2_nxt = edge_cur.next.orig.edge.next.next;
//
//        edge_1_cur.pair = edge_2_nxt;
//        edge_2_nxt.pair = edge_1_cur;
//        edge_cur = edge_2_nxt.next;
//      } while( edge_cur != edge_start);
//
//      vert__.edge = edge_start.next.pair;
//      mesh.edge = vert__.edge;
//
//      //TODO: update everything that was in the neighbourhood (vertices, faces)
//
//      return num_new_faces;
//    }


    /**
     * deletes a given vertex from a mesh, including its adjacent edges and faces.<br>
     * the resulting face might not be planar!<br>
     * <br>
     * TODO: check old links, check id ok, when deleting multiple edges (creating a ring of faces)<br>
     *
     * @param mesh  mesh, to delete the vertex from
     * @param vert  vertex to delete
     * @return number of deleted edges ( = number of deleted faces +1)
     */
    public static int deleteVert(HMesh mesh, HVert vert){
        int num_removed_edges = 0;

        HEdge start = vert.edge.next;
        start.face.edge = start;

        HEdge edge = start;

        do{
            edge.face = start.face;
            if( edge.next.pair.orig == vert){
                edge.next = edge.next.pair.next;
                edge.next.orig.edge = edge.next;
                num_removed_edges++;
            }
        }
        while( (edge = edge.next) != start );

        Update.face(start.face); // face is not a triangle
        //TODO: update everything that was in the neighbourhood (vertices, faces)

        mesh.edge = start;
        return num_removed_edges;
    }



    /**
     * mesh.edge = edge.next<br>
     *
     * @param mesh
     * @param edge
     * @return number of removed faces, which is always 1
     */
    public static int deleteEdge(HMesh mesh, HEdge edge){

        HFace face = edge.face; // this face remains part of the mesh
        face.edge = edge.next;

        HEdge edge_A = edge;
        HEdge edge_B = edge.pair;

        HVert vert_A = edge_A.orig;
        HVert vert_B = edge_B.orig;

        vert_A.edge = edge_B.next;
        vert_B.edge = edge_A.next;

        HEdge prev;

        prev = edge_A;
        while((prev=prev.next).pair.orig != vert_A);
        prev.next = edge_B.next;

        prev = edge_B;
        while((prev=prev.next).pair.orig != vert_B) prev.face = face;
        prev.face = face;
        prev.next = edge_A.next;


        // control loop, if everything is linked correctly
//      {
//        Edge cur, start;
//        cur = start = edge.next;
//        int it = 0;
//
//        do {
//          if( cur.face != face ){
//            System.out.println("(MeshOps.delete(Mesh mesh, Edge edge) EDGE NOT LINKED TO RIGHT FACE "+it);
//          }
//          it++;
//        } while( (cur=cur.next) != start);
//        System.out.println("number of edges = "+it);
//      }


        Update.face(face);
        //TODO: update everything that was in the neighborhood (vertices, faces?)

        mesh.edge = edge.next;
        return 1; // return number of removed faces, ...always 1 here
    }


    public static int delete(HMesh mesh, HFace edge){
        //TODO
        return 0;
    }


    /**
     * flips and edge, that is shared by two TRIANGLES!
     *
     * @param mesh
     * @param edge
     */



    /*


     CCW

     |           |          |           |
   --o-----------o--      --o-----------o--
     |         / |          | \         |
     |  A    /   |          |   \   B   |
     |     /     |    ->    |     \     |
     |   /   B   |          |  A    \   |
     | /         |          |         \ |
   --o-----------o--      --o-----------o--
     |           |          |           |
     */
    /**
     *
     * flips an edge with two adjacent triangles.
     *
     *
     * @param mesh
     * @param edge
     */
    public static void edgeFlip(HMesh mesh, HEdge edge){

        // copy old values
        final HEdge edge_A_0 = edge;
        final HEdge edge_A_1 = edge_A_0.next;
        final HEdge edge_A_2 = edge_A_1.next;

        final HEdge edge_B_0 = edge.pair;
        final HEdge edge_B_1 = edge_B_0.next;
        final HEdge edge_B_2 = edge_B_1.next;

        // check, if faces are triangles
        if( edge_A_2.next != edge_A_0) { System.out.println("(edgeFlip) 1. face is not a triangle."); }
        if( edge_B_2.next != edge_B_0) { System.out.println("(edgeFlip) 2. face is not a triangle."); }

        // EDGES
        // link next-edges for the flip-edge
        edge_A_0.next = edge_A_2;
        edge_B_0.next = edge_B_2;

        // link next.next-edges for the flip-edge
        edge_A_0.next.next = edge_B_1;
        edge_B_0.next.next = edge_A_1;

        // link next.next.next-edges for the flip-edge ( to close the loop )
        edge_A_0.next.next.next = edge_A_0;
        edge_B_0.next.next.next = edge_B_0;

        // FACES
        // assure that adjacent faces are linked to the flip-edge.
        edge_A_0.face.edge = edge_A_0;
        edge_B_0.face.edge = edge_B_0;

        // link prev-edge to current face
        edge_A_0.next.next.face = edge_A_0.face;
        edge_B_0.next.next.face = edge_B_0.face;

        // VERTS
        // link new origins of flip-edge
        edge_A_0.orig = edge_B_2.orig;
        edge_B_0.orig = edge_A_2.orig;

        // assure that flip-edge origins point to flip-edge
        edge_A_0.orig.edge = edge_A_0;
        edge_B_0.orig.edge = edge_B_0;

        // assure that opposite origins link to next-next
        edge_A_0.next.next.orig.edge = edge_A_0.next.next;
        edge_B_0.next.next.orig.edge = edge_B_0.next.next;


        // UPDATE FACES
//      Update.faceTriangle(edge_A_0.face);
//      Update.faceTriangle(edge_B_0.face);
        if( !Update.faceTriangle(edge_A_0.face) ){
            //TODO
            System.err.println( "TRIANGLE IS DEGENERATE, cant comput normal");
        }
        if( !Update.faceTriangle(edge_B_0.face) ){
            //TODO
            System.err.println( "TRIANGLE IS DEGENERATE, cant comput normal");
        }


        mesh.edge = edge;


        // error checking - TODO only for debugging purposes
        int count;
        HEdge start;
        HEdge iter;

        if( edge_A_0.orig.edge != edge_A_0) System.err.println("(edgeFlip) error: vert-edge not linked 1");
        if( edge_B_0.orig.edge != edge_B_0) System.err.println("(edgeFlip) error: vert-edge not linked 2 ");

        if( edge_A_0.next.next.orig.edge != edge_A_0.next.next ) System.err.println("(edgeFlip) error: vert-edge not linked 3");
        if( edge_B_0.next.next.orig.edge != edge_B_0.next.next ) System.err.println("(edgeFlip) error: vert-edge not linked 4");

        start = iter = edge_A_0;
        count = 0;
        do
        {
            if( iter.face != start.face ){
                System.err.println("(edgeFlip) error: edge not linked to face "+count);
            }
            count++;
        } while( (iter = iter.next) != start);

        if( count != 3 ) System.err.println("(edgeFlip) error: to many edges");

        start = iter = edge_B_0;
        count = 0;
        do
        {
            if( iter.face != start.face ){
                System.err.println("(edgeFlip) error: edge not linked to face "+count);
            }
            count++;
        } while( (iter = iter.next) != start);

        if( count != 3 ) System.err.println("(edgeFlip) error: to many edges");

    }

    /**
     * inserts a new edge, connecting the two vertices of the given edges.<br>
     * make sure, the two edges are part of the same face (and are not connected already).<br>
     * the result is a new edge, that splits the original face.<br>
     * The two edges of the two vertices (vert_A.edge and vert_B.edge) must point
     * to the same face.<br>
     * After inserting the edge, the two vertice-edges point to the new edge.<br>
     *
     * new created: edge_A, edge_B, face_B<br>
     *
     * mesh.edge = edge_Av
     * vert_A.edge      = edge_A<br>
     * vert_A.edge.face = face_A<br>
     * vert_B.edge      = edge_B<br>
     * vert_B.edge.face = edge_B<br>
     *

     */
    public static boolean insertEdge(HMesh mesh, HEdge edge_A, HEdge edge_B){
        edge_A.orig.edge = edge_A;
        edge_B.orig.edge = edge_B;
        return insertEdge(mesh, edge_A.orig, edge_B.orig);
    }
    /**
     * inserts a new edge, connecting the two given vertices.<br>
     * make sure, the two vertices are part of the same face (and are not connected already).<br>
     * the result is a new edge, that splits the original face.<br>
     * The two edges of the two vertices (vert_A.edge and vert_B.edge) must point
     * to the same face.<br>
     * After inserting the edge, the two vertice-edges point to the new edge.<br>
     *
     * new created: edge_A, edge_B, face_B<br>
     *
     * mesh.edge = edge_Av
     * vert_A.edge      = edge_A<br>
     * vert_A.edge.face = face_A<br>
     * vert_B.edge      = edge_B<br>
     * vert_B.edge.face = edge_B<br>
     *
     * @param mesh
     * @param vert_A
     * @param vert_B
     */
    public static boolean insertEdge(HMesh mesh, HVert vert_A, HVert vert_B){
        // check if the two vertice edges point to the same face.
        if( vert_A.edge.face != vert_B.edge.face ){
            System.err.println("(insertEdge) vert_A.edge.face != vert_B.edge.face" );
            return false;
        }

        HEdge edge_A_old = vert_A.edge;
        HEdge edge_B_old = vert_B.edge;
        // check if the two vertice are already connected
        if( edge_A_old.next == edge_B_old || edge_B_old.next == edge_A_old){
            System.err.println("(insertEdge) edge_A.next == edge_B ||edge_B.next == edge_A" );
            return false;
        }

        HFace face_A     = edge_A_old.face;
        HFace face_B     = new HFace();

        HEdge edge_A_ins = new HEdge();
        HEdge edge_B_ins = new HEdge();

        // pairs
        edge_A_ins.pair = edge_B_ins;
        edge_B_ins.pair = edge_A_ins;

        // faces
        edge_A_ins.face = face_A;  face_A.edge = edge_A_ins;
        edge_B_ins.face = face_B;  face_B.edge = edge_B_ins;

        // origins
        edge_A_ins.orig = vert_A;   vert_A.edge = edge_A_ins;
        edge_B_ins.orig = vert_B;   vert_B.edge = edge_B_ins;

        HEdge iter;

        // Face B: next-links
        for(iter = edge_A_old; iter.next != edge_B_old; iter = iter.next){
            iter.face = face_B;
        }
        iter.face = face_B;
        iter.next = edge_B_ins;
        edge_B_ins.next = edge_A_old;

        // Face A (original face): next-links
        for(iter = edge_B_old; iter.next != edge_A_old; iter = iter.next){
//        iter.face = face_A; // already linked
        }
        iter.face = face_A;
        iter.next = edge_A_ins;
        edge_A_ins.next = edge_B_old;

        mesh.edge = edge_A_ins;

        Update.face(face_A);
        Update.face(face_B);
        return true;
    }


    /**
     * inserts an edge vertex / splits the given edge at param t.<br>
     * <br>
     * mesh.edge           = edge<br>
     * mesh.edge.next.orig = new vertex (given edge points towards the new vertex)<br>
     *
     * @param mesh
     * @param edge the edge to split

     */
    public static void insertVertex(HMesh mesh, HEdge edge, float t){
        if( t <= 0 || t >= 1) return;
        float[] point =  DwVec3.lerp_new(edge.orig.data.v, edge.next.orig.data.v, t);
        insertVertex(mesh, edge, point);
    }

    /**
     * inserts an edge vertex / splits the given edge in half, using the edges'
     * mitpoint.<br>
     * <br>
     * mesh.edge           = edge<br>
     * mesh.edge.next.orig = edge-mitpoint (given edge points towards the new vertex)<br>
     *
     * @param mesh
     * @param edge the edge to split
     */
    public static void insertVertex(HMesh mesh, HEdge edge){
        float[] mid_point = DwVec3.add_new(edge.orig.data.v, edge.next.orig.data.v);
        DwVec3.scale_ref_slf(mid_point, 0.5f);
        insertVertex(mesh, edge, mid_point);
    }



    /**
     * inserts an edge vertex / splits the given edge in half, using the given
     * point as the new vertex.<br>
     *
     * mesh.edge           = edge<br>
     * mesh.edge.next.orig = edge-mitpoint (given edge points towards the new vertex)<br>
     *
     * @param mesh
     * @param edge
     * @param point
     */
    public static void insertVertex(HMesh mesh, HEdge edge, float[] point){


        HVert vert_new = new HVert(point);

        HEdge edge_L = edge;
        HEdge edge_R = edge.pair;

        HEdge edge_L_next = new HEdge();
        edge_L_next.face = edge_L.face;
        edge_L_next.orig = vert_new;
        edge_L_next.pair = edge_R;
        edge_L_next.next = edge_L.next;
        edge_R.pair = edge_L_next;
        edge_L.next = edge_L_next;

        HEdge edge_R_next = new HEdge();
        edge_R_next.face = edge_R.face;
        edge_R_next.orig = vert_new;
        edge_R_next.pair = edge_L;
        edge_R_next.next = edge_R.next;
        edge_L.pair = edge_R_next;
        edge_R.next = edge_R_next;

        vert_new.edge = edge_L_next;

        mesh.edge = edge;
    }

    /**
     * splits an edge of two adjacent faces (polygons, triangles, ...), using the given point as the new
     * vertex position.
     * the adjacent faces are triangulated from the new vertex, to every
     * other vertex, the polygon contains
     *
     * @param mesh
     * @param point
     * @param edge
     * @return
     */
//    public static int edgeSplit(Mesh mesh, Edge edge, float[] point){
//      int num_new_faces = 0;
//
//      Vert vert_new = new Vert(point);
//
//
//      // edge
//      vert_new.edge = edge;
//
//      return num_new_faces;
//    }

    /**
     * triangulate a face, given by its adjacent edge.<br>
     * The triangulation is done from the edges origin, to all other face-verts.<br>
     * except the edge's preceding and next vertex (of course).<br>
     * in other words, the polygon becomes a triangle-fan.<br>
     *
     * mesh.edge remains unchanged.<br>
     *
     * <br>
     * in case the face has 3 verts, the face stays the same.<br>
     * in case the face has 4 verts, 1 edge is added.<br>
     * in case the face has 5 verts, 2 edges are added.<br>
     *
     *
     * @param edge
     * @return
     */

    public static int triangulateFace(HMesh mesh, HEdge edge, List<HFace> faces_new){

        int num_new_faces = 0;

        final HEdge edge__ = edge;
        final HFace face__ = edge.face;
        final HVert vert__ = edge.orig;

        HEdge edge_0 = edge__;
        HEdge edge_1 = edge_0.next;
        HEdge edge_2 = edge_1.next;

//      int counter = 0;
        while( edge_2.next.orig != vert__ ){

            HFace face_N = new HFace();
            if( faces_new != null) faces_new.add(face_N);
            num_new_faces++;

            HEdge edge_N = newEdge(edge_2.orig, face_N, vert__, face__);

//        edge_0.next = edge_1;
            edge_1.next = edge_N;
            edge_N.next = edge_0;

            edge_0.face = face_N;
            edge_1.face = face_N;
//        edge_N.face = face_N;

            if( !Update.faceTriangle(face_N) ){
                //TODO
                System.err.println( "TRIANGLE IS DEGENERATE, cant comput normal");
            }
            edge_0 = edge_N.pair;
            edge_1 = (edge_0.next = edge_2);
            edge_2 = edge_1.next;

//        counter++;
        }

//      System.out.println("counter = "+counter);
        if( num_new_faces > 0 ){
            edge_0.next.next.next = edge_0; // close triangle loop
            face__.edge = edge_0.next.next;

            if( !Update.faceTriangle(face__) ){
                //TODO
                System.err.println( "TRIANGLE IS DEGENERATE, cant comput normal");
            }
        }

//      vert__.edge  = edge__;
//      edge__.orig = vert__;
//      mesh.edge = edge__;
        return num_new_faces;
    }


//    public static int triangulateFace(Mesh mesh, Edge edge, List<Face> faces_new){
//
//      int num_new_faces = 0;
//      Edge edge__ = edge;
//      Face face__ = edge.face;
//      Vert vert__ = edge.orig;
//
//      int counter = 0;
//      while( edge.next.next.next.orig != vert__ ){
//        Edge edge_0 = edge;
//        Edge edge_1 = edge.next;
//        Edge edge_2 = edge_1.next;
//
//        Face face_N = new Face(); faces_new.add(face_N); num_new_faces++;
//        Edge edge_N = newEdge(edge_2.orig, face_N, vert__, face__);
//        edge_1.next = edge_N;
//        edge_N.next = edge;
//
//        edge_0.face = face_N;
//        edge_1.face = face_N;
//
//        Update.face(face_N);
//
//        edge = edge_N.pair;
//        edge.next = edge_2;
//
//        counter++;
//      }
//
//      System.out.println("counter = "+counter);
//      if( num_new_faces > 0 ){
//        edge.next.next.next = edge; // close triangle loop
//        face__.edge = edge.next.next;
//        Update.face(face__);
//      }
//
//      mesh.edge = edge__;
//      return num_new_faces;
//    }


    private static HEdge newEdge(
            HVert vert_L, HFace face_L,
            HVert vert_R, HFace face_R){

        HEdge edge_L = new HEdge();
        HEdge edge_R = new HEdge();

        edge_L.pair = edge_R;
        edge_R.pair = edge_L;

        edge_L.face = face_L; face_L.edge = edge_L;
        edge_R.face = face_R; face_R.edge = edge_R;

        edge_L.orig = vert_L; vert_L.edge = edge_L;
        edge_R.orig = vert_R; vert_R.edge = edge_R;

        return edge_L;
    }






//   public static int triangulateFace(Mesh mesh, Edge edge, List<Face> faces_new){
//
//      if( edge.next.next.next == edge ){
//        System.out.println("(triangulateFace(Edge edge) - face is triangle");
//        return 0; // face is a triangle, so no triangulation makes sense
//      }
//
//      int num_new_faces = 0;
//
//      // keep start values
////      Edge edge_start = edge;
//      Vert vert__ = edge.orig;
//      Face face_A = edge.face;
//
//      // get last edge
//      Edge last = edge;
//      while( last.next != edge ) last = last.next;
//
//      Edge edge_0 = edge;
//      Edge edge_1 = edge.next;
//      Edge edge_2 = edge.next.next;
//
//      // new face
//      Face face_B = new Face();
//      faces_new.add(face_B);
//      num_new_faces++;
//
//      // new edge
//      Edge edge_2_new = new Edge();
//      edge_2_new.next = edge_0;
//      edge_2_new.orig = edge_2.orig;
//
//      face_B.edge = edge_0;
//      edge_0.face = face_B;
//      edge_1.face = face_B;
//      edge_2_new.face = face_B;
//
//      edge_0.next = edge_1;
//      edge_1.next = edge_2_new;
//      edge_2_new.next = edge_0;
//
//      // new pair edge == start edge for next iteration
//      Edge edge_2_new_pair = new Edge();
//      edge_2_new_pair.next = edge_2;
//      edge_2_new_pair.face = face_A;
//      edge_2_new_pair.orig = vert__;
//      face_A.edge = edge_2_new_pair;
//
//      edge_2_new.pair = edge_2_new_pair;
//      edge_2_new_pair.pair = edge_2_new;
//
//      last.next = edge_2_new_pair;
//
//      //TODO: loop
//
//      Update.face(face_B);
//
//      Update.face(face_A);
//
//      mesh.edge = edge_2_new_pair;
//
//      return num_new_faces;
//    }

}