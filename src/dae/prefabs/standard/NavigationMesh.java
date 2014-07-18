/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.prefabs.standard;

import com.jme3.asset.AssetManager;
import com.jme3.export.binary.BinaryExporter;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.scene.mesh.IndexBuffer;
import com.jme3.terrain.Terrain;
import dae.navmesh.NavMesh;
import dae.navmesh.util.NavMeshGenerator;
import dae.prefabs.Prefab;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.FloatBuffer;
import java.util.logging.Level;
import java.util.logging.Logger;
import jme3tools.optimize.GeometryBatchFactory;
import org.critterai.nmgen.NavmeshGenerator;
import org.critterai.nmgen.TriangleMesh;

/**
 *
 * @author samyn_000
 */
public class NavigationMesh extends Prefab {

    private AssetManager manager;
    private org.critterai.nmgen.NavmeshGenerator nmgen;
    private float cellSize = 0.5f;
    private float cellHeight = 0.5f;
    private float minTraversableHeight = 1.0f;
    private float maxTraversableStep = 0.5f;
    private float maxTraversableSlope = 48.0f;
    private boolean clipLedges = false;
    private float traversableAreaBorderSize = 0.5f;
    private int smoothingThreshold = 2;
    private boolean useConservativeExpansion = false;
    private int minUnconnectedRegionSize = 1;
    private int mergeRegionSize = 4;
    private float maxEdgeLength = 0;
    private float edgeMaxDeviation = 0.5f;
    private int maxVertsPerPoly = 6;
    private float contourSampleDistance = 10;
    private float contourMaxDeviation = 10;
    private String navigationMesh;
    private String compiledMesh;
    private String pathToCompiledMesh;
    private NavMesh navMesh;

    public NavigationMesh() {
    }

    @Override
    public void create(String name, AssetManager manager, String extraInfo) {
        this.manager = manager;
        System.out.println("Creating a navigation mesh");
        this.attachChild(manager.loadModel("Entities/M_NavigationMesh.j3o"));
        this.setCategory("Standard");
        this.setType("NavigationMesh");
        this.setName(name);

        if (compiledMesh != null && compiledMesh.length() > 0) {
            this.attachChild(manager.loadModel(compiledMesh));
        }
    }

    public String getSourceMesh() {
        return navigationMesh;
    }

    public void setSourceMesh(String navigationMesh) {
        this.navigationMesh = navigationMesh;
    }

    public String getCompiledMesh() {
        return compiledMesh;
    }

    public void setCompiledMesh(String compiledMesh) {
        this.compiledMesh = compiledMesh;
    }

    public void generateMesh() {
        Node navMeshNode;
        System.out.println("Generating mesh for : " + navigationMesh);
        if (navigationMesh != null) {
            Spatial navmesh = manager.loadModel(navigationMesh);
            if (navmesh instanceof Node) {
                navMeshNode = (Node) navmesh;
            } else {
                navMeshNode = new Node();
                navMeshNode.attachChild(navmesh);
            }
            URL url = manager.getClass().getResource(navigationMesh);
            if (url != null) {
                String file = url.getFile();
                int dotIndex = file.lastIndexOf('.');
                pathToCompiledMesh = file.substring(0, dotIndex) + ".navmesh.j3o";

                int relativeDotIndex = navigationMesh.lastIndexOf('.');
                compiledMesh = navigationMesh.substring(0, relativeDotIndex) + ".navmesh.j3o";
            }

        } else {
            return;
        }

        NavMeshGenerator gen = new NavMeshGenerator();
        gen.setCellSize(cellSize);
        gen.setCellHeight(cellHeight);
        gen.setMinTraversableHeight(minTraversableHeight);
        gen.setMaxTraversableSlope(maxTraversableSlope);
        gen.setMaxTraversableStep(maxTraversableStep);
        gen.setClipLedges(clipLedges);
        gen.setTraversableAreaBorderSize(traversableAreaBorderSize);
        gen.setSmoothingThreshold(smoothingThreshold);
        gen.setUseConservativeExpansion(useConservativeExpansion);
        gen.setMinUnconnectedRegionSize(minUnconnectedRegionSize);
        gen.setMergeRegionSize(mergeRegionSize);
        gen.setMaxEdgeLength(maxEdgeLength);
        gen.setEdgeMaxDeviation(edgeMaxDeviation);
        gen.setMaxVertsPerPoly(maxVertsPerPoly);
        gen.setContourSampleDistance(contourSampleDistance);
        gen.setContourMaxDeviation(contourMaxDeviation);

        // Merge geometry
        System.out.println("Optimizing : " + navMeshNode);
        Spatial s = GeometryBatchFactory.optimize(navMeshNode);
        Mesh navMeshGeometry = null;
        if (s instanceof Node) {
            Node n = (Node) s;
            if (n.getChildren().size() != 1) {
                System.out.println("Not optimized");
                return;
            } else {
                Spatial first = n.getChild(0);
                if (first instanceof Geometry) {
                    Geometry g = (Geometry) first;
                    navMeshGeometry = g.getMesh();
                } else {
                    return;
                }
            }
        }

        // create the navmesh
        navMesh = new NavMesh();
        System.out.println("Generating NavMesh Geometry...");

        Mesh optimized = gen.optimize(navMeshGeometry);

        if (optimized != null) {
            System.out.println("Navmesh Geometry Generated!");
            System.out.println("Loading NavMesh...");

            navMesh.loadFromMesh(optimized);
            System.out.println("NavMesh Loaded!");
            //if (this.getChild("NavMesh")!= null){
            this.detachChildNamed("NavMesh");
            //}


            Geometry geom = new Geometry("NavMesh", optimized);
            geom.setLocalTranslation(new Vector3f(0, 0.05f, 0));
            geom.setMaterial(this.manager.loadMaterial("/Materials/SelectionMaterial.j3m"));
            attachChild(geom);

            if (compiledMesh != null) {
                BinaryExporter be = com.jme3.export.binary.BinaryExporter.getInstance();

                try {
                    be.save(geom, new File(pathToCompiledMesh));
                } catch (IOException ex) {
                    Logger.getLogger(NavigationMesh.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        } else {
            System.out.println("Generating NavMesh Geometry Failed!");
        }
    }

    public void printParams() {
        System.out.println("Cell Size: " + cellSize);
        System.out.println("Cell Height: " + cellHeight);
        System.out.println("Min Trav. Height: " + minTraversableHeight);
        System.out.println("Max Trav. Step: " + maxTraversableStep);
        System.out.println("Max Trav. Slope: " + maxTraversableSlope);
        System.out.println("Clip Ledges: " + clipLedges);
        System.out.println("Trav. Area Border Size: " + traversableAreaBorderSize);
        System.out.println("Smooth Thresh.: " + smoothingThreshold);
        System.out.println("Use Cons. Expansion: " + useConservativeExpansion);
        System.out.println("Min Unconn. Region Size: " + minUnconnectedRegionSize);
        System.out.println("Merge Region Size: " + mergeRegionSize);
        System.out.println("Max Edge Length: " + maxEdgeLength);
        System.out.println("Edge Max Dev.: " + edgeMaxDeviation);
        System.out.println("Max Verts/Poly: " + maxVertsPerPoly);
        System.out.println("Contour Sample Dist: " + contourSampleDistance);
        System.out.println("Contour Max Dev.: " + contourMaxDeviation);
    }

    public Mesh optimize(Mesh mesh) {
        nmgen = new NavmeshGenerator(cellSize, cellHeight, minTraversableHeight,
                maxTraversableStep, maxTraversableSlope,
                clipLedges, traversableAreaBorderSize,
                smoothingThreshold, useConservativeExpansion,
                minUnconnectedRegionSize, mergeRegionSize,
                maxEdgeLength, edgeMaxDeviation, maxVertsPerPoly,
                contourSampleDistance, contourMaxDeviation);

        FloatBuffer pb = mesh.getFloatBuffer(Type.Position);
        IndexBuffer ib = mesh.getIndexBuffer();

        // copy positions to float array
        float[] positions = new float[pb.capacity()];
        pb.clear();
        pb.get(positions);

        // generate int array of indices
        int[] indices = new int[ib.size()];

        for (int i = 0; i < indices.length; i++) {
            indices[i] = ib.get(i);
        }
        TriangleMesh triMesh = nmgen.build(positions, indices, null);
        if (triMesh == null) {
            return null;

        }
        System.out.println("triMesh nr of triangles:" + triMesh.indices.length / 3);
        int[] indices2 = triMesh.indices;
        float[] positions2 = triMesh.vertices;

        Mesh mesh2 = new Mesh();
        mesh2.setBuffer(Type.Position, 3, positions2);
        mesh2.setBuffer(Type.Index, 3, indices2);
        mesh2.updateBound();
        mesh2.updateCounts();

        return mesh2;
    }

    public Mesh optimize(Terrain terr) {
        float[] floats = terr.getHeightMap();
        int length = floats.length;
        float size = (int) FastMath.sqrt(floats.length) * 3;
        float[] vertices = new float[length * 3];
        int[] indices = new int[length * 3];

        //TODO: indices are wrong
        for (int i = 0; i < length * 3; i += 3) {
            float xPos = (float) Math.IEEEremainder(i, size);
            float yPos = floats[i / 3];
            float zPos = i / (int) size;
            vertices[i] = xPos;
            vertices[i + 1] = yPos;
            vertices[i + 2] = yPos;
            indices[i] = i;
            indices[i + 1] = i + 1;
            indices[i + 2] = i + 2;
        }
        Mesh mesh2 = new Mesh();
        mesh2.setBuffer(Type.Position, 3, vertices);
        mesh2.setBuffer(Type.Index, 3, indices);
        mesh2.updateBound();
        mesh2.updateCounts();
        return mesh2;
    }

    /**
     * @return The height resolution used when sampling the source mesh. Value
     * must be > 0.
     */
    public float getCellHeight() {
        return cellHeight;
    }

    /**
     * @param cellHeight - The height resolution used when sampling the source
     * mesh. Value must be > 0.
     */
    public void setCellHeight(float cellHeight) {
        this.cellHeight = cellHeight;
    }

    /**
     * @return The width and depth resolution used when sampling the the source
     * mesh.
     */
    public float getCellSize() {
        return cellSize;
    }

    /**
     * @param cellSize - The width and depth resolution used when sampling the
     * the source mesh.
     */
    public void setCellSize(float cellSize) {
        this.cellSize = cellSize;
    }

    public boolean isClipLedges() {
        return clipLedges;
    }

    public void setClipLedges(boolean clipLedges) {
        this.clipLedges = clipLedges;
    }

    public float getContourMaxDeviation() {
        return contourMaxDeviation;
    }

    public void setContourMaxDeviation(float contourMaxDeviation) {
        this.contourMaxDeviation = contourMaxDeviation;
    }

    public float getContourSampleDistance() {
        return contourSampleDistance;
    }

    public void setContourSampleDistance(float contourSampleDistance) {
        this.contourSampleDistance = contourSampleDistance;
    }

    public float getEdgeMaxDeviation() {
        return edgeMaxDeviation;
    }

    public void setEdgeMaxDeviation(float edgeMaxDeviation) {
        this.edgeMaxDeviation = edgeMaxDeviation;
    }

    public float getMaxEdgeLength() {
        return maxEdgeLength;
    }

    public void setMaxEdgeLength(float maxEdgeLength) {
        this.maxEdgeLength = maxEdgeLength;
    }

    public float getMaxTraversableSlope() {
        return maxTraversableSlope;
    }

    public void setMaxTraversableSlope(float maxTraversableSlope) {
        this.maxTraversableSlope = maxTraversableSlope;
    }

    public float getMaxTraversableStep() {
        return maxTraversableStep;
    }

    public void setMaxTraversableStep(float maxTraversableStep) {
        this.maxTraversableStep = maxTraversableStep;
    }

    public int getMaxVertsPerPoly() {
        return maxVertsPerPoly;
    }

    public void setMaxVertsPerPoly(int maxVertsPerPoly) {
        this.maxVertsPerPoly = maxVertsPerPoly;
    }

    public int getMergeRegionSize() {
        return mergeRegionSize;
    }

    public void setMergeRegionSize(int mergeRegionSize) {
        this.mergeRegionSize = mergeRegionSize;
    }

    public float getMinTraversableHeight() {
        return minTraversableHeight;
    }

    public void setMinTraversableHeight(float minTraversableHeight) {
        this.minTraversableHeight = minTraversableHeight;
    }

    public int getMinUnconnectedRegionSize() {
        return minUnconnectedRegionSize;
    }

    public void setMinUnconnectedRegionSize(int minUnconnectedRegionSize) {
        this.minUnconnectedRegionSize = minUnconnectedRegionSize;
    }

    public NavmeshGenerator getNmgen() {
        return nmgen;
    }

    public void setNmgen(NavmeshGenerator nmgen) {
        this.nmgen = nmgen;
    }

    public int getSmoothingThreshold() {
        return smoothingThreshold;
    }

    public void setSmoothingThreshold(int smoothingThreshold) {
        this.smoothingThreshold = smoothingThreshold;
    }

    public float getTraversableAreaBorderSize() {
        return traversableAreaBorderSize;
    }

    public void setTraversableAreaBorderSize(float traversableAreaBorderSize) {
        this.traversableAreaBorderSize = traversableAreaBorderSize;
    }

    public boolean isUseConservativeExpansion() {
        return useConservativeExpansion;
    }

    public void setUseConservativeExpansion(boolean useConservativeExpansion) {
        this.useConservativeExpansion = useConservativeExpansion;
    }
}
