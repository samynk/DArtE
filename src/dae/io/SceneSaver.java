package dae.io;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import dae.GlobalObjects;
import dae.animation.skeleton.Body;
import dae.components.ComponentType;
import dae.components.PrefabComponent;
import dae.io.writers.DefaultPrefabExporter;
import dae.io.writers.PrefabTextExporter;
import dae.prefabs.Klatch;
import dae.prefabs.Prefab;
import dae.prefabs.brush.BrushBatch;
import dae.prefabs.gizmos.PivotGizmo;
import dae.prefabs.standard.J3ONPC;
import dae.prefabs.standard.NPCLocationEntity;
import dae.prefabs.standard.NavigationMesh;
import dae.prefabs.standard.SituationEntity;
import dae.prefabs.standard.Terrain;
import dae.prefabs.standard.TriggerBox;
import dae.prefabs.types.ObjectType;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Koen
 */
public class SceneSaver {

    private static PrefabTextExporter prefabExporter = new DefaultPrefabExporter();
    private static StringBuilder helper = new StringBuilder();

    /**
     * Write the scene to a file.
     *
     * @param location The location of the file.
     * @param node the root node of the scene.
     */
    public static void writeScene(String location, Node node) {
        writeScene(new File(location), node);
    }

    /**
     * Write the scene to a file.
     *
     * @param location The location of the file.
     * @param node the root node of the scene.
     */
    public static void writeScene(File location, Node node) {
        try {
            FileWriter fw;
            BufferedWriter bw = null;

            if (!location.getParentFile().exists()) {
                location.getParentFile().mkdirs();
            }
            fw = new FileWriter(location);
            bw = new BufferedWriter(fw);
            writeScene(bw, node);
        } catch (IOException ex) {
            Logger.getLogger(SceneSaver.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void writeScene(BufferedWriter w, Node node) {
        try {
            w.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            w.write("<scene>\n");
            if (node.getUserData("eventlocation") != null) {
                String eventloc = node.getUserData("eventlocation");
                w.write("\t<event ");
                writeAttribute(w, "location", eventloc);
                w.write("/>\n");
            }
            if (node.getUserData("radarmodel") != null) {
                String model = node.getUserData("radarmodel");
                w.write("\t<radar ");
                writeAttribute(w, "model", model);
                w.write("/>\n");
            }
            if (node instanceof dae.project.Level) {
                dae.project.Level l = (dae.project.Level) node;
                w.write("\t<skybox ");
                XMLUtils.writeAttribute(w, "tex", l.getSkyBoxTexture());
                w.write("/>\n");
            }
            for (Spatial child : node.getChildren()) {
                if (child instanceof Prefab) {
                    Prefab p = (Prefab) child;
                    Object save = p.getUserData("Save");
                    if (save != Boolean.FALSE) {
                        writePrefab(p, w, 1);
                    }
                }

            }
            w.write("</scene>\n");
        } catch (IOException ex) {
            Logger.getLogger(SceneSaver.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                w.close();
            } catch (IOException ex) {
                Logger.getLogger(SceneSaver.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    public static String createTabString(int depth) {
        if (helper.length() > 0) {
            helper.delete(0, helper.length());
        }
        for (int i = 0; i < depth; ++i) {
            helper.append('\t');
        }
        return helper.toString();
    }

    public static String writeEndTag(Writer bw, String tag, Prefab p, int depth) throws IOException {
        if (!p.hasChildren() && !p.hasComponents() && !p.getObjectType().hasParameters()) {
            bw.write("/>\n");
            return "";
        } else {
            bw.write(">\n");
            return createTabString(depth) + "</" + tag + ">\n";
        }
    }

    public static void writeAttribute(Writer bw, String key, String value) throws IOException {
        if (value == null || value.length() == 0) {
            return;
        }
        bw.write(key);
        bw.write("='");
        bw.write(value);
        bw.write("' ");
    }

    public static void writeAttribute(Writer bw, String key, float value) throws IOException {
        bw.write(key);
        bw.write("='");
        bw.write(Float.toString(value));
        bw.write("' ");
    }

    public static void writeAttribute(Writer bw, String key, int value) throws IOException {
        bw.write(key);
        bw.write("='");
        bw.write(Integer.toString(value));
        bw.write("' ");
    }

    public static void writeAttribute(Writer bw, String key, boolean value) throws IOException {
        bw.write(key);
        bw.write("='");
        bw.write(Boolean.toString(value));
        bw.write("' ");
    }

    public static void writeAttribute(Writer bw, String key, Vector3f value) throws IOException {
        if (value == null) {
            return;
        }
        bw.write(key);
        bw.write("='[");
        bw.write(Float.toString(value.x));
        bw.write(',');
        bw.write(Float.toString(value.y));
        bw.write(',');
        bw.write(Float.toString(value.z));
        bw.write("]' ");
    }

    public static void writeAttribute(Writer bw, String key, Quaternion value) throws IOException {
        if (value == null) {
            return;
        }
        bw.write(key);
        bw.write("='[");
        bw.write(Float.toString(value.getX()));
        bw.write(',');
        bw.write(Float.toString(value.getY()));
        bw.write(',');
        bw.write(Float.toString(value.getZ()));
        bw.write(',');
        bw.write(Float.toString(value.getW()));
        bw.write("]' ");
    }

    public static void writeAttribute(Writer bw, String key, ColorRGBA value) throws IOException {
        if (value == null) {
            return;
        }
        bw.write(key);
        bw.write("='[");
        bw.write(Float.toString(value.getRed()));
        bw.write(',');
        bw.write(Float.toString(value.getGreen()));
        bw.write(',');
        bw.write(Float.toString(value.getBlue()));
        bw.write(',');
        bw.write(Float.toString(value.getAlpha()));
        bw.write("]' ");
    }

    private static String writeKlatch(Klatch k, Writer bw, int depth) throws IOException {
        XMLUtils.writeTabs(bw, depth);
        bw.write("<klatch ");
        writeAttribute(bw, "name", k.getName());
        writeAttribute(bw, "category", k.getCategory());
        writeAttribute(bw, "type", k.getType());
        writeAttribute(bw, "prefix", k.getPrefix());
        writeAttribute(bw, "offset", k.getOffset());
        writeAttribute(bw, "translation", k.getLocalTranslation());
        writeAttribute(bw, "rotation", k.getLocalRotation());
        writeAttribute(bw, "scale", k.getLocalScale());
        writeAttribute(bw, "klatch", k.getKlatchFile());
        writeAttribute(bw, "shadowmode", k.getShadowMode().toString());

        return writeEndTag(bw, "klatch", k, depth);
    }

    private static String writeBody(Writer bw, Spatial child, int depth) throws IOException {
        XMLUtils.writeTabs(bw, depth);
        bw.write("<body ");
        Body b = (Body) child;
        writeAttribute(bw, "name", b.getName());
        writeAttribute(bw, "category", b.getCategory());
        writeAttribute(bw, "type", b.getType());
        writeAttribute(bw, "prefix", b.getPrefix());
        writeAttribute(bw, "translation", b.getLocalTranslation());
        writeAttribute(bw, "rotation", b.getLocalRotation());
        writeAttribute(bw, "scale", b.getLocalScale());
        writeAttribute(bw, "skeleton", b.getSkeletonFile());
        return writeEndTag(bw, "body", b, depth);
    }

    private static String writeJ3ONPC(Writer bw, Spatial child, int depth) throws IOException {
        XMLUtils.writeTabs(bw, depth);
        bw.write("<j3onpc ");
        J3ONPC npc = (J3ONPC) child;
        writeAttribute(bw, "class", npc.getClass().getName());
        writeAttribute(bw, "name", npc.getName());
        writeAttribute(bw, "category", npc.getCategory());
        writeAttribute(bw, "type", npc.getType());
        writeAttribute(bw, "prefix", npc.getPrefix());
        writeAttribute(bw, "offset", npc.getOffset());
        writeAttribute(bw, "translation", npc.getLocalTranslation());
        writeAttribute(bw, "rotation", npc.getLocalRotation());
        writeAttribute(bw, "scale", npc.getLocalScale());
        writeAttribute(bw, "animation", npc.getAnimation());
        writeAttribute(bw, "actorName", npc.getActorName());
        writeAttribute(bw, "actorId", npc.getActorId());
        writeAttribute(bw, "mesh", npc.getMeshFile());
        return writeEndTag(bw, "j3onpc", npc, depth);
    }

    private static String writeSituationEntity(Writer bw, Spatial child, int depth) throws IOException {
        XMLUtils.writeTabs(bw, depth);
        bw.write("<situation ");
        writeAttribute(bw, "class", child.getClass().getName());
        writeAttribute(bw, "name", child.getName());
        writeAttribute(bw, "category", "Standard");
        writeAttribute(bw, "eventid", ((SituationEntity) child).getEventId());
        writeAttribute(bw, "type", "Situation");
        writeAttribute(bw, "translation", child.getLocalTranslation());
        writeAttribute(bw, "rotation", child.getLocalRotation());
        return writeEndTag(bw, "situation", (Prefab) child, depth);
    }

    /*
    private static String writeNavigationMesh(Spatial child, BufferedWriter bw, int depth) throws IOException {
        XMLUtils.writeTabs(bw, depth);
        NavigationMesh nm = (NavigationMesh) child;
        bw.write("<navmesh ");
        writeAttribute(bw, "sourcemesh", nm.getSourceMesh());
        writeAttribute(bw, "compiledmesh", nm.getCompiledMesh());
        writeAttribute(bw, "cellsize", nm.getCellSize());
        writeAttribute(bw, "cellheight", nm.getCellHeight());
        writeAttribute(bw, "mintraversableheight", nm.getMinTraversableHeight());
        writeAttribute(bw, "maxtraversableslope", nm.getMaxTraversableSlope());
        writeAttribute(bw, "maxtraversablestep", nm.getMaxTraversableStep());
        writeAttribute(bw, "clipledges", nm.isClipLedges());
        writeAttribute(bw, "traversableareabordersize", nm.getTraversableAreaBorderSize());
        writeAttribute(bw, "smoothingTreshold", nm.getSmoothingThreshold());
        writeAttribute(bw, "useconservativeexpansion", nm.isUseConservativeExpansion());
        writeAttribute(bw, "minunconnectedregionsize", nm.getMinUnconnectedRegionSize());
        writeAttribute(bw, "mergeregionsize", nm.getMergeRegionSize());
        writeAttribute(bw, "maxedgelength", nm.getMaxEdgeLength());
        writeAttribute(bw, "edgemaxdeviation", nm.getEdgeMaxDeviation());
        writeAttribute(bw, "maxvertsperpoly", nm.getMaxVertsPerPoly());
        writeAttribute(bw, "contoursampledistance", nm.getContourSampleDistance());
        writeAttribute(bw, "contourmaxdeviation", nm.getContourMaxDeviation());
        return writeEndTag(bw, "navmesh", nm, depth);
    }*/
    private static String writePivot(Spatial child, Writer bw, int depth) throws IOException {
        XMLUtils.writeTabs(bw, depth);
        PivotGizmo pg = (PivotGizmo) child;
        bw.write("<pivot ");
        writeAttribute(bw, "name", pg.getName());
        writeAttribute(bw, "translation", pg.getWorldTranslation());
        writeAttribute(bw, "rotation", pg.getWorldRotation());
        return writeEndTag(bw, "pivot", pg, depth);
    }

    private static String writeBrushBatch(BrushBatch brushBatch, Writer bw, int depth) throws IOException {
        XMLUtils.writeTabs(bw, depth);
        bw.write("<brushbatch ");
        writeAttribute(bw, "creategeometrybatch", brushBatch.getCreateGeometryBatch());
        return writeEndTag(bw, "brushbatch", brushBatch, depth);
    }

    public static void writePrefab(Prefab child, Writer bw, int depth) throws IOException {
        boolean hasChildren = child.hasSavableChildren();
        String endtag = "";
        if (child instanceof Body) {
            endtag = writeBody(bw, child, depth);
        } else if (child instanceof J3ONPC) {
            endtag = writeJ3ONPC(bw, child, depth);
        } else if (child instanceof SituationEntity) {
            endtag = writeSituationEntity(bw, child, depth);
        } else if (child instanceof PivotGizmo) {
            endtag = writePivot(child, bw, depth);
        } else if (child instanceof Klatch) {
            endtag = writeKlatch((Klatch) child, bw, depth);
        } else if (child instanceof BrushBatch) {
            endtag = writeBrushBatch((BrushBatch) child, bw, depth);
        } else if (child instanceof Prefab) {
            endtag = defaultWritePrefab(bw, child, depth);
        }

        if (hasChildren) {
            for (int i = 0; i < child.getPrefabChildCount(); ++i) {
                Prefab prefabChild = (Prefab) child.getPrefabChildAt(i);
                writePrefab((Prefab) prefabChild, bw, depth + 1);
            }
        }
        if (child.getObjectType().hasParameters()) {
            prefabExporter.writePrefabParameters(bw, child, depth);
        }
        if (child.hasComponents()) {
            for (PrefabComponent pc : child.getComponents()) {
                ComponentType ct = GlobalObjects.getInstance().getObjectsTypeCategory().getComponent(pc.getId());
                if (ct != null) {
                    prefabExporter.writeComponent(bw, pc, ct, depth + 1);
                }
            }
        }

        if (endtag.length() > 0) {
            bw.write(endtag);
        }
    }

    /**
     *
     * @param bw
     * @param child
     * @param depth
     * @return
     */
    public static String defaultWritePrefab(Writer bw, Prefab child, int depth) throws IOException {
        ObjectType ot = child.getObjectType();
        XMLUtils.writeTabs(bw, depth);
        bw.write("<prefab ");
        XMLUtils.writeAttribute(bw, "category", ot.getCategory());
        XMLUtils.writeAttribute(bw, "label", ot.getLabel());

        return writeEndTag(bw, "prefab", child, depth);
    }
}
