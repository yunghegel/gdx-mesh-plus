package org.yunghegel.gdx.meshplus.mesh.shapes;

import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import org.yunghegel.gdx.meshplus.mesh.math.Mathf;

public class UVSphereCreator extends ModelCreator{

    private int rings;
    private int segments;
    private float radius;

    public UVSphereCreator(int rings, int segments, float radius) {
        this.rings = rings;
        this.segments = segments;
        this.radius = radius;
    }

    @Override
    public Model create() {
        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();

        MeshPartBuilder builder = modelBuilder.part("uv_sphere", shapeType, VertexAttributes.Usage.Position, mat);
        float stepTheta = Mathf.PI / (float) rings;
        float stepPhi = Mathf.TWO_PI / (float) segments;

        for (int row = 1; row < rings; row++) {
            float theta = row * stepTheta;
            for (int col = 0; col < segments; col++) {
                float phi = col * stepPhi;
                float x = radius * Mathf.cos(phi) * Mathf.sin(theta);
                float y = radius * Mathf.cos(theta);
                float z = radius * Mathf.sin(phi) * Mathf.sin(theta);
                builder.vertex(x, y, z);
            }
        }
        //top
        builder.vertex(0, radius, 0);
        //bottom
        builder.vertex(0, -radius, 0);

        for (int row = 0; row < rings - 2; row++) {
            for (int col = 0; col < segments; col++) {
                short index0 = (short) getIndex(row, (col+1)%segments, builder);
                short index1 = (short) getIndex(row+1, (col + 1)%segments, builder);
                short index2 = (short) getIndex(row + 1, col, builder);
                short index3 = (short) getIndex(row, col, builder);
                builder.rect(index0, index1, index2, index3);
                if(row == 0){
                    builder.triangle(index3, (short) (builder.lastIndex() - 1), index0);
                }
                if(row == rings - 3){
                    builder.triangle(index2, index1, (short) (builder.lastIndex() - 2));
                }
            }
        }
        return modelBuilder.end();
    }

    private int getIndex(int row, int col,MeshPartBuilder builder) {
        int idx = segments * row + col;
        return idx % builder.lastIndex();
    }
}
