package dae.io;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import dae.GlobalObjects;
import dae.animation.custom.CharacterPath;
import dae.animation.custom.Waypoint;
import dae.animation.skeleton.Body;
import dae.components.ComponentType;
import dae.components.PrefabComponent;
import dae.io.writers.DefaultPrefabExporter;
import dae.io.writers.PrefabTextExporter;
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
import dae.prefabs.standard.Terrain;
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
                bw.write("\t<event ");
                writeAttribute(bw, "location", eventloc);
                bw.write("/>\n");
            }
            if (node.getUserData("radarmodel") != null) {
                String model = node.getUserData("radarmodel");
                bw.write("\t<radar ");
                writeAttribute(bw, "model", model);
                bw.write("/>\n");
            }
            for (Spatial child : node.getChildren()) {
                if (child instanceof Prefab) {
                    Prefab p = (Prefab) child;
                    Object save = p.getUserData("Save");
                    if (save != Boolean.FALSE) {
                        writePrefab(p, bw, 1);
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

    public static String createTabString(int depth) {
        if (helper.length() > 0) {
            helper.delete(0, helper.length() );
        }
        for (int i = 0; i < depth; ++i) {
            helper.append('\t');
        }
        return helper.toString();
    }

    public static String writeEndTag(BufferedWriter bw, String tag, Prefab p, int depth) throws IOException {
        if (!p.hasChildren() && !p.hasComponents()) {
            bw.write("/>\n");
            return "";
        } else {
            bw.write(">\n");
            return createTabString(depth) + "</" + tag + ">\n";
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

    private static String writeSinglePrefab(BufferedWriter bw, Spatial child, int depth) throws IOException {
        XMLUtils.writeTabs(bw, depth);
        bw.write("<prefab ");
        Prefab p = (Prefab) child;
        writeAttribute(bw, "class", p.getClass().getName());
        writeAttribute(bw, "name", p.getName());
        writeAttribute(bw, "category", p.getCategory());
        writeAttribute(bw, "type", p.getType());
        writeAttribute(bw, "prefix", p.getPrefix());
        writeAttribute(bw, "offset", p.getOffset());

        writeAttribute(bw, "shadowmode", p.getShadowMode().toString());

        if (child instanceof MeshObject) {
            MeshObject mo = (MeshObject) child;
            writeAttribute(bw, "mesh", mo.getMeshFile());
        }
        return writeEndTag(bw, "prefab", p, depth);
    }

    private static String writeKlatch(Klatch k, BufferedWriter bw, int depth) throws IOException {
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

    private static String writeBody(BufferedWriter bw, Spatial child, int depth) throws IOException {
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

    private static String writeJ3ONPC(BufferedWriter bw, Spatial child, int depth) throws IOException {
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

    private static String writeCameraEntity(BufferedWriter bw, Spatial child, int depth) throws IOException {
        XMLUtils.writeTabs(bw, depth);
        bw.write("<camera ");
        writeAttribute(bw, "class", child.getClass().getName());
        writeAttribute(bw, "name", child.getName());
        writeAttribute(bw, "category", "Standard");
        writeAttribute(bw, "type", "Camera");
        writeAttribute(bw, "cameraid", ((CameraEntity) child).getCameraId());
        writeAttribute(bw, "translation", child.getLocalTranslation());
        writeAttribute(bw, "rotation", child.getLocalRotation());
        writeAttribute(bw, "startcam", ((CameraEntity) child).getStartCam());
        return writeEndTag(bw, "camera", (Prefab) child, depth);
    }

    private static String writeSituationEntity(BufferedWriter bw, Spatial child, int depth) throws IOException {
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

    private static String writeSoundEntity(BufferedWriter bw, Spatial child, int depth) throws IOException {
        XMLUtils.writeTabs(bw, depth);
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
        writeAttribute(bw, "refDistance", se.getRefDistance());
        writeAttribute(bw, "maxDistance", se.getMaxDistance());
        return writeEndTag(bw, "sound", se, depth);
    }

    private static String writePlayerStartEntity(BufferedWriter bw, Spatial child, int depth) throws IOException {
        XMLUtils.writeTabs(bw, depth);
        bw.write("<playerstart ");
        writeAttribute(bw, "class", child.getClass().getName());
        writeAttribute(bw, "name", child.getName());
        writeAttribute(bw, "category", "Standard");
        writeAttribute(bw, "type", "PlayerStart");
        writeAttribute(bw, "translation", child.getLocalTranslation());
        writeAttribute(bw, "rotation", child.getLocalRotation());
        return writeEndTag(bw, "playerstart", (Prefab) child, depth);
    }

    private static String writeWaypointEntity(BufferedWriter bw, Spatial child, int depth) throws IOException {
        XMLUtils.writeTabs(bw, depth);
        bw.write("<waypoint ");
        writeAttribute(bw, "class", child.getClass().getName());
        writeAttribute(bw, "name", child.getName());
        writeAttribute(bw, "waypointid", ((WayPointEntity) child).getWaypointId());
        writeAttribute(bw, "category", "Standard");
        writeAttribute(bw, "type", "Waypoint");
        writeAttribute(bw, "translation", child.getLocalTranslation());
        writeAttribute(bw, "rotation", child.getLocalRotation());
        return writeEndTag(bw, "waypoint", (Prefab) child, depth);
    }

    private static String writeTriggerBoxEntity(BufferedWriter bw, Spatial child, int depth) throws IOException {
        XMLUtils.writeTabs(bw, depth);
        bw.write("<trigger ");
        TriggerBox box = (TriggerBox) child;
        writeAttribute(bw, "id", box.getId());
        writeAttribute(bw, "translation", child.getLocalTranslation());
        writeAttribute(bw, "dimension", box.getDimension());
        writeAttribute(bw, "rotation", child.getLocalRotation());
        writeAttribute(bw, "triggerid", box.getId());
        writeAttribute(bw, "category", "Standard");
        writeAttribute(bw, "type", "Trigger");
        return writeEndTag(bw, "trigger", box, depth);
    }

    private static String writeNPCLocationEntity(Spatial child, BufferedWriter bw, int depth) throws IOException {
        XMLUtils.writeTabs(bw, depth);
        NPCLocationEntity npcloc = (NPCLocationEntity) child;
        bw.write("<npclocation ");
        writeAttribute(bw, "name", npcloc.getName());
        writeAttribute(bw, "rotation", npcloc.getLocalRotation());
        writeAttribute(bw, "translation", child.getLocalTranslation());
        writeAttribute(bw, "animation", npcloc.getAnimation());
        writeAttribute(bw, "npcmesh", npcloc.getNpc());
        return writeEndTag(bw, "npclocation", npcloc, depth);
    }

    private static String writeGrid(Spatial child, BufferedWriter bw, int depth) throws IOException {
        XMLUtils.writeTabs(bw, depth);
        Grid g = (Grid) child;
        bw.write("<ground ");
        writeAttribute(bw, "width", g.getWidth());
        writeAttribute(bw, "length", g.getLength());
        writeAttribute(bw, "translation", g.getLocalTranslation());
        return writeEndTag(bw, "ground", g, depth);
    }

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
    }

    private static String writeSpotLight(Spatial child, BufferedWriter bw, int depth) throws IOException {
        XMLUtils.writeTabs(bw, depth);
        SpotLightPrefab sl = (SpotLightPrefab) child;
        bw.write("<spotlight ");
        writeAttribute(bw, "name", sl.getName());
        writeAttribute(bw, "spotinnerangle", sl.getSpotInnerAngle());
        writeAttribute(bw, "spotouterangle", sl.getSpotOuterAngle());
        writeAttribute(bw, "spotrange", sl.getSpotRange());
        writeAttribute(bw, "spotcolor", sl.getSpotLightColor());
        writeAttribute(bw, "spotintensity", sl.getSpotLightIntensity());
        return writeEndTag(bw, "spotlight", sl, depth);
    }

    private static String writePointLight(Prefab child, BufferedWriter bw, int depth) throws IOException {
        XMLUtils.writeTabs(bw, depth);
        PointLightPrefab sl = (PointLightPrefab) child;
        bw.write("<pointlight ");
        writeAttribute(bw, "name", sl.getName());
        writeAttribute(bw, "color", sl.getPointLightColor());
        writeAttribute(bw, "intensity", sl.getPointLightIntensity());
        return writeEndTag(bw, "pointlight", sl, depth);
    }

    private static String writeDirectionalLight(Prefab child, BufferedWriter bw, int depth) throws IOException {
        XMLUtils.writeTabs(bw, depth);
        DirectionalLightPrefab sl = (DirectionalLightPrefab) child;
        bw.write("<directionallight ");
        writeAttribute(bw, "name", sl.getName());
        writeAttribute(bw, "color", sl.getDirectionalLightColor());
        writeAttribute(bw, "intensity", sl.getDirectionalLightIntensity());
        writeAttribute(bw, "castshadow", sl.getCastShadow());
        return writeEndTag(bw, "directionallight", sl, depth);
    }

    private static String writeAmbientLight(Prefab child, BufferedWriter bw, int depth) throws IOException {
        XMLUtils.writeTabs(bw, depth);
        AmbientLightPrefab sl = (AmbientLightPrefab) child;
        bw.write("<ambientlight ");
        writeAttribute(bw, "name", sl.getName());
        writeAttribute(bw, "translation", sl.getLocalPrefabTranslation());
        writeAttribute(bw, "rotation", sl.getLocalPrefabRotation());

        writeAttribute(bw, "color", sl.getAmbientLightColor());
        writeAttribute(bw, "intensity", sl.getAmbientLightIntensity());
        return writeEndTag(bw, "ambientlight", sl, depth);
    }

    private static String writePivot(Spatial child, BufferedWriter bw, int depth) throws IOException {
        XMLUtils.writeTabs(bw, depth);
        PivotGizmo pg = (PivotGizmo) child;
        bw.write("<pivot ");
        writeAttribute(bw, "name", pg.getName());
        writeAttribute(bw, "translation", pg.getWorldTranslation());
        writeAttribute(bw, "rotation", pg.getWorldRotation());
        return writeEndTag(bw, "pivot", pg, depth);
    }

    private static String writeTerrain(Terrain terrain, BufferedWriter bw, int depth) throws IOException {
        XMLUtils.writeTabs(bw, depth);
        bw.write("<terrain ");
        writeAttribute(bw, "name", terrain.getName());
        writeAttribute(bw, "texture1", terrain.getTexture1());
        writeAttribute(bw, "tex1scale", terrain.getTex1Scale());
        writeAttribute(bw, "texture2", terrain.getTexture2());
        writeAttribute(bw, "tex2scale", terrain.getTex1Scale());
        writeAttribute(bw, "texture3", terrain.getTexture3());
        writeAttribute(bw, "tex3scale", terrain.getTex1Scale());
        writeAttribute(bw, "alphamap", terrain.getAlphaMap());
        writeAttribute(bw, "heightmap", terrain.getHeightMap());

        return writeEndTag(bw, "terrain", terrain, depth);

    }

    private static String writeCharacterPath(CharacterPath characterPath, BufferedWriter bw, int depth) throws IOException {
        XMLUtils.writeTabs(bw, depth);
        bw.write("<characterpath ");
        writeAttribute(bw, "name", characterPath.getName());
        return writeEndTag(bw, "characterpath", characterPath, depth);
    }

    private static String writeWaypoint(Waypoint waypoint, BufferedWriter bw, int depth) throws IOException {
        XMLUtils.writeTabs(bw, depth);
        bw.write("<waypoint ");
        writeAttribute(bw, "name", waypoint.getName());
        return writeEndTag(bw, "waypoint", waypoint, depth);
    }

    private static void writePrefab(Prefab child, BufferedWriter bw, int depth) throws IOException {
        boolean hasChildren = child.hasSavableChildren();
        String endtag = "";
        if (child instanceof Body) {
            endtag = writeBody(bw, child, depth);
        } else if (child instanceof J3ONPC) {
            endtag = writeJ3ONPC(bw, child, depth);
        } else if (child instanceof CameraEntity) {
            endtag = writeCameraEntity(bw, child, depth);
        } else if (child instanceof SituationEntity) {
            endtag = writeSituationEntity(bw, child, depth);
        } else if (child instanceof SoundEntity) {
            endtag = writeSoundEntity(bw, child, depth);
        } else if (child instanceof PlayerStartEntity) {
            endtag = writePlayerStartEntity(bw, child, depth);
        } else if (child instanceof WayPointEntity) {
            endtag = writeWaypointEntity(bw, child, depth);
        } else if (child instanceof TriggerBox) {
            endtag = writeTriggerBoxEntity(bw, child, depth);
        } else if (child instanceof NPCLocationEntity) {
            endtag = writeNPCLocationEntity(child, bw, depth);
        } else if (child instanceof Grid) {
            endtag = writeGrid(child, bw, depth);
        } else if (child instanceof NavigationMesh) {
            endtag = writeNavigationMesh(child, bw, depth);
        } else if (child instanceof SpotLightPrefab) {
            endtag = writeSpotLight(child, bw, depth);
        } else if (child instanceof PointLightPrefab) {
            endtag = writePointLight(child, bw, depth);
        } else if (child instanceof DirectionalLightPrefab) {
            endtag = writeDirectionalLight(child, bw, depth);
        } else if (child instanceof AmbientLightPrefab) {
            endtag = writeAmbientLight(child, bw, depth);
        } else if (child instanceof PivotGizmo) {
            endtag = writePivot(child, bw, depth);
        } else if (child instanceof Klatch) {
            endtag = writeKlatch((Klatch) child, bw, depth);
        } else if (child instanceof Terrain) {
            endtag = writeTerrain((Terrain) child, bw, depth);
        } else if (child instanceof CharacterPath) {
            endtag = writeCharacterPath((CharacterPath) child, bw, depth);
        } else if (child instanceof Waypoint) {
            endtag = writeWaypoint((Waypoint) child, bw, depth);
        } else if (child instanceof Prefab) {
            endtag = writeSinglePrefab(bw, child, depth);
        }

        if (hasChildren) {
            for (Spatial prefabChild : child.getChildren()) {
                if (prefabChild instanceof Prefab) {
                    writePrefab((Prefab) prefabChild, bw, depth + 1);
                }
            }
        }
        if (child.hasComponents()) {
            for (PrefabComponent pc : child.getComponents()) {
                ComponentType ct = GlobalObjects.getInstance().getObjectsTypeCategory().getComponent(pc.getId());
                if (ct != null) {
                    prefabExporter.writeComponent(bw, pc, ct, depth+1);
                }
            }
        }

        if (endtag.length() > 0) {
            bw.write(endtag);
        }
    }
}