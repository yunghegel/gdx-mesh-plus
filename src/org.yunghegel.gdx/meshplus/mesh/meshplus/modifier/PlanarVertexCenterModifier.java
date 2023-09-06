package org.yunghegel.gdx.meshplus.mesh.meshplus.modifier;

import org.yunghegel.gdx.meshplus.mesh.math.Vector3f;
import org.yunghegel.gdx.meshplus.mesh.util.Face3D;
import org.yunghegel.gdx.meshplus.mesh.util.Mesh3D;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PlanarVertexCenterModifier implements IMeshModifier{
    @Override
    public Mesh3D modify(Mesh3D mesh) {
        return modify(mesh, mesh.getFaces());
    }

    public Mesh3D modify(Mesh3D mesh, Collection<Face3D> selection) {
        for (Face3D face : selection) {
            modify(mesh, face);
        }
        return mesh;
    }

    public void modify(Mesh3D mesh, Face3D f) {
        int index = mesh.getVertexCount();
        int n = f.indices.length;
        List<Face3D> toAdd = new ArrayList<Face3D>();
        Vector3f center = mesh.calculateFaceCenter(f);
        mesh.add(center);
        for (int i = 0; i < f.indices.length; i++) {
            Face3D f1 = new Face3D(f.indices[i % n], f.indices[(i + 1) % n], index);
            toAdd.add(f1);
        }
        mesh.faces.addAll(toAdd);
        mesh.faces.remove(f);
    }


}
