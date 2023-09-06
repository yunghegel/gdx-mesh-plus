package org.yunghegel.gdx.meshplus.mesh.shapes;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import org.yunghegel.gdx.meshplus.mesh.math.Mathf;

public class ArcCreator extends ModelCreator{

    public ArcCreator(float startAngle, float endAngle, float radius, int vertices) {
        this.startAngle = startAngle;
        this.endAngle = endAngle;
        this.radius = radius;
        this.vertices = vertices;
    }

    private float startAngle;
    private float endAngle;
    private float radius;
    private int vertices;

    @Override
    public Model create() {
        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        short i1, i2, i3, i4;
        Material material = mat.copy();
        material.set(IntAttribute.createCullFace(GL20.GL_NONE));


        MeshPartBuilder b = modelBuilder.part("arc", shapeType, VertexAttributes.Usage.Position, material);

        float angleBetweenPoints = calculateAngleBetweenPoints();
        for (int i = 0; i < vertices; i++) {
            float currentAngle = angleBetweenPoints * i;
            float x = radius * Mathf.cos(currentAngle);
            float z = radius * Mathf.sin(currentAngle);

            float x2 = radius * Mathf.cos(currentAngle + angleBetweenPoints);
            float z2 = radius * Mathf.sin(currentAngle + angleBetweenPoints);

            i1 = b.vertex(0, 0, 0);
            i2= b.vertex(x, 0, z);
            i3= b.vertex(x2, 0, z2);

            b.triangle(i1, i2, i3);

        }


        return modelBuilder.end();


    }

    private float calculateAngleBetweenPoints() {
        return startAngle + ((endAngle - startAngle) / ((float) vertices - 1));
    }
}
