/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.io;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import dae.animation.skeleton.Body;
import dae.prefabs.Klatch;
import dae.prefabs.Prefab;
import dae.prefabs.gizmos.PivotGizmo;
import dae.prefabs.lights.AmbientLightPrefab;
import dae.prefabs.lights.DirectionalLightPrefab;
import dae.prefabs.lights.PointLightPrefab;
import dae.prefabs.lights.SpotLightPrefab;
import dae.prefabs.standard.CameraEntity;
import dae.prefabs.standard.J3ONPC;
import dae.prefabs.standard.MeshObject;
import dae.prefabs.standard.NPCLocationEntity;
import dae.prefabs.standard.NavigationMesh;
import dae.prefabs.standard.PlayerStartEntity;
import dae.prefabs.standard.SituationEntity;
import dae.prefabs.standard.SoundEntity;
import dae.prefabs.standard.TriggerBox;
import dae.prefabs.standard.WayPointEntity;
import dae.project.Grid;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Koen
 */
public class SceneSaver {

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
        FileWriter fw;
        BufferedWriter bw = null;
        try {
            if (!location.getParentFile().exists()) {
                location.getParentFile().mkdirs();
            }
            fw = new FileWriter(location);

            bw = new BufferedWriter(fw);
            bw.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
            bw.write("<scene>\n");
            if (node.getUserData("eventlocation") != null) {
                String eventloc = node.getUserData("eventlocation");
                bw.write("<event ");
                writeAttribute(bw, "location", eventloc);
                bw.write("/>\n");
            }
            if (node.getUserData("radarmodel") != null) {
                String model = node.getUserData("radarmodel");
                bw.write("<radar ");
                writeAttribute(bw, "model", model);
                bw.write("/>\n");
            }
            for (Spatial child : node.getChildren()) {
                if (child instanceof Prefab) {
                    Prefab p = (Prefab) child;
                    Object save = p.getUserData("Save");
                    if ( save != Boolean.FALSE) {
                        writePrefab(p, bw);
                    }
                }

            }
            bw.write("</scene>\n");
        } catch (IOException ex) {
            Logger.getLogger(SceneSaver.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                bw.close();
            } catch (IOException ex) {
                Logger.getLogger(SceneSaver.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public static void writeAttribute(BufferedWriter bw, String key, String value) throws IOException {
        if (value == null || value.length() == 0) {
            return;
        }
        bw.write(key);
        bw.write("='");
        bw.write(value);
        bw.write("' ");
    }

    public static void writeAttribute(BufferedWriter bw, String key, float value) throws IOException {
        bw.write(key);
        bw.write("='");
        bw.write(Float.toString(value));
        bw.write("' ");
    }

    public static void writeAttribute(BufferedWriter bw, String key, int value) throws IOException {
        bw.write(key);
        bw.write("='");
        bw.write(Integer.toString(value));
        bw.write("' ");
    }

    public static void writeAttribute(BufferedWriter bw, String key, boolean value) throws IOException {
        bw.write(key);
        bw.write("='");
        bw.write(Boolean.toString(value));
        bw.write("' ");
    }

    public static void writeAttribute(BufferedWriter bw, String key, Vector3f value) throws IOException {
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

    public static void writeAttribute(BufferedWriter bw, String key, Quaternion value) throws IOException {
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

    public static void writeAttribute(BufferedWriter bw, String key, ColorRGBA value) throws IOException {
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

    private static String writeSinglePrefab(BufferedWriter bw, Spatial child, boolean hasChildren) throws IOException {
        bw.write("<prefab ");
        Prefab p = (Prefab) child;
        writeAttribute(bw, "class", p.getClass().getName());
        writeAttribute(bw, "name", p.getName());
        writeAttribute(bw, "category", p.getCategory());
        writeAttribute(bw, "type", p.getType());
        writeAttribute(bw, "prefix", p.getPrefix());
        writeAttribute(bw, "offset", p.getOffset());
        writeAttribute(bw, "translation", p.getLocalTranslation());
        writeAttribute(bw, "rotation", p.getLocalRotation());
        writeAttribute(bw, "scale", p.getLocalScale());
        if (p.hasPhysicsMesh()) {
            writeAttribute(bw, "physicsMesh", p.getPhysicsMesh());
        }

        writeAttribute(bw, "shadowmode", p.getShadowMode().toString());

        if (child instanceof MeshObject) {
            MeshObject mo = (MeshObject) child;
            writeAttribute(bw, "mesh", mo.getMeshFile());
        }
        if (!hasChildren) {
            bw.write("/>\n");
            return "";
        } else {
            bw.write(">\n");
            return "</prefab>";
        }
    }

    private static String writeKlatch(Klatch k, BufferedWriter bw, boolean hasChildren) throws IOException {
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

        if (!hasChildren) {
            bw.write("/>\n");
            return "";
        } else {
            bw.write(">\n");
            return "</klatch>";
        }
    }

    private static String writeBody(BufferedWriter bw, Spatial child, boolean hasChildren) throws IOException {
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
        if (!hasChildren) {
            bw.write("/>\n");
            return "";
        } else {
            bw.write(">\n");
            return "</body>";
        }
    }

    private static String writeJ3ONPC(BufferedWriter bw, Spatial child, boolean hasChildren) throws IOException {
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
        if (!hasChildren) {
            bw.write("/>\n");
            return "";
        } else {
            bw.write(">\n");
            return "</j3onpc>";
        }
    }

    private static String writeCameraEntity(BufferedWriter bw, Spatial child, boolean hasChildren) throws IOException {
        bw.write("<camera ");
        writeAttribute(bw, "class", child.getClass().getName());
        writeAttribute(bw, "name", child.getName());
        writeAttribute(bw, "category", "Standard");
        writeAttribute(bw, "type", "Camera");
        writeAttribute(bw, "cameraid", ((CameraEntity) child).getCameraId());
        writeAttribute(bw, "translation", child.getLocalTranslation());
        writeAttribute(bw, "rotation", child.getLocalRotation());
        writeAttribute(bw, "startcam", ((CameraEntity) child).getStartCam());
        if (!hasChildren) {
            bw.write("/>\n");
            return "";
        } else {
            bw.write(">\n");
            return "</camera>";
        }
    }

    private static String writeSituationEntity(BufferedWriter bw, Spatial child, boolean hasChildren) throws IOException {
        bw.write("<situation ");
        writeAttribute(bw, "class", child.getClass().getName());
        writeAttribute(bw, "name", child.getName());
        writeAttribute(bw, "category", "Standard");
        writeAttribute(bw, "eventid", ((SituationEntity) child).getEventId());
        writeAttribute(bw, "type", "Situation");
        writeAttribute(bw, "translation", child.getLocalTranslation());
        writeAttribute(bw, "rotation", child.getLocalRotation());
        if (!hasChildren) {
            bw.write("/>\n");
            return "";
        } else {
            bw.write(">\n");
            return "</situation>";
        }
    }

    private static String writeSoundEntity(BufferedWriter bw, Spatial child, boolean hasChildren) throws IOException {
        bw.write("<sound ");
        SoundEntity se = (SoundEntity) child;
        writeAttribute(bw, "class", child.getClass().getName());
        writeAttribute(bw, "name", child.getName());
        writeAttribute(bw, "category", "Standard");
        writeAttribute(bw, "soundfile", ((SoundEntity) child).getSoundFile());
        writeAttribute(bw, "type", "Sound");
        writeAttribute(bw, "positional", se.getPositional());
        writeAttribute(bw, "looping", se.getLooping());
        writeAttribute(bw, "volume", se.getVolume());
        writeAttribute(bw, "translation", child.getLocalTranslation());
        writeAttribute(bw, "rotation", child.getLocalRotation());
        if (!hasChildren) {
            bw.write("/>\n");
            return "";
        } else {
            bw.write(">\n");
            return "</sound>";
        }
    }

    private static String writePlayerStartEntity(BufferedWriter bw, Spatial child, boolean hasChildren) throws IOException {
        bw.write("<playerstart ");
        writeAttribute(bw, "class", child.getClass().getName());
        writeAttribute(bw, "name", child.getName());
        writeAttribute(bw, "category", "Standard");
        writeAttribute(bw, "type", "PlayerStart");
        writeAttribute(bw, "translation", child.getLocalTranslation());
        writeAttribute(bw, "rotation", child.getLocalRotation());
        if (!hasChildren) {
            bw.write("/>\n");
            return "";
        } else {
            bw.write(">\n");
            return "</playerstart>";
        }
    }

    private static String writeWaypointEntity(BufferedWriter bw, Spatial child, boolean hasChildren) throws IOException {
        bw.write("<waypoint ");
        writeAttribute(bw, "class", child.getClass().getName());
        writeAttribute(bw, "name", child.getName());
        writeAttribute(bw, "waypointid", ((WayPointEntity) child).getWaypointId());
        writeAttribute(bw, "category", "Standard");
        writeAttribute(bw, "type", "Waypoint");
        writeAttribute(bw, "translation", child.getLocalTranslation());
        writeAttribute(bw, "rotation", child.getLocalRotation());
        if (!hasChildren) {
            bw.write("/>\n");
            return "";
        } else {
            bw.write(">\n");
            return "</waypoint>";
        }
    }

    private static String writeTriggerBoxEntity(BufferedWriter bw, Spatial child, boolean hasChildren) throws IOException {
        bw.write("<trigger ");
        TriggerBox box = (TriggerBox) child;
        writeAttribute(bw, "id", box.getId());
        writeAttribute(bw, "translation", child.getLocalTranslation());
        writeAttribute(bw, "dimension", box.getDimension());
        writeAttribute(bw, "rotation", child.getLocalRotation());
        writeAttribute(bw, "triggerid", box.getId());
        writeAttribute(bw, "category", "Standard");
        writeAttribute(bw, "type", "Trigger");
        if (!hasChildren) {
            bw.write("/>\n");
            return "";
        } else {
            bw.write(">\n");
            return "</trigger>";
        }
    }

    private static String writeNPCLocationEntity(Spatial child, BufferedWriter bw, boolean hasChildren) throws IOException {
        NPCLocationEntity npcloc = (NPCLocationEntity) child;
        bw.write("<npclocation ");
        writeAttribute(bw, "name", npcloc.getName());
        writeAttribute(bw, "yRotation", Float.toString(npcloc.getYRotation()));
        writeAttribute(bw, "translation", child.getLocalTranslation());
        writeAttribute(bw, "animation", npcloc.getAnimation());
        writeAttribute(bw, "npcmesh", npcloc.getNpc());
        if (!hasChildren) {
            bw.write("/>\n");
            return "";
        } else {
            bw.write(">\n");
            return "</npclocation>";
        }
    }

    private static String writeGrid(Spatial child, BufferedWriter bw, boolean hasChildren) throws IOException {
        Grid g = (Grid) child;
        bw.write("<ground ");
        writeAttribute(bw, "width", g.getWidth());
        writeAttribute(bw, "length", g.getLength());
        writeAttribute(bw, "translation", g.getLocalTranslation());
        if (!hasChildren) {
            bw.write("/>\n");
            return "";
        } else {
            bw.write(">\n");
            return "</ground>";
        }
    }

    private static String writeNavigationMesh(Spatial child, BufferedWriter bw, boolean hasChildren) throws IOException {
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
        if (!hasChildren) {
            bw.write("/>\n");
            return "";
        } else {
            bw.write(">\n");
            return "</navmesh>";
        }
    }

    private static String writeSpotLight(Spatial child, BufferedWriter bw, boolean hasChildren) throws IOException {
        SpotLightPrefab sl = (SpotLightPrefab) child;
        bw.write("<spotlight ");
        writeAttribute(bw, "name", sl.getName());
        writeAttribute(bw, "translation", sl.getLocalPrefabTranslation());
        writeAttribute(bw, "rotation", sl.getLocalPrefabRotation());
        writeAttribute(bw, "spotinnerangle", sl.getSpotInnerAngle());
        writeAttribute(bw, "spotouterangle", sl.getSpotOuterAngle());
        writeAttribute(bw, "spotrange", sl.getSpotRange());
        writeAttribute(bw, "spotcolor", sl.getSpotLightColor());
        writeAttribute(bw, "spotintensity", sl.getSpotLightIntensity());
        if (!hasChildren) {
            bw.write("/>\n");
            return "";
        } else {
            bw.write(">\n");
            return "</spotlight>";
        }
    }

    private static String writePointLight(Prefab child, BufferedWriter bw, boolean hasChildren) throws IOException {
        PointLightPrefab sl = (PointLightPrefab) child;
        bw.write("<pointlight ");
        writeAttribute(bw, "name", sl.getName());
        writeAttribute(bw, "translation", sl.getLocalPrefabTranslation());
        writeAttribute(bw, "rotation", sl.getLocalPrefabRotation());

        writeAttribute(bw, "color", sl.getPointLightColor());
        writeAttribute(bw, "intensity", sl.getPointLightIntensity());
        if (!hasChildren) {
            bw.write("/>\n");
            return "";
        } else {
            bw.write(">\n");
            return "</pointlight>";
        }
    }

    private static String writeDirectionalLight(Prefab child, BufferedWriter bw, boolean hasChildren) throws IOException {
        DirectionalLightPrefab sl = (DirectionalLightPrefab) child;
        bw.write("<directionallight ");
        writeAttribute(bw, "name", sl.getName());
        writeAttribute(bw, "translation", sl.getLocalPrefabTranslation());
        writeAttribute(bw, "rotation", sl.getLocalPrefabRotation());

        writeAttribute(bw, "color", sl.getDirectionalLightColor());
        writeAttribute(bw, "intensity", sl.getDirectionalLightIntensity());
        writeAttribute(bw, "castshadow", sl.getCastShadow());
        if (!hasChildren) {
            bw.write("/>\n");
            return "";
        } else {
            bw.write(">\n");
            return "</directionallight>";
        }
    }

    private static String writeAmbientLight(Prefab child, BufferedWriter bw, boolean hasChildren) throws IOException {
        AmbientLightPrefab sl = (AmbientLightPrefab) child;
        bw.write("<ambientlight ");
        writeAttribute(bw, "name", sl.getName());
        writeAttribute(bw, "translation", sl.getLocalPrefabTranslation());
        writeAttribute(bw, "rotation", sl.getLocalPrefabRotation());

        writeAttribute(bw, "color", sl.getAmbientLightColor());
        writeAttribute(bw, "intensity", sl.getAmbientLightIntensity());
        if (!hasChildren) {
            bw.write("/>\n");
            return "";
        } else {
            bw.write(">\n");
            return "</ambientlight>";
        }
    }

    private static String writePivot(Spatial child, BufferedWriter bw, boolean hasChildren) throws IOException {
        PivotGizmo pg = (PivotGizmo) child;
        bw.write("<pivot ");
        writeAttribute(bw, "name", pg.getName());
        writeAttribute(bw, "translation", pg.getWorldTranslation());
        writeAttribute(bw, "rotation", pg.getWorldRotation());
        if (!hasChildren) {
            bw.write("/>\n");
            return "";
        } else {
            bw.write(">\n");
            return "</pivot>";
        }
    }

    private static void writePrefab(Prefab child, BufferedWriter bw) throws IOException {
        boolean hasChildren = child.hasSavableChildren();
        String endtag = "";
        if (child instanceof Body) {
            endtag = writeBody(bw, child, hasChildren);
        } else if (child instanceof J3ONPC) {
            endtag = writeJ3ONPC(bw, child, hasChildren);
        } else if (child instanceof CameraEntity) {
            endtag = writeCameraEntity(bw, child, hasChildren);
        } else if (child instanceof SituationEntity) {
            endtag = writeSituationEntity(bw, child, hasChildren);
        } else if (child instanceof SoundEntity) {
            endtag = writeSoundEntity(bw, child, hasChildren);
        } else if (child instanceof PlayerStartEntity) {
            endtag = writePlayerStartEntity(bw, child, hasChildren);
        } else if (child instanceof WayPointEntity) {
            endtag = writeWaypointEntity(bw, child, hasChildren);
        } else if (child instanceof TriggerBox) {
            endtag = writeTriggerBoxEntity(bw, child, hasChildren);
        } else if (child instanceof NPCLocationEntity) {
            endtag = writeNPCLocationEntity(child, bw, hasChildren);
        } else if (child instanceof Grid) {
            endtag = writeGrid(child, bw, hasChildren);
        } else if (child instanceof NavigationMesh) {
            endtag = writeNavigationMesh(child, bw, hasChildren);
        } else if (child instanceof SpotLightPrefab) {
            endtag = writeSpotLight(child, bw, hasChildren);
        } else if (child instanceof PointLightPrefab) {
            endtag = writePointLight(child, bw, hasChildren);
        } else if (child instanceof DirectionalLightPrefab) {
            endtag = writeDirectionalLight(child, bw, hasChildren);
        } else if (child instanceof AmbientLightPrefab) {
            endtag = writeAmbientLight(child, bw, hasChildren);
        } else if (child instanceof PivotGizmo) {
            endtag = writePivot(child, bw, hasChildren);
        } else if (child instanceof Klatch) {
            endtag = writeKlatch((Klatch) child, bw, hasChildren);
        } else if (child instanceof Prefab) {
            endtag = writeSinglePrefab(bw, child, hasChildren);
        }

        if (hasChildren) {
            for (Spatial prefabChild : child.getChildren()) {
                if (prefabChild instanceof Prefab) {
                    writePrefab((Prefab) prefabChild, bw);
                }
            }
        }
        if (endtag.length() > 0) {
            bw.write(endtag);
        }
    }
}