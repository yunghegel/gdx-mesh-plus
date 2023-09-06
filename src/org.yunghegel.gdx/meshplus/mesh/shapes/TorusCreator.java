package org.yunghegel.gdx.meshplus.mesh.shapes;

import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import org.yunghegel.gdx.meshplus.mesh.math.Mathf;

public class TorusCreator extends ModelCreator {

    private float majorRadius;
    private float minorRadius;
    private int majorSegments;
    private int minorSegments;

    public TorusCreator(float majorRadius, float minorRadius, int majorSegments, int minorSegments, Material mat){
        this.majorRadius = majorRadius;
        this.minorRadius = minorRadius;
        this.majorSegments = majorSegments;
        this.minorSegments = minorSegments;
        this.mat = mat;
    }

    @Override
    public Model create() {
        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();

        MeshPartBuilder builder = modelBuilder.part("torus", shapeType, VertexAttributes.Usage.Position, mat);

        float stepU = Mathf.TWO_PI / minorSegments;
        float stepV = Mathf.TWO_PI / majorSegments;

        for (int i = 0; i < majorSegments; i++){
            float v = i * stepV;
            for (int j = 0; j < minorSegments; j++) {
                float u = j * stepU;
                float x = (majorRadius + minorRadius * Mathf.cos(u)) * Mathf.cos(v);
                float y = minorRadius * Mathf.sin(u);
                float z = (majorRadius + minorRadius * Mathf.cos(u)) * Mathf.sin(v);
                builder.vertex(x, y, z);
            }
        }

        for (int i = 0; i < minorSegments; i++)
            for (int j = 0; j < majorSegments; j++) {
                short index0 = toOneDimensionalIndex(i, j + 1);
                short index1 = toOneDimensionalIndex(i, j);
                short index2 = toOneDimensionalIndex(i + 1, j);
                short index3 = toOneDimensionalIndex(i + 1, j + 1);
                builder.rect(index0, index1, index2, index3);
            }
        return modelBuilder.end();
    }



    private short toOneDimensionalIndex(int i, int j) {
        return (short) ((short) (j % majorSegments) * minorSegments + (i % minorSegments));
    }
}
