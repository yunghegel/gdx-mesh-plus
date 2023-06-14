package mesh.shapes;

import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.attributes.IntAttribute;
import com.badlogic.gdx.graphics.g3d.utils.MeshPartBuilder;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import mesh.math.Mathf;
import org.lwjgl.opengl.GL20;

public class CircleCreator extends ModelCreator{

    private int vertices,radius,centerY;

    public CircleCreator(int vertices, int radius, int centerY, Material mat){
        this.vertices = vertices;
        this.radius = radius;
        this.centerY = centerY;
        this.mat = mat;
    }

    @Override
    public Model create() {
        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        setShapeType(GL20.GL_LINES);
        Material material = mat.copy();
        material.set(IntAttribute.createCullFace(GL20.GL_NONE));

        MeshPartBuilder b = modelBuilder.part("square_pyramid", shapeType, VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal, material);


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
                short i1 = (short) ((short) i%vertices);
                short i2 = (short) ((short) (i+1)%vertices);
                b.triangle(i1, i2, (short) vertices);
            }


        return modelBuilder.end();
    }
}
