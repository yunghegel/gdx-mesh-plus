package org.yunghegel.gdx.meshplus.mesh.data.halfedge;

import org.yunghegel.gdx.meshplus.mesh.data.gdx.MeshInfo;

public class HMesh {
    public HEdge edge;



    public MeshData data;
    public Flag flag;
    public HMesh(){
        flag = new Flag();
        data = new MeshData();
    }
    //TODO: face-count, edge-count, vert-count - keep counters?
    public static class MeshData{
        public String name = "halfedge_mesh_unnamed";
        public HFace[] faces;
        public HVert[] verts;
        public HEdge[] edges;
        public MeshInfo info;


        public HFace[] faces() {
            return faces;
        }

        public void setFaces(HFace[] faces) {
            this.faces = faces;
        }

        public HVert[] verts() {
            return verts;
        }

        public void setVerts(HVert[] verts) {
            this.verts = verts;
        }

        public HEdge[] edges() {
            return edges;
        }

        public void setEdges(HEdge[] edges) {
            this.edges = edges;
        }

        @Override
        public String toString() {
            return "MeshData{" +
                    "name='" + name + '\'' +
                    ", faces=" + faces.length +
                    ", verts=" + verts.length +
                    ", edges=" + edges.length +
                    '}';
        }
    }
}

