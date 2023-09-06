package org.yunghegel.gdx.meshplus.mesh.shapes;

import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import org.yunghegel.gdx.meshplus.mesh.math.Mathf;

public class DiscCreator extends ModelCreator{

    private int rotationSegments;
    private int discSegments;
    private float outerRadius;
    private float innerRadius;


    public DiscCreator(float outerRadius,float innerRadius, int rotationSegments, int discSegments){
        this.outerRadius = outerRadius;
        this.innerRadius = innerRadius;
        this.rotationSegments = rotationSegments;
        this.discSegments = discSegments;
    }

    @Override
    public Model create() {
        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();

        b = modelBuilder.part("disc", shapeType, VertexAttributes.Usage.Position, mat);
        if (innerRadius > 0) {
            createDisc(discSegments, innerRadius);
        } else {
            createDisc(discSegments - 1, outerRadius / discSegments);
            createTriangleFan();
        }

        return modelBuilder.end();
    }

    private void addFace(int i, int j, int segments) {
        if (i >= segments)
            return;
        int idx0 = toOneDimensionalIndex(i, j);
        int idx1 = toOneDimensionalIndex(i + 1, j);
        int idx2 = toOneDimensionalIndex(i + 1, j + 1);
        int idx3 = toOneDimensionalIndex(i, j + 1);
        b.rect((short) idx0, (short) idx1, (short) idx2, (short) idx3);
    }

    private int toOneDimensionalIndex(int i, int j) {
        return Mathf.toOneDimensionalIndex(i, j % rotationSegments, rotationSegments);
    }

    private void createDisc(int segments, float startRadius) {
        float angle = 0;
        float radius = (outerRadius - innerRadius) / (float) discSegments;


        for (int i = 0; i <= segments; i++) {
            for (int j = 0; j < rotationSegments; j++) {
                float x = (startRadius + (i * radius)) * Mathf.cos(angle);
                float y = 0;
                float z = (startRadius + (i * radius)) * Mathf.sin(angle);
                b.vertex(x, y, z);
                addFace(i, j, segments);
                angle += Mathf.TWO_PI / (float) rotationSegments;
            }
            angle = 0;
        }
    }

    private void createTriangleFan() {
        int idx = b.lastIndex();
        b.vertex(0, 0, 0);
        for (int i = 0; i < rotationSegments; i++) {
            b.triangle((short) idx, (short) i, (short) ((i + 1) % rotationSegments));

        }
    }




}
