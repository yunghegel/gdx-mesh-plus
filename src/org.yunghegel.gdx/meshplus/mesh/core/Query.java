package org.yunghegel.gdx.meshplus.mesh.core;

import org.yunghegel.gdx.meshplus.mesh.data.halfedge.HEdge;
import org.yunghegel.gdx.meshplus.mesh.data.halfedge.HFace;
import org.yunghegel.gdx.meshplus.mesh.data.halfedge.HMesh;
import org.yunghegel.gdx.meshplus.mesh.data.halfedge.HVert;
import org.yunghegel.gdx.meshplus.mesh.math.DwVec3;

import java.util.ArrayList;
import java.util.List;

public abstract class Query {

    public static final double EPSILON = 0.001;

    /** get adjacent VERTICES of given VERTEX.<br> rotation: CW */
    public static void adjacentVerts(List<HVert> list, HVert vert){
        if(vert.edge==null)
            return;

        HEdge edge = vert.edge;


        do {
            if (edge==null||edge.pair == null||edge.pair.orig == null||edge.pair.next == null) {
                return;
            }
            list.add(edge.pair.orig);    } while( (edge = edge.pair.next) != vert.edge);
    }
    /** get 2 adjacent VERTICES of given EDGE.<br> A to B */
    public static void adjacentVerts(List<HVert> list, HEdge edge){
        list.add( edge.orig);
        list.add( edge.next.orig);
    }
    /** get adjacent VERTICES of given FACE.<br> rotation: CCW */
    public static void adjacentVerts(List<HVert> list, HFace face){
        HEdge edge = face.edge;
        do {   list.add(edge.orig);         } while( (edge = edge.next) != face.edge);
    }

    /** get adjacent (outgoing) EDGES of given VERTEX.<br> rotation: CW */
    public static void adjacentEdges(List<HEdge> list, HVert vert){
        HEdge edge = vert.edge;
        do {   list.add(edge);              } while( (edge = edge.pair.next) != vert.edge);
    }
    /** get adjacent EDGES of given EDGE (edges of vertex A + edges of vertex B).<br> rotation: CW */
    public static void adjacentEdges(List<HEdge> list, HEdge edge){
        adjacentEdges(list, edge.orig);
        adjacentEdges(list, edge.next.orig);
    }
    /** get adjacent EDGES of given FACE.<br> rotation: CCW */
    public static void adjacentEdges(List<HEdge> list, HFace face){
        HEdge edge = face.edge;
        do {   list.add(edge);              } while( (edge = edge.next) != face.edge);
    }

    /** get adjacent FACES of given VERTEX.<br> rotation: CW */
    public static void adjacentFaces(List<HFace> list, HVert vert){
        HEdge edge = vert.edge;
        do {   list.add(edge.face);         } while( (edge = edge.pair.next) != vert.edge);
    }
    /** get 2 adjacent (left+right) FACES of given EDGE. */
    public static void adjacentFaces(List<HFace> list, HEdge edge){
        list.add( edge.face);
        list.add( edge.pair.face);
    }
    /** get adjacent FACES (that share an edge) of given FACE.<br> rotation: CCW */
    public static void adjacentFaces(List<HFace> list, HFace face){
        HEdge edge = face.edge;
        do {   list.add(edge.pair.face);    } while( (edge = edge.next) != face.edge);
    }
    public static void adjacentFacesAll(List<HFace> list, HFace face){
        //TODO
        System.err.println("TODO: Query.adjacentFacesAll is not impleemented");
    }

    /**
     * test if a given face is planar.
     * @param face
     * @return true, if planar
     */
    public static boolean isPlanar(HFace face){
        List<HVert> adj = new ArrayList<HVert>();
        Query.adjacentVerts(adj, face);
        for(HVert vert : adj){
            float dis = (DwVec3.dot(vert.data.v, face.data.n) - face.data.d); //TODO: Plane in face-data
            if( Math.abs(dis) > EPSILON ){ //TODO: relative epsilon?
                return false;
            }
        }
        return true;
    }
    /**
     * test all mesh-faces for beeing planar, and return them in a list.
     *
     * @param mesh half-edge mesh
     * @return list of non-planar faces.
     */
    public static List<HFace> nonPlanarFaces(HMesh mesh){
        // get all faces
        HFace[] faces = Collect.faces(null, mesh);
        // save non-planar faces
        List<HFace> nonplanar = new ArrayList<HFace>();
        for( HFace face : faces ){
            if( !Query.isPlanar(face) ) nonplanar.add(face);
        }
        return nonplanar;
    }


    //TODO: non convex faces



    /**
     * tries to find an edge, that shares both, face_A and face_B.
     * this method can also be used, for checking if two faces are adjacent.
     *
     * @param face_A
     * @param face_B
     * @return the sharing edge (on face_A), or null, of the faces are not adjacent.
     */
    public static HEdge sharingEdge(HFace face_A, HFace face_B){
        HEdge start = face_A.edge;
        HEdge iter  = start;
        do {
            if( iter.pair.face == face_B)
                return iter;
        } while( (iter=iter.next) != start );

        return null;
    }
}