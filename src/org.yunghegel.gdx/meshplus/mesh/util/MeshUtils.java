package org.yunghegel.gdx.meshplus.mesh.util;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttribute;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.model.Node;
import com.badlogic.gdx.graphics.g3d.model.NodePart;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;

import java.util.function.Consumer;

public class MeshUtils {

    private static final Vector3 v = new Vector3();
    private static final Matrix4 m = new Matrix4();

    /**
     * Get Vertices count of a Model
     */
    public static int getVerticesCount(Model model) {
        int vertices = 0;
        for (Mesh mesh : model.meshes) {
            vertices += mesh.getNumVertices();
        }
        return vertices;
    }

    /**
     * Get Indices count of a Model
     */
    public static int getIndicesCount(Model model) {
        int indices = 0;
        for (Mesh mesh : model.meshes) {
            indices += mesh.getNumIndices();
        }
        return indices;
    }

    public static float[] extractVerts(Mesh mesh) {
        float[] vertices = new float[mesh.getNumVertices() * 3];
        mesh.getVertices(vertices);
        return vertices;
    }

    public static float[] extractTriangles(ModelInstance modelInstance) {
        int indices = 0;
        Array<Node> nodes = collectNodes(modelInstance);
        for(Node node : nodes)
            for(NodePart part : node.parts)
                indices += part.meshPart.size;
        float[] triangles = new float[indices * 3];
        int offset = 0;
        for(Node node : nodes)
            for(NodePart part : node.parts)
                offset = extractTriangles(triangles, offset, node, part, modelInstance.transform);
        return triangles;
    }

    public static int extractTriangles(float[] triangles, int triangleOffset, Node node, NodePart part, Matrix4 transform) {
        int size = part.meshPart.size;
        int offset = part.meshPart.offset;
        Mesh mesh = part.meshPart.mesh;
        short[] indices = new short[size];
        mesh.getIndices(offset, size, indices, 0);
        int stride = mesh.getVertexSize() / 4;
        float[] vertices = new float[mesh.getNumVertices() * stride];
        mesh.getVertices(vertices);
        VertexAttribute attribute = mesh.getVertexAttribute(VertexAttributes.Usage.Position);
        int posOffset = attribute.offset / 4;
        m.set(node.globalTransform).mul(transform);
        for(int i=0 ; i<size ; i+=3){
            int vertex = (int)(indices[i] & 0xFFFF);
            int vindex = vertex * stride + posOffset;
            float x = vertices[vindex];
            float y = vertices[vindex+1];
            float z = vertices[vindex+2];
            v.set(x, y, z).mul(m);
            triangles[triangleOffset++] = v.x;
            triangles[triangleOffset++] = v.y;
            triangles[triangleOffset++] = v.z;
        }
        return triangleOffset;
    }

    public static void eachNodeRecusrsive(Iterable<Node> nodes, Consumer<Node> callback){
        for(Node node : nodes){
            callback.accept(node);
            if(node.hasChildren()) eachNodeRecusrsive(node.getChildren(), callback);
        }
    }

    public static void eachNodeRecusrsive(Node node, Consumer<Node> callback){
        callback.accept(node);
        if(node.hasChildren()) eachNodeRecusrsive(node.getChildren(), callback);
    }

    public static Array<NodePart> collectNodeParts(ModelInstance modelInstance) {
        Array<NodePart> results = new Array<NodePart>();
        eachNodeRecusrsive(modelInstance.nodes, node->results.addAll(node.parts));
        return results;
    }
    public static Array<Node> collectNodes(ModelInstance modelInstance) {
        Array<Node> results = new Array<Node>();
        eachNodeRecusrsive(modelInstance.nodes, node->results.add(node));
        return results;
    }

    public static void eachNodePartRecusrsive(Iterable<Node> nodes, Consumer<NodePart> callback) {
        eachNodeRecusrsive(nodes, node->{
            for(NodePart part : node.parts){
                callback.accept(part);
            }
        });
    }
    public static void eachNodePartRecusrsive(Node node, Consumer<NodePart> callback) {
        eachNodeRecusrsive(node, n->{
            for(NodePart part : n.parts){
                callback.accept(part);
            }
        });
    }

    /**
     * Input a gdx mesh. receive a 2d float array where
     */

}
