package org.yunghegel.gdx.meshplus.mesh.core;

import org.yunghegel.gdx.meshplus.mesh.data.halfedge.HEdge;
import org.yunghegel.gdx.meshplus.mesh.data.halfedge.HFace;
import org.yunghegel.gdx.meshplus.mesh.data.halfedge.HMesh;
import org.yunghegel.gdx.meshplus.mesh.data.halfedge.HVert;

import java.util.Arrays;

public abstract class Collect{

    /**
     * collect all faces of the mesh.<br>
     * stack, is used for iteration, and also for storing the faces.<br>
     * the method will have the best performance if stack is allocated to about <br>
     * the number of faces that are expected.<br>
     * anyways, the returned list contains all faces.
     */
    public static HFace[] faces(HFace[] stack, HMesh mesh){

        final int flag = ++mesh.flag.visited;

        HFace face = mesh.edge.face;

        int ptr_beg = 0;
        int ptr_end = 0;
        if( stack == null ) stack = new HFace[100];
        stack[ptr_end++] = mesh.edge.face;

        face.flag.visited = flag;
        while( ptr_beg < ptr_end ){

            face = stack[ptr_beg++];
            HEdge start = face.edge;
            HEdge edge = start;
            do {
                face = edge.pair.face;
                if( face.flag.visited != flag ){
                    if( ptr_end >= stack.length){
                        stack = Arrays.copyOf(stack, ptr_end<<1);
                    }
                    stack[ptr_end++] = face;
                    face.flag.visited = flag;
                }
            }
            while( (edge = edge.next) != start);
        }

        if( stack.length > ptr_end )
            stack = Arrays.copyOf(stack, ptr_end);
        return stack;
    }



    /**
     * collect all edges of the mesh.<br>
     * stack, is used for iteration, and also for storing the edges.<br>
     * the method will have the best performance if stack is allocated to about <br>
     * the number of edges that are expected.<br>
     * anyways, the returned list contains all edges, this is: only
     * one half-edge per edge, although both are flagged as visited.
     */
    public static HEdge[] edges(HEdge[] stack, HMesh mesh){

        final int flag = ++mesh.flag.visited;
        HEdge edge = mesh.edge;

        int ptr_beg = 0;
        int ptr_end = 0;
        if( stack == null ) stack = new HEdge[100];
        stack[ptr_end++] = mesh.edge;

        edge.flag.visited = flag;
        while( ptr_beg < ptr_end ){

            edge = stack[ptr_beg++];

            HEdge start    = edge.pair;
            HEdge edge_adj = start;
            do {
                if( edge_adj.     flag.visited != flag &&  // only one of the two half-edges
                        edge_adj.pair.flag.visited != flag )
                {
                    if( ptr_end >= stack.length){
                        stack = Arrays.copyOf(stack, ptr_end<<1);
                    }
                    stack[ptr_end++] = edge_adj;
                    edge_adj.flag.visited = flag;
                }
            } while( (edge_adj = edge_adj.pair.next) != start);

        }
        if( stack.length > ptr_end )
            stack = Arrays.copyOf(stack, ptr_end);
        return stack;
    }


    /**
     * collect all verts of the mesh.<br>
     * stack, is used for iteration, and also for storing the verts.<br>
     * the method will have the best performance if stack is allocated to about <br>
     * the number of verts that are expected.<br>
     * anyways, the returned list contains all verts.<br>
     * during collecting, the vertex' ID is set, based on the position in the list.<br>
     * this is useful for converting to an IFS-representation.
     */
    public static HVert[] verts(HVert[] stack, HMesh mesh){

        int flag = ++mesh.flag.visited;
        HVert vert = mesh.edge.orig;

        int ptr_beg = 0;
        int ptr_end = 0;
        if( stack == null ) stack = new HVert[100];
        vert.flag.visited = flag;
        vert.data.id = ptr_end;
        stack[ptr_end++] = vert;

        while( ptr_beg < ptr_end ){
            vert = stack[ptr_beg++];

            HEdge edge = vert.edge.pair;
            HEdge iter = edge;
            do{
                if( iter.orig.flag.visited != flag ){
                    if( ptr_end >= stack.length){
                        stack = Arrays.copyOf(stack, ptr_end<<1);
                    }
                    iter.orig.flag.visited = flag;
                    iter.orig.data.id = ptr_end;
                    stack[ptr_end++] = vert = iter.orig;
                }
            } while( (iter=iter.next.pair) != edge);
        }

        if( stack.length > ptr_end )
            stack = Arrays.copyOf(stack, ptr_end);
        return stack;
    }

    /**
     * collect all verts of a gdx mesh.<br>
     * stack, is used for iteration, and also for storing the verts.<br>
     */

    public static HVert[] verts(HVert[] stack, com.badlogic.gdx.graphics.Mesh mesh){

        float[] vertices = new float[mesh.getNumVertices()*3];
        mesh.getVertices(vertices);

        int ptr_beg = 0;
        int ptr_end = 0;
        if( stack == null ) stack = new HVert[mesh.getNumVertices()];
        for(int i=0; i<mesh.getNumVertices(); i++){
            HVert vert = new HVert(vertices[i*3], vertices[i*3+1], vertices[i*3+2]);
            vert.data.id = ptr_end;
            stack[ptr_end++] = vert;
        }
        return stack;

    }
}