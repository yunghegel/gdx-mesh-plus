package org.yunghegel.gdx.meshplus.mesh.shapes;

import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.shapebuilders.ConeShapeBuilder;
import com.badlogic.gdx.math.Matrix4;

public class DoubleConeCreator extends ModelCreator {

    private int vertices;
    private float radius;
    private float height;
    private Material mat;
    private int divisions;

    public DoubleConeCreator(float radius, float height, int vertices,int divisions,Material material){
        this.radius = radius;
        this.height = height;
        this.vertices = vertices;
        this.mat = material;
        this.divisions = divisions;
    }


    @Override
    public Model create() {
        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();

        b = modelBuilder.part("top_cone", shapeType, VertexAttributes.Usage.Position, mat);
        ConeShapeBuilder.build(b,radius, height/2, radius,divisions, 0, 360f,false);
        Model top = modelBuilder.end();

        modelBuilder.begin();
        b = modelBuilder.part("bottom_cone", shapeType, VertexAttributes.Usage.Position, mat);
        ConeShapeBuilder.build(b,radius, height/2, radius,divisions, 0, -360f,true);
        //flip so the cone is facing down


        Model bottom = modelBuilder.end();

        modelBuilder.begin();
        b = modelBuilder.part("double_cone", shapeType, VertexAttributes.Usage.Position, mat);
        Mesh topMesh = top.meshes.get(0);
        topMesh.transform(new Matrix4().setToTranslation(0,height/2,0));


        b.addMesh(top.meshes.get(0));
        Mesh bottomMesh = bottom.meshes.get(0);
        //rotate the bottom cone 180 degrees so it's facing up
        bottomMesh.transform(new Matrix4().setToRotation(1,0,0,-180));
        b.addMesh(bottomMesh);

        Model doubleCone = modelBuilder.end();
        doubleCone.materials.first().set(mat);

        return doubleCone;
    }



    private void createFaces() {
        for (int i = 0; i < vertices; i++) {
            createTopFaceAt(i);
            createBottomFaceAt(i);
        }
    }

    private void createVertices() {
        createVerticesAroundOrigin();
        createTopCenterVertex();
        createBottomCenterVertex();
    }


    private void createVerticesAroundOrigin() {
        Model cir = new CircleCreator(vertices, radius,0,mat).create();
    }

    private void createBottomCenterVertex() {
        addVertex(0, height /2f, 0);
        for(int i=0;i<vertices;i++)
            addFace(vertices+1,i,(i+1)%vertices);
    }

    private void createTopCenterVertex() {
        addVertex(0, -height / 2f, 0);
    }

    private void createTopFaceAt(int i) {
        addFace(vertices, i, (i + 1) % vertices);
    }

    private void createBottomFaceAt(int i) {
        addFace(vertices + 1, (i + 1) % vertices, i);
    }

    private void addVertex(float x, float y, float z) {
        b.vertex(x, y, z);
    }

    private void addFace(int... indices) {
        if (indices.length==3)
            b.triangle((short) indices[0], (short) indices[1], (short) indices[2]);
        else if (indices.length==4)
            b.rect((short) indices[0], (short) indices[1], (short) indices[2], (short) indices[3]);
    }
}
