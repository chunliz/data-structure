import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;


/**
 * Graph for storing all of the intersection (vertex) and road (edge) information.
 * Uses your GraphBuildingHandler to convert the XML files into a graph. Your
 * code must include the vertices, adjacent, distance, closest, lat, and lon
 * methods. You'll also need to include instance variables and methods for
 * modifying the graph (e.g. addNode and addEdge).
 *
 * @author Alan Yao, Josh Hug
 */
public class GraphDB {
    /** Your instance variables for storing the graph. You should consider
     * creating helper classes, e.g. Node, Edge, etc. */

    /**
     * Example constructor shows how to create and start an XML parser.
     * You do not need to modify this constructor, but you're welcome to do so.
     * @param dbPath Path to the XML file to be parsed.
     */

    public Map<Long, Node> nodes = new LinkedHashMap<>();
    public Map<Long, Way> ways = new HashMap<>();
    public GraphDB(String dbPath) {
        try {
            File inputFile = new File(dbPath);
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            GraphBuildingHandler gbh = new GraphBuildingHandler(this);
            saxParser.parse(inputFile, gbh);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
        clean();
    }

    /**
     * Helper to process strings into their "cleaned" form, ignoring punctuation and capitalization.
     * @param s Input string.
     * @return Cleaned string.
     */
    static String cleanString(String s) {
        return s.replaceAll("[^a-zA-Z ]", "").toLowerCase();
    }

    /**
     *  Remove nodes with no connections from the graph.
     *  While this does not guarantee that any two nodes in the remaining graph are connected,
     *  we can reasonably assume this since typically roads are connected.
     */
    private void clean() {
        Set<Long> removeNode = new HashSet<>();
        for (long id : nodes.keySet()) {
            if (nodes.get(id).adj.size() == 0) {
                removeNode.add(id);
            }
        }
        nodes.keySet().removeAll(removeNode);
    }

    /** Returns an iterable of all vertex IDs in the graph. */
    Iterable<Long> vertices() {
        return nodes.keySet();
    }

    /** Returns ids of all vertices adjacent to v. */
    Iterable<Long> adjacent(long v) {
        return nodes.get(v).adj;
    }

    /** Returns the Euclidean distance between vertices v and w, where Euclidean distance
     *  is defined as sqrt( (lonV - lonV)^2 + (latV - latV)^2 ). */
    double distance(long v, long w) {
        Node n1 = nodes.get(v);
        Node n2 = nodes.get(w);
        double result = Math.pow(n1.lon - n2.lon, 2) + Math.pow(n1.lat - n2.lat, 2);
        return Math.sqrt(result);
    }

    /** Returns the vertex id closest to the given longitude and latitude. */
    long closest(double lon, double lat) {
        if (nodes.keySet().size() == 0) {
            System.out.println("Nothing inside the Graph, cannot find the closet vertex.");
        }
        long retId;
        List<Long> nodesList = new ArrayList<>(nodes.keySet());

        retId = nodesList.get(0);
        double minDist = distance(lon, lat, retId);

        for (long id : nodes.keySet()) {
            double newDist = distance(lon, lat, id);
            if (newDist < minDist) {
                minDist = newDist;
                retId = id;
            }
        }
        return retId;
    }

    private double distance(double lon, double lat, long id) {
        double result = Math.pow(nodes.get(id).lon - lon, 2) + Math.pow(nodes.get(id).lat - lat, 2);
        return Math.sqrt(result);
    }

    /** Longitude of vertex v. */
    double lon(long v) {
        return nodes.get(v).lon;
    }

    /** Latitude of vertex v. */
    double lat(long v) {
        return nodes.get(v).lat;
    }

    void addNode(Node node) {
        nodes.put(node.id, node);
    }

    void deleteNode(Node node) {
        if (nodes.containsKey(node.id)) {
            for (long id : node.adj) {
                nodes.get(id).adj.remove(node.id);
            }
            nodes.remove(node.id);
        }
    }

    void addEdge(Way way) {
        ways.put(way.id, way);
        List<Long> ns = way.getNodesInAWay();
        for (int i = 0; i < ns.size() - 1; i++) {
            long id1 = ns.get(i);
            long id2 = ns.get(i+1);
            if (!nodes.containsKey(id1) || !nodes.containsKey(id2)) continue;
            Node n1 = nodes.get(id1);
            Node n2 = nodes.get(id2);
            n1.addAdj(id2);
            n2.addAdj(id1);
        }
    }

    static class Node {
        long id;
        double lat;
        double lon;
        Set<Long> adj;
        Map<String, String> extraInfo;

        Node(long id, double lat, double lon) {
            this.id = id;
            this.lat = lat;
            this.lon = lon;
            adj = new HashSet<>();
            extraInfo = new HashMap<>();
        }
        void addAdj(long id) {
            adj.add(id);
        }
    }

    static class Way {
        long id;
        boolean isValid;
        List<Long> nodesInAWay;
        Map<String, String> extraInfo;
        Way(long id) {
            this.id = id;
            isValid = false;
            nodesInAWay = new ArrayList<>();
            extraInfo = new HashMap<>();
        }
        List<Long> getNodesInAWay() {
            return new ArrayList<Long>(nodesInAWay);
        }
    }
}
