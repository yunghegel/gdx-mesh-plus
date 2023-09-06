package org.yunghegel.gdx.meshplus.mesh.shapes;

import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;

public class FlatTopPyramidCreator extends ModelCreator{

    private float size, topScale;

    public FlatTopPyramidCreator(float size, float topScale){
        this.size = size;
        this.topScale = topScale;
    }

    @Override
    public Model create() {
        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();

        b = modelBuilder.part("disc", shapeType, VertexAttributes.Usage.Position, mat);
        //top face is scaled down
        b.vertex(size,-size,-size);
        b.vertex(size,-size,size);
        b.vertex(-size,-size,size);
        b.vertex(-size,-size,-size);
        b.vertex(size * topScale,size,-size * topScale);
        b.vertex(size * topScale,size,size * topScale);
        b.vertex(-size * topScale,size,size * topScale);
        b.vertex(-size * topScale,size,-size * topScale);

        b.triangle((short) 0,(short) 1,(short) 2);
        b.triangle((short) 0,(short) 2,(short) 3);
        b.triangle((short) 4,(short) 5,(short) 6);
        b.triangle((short) 4,(short) 6,(short) 7);
        b.triangle((short) 0,(short) 1,(short) 5);
        b.triangle((short) 0,(short) 5,(short) 4);
        b.triangle((short) 1,(short) 2,(short) 6);
        b.triangle((short) 1,(short) 6,(short) 5);
        b.triangle((short) 2,(short) 3,(short) 7);
        b.triangle((short) 2,(short) 7,(short) 6);
        b.triangle((short) 3,(short) 0,(short) 4);
        b.triangle((short) 3,(short) 4,(short) 7);



        return modelBuilder.end();
    }

}
