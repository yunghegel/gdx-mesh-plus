package org.yunghegel.gdx.meshplus.mesh.shapes;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.math.Vector3;
import org.yunghegel.gdx.meshplus.mesh.math.Vector3f;

public abstract class ModelCreator {
    public int shapeType = GL20.GL_TRIANGLES;
    public MeshPartBuilder.VertexInfo v0 = new MeshPartBuilder.VertexInfo();
    public MeshPartBuilder.VertexInfo v1 = new MeshPartBuilder.VertexInfo();
    public MeshPartBuilder b;
    public int faceCount=0;
    public int vertexCount=0;
    public Material mat = new Material();

    public short i1, i2, i3, i4;


    public abstract Model create();

    public ModelCreator setShapeType(int shapeType) {
        this.shapeType = shapeType;
        return this;
    }

    public ModelCreator setMaterial(Material mat) {
        this.mat = mat;
        return this;
    }

    protected Vector3f calculateNormal(Vector3f v1, Vector3f v2, Vector3f v3) {
        Vector3f v1v2 = (Vector3f) v2.sub(v1);
        Vector3f v1v3 = (Vector3f) v3.sub(v1);
        Vector3f normal = v1v2.cross(v1v3);
        normal.normalize();
        return normal;
    }

    //normal for quad
    protected Vector3f calculateNormal(Vector3f v1, Vector3f v2, Vector3f v3, Vector3f v4) {
        Vector3f v1v2 = (Vector3f) v2.sub(v1);
        Vector3f v1v3 = (Vector3f) v3.sub(v1);
        Vector3f v1v4 = (Vector3f) v4.sub(v1);
        Vector3f normal = v1v2.cross(v1v3);
        normal.normalize();
        return normal;
    }


    protected float[] computeNormals(float[] vertices, short[] indices, VertexAttributes attributesGroup) {
        int posOffset = attributesGroup.getOffset(VertexAttributes.Usage.Position);
        int normalOffset = attributesGroup.getOffset(VertexAttributes.Usage.Normal);
        int stride = attributesGroup.vertexSize / 4;

        Vector3 vab = new Vector3();
        Vector3 vac = new Vector3();
        if(indices != null){
            for(int index = 0, count = indices.length ; index<count ; ){

                int vIndexA = indices[index++] & 0xFFFF;
                float ax = vertices[vIndexA * stride + posOffset];
                float ay = vertices[vIndexA * stride + posOffset+1];
                float az = vertices[vIndexA * stride + posOffset+2];

                int vIndexB = indices[index++] & 0xFFFF;
                float bx = vertices[vIndexB * stride + posOffset];
                float by = vertices[vIndexB * stride + posOffset+1];
                float bz = vertices[vIndexB * stride + posOffset+2];

                int vIndexC = indices[index++] & 0xFFFF;
                float cx = vertices[vIndexC * stride + posOffset];
                float cy = vertices[vIndexC * stride + posOffset+1];
                float cz = vertices[vIndexC * stride + posOffset+2];

                vab.set(bx,by,bz).sub(ax,ay,az);
                vac.set(cx,cy,cz).sub(ax,ay,az);
                Vector3 n = vab.crs(vac).nor();

                vertices[vIndexA * stride + normalOffset] = n.x;
                vertices[vIndexA * stride + normalOffset+1] = n.y;
                vertices[vIndexA * stride + normalOffset+2] = n.z;

                vertices[vIndexB * stride + normalOffset] = n.x;
                vertices[vIndexB * stride + normalOffset+1] = n.y;
                vertices[vIndexB * stride + normalOffset+2] = n.z;

                vertices[vIndexC * stride + normalOffset] = n.x;
                vertices[vIndexC * stride + normalOffset+1] = n.y;
                vertices[vIndexC * stride + normalOffset+2] = n.z;
            }
        }else{
            for(int index = 0, count = vertices.length / stride ; index<count ; ){

                int vIndexA = index++;
                float ax = vertices[vIndexA * stride + posOffset];
                float ay = vertices[vIndexA * stride + posOffset+1];
                float az = vertices[vIndexA * stride + posOffset+2];

                int vIndexB = index++;
                float bx = vertices[vIndexB * stride + posOffset];
                float by = vertices[vIndexB * stride + posOffset+1];
                float bz = vertices[vIndexB * stride + posOffset+2];

                int vIndexC = index++;
                float cx = vertices[vIndexC * stride + posOffset];
                float cy = vertices[vIndexC * stride + posOffset+1];
                float cz = vertices[vIndexC * stride + posOffset+2];

                vab.set(bx,by,bz).sub(ax,ay,az);
                vac.set(cx,cy,cz).sub(ax,ay,az);
                Vector3 n = vab.crs(vac).nor();

                vertices[vIndexA * stride + normalOffset] = n.x;
                vertices[vIndexA * stride + normalOffset+1] = n.y;
                vertices[vIndexA * stride + normalOffset+2] = n.z;

                vertices[vIndexB * stride + normalOffset] = n.x;
                vertices[vIndexB * stride + normalOffset+1] = n.y;
                vertices[vIndexB * stride + normalOffset+2] = n.z;

                vertices[vIndexC * stride + normalOffset] = n.x;
                vertices[vIndexC * stride + normalOffset+1] = n.y;
                vertices[vIndexC * stride + normalOffset+2] = n.z;
            }
        }
        return vertices;
    }



}
