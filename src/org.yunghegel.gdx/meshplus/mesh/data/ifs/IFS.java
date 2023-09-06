package org.yunghegel.gdx.meshplus.mesh.data.ifs;

import java.util.ArrayList;

public class IFS {

    public float[][]   v, t, n;
    public IFS.Face[]  f;
    public IFS.Group[] g;
    public IFS.Mesh[]  m;

    public IFS(){
    }
    public IFS(Group[] g, IFS.Mesh[] m, IFS.Face[] f, float[][] v, float[][] t, float[][] n){
        set(g, m, f, v, t, n);
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


    public IFS.Group getGroupByName(String name){
        for(IFS.Group g_ : g){
            if( g_.name.equals(name))
                return g_;
        }
        return null;
    }

    public IFS.Mesh getMeshByName(String name){
        for(IFS.Mesh m_ : m){
            if( m_.name.equals(name))
                return m_;
        }
        return null;
    }



    // obj ID: "g"
    public static class Group implements Comparable<Group>{
        public IFS parent;
        public ArrayList<IFS.Mesh> m = new ArrayList<IFS.Mesh>();
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


}