package hs_kempten.ibrush.utils;

import android.graphics.PointF;

/**
 * Created by Antoine on 12.11.2016.
 */

public class InsideChecker {

    private final PointF determine;
    private final java.util.List<PointF> edges;

    public InsideChecker(final PointF determine) {
        this.determine = determine;
        edges = new java.util.ArrayList<>();
    }

    public void addEdge(final PointF edge) {
        edges.add(edge);
    }

    public boolean checkInside() {
        int j = edges.size() - 1;
        boolean oddNodes = false;

        for (int i = 0; i < edges.size(); i++) {
            if ((edges.get(i).y < determine.y && edges.get(j).y >= determine.y || edges.get(j).y < determine.y && edges.get(i).y >= determine.y) && (edges.get(i).x <= determine.x || edges.get(j).x <= determine.x)) {
                oddNodes ^= (edges.get(i).x + (determine.y - edges.get(i).y) / (edges.get(j).y - edges.get(i).y) * (edges.get(j).x - edges.get(i).x) < determine.x);
            }
            j = i;
        }

        return oddNodes;
    }
}
