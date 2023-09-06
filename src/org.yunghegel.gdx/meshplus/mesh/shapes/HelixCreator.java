package org.yunghegel.gdx.meshplus.mesh.shapes;

import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import org.yunghegel.gdx.meshplus.mesh.math.Mathf;
import org.yunghegel.gdx.meshplus.mesh.math.Vector3f;
import org.yunghegel.gdx.meshplus.mesh.util.Face3D;
import org.yunghegel.gdx.meshplus.mesh.util.Mesh3D;

public class HelixCreator extends ModelCreator{

    private float majorRadius;
    private float minorRadius;
    private int majorSegments;
    private int minorSegments;
    private int turns;
    private float dy;
    private boolean cap;
    private Mesh3D mesh;

    public HelixCreator(float majorRadius, float minorRadius, int majorSegments, int minorSegments, int turns, float dy, boolean cap){
        this.majorRadius = majorRadius;
        this.minorRadius = minorRadius;
        this.majorSegments = majorSegments;
        this.minorSegments = minorSegments;
        this.turns = turns;
        this.dy = dy;
        this.cap = cap;
        mesh = new Mesh3D();
    }

    @Override
    public Model create() {
        ModelBuilder modelBuilder = new ModelBuilder();
        modelBuilder.begin();

        b = modelBuilder.part("top_cone", shapeType, VertexAttributes.Usage.Position, mat);
        createVertices();
        createFaces();
        capEnds();
        Model helix = modelBuilder.end();
        return helix;
    }

    private void createVertices() {
        float y0 = -(turns * dy) / 2f; // Apply offset to center the helix
        float stepY = dy / (float) majorSegments;
        float majorAngle = 0;
        float minorAngle = 0;
        float majorAngleStep = Mathf.TWO_PI / majorSegments;
        float minorAngleStep = Mathf.TWO_PI / minorSegments;
        Vector3f[] verts = new Vector3f[majorSegments * minorSegments * turns];

        for (int n = 0; n < turns; n++) {
            for (int j = 0; j < majorSegments; j++) {
                Vector3f v0 = new Vector3f(majorRadius * Mathf.cos(majorAngle), y0,
                        majorRadius * Mathf.sin(majorAngle));
                for (int i = 0; i < minorSegments; i++) {
                    Vector3f v1 = new Vector3f(minorRadius * Mathf.cos(minorAngle), minorRadius * Mathf.sin(minorAngle),
                            0);
                    // Rotate
                    float a = Mathf.TWO_PI - majorAngle;
                    float x2 = Mathf.cos(a) * v1.getX() + Mathf.sin(a) * v1.getZ();
                    float z2 = -Mathf.sin(a) * v1.getX() + Mathf.cos(a) * v1.getZ();
                    v1.set(x2, v1.getY(), z2);
                    v1.addLocal(v0);

                    minorAngle += minorAngleStep;

                    verts[n * (majorSegments * minorSegments) + (j * minorSegments + i)] = v1;
                }
                y0 += stepY;
                majorAngle += majorAngleStep;
            }
        }

        for(Vector3f v: verts){
            b.vertex(v.getX(),v.getY(),v.getZ());
            vertexCount++;
        }





    }

    private void createFaces() {
        int l = majorSegments * turns;
        for (int j = 0; j < majorSegments * turns - 1; j++) {
            for (int i = 0; i < minorSegments; i++) {
                int[] k = new int[] { j % l, (j + 1) % l, i % minorSegments, (i + 1) % minorSegments };
                int index0 = k[1] * minorSegments + k[2];
                int index1 = k[0] * minorSegments + k[2];
                int index2 = k[1] * minorSegments + k[3];
                int index3 = k[0] * minorSegments + k[3];
                Face3D face = new Face3D(index0, index1, index3, index2);
                mesh.add(face);
                b.rect((short) index0, (short) index1, (short) index3, (short) index2);
                faceCount++;
            }
        }
    }

    private void capEnds() {
        if (!cap)
            return;
        int n = mesh.vertices.size() - 1;
        int m = minorSegments - 1;

        short[] endCap = new short[minorSegments];
        short[] startCap = new short[minorSegments];

        for (int i = 0; i < minorSegments; i++) {
            endCap[m - i] = (short) i;
            startCap[m - i] = (short) (n - i);
        }

        b.rect(endCap[0], endCap[1], endCap[2], endCap[3]);
        b.rect(startCap[0], startCap[1], startCap[2], startCap[3]);
        faceCount += 2;

        //we need a vertex for the center of the cap, and to connect the cap to the helix
        b.vertex(0, -dy / 2f, 0);
        b.vertex(0, dy / 2f, 0);
        vertexCount += 2;

        for (int i = 0; i < minorSegments; i++) {
            b.rect(endCap[i], endCap[(i + 1) % minorSegments], (short) (n + 1), (short) (n + 1));
            b.rect(startCap[i], startCap[(i + 1) % minorSegments], (short) n, (short) n);
            faceCount += 2;
        }



















    }



}
