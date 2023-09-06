package org.yunghegel.gdx.meshplus.mesh.shapes;

import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Vector3;
import org.yunghegel.gdx.meshplus.mesh.math.Mathf;

public class CapsuleCreator extends ModelCreator{

    private float topRadius;
    private float bottomRadius;
    private float cylinderHeight;
    private float topCapHeight;
    private float bottomCapHeight;
    private int topCapSegments;
    private int bottomCapSegments;
    private int cylinderSegments;
    private int rotationSegments;

    public CapsuleCreator(float topRadius, float bottomRadius, float cylinderHeight, float topCapHeight, float bottomCapHeight, int topCapSegments, int bottomCapSegments, int cylinderSegments, int rotationSegments){
        this.topRadius = topRadius;
        this.bottomRadius = bottomRadius;
        this.cylinderHeight = cylinderHeight;
        this.topCapHeight = topCapHeight;
        this.bottomCapHeight = bottomCapHeight;
        this.topCapSegments = topCapSegments;
        this.bottomCapSegments = bottomCapSegments;
        this.cylinderSegments = cylinderSegments;
        this.rotationSegments = rotationSegments;
    }

    @Override
    public Model create() {
        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        short i1, i2, i3, i4;


        b = modelBuilder.part("capsule", shapeType, VertexAttributes.Usage.Position, mat);


        createTopCapVertices(b);
        createCylinderVertices(b);
        createBottomCapVertices(b);
        createQuadFaces(b);
        createTriangleFaces();

        return modelBuilder.end();
    }

    private void createTopCapVertices(MeshPartBuilder b) {
        createCapVertices(-1, topRadius, topCapHeight, topCapSegments,b);
    }

    private void createBottomCapVertices(MeshPartBuilder b) {
        createCapVertices(1, bottomRadius, bottomCapHeight, bottomCapSegments,b);
    }

    private void createCapVertices(int a, float radius, float height, int segments, MeshPartBuilder b) {
        float yOffset = cylinderHeight / 2.0f * a;
        float stepTheta = Mathf.HALF_PI / segments;
        float stepPhi = Mathf.TWO_PI / rotationSegments;
        float thetaA = segments * stepTheta;
        for (int i = 1; i < segments; i++) {
            float theta = a == 1 ? thetaA - (i * stepTheta) : i * stepTheta;
            for (int j = 0; j < rotationSegments; j++) {
                float phi = j * stepPhi;
                float x = radius * Mathf.cos(phi) * Mathf.sin(theta);
                float y = height * a * Mathf.cos(theta) + yOffset;
                float z = radius * Mathf.sin(phi) * Mathf.sin(theta);
                b.vertex(x, y, z);
                Vector3 normal = new Vector3(x, y, z).nor();

            }
        }
    }

    private void createCylinderVertices(MeshPartBuilder b) {
        float radiusStep = (topRadius - bottomRadius) / cylinderSegments;
        float segmentHeight = cylinderHeight / cylinderSegments;
        float angle = Mathf.TWO_PI / rotationSegments;
        for (int i = 0; i <= cylinderSegments; i++) {
            for (int j = 0; j < rotationSegments; j++) {
                float x = (topRadius - (i * radiusStep)) * (Mathf.cos(j * angle));
                float y = i * segmentHeight - (cylinderHeight / 2f);
                float z = (topRadius - (i * radiusStep)) * (Mathf.sin(j * angle));
                b.vertex(x, y, z);
            }
        }
    }

    private void createTopCapTriangleFan() {
        triangleFan(0, -cylinderHeight / 2 - topCapHeight,b);
    }

    private void createBottomCapTriangleFan() {
        int offset = (getSegmentsCount() - 2) * rotationSegments;
        triangleFan(offset, cylinderHeight / 2f + bottomCapHeight,b);
    }

    private void triangleFan(int indexOffset, float centerY,MeshPartBuilder b) {

        b.vertex(0, centerY, 0);
        short centerIndex = (short) b.lastIndex();
        for (short i = 0; i < rotationSegments; i++) {
            short idx0 = (short) (i + indexOffset);
            short idx1 = (short) ((i + 1) % rotationSegments + indexOffset);
            if (indexOffset == 0) {
               b.triangle(idx0, idx1, centerIndex);
            } else {
                b.triangle(idx1, idx0, centerIndex);
            }
        }
    }

    private void createQuadFaces(MeshPartBuilder b) {
        for (short i = 0; i < getSegmentsCount() - 2; i++)
            for (short j = 0; j < rotationSegments; j++)
                addFace(i, j,b);
    }

    private void createTriangleFaces() {
        createTopCapTriangleFan();
        createBottomCapTriangleFan();
    }

    private void addFace(int i, int j, MeshPartBuilder b) {
        short idx0 = toOneDimensionalIndex(i, j);
        short idx1 = toOneDimensionalIndex(i + 1, j);
        short idx2 = toOneDimensionalIndex(i + 1, (j + 1) % rotationSegments);
        short idx3 = toOneDimensionalIndex(i, (j + 1) % rotationSegments);
//        b.rect(idx0, idx1, idx2, idx3);
        //as two triangles
        b.triangle(idx0, idx1, idx2);
        b.triangle(idx0, idx2, idx3);

    }

    private short toOneDimensionalIndex(int i, int j) {
        return (short) Mathf.toOneDimensionalIndex(i, j, rotationSegments);
    }

    private int getSegmentsCount() {
        return topCapSegments + cylinderSegments + bottomCapSegments;
    }

}
