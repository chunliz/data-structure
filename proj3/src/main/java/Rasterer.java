import java.util.HashMap;
import java.util.Map;
import java.io.File;
import java.util.List;
import java.util.ArrayList;
/**
 * This class provides all code necessary to take a query box and produce
 * a query result. The getMapRaster method must return a Map containing all
 * seven of the required fields, otherwise the front end code will probably
 * not draw the output correctly.
 */
public class Rasterer {
    // Recommended: QuadTree instance variable. You'll need to make
    //              your own QuadTree since there is no built-in quadtree in Java.

    private final double PIXEL = 256.0;

    private class QuadTreeNode {
        double tile_ullon;
        double tile_ullat;
        double tile_lrlon;
        double tile_lrlat;
        String fileName;

        QuadTreeNode[] children;

        public QuadTreeNode(double ullon, double ullat, double lrlon, double lrlat, String file) {
            tile_ullon = ullon;
            tile_ullat = ullat;
            tile_lrlon = lrlon;
            tile_lrlat = lrlat;
            fileName = file;

            children = new QuadTreeNode[4];
        }

        public double getLonDPP() {
            return (tile_lrlon - tile_ullon) / PIXEL;
        }

    }

    private QuadTreeNode root;
    private String imageDir;
    private String suffix = ".png";
    /** imgRoot is the name of the directory containing the images.
     *  You may not actually need this for your class. */
    public Rasterer(String imgRoot) {
        imageDir = imgRoot;
        root = new QuadTreeNode(-122.2998046875, 37.892195547244356, -122.2119140625, 37.82280243352756, imageDir + "root" + suffix);
        buildTree(root, imageDir);
    }

    private void buildTree (QuadTreeNode node, String fileName) {
        for (int i = 0; i < 4; i++) {
            String newFileName = fileName + String.valueOf(i + 1);
            String file = newFileName + suffix;
            File f = new File(file);
            if (f.exists() && !f.isDirectory()) {
                double ullon = (i == 0 || i == 2)? node.tile_ullon : ((node.tile_ullon + node.tile_lrlon) / 2.0);
                double ullat = (i == 0 || i == 1)? node.tile_ullat : ((node.tile_ullat + node.tile_lrlat) / 2.0);
                double lrlon = (i == 1 || i == 3)? node.tile_lrlon : ((node.tile_ullon + node.tile_lrlon) / 2.0);
                double lrlat = (i == 2 || i == 3)? node.tile_lrlat : ((node.tile_ullat + node.tile_lrlat) / 2.0);
                node.children[i] = new QuadTreeNode(ullon, ullat, lrlon, lrlat, newFileName + suffix);
                buildTree(node.children[i], newFileName);
            }
        }

    }


    public QuadTreeNode getNode(String fileName) {
        //System.out.println("getNode: " + fileName);

        String noSuffix = fileName.substring(fileName.lastIndexOf("/") + 1, fileName.indexOf("."));
        if (noSuffix.equals("root")) return root;
        else {
            int i = 0;
            QuadTreeNode node = root;
            while (node != null && i < noSuffix.length()) {
                node = node.children[noSuffix.charAt(i) - '1'];
                i++;
            }
            return node;
        }
    }

    /**
     * Takes a user query and finds the grid of images that best matches the query. These
     * images will be combined into one big image (rastered) by the front end. <br>
     * <p>
     *     The grid of images must obey the following properties, where image in the
     *     grid is referred to as a "tile".
     *     <ul>
     *         <li>The tiles collected must cover the most longitudinal distance per pixel
     *         (LonDPP) possible, while still covering less than or equal to the amount of
     *         longitudinal distance per pixel in the query box for the user viewport size. </li>
     *         <li>Contains all tiles that intersect the query bounding box that fulfill the
     *         above condition.</li>
     *         <li>The tiles must be arranged in-order to reconstruct the full image.</li>
     *     </ul>
     * </p>
     * @param params Map of the HTTP GET request's query parameters - the query box and
     *               the user viewport width and height.
     *
     * @return A map of results for the front end as specified:
     * "render_grid"   -> String[][], the files to display
     * "raster_ul_lon" -> Number, the bounding upper left longitude of the rastered image <br>
     * "raster_ul_lat" -> Number, the bounding upper left latitude of the rastered image <br>
     * "raster_lr_lon" -> Number, the bounding lower right longitude of the rastered image <br>
     * "raster_lr_lat" -> Number, the bounding lower right latitude of the rastered image <br>
     * "depth"         -> Number, the 1-indexed quadtree depth of the nodes of the rastered image.
     *                    Can also be interpreted as the length of the numbers in the image
     *                    string. <br>
     * "query_success" -> Boolean, whether the query was able to successfully complete. Don't
     *                    forget to set this to true! <br>
     * @see #REQUIRED_RASTER_REQUEST_PARAMS
     */
    public Map<String, Object> getMapRaster(Map<String, Double> params) {
        System.out.println(params);
        Map<String, Object> results = new HashMap<>();
        // System.out.println("Since you haven't implemented getMapRaster, nothing is displayed in "
        //                   + "your browser.");
        double required_ullon = params.get("ullon");
        double required_ullat = params.get("ullat");
        double required_lrlon = params.get("lrlon");
        double required_lrlat = params.get("lrlat");
        double required_w = params.get("w");
        double required_h = params.get("h");
        double LonDPP = (required_lrlon - required_ullon) / required_w;

        if (required_ullon > required_lrlon || required_lrlat > required_ullat || required_lrlon < root.tile_ullon || required_ullon > root.tile_lrlon || required_lrlat > root.tile_ullat || required_ullat < root.tile_lrlat) {
            results.put("query_success", false);
            return results;
        }

        required_ullon = Math.max(required_ullon, root.tile_ullon);
        required_ullat = Math.min(required_ullat, root.tile_ullat);
        required_lrlon = Math.min(required_lrlon, root.tile_lrlon);
        required_lrlat = Math.max(required_lrlat, root.tile_lrlat);

        //System.out.println("required DPP: " + LonDPP);
        //System.out.println("root DPP: " + root.getLonDPP());

        QuadTreeNode nodeUl = findNode(root, required_ullon, required_ullat, LonDPP);
        QuadTreeNode nodeLr = findNode(root, required_lrlon, required_lrlat, LonDPP);

        //System.out.println("nodeUl: " + nodeUl.fileName);
        //System.out.println("nodeLr: " + nodeLr.fileName);

        String noSuffix = nodeUl.fileName.substring(nodeUl.fileName.lastIndexOf("/") + 1, nodeUl.fileName.lastIndexOf("."));
        int len = noSuffix.equals("root")? 0 : noSuffix.length();

        results.put("raster_ul_lon", nodeUl.tile_ullon);
        results.put("raster_ul_lat", nodeUl.tile_ullat);
        results.put("raster_lr_lon", nodeLr.tile_lrlon);
        results.put("raster_lr_lat", nodeLr.tile_lrlat);
        results.put("depth", len);
        results.put("query_success", true);

        List<List<String>> files = new ArrayList<>();
        String newRow = nodeUl.fileName;

        while (newRow != null && getNode(newRow).tile_ullat > nodeLr.tile_lrlat) {
            List<String> ls = new ArrayList<>();
            ls.add(newRow);
            files.add(ls);
            newRow = nextFileInCol(newRow);
        }

        for (int i = 0; i < files.size(); i++) {
            String newCol = files.get(i).get(0);
            newCol = nextFileInRow(newCol);
            while (newCol != null && getNode(newCol).tile_ullon < nodeLr.tile_lrlon) {
                files.get(i).add(newCol);
                newCol = nextFileInRow(newCol);

            }
        }

        String[][] fileArray;
        if (files.size() == 0 || files.get(0).size() == 0) {
            fileArray = null;
            results.put("query_success", false);
        } else {
            fileArray = new String[files.size()][files.get(0).size()];
            convert(files, fileArray);
        }

        results.put("render_grid", fileArray);
        return results;
    }

    private void convert(List<List<String>> ls, String[][] str) {
        for (int i = 0; i < ls.size(); i++) {
            for (int j = 0; j < ls.get(i).size(); j++) {
                str[i][j] = ls.get(i).get(j);
                //System.out.print(str[i][j] + " ");
            }
            //System.out.println();
        }
    }

    private String nextFileInRow(String name) {
        String noSuffix = name.substring(name.lastIndexOf("/") + 1, name.lastIndexOf("."));
        char[] result = noSuffix.toCharArray();

        for (int i = noSuffix.length() - 1; i >= 0; i--) {
            if (noSuffix.charAt(i) == '1') {
                result[i] = '2';
                return imageDir + String.valueOf(result) + suffix;
            } else if (noSuffix.charAt(i) == '3') {
                result[i] = '4';
                return imageDir + String.valueOf(result) + suffix;
            } else if (noSuffix.charAt(i) == '2') {
                result[i] = '1';
                continue;
            } else {
                result[i] = '3';
                continue;
            }
        }
        return null;
    }

    private String nextFileInCol(String name) {
        String noSuffix = name.substring(name.lastIndexOf("/") + 1, name.indexOf("."));
        char[] result = noSuffix.toCharArray();

        for (int i = noSuffix.length() - 1; i >= 0; i--) {
            if (noSuffix.charAt(i) == '1') {
                result[i] = '3';
                return imageDir + String.valueOf(result) + suffix;
            } else if (noSuffix.charAt(i) == '2') {
                result[i] = '4';
                return imageDir + String.valueOf(result) + suffix;
            } else if (noSuffix.charAt(i) == '3') {
                result[i] = '1';
                continue;
            } else {
                result[i] = '2';
                continue;
            }
        }
        return null;
    }

    private QuadTreeNode findNode(QuadTreeNode node, double ullon, double ullat, double LonDPP) {
        if (node == null || node.tile_ullon > ullon || node.tile_lrlon < ullon || node.tile_lrlat > ullat || node.tile_ullat < ullat) return null;

        while (node.getLonDPP() > LonDPP) {
            QuadTreeNode nextNode = null;
            for (int i = 0; i < node.children.length; i++) {
                if (node.children[i] != null && ullon >= node.children[i].tile_ullon && ullon <= node.children[i].tile_lrlon && ullat >= node.children[i].tile_lrlat && ullat <= node.children[i].tile_ullat) {
                    nextNode = node.children[i];
                }
            }
            if (nextNode != null) {
                node = nextNode;
            } else {
                return node;
            }
        }
        return node;
    }

}
