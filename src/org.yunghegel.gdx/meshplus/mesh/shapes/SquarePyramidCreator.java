package org.yunghegel.gdx.meshplus.mesh.shapes;

import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;

public class SquarePyramidCreator extends ModelCreator{

    private float size,height;

    public SquarePyramidCreator(float size, float height){
        this.size = size;
        this.height = height;
    }

    @Override
    public Model create() {

        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        short i1, i2, i3, i4;

        MeshPartBuilder b = modelBuilder.part("square_pyramid", shapeType, VertexAttributes.Usage.Position, mat);
        b.vertex(-size,0,-size);
        b.vertex(-size,0,size);
        b.vertex(size,0,size);
        b.vertex(size,0,-size);
        b.vertex(0,height,0);

        b.rect( i1 = 0, i2 = 1, i3 = 2, i4 = 3);
        b.index( i1 = 1,i2=0,i3=4);
        b.index( i1 = 2,i2=1,i3=4);
        b.index( i1 = 3,i2=2,i3=4);
        b.index( i1 = 0,i2=3,i3=4);

        return modelBuilder.end();
    }
}
