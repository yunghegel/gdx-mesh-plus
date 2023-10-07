package org.yunghegel.gdx.meshplus.mesh.shapes;

import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import org.yunghegel.gdx.meshplus.mesh.math.Mathf;
import org.yunghegel.gdx.meshplus.mesh.meshplus.modifier.PlanarVertexCenterModifier;
import org.yunghegel.gdx.meshplus.mesh.util.Face3D;
import org.yunghegel.gdx.meshplus.mesh.util.Mesh3D;

public class HalfUVSphereCreator extends ModelCreator   {

    private int rings;
    private int segments;
    private float radius;
    private Mesh3D mesh;
    int numVertices=0;
    int numFaces=0;

    public HalfUVSphereCreator(float radius, int rings, int segments){
        this.rings = rings;
        this.segments = segments;
        this.radius = radius;

        mesh = new Mesh3D();
    }


    @Override
    public Model create() {
        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();
        b = modelBuilder.part("half_uv_sphere", shapeType, VertexAttributes.Usage.Position, mat);
        createVertices();
        System.out.println("created vertices");
        createFaces();
        System.out.println("created faces");
        createCap();
        System.out.println("created cap");


        Model mdl =  modelBuilder.end();
        mdl.meshes.get(0).transform(new Matrix4().translate(0,-radius/2f,0));
        return mdl;
    }

    private void createVertices() {
        float stepTheta = Mathf.PI / (float) rings;
        float stepPhi = Mathf.TWO_PI / (float) segments;
        for (int row = 1; row < rings / 2 + 1; row++) {
            float theta = row * stepTheta;
            for (int col = 0; col < segments; col++) {
                float phi = col * stepPhi;
                float x = radius * Mathf.cos(phi) * Mathf.sin(theta);
                float y = radius * Mathf.cos(theta);
                float z = radius * Mathf.sin(phi) * Mathf.sin(theta);
                b.vertex(x, y, z);
                mesh.addVertex(x, y, z);
                numVertices++;
            }
        }
        b.vertex(0, radius, 0);
        mesh.addVertex(0, radius, 0);
        numVertices++;
    }

    private int getIndex(int row, int col) {
        int idx = segments * row + (col % segments);
        return idx % numVertices;
    }

    private void createFaces() {
        for (int row = 0; row < (rings - 2) / 2; row++) {
            for (int col = 0; col < segments; col++) {
                int a = getIndex(row, col + 1);
                int b = getIndex(row + 1, col + 1);
                int c = getIndex(row + 1, col);
                int d = getIndex(row, col);
                this.b.rect((short) a, (short) b, (short) c, (short) d);
                numFaces++;
                numFaces++;
                mesh.addFace(a, b, c, d);
                if (row == 0) {
                    this.b.triangle((short) d, (short) (numVertices - 1), (short) a);
                    numFaces++;
                    mesh.addFace(d, numVertices - 1, a); }
                if (row == rings - 3) {
                    this.b.triangle((short) c, (short) b, (short) (numVertices - 2));
                    numFaces++;
                    mesh.addFace(c, b, numVertices - 2);}
            }
        }
    }

    private void createCap() {
        capNGon();
        splitCapIntoTriangleFan();
    }

    private void splitCapIntoTriangleFan() {
        Face3D faceToSplit = mesh.getFaceAt(mesh.faces.size() - 1);
        new PlanarVertexCenterModifier().modify(mesh, faceToSplit);
    }

    private void capNGon() {
        int center = numVertices - 1;
        for (int i = 0; i < segments; i++) {
            int a = getIndex(0, i);
            int b = getIndex(0, i + 1);
            this.b.triangle((short) a, (short) b, (short) center);
            numFaces++;
            mesh.addFace(a, b, center);
        }

        b.vertex(0, 0, 0);
        mesh.addVertex(0, -radius, 0);
        numVertices++;
        center = numVertices - 1;
        for (int i = 0; i < segments; i++) {
            int a = getIndex(rings / 2 - 1, i);
            int b = getIndex(rings / 2 - 1, i + 1);
            this.b.triangle((short) a, (short) center, (short) b);
            numFaces++;
            mesh.addFace(a, center, b);
        }

    }
}
