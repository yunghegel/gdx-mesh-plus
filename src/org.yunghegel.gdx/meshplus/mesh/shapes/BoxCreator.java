package org.yunghegel.gdx.meshplus.mesh.shapes;

import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;

public class BoxCreator extends ModelCreator{

    public float width, height, depth;

    public BoxCreator(float width, float height, float depth){
        this.width = width;
        this.height = height;
        this.depth = depth;
    }

    @Override
    public Model create() {
        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        short i1, i2, i3, i4;

        MeshPartBuilder b = modelBuilder.part("square_pyramid", shapeType, VertexAttributes.Usage.Position , mat);

        b.vertex(-width/2,-height/2,-depth/2);
        b.vertex(-width/2,-height/2,depth/2);
        b.vertex(width/2,-height/2,depth/2);
        b.vertex(width/2,-height/2,-depth/2);

        b.vertex(-width/2,height/2,-depth/2);
        b.vertex(-width/2,height/2,depth/2);
        b.vertex(width/2,height/2,depth/2);
        b.vertex(width/2,height/2,-depth/2);

        b.rect( i1 = 0, i2 = 1, i3 = 2, i4 = 3);
        b.rect( i1 = 4, i2 = 5, i3 = 6, i4 = 7);
        b.rect( i1 = 0, i2 = 1, i3 = 5, i4 = 4);
        b.rect( i1 = 1, i2 = 2, i3 = 6, i4 = 5);
        b.rect( i1 = 2, i2 = 3, i3 = 7, i4 = 6);
        b.rect( i1 = 3, i2 = 0, i3 = 4, i4 = 7);

        return modelBuilder.end();
    }
}
