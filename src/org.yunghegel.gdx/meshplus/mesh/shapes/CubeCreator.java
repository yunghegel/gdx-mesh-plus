package org.yunghegel.gdx.meshplus.mesh.shapes;

import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;

public class CubeCreator extends ModelCreator {

    private final float radius;

    private int shapeType = GL20.GL_LINES;


    public CubeCreator (float radius, Material mat){
        this.radius = radius;
        this.mat = mat;
    }

    @Override
    public Model create() {
        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        short i1, i2, i3 = 0, i4 = 0;
        MeshPartBuilder builder = modelBuilder.part("cube", shapeType, VertexAttributes.Usage.Position , mat);
        builder.vertex(radius, -radius, -radius);
        builder.vertex(radius, -radius, radius);
        builder.vertex(-radius, -radius, radius);
        builder.vertex(-radius, -radius, -radius);
        builder.vertex(radius, radius, -radius);
        builder.vertex(radius, radius, radius);
        builder.vertex(-radius, radius, radius);
        builder.vertex(-radius, radius, -radius);

        builder.rect(i1 = 3, i2 = 2, i3 = 1, i4 = 0);
        builder.rect(i1 = 4, i2 = 5, i3 = 6, i4 = 7);
        builder.rect(i1 = 0, i2 = 1, i3 = 5, i4 = 4);
        builder.rect(i1 = 1, i2 = 2, i3 = 6, i4 = 5);
        builder.rect(i1 = 2, i2 = 3, i3 = 7, i4 = 6);
        builder.rect(i1 = 3, i2 = 0, i3 = 4, i4 = 7);
        return modelBuilder.end();
        }

    }

