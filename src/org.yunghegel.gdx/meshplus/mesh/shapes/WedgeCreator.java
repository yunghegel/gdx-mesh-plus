package org.yunghegel.gdx.meshplus.mesh.shapes;

import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;

public class WedgeCreator extends ModelCreator{

    float radius=1;

    public WedgeCreator(float radius) {
        this.radius = radius;
    }

    @Override
    public Model create() {
        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();

        MeshPartBuilder builder = modelBuilder.part("wedge", shapeType, VertexAttributes.Usage.Position, mat);


        builder.vertex(-radius,radius,radius);
        builder.vertex(radius,radius,radius);
        builder.vertex(radius,radius,-radius);
        builder.vertex(-radius,radius,-radius);

        builder.vertex(radius,-radius,-radius);
        builder.vertex(-radius,-radius,-radius);

        short i1, i2, i3, i4;

        builder.rect( i1 = 0, i2 = 1, i3 = 2, i4 = 3);
        builder.index( i1 = 0,i2=3,i3=5);
        builder.index( i1 = 4,i2=2,i3=1);
        builder.rect( i1 = 1,i2=0,i3=5,i4=4);
        builder.rect( i1 = 4,i2=5,i3=3,i4=2);

        return modelBuilder.end();
    }
}
