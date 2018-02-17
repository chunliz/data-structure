import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Comparator;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.LinkedList;

/**
 * This class provides a shortestPath method for finding routes between two points
 * on the map. Start by using Dijkstra's, and if your code isn't fast enough for your
 * satisfaction (or the autograder), upgrade your implementation by switching it to A*.
 * Your code will probably not be fast enough to pass the autograder unless you use A*.
 * The difference between A* and Dijkstra's is only a couple of lines of code, and boils
 * down to the priority you use to order your vertices.
 */
public class Router {
    /**
     * Return a LinkedList of <code>Long</code>s representing the shortest path from st to dest, 
     * where the longs are node IDs.
     */
    public static LinkedList<Long> shortestPath(GraphDB g, double stlon, double stlat, double destlon, double destlat) {
        long startId = g.closest(stlon, stlat);
        long destId = g.closest(destlon, destlat);
        Map<Long, Double> map = new HashMap<>(); // Distance from current node to the initial node.
        Set<Long> visited = new HashSet<>();
        LinkedList<Long> ret = new LinkedList<>();
        ArrayList<Long> retArray = new ArrayList<>();
        Map<Long, Long> edgeTo = new HashMap<>();

        PriorityQueue<Long> pq = new PriorityQueue<>(new Comparator<Long>() {
            @Override
            public int compare(Long l1, Long l2) {
                double l1ToDest = map.get(l1) + g.distance(l1, destId);
                double l2ToDest = map.get(l2) + g.distance(l2, destId);
                if (l1ToDest < l2ToDest) return -1;
                else if (l1ToDest > l2ToDest) return 1;
                else return 0;
            }
        });

        visited.add(startId);
        map.put(startId, 0.0);
        pq.add(startId);

        while (!pq.isEmpty()) {
            long tmp = pq.poll();
            if (tmp == destId) break;
            for (long node : g.nodes.get(tmp).adj) {
                if (visited.contains(node)) {
                    if (map.get(tmp) + g.distance(tmp, node) < map.get(node)) {
                        map.put(node, map.get(tmp) + g.distance(tmp, node));
                        edgeTo.put(node, tmp);
                    }
                    continue;
                }
                visited.add(node);
                map.put(node, map.get(tmp) + g.distance(tmp, node));
                edgeTo.put(node, tmp);
                pq.add(node);
            }
        }

        long node = destId;
        while (node != startId) {
            retArray.add(node);
            node = edgeTo.get(node);
        }
        retArray.add(startId);

        for (int i = retArray.size() - 1; i >= 0; i--) {
            ret.add(retArray.get(i));
        }
        return ret;
    }

}
