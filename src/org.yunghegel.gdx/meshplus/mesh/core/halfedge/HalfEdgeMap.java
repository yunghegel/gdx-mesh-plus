package org.yunghegel.gdx.meshplus.mesh.core.halfedge;


import com.badlogic.gdx.math.MathUtils;

public class HalfEdgeMap {

    static final float scaleFactor = 1.5f;
    HalfEdge[] entries;
    int size;
    int mask;

    HalfEdgeMap(int size) {
        int tableSize = MathUtils.round((int) (size * scaleFactor));
        entries = new HalfEdge[tableSize];
        mask = entries.length - 1;
    }

    HalfEdge get(long key) {
        int hash = (int) (key & mask);
        while (true) {
            if (entries[hash] == null) {
                return null;
            } else if (entries[hash].id == key) {
                return entries[hash];
            }
            hash = (hash + 1) & mask;
        }
    }

    HalfEdge put(HalfEdge value) {
        long key = value.id;
        int hash = (int) (key & mask);
        int count = size;
        while (count-- >= 0) {
            if (entries[hash] == null) {
                entries[hash] = value;
                size++;
                return null;
            } else if (key != entries[hash].id) {
                hash = (hash + 1) & mask;
                continue;
            } else {
                HalfEdge old = entries[hash];
                entries[hash] = value;
                return old;
            }
        }
        return null;
    }
}
