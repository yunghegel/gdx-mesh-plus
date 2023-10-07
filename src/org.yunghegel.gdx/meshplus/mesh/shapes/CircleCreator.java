package org.yunghegel.gdx.meshplus.mesh.shapes;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import org.yunghegel.gdx.meshplus.mesh.math.Mathf;

public class CircleCreator extends ModelCreator{

    private int vertices,centerY;
    private float radius;

    public CircleCreator(int vertices, float radius, int centerY, Material mat){
        this.vertices = vertices;
        this.radius = radius;
        this.centerY = centerY;
        this.mat = mat;
    }

    @Override
    public Model create() {
        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        setShapeType(com.badlogic.gdx.graphics.GL20.GL_LINES);
        Material material = mat.copy();
        material.set(IntAttribute.createCullFace(GL20.GL_NONE));

        MeshPartBuilder b = modelBuilder.part("square_pyramid", shapeType, VertexAttributes.Usage.Position, material);


        float angle = 0;
        float step = Mathf.TWO_PI / (float) vertices;
        for (int i = 0; i < vertices; i++) {
            float x = Mathf.cos(angle) * radius;
            float z = Mathf.sin(angle) * radius;
            b.vertex(x, centerY, z);
            angle += step;
        }


            b.vertex(0, centerY, 0);
            for (short i = 0; i < vertices; i++) {
                short i1 = (short) (i %vertices);
                short i2 = (short) ((short) (i+1)%vertices);
                b.triangle(i1, i2, (short) vertices);
            }


        return modelBuilder.end();
    }
}
