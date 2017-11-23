package dae.animation.custom;

import com.jme3.asset.AssetManager;
import com.jme3.math.FastMath;
import com.jme3.math.Matrix3f;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Spatial;
import com.jme3.scene.VertexBuffer;
import com.jme3.util.BufferUtils;
import dae.animation.rig.Rig;
import dae.prefabs.Prefab;
import java.nio.FloatBuffer;
import java.util.ArrayList;

/**
 * Defines a path for a character to walk on.
 *
 * @author Koen Samyn
 */
public class CharacterPath extends Prefab {

    private final ArrayList<Vector3f> waypoints =
            new ArrayList<>();
    private final ArrayList<Waypoint> waypointList = new ArrayList<>();
    private Vector3f finalLookAt;
    /**
     * The current segment of the character path.
     */
    private int currentSegment = 0;
    private boolean restoreOP = false;
    /**
     * An object that defines the destination and orientation as a last step of
     * the animation.
     */
    private Spatial finalDestination;
    /**
     * To create the line material.
     */
    private AssetManager manager;
    /**
     * The geometry object with the line representation.
     */
    private Geometry pathMesh;
    /**
     * The body that will walk on the path.
     */
    private Rig body;

    /**
     * Creates a new CharacterPath object.
     */
    public CharacterPath() {
        super.canHaveChildren = true;
    }

    @Override
    public void create(AssetManager manager, String extraInfo) {
        // show the start of the characterpath.
        attachChild(manager.loadModel("Skeleton/Helpers/pathstart.j3o"));
        this.manager = manager;
    }

    /**
     * Add a segment to the character path.
     *
     * @param segment the segment to add.
     */
    public void addWayPoint(Vector3f waypoint) {
        waypoints.add(waypoint);
    }

    public int getNrOfWaypoints() {
        return waypointList.size()+1;
    }

    /**
     * Activates the next segment.
     *
     * @return true if there is a next segment, false otherwise.
     */
    public boolean nextSegment() {
        ++currentSegment;
        if (finalDestination == null) {
            return currentSegment < waypoints.size();
        } else {
            return currentSegment < waypoints.size() + 1;
        }

    }

    public PathSegment getCRCurrentSegment(Animatable toMove) {
        if (currentSegment < waypoints.size()) {
            Vector3f p1 = new Vector3f();
            worldToLocal(toMove.getWorldTranslation(), p1);

            Vector3f p2 = waypoints.get(currentSegment);
            Vector3f p3;
            if (currentSegment < waypoints.size() - 1) {
                p3 = waypoints.get(currentSegment + 1);
            } else {
                if (finalLookAt != null) {
                    p3 = finalLookAt;
                } else if (finalDestination != null) {
                    p3 = new Vector3f(finalDestination.getWorldTranslation());
                } else {
                    Vector3f diff = p2.subtract(p1);
                    p3 = p2.add(diff);
                }
            }
            //Vector3f dir1 = p1.subtract(p0);
            Vector3f dir2 = p3.subtract(p2);
            return new NaturalCharacterPathSegmentXZ(p1, convertVectorToLocal(toMove.getForwardDirection()), p2, dir2);
            //return new CRCharacterPathSegment(p0, p1, p2, p3, 0.2f);
        } else if (finalDestination != null) {
            return createCRFinalDestination(toMove);
        } else {
            return null;
        }
    }

    private Vector3f convertVectorToLocal(Vector3f direction) {
        return this.getWorldRotation().inverse().mult(direction);
    }

    public PathSegment getCurrentSegment(Animatable toMove) {
        return getCRCurrentSegment(toMove);
    }

    public PathSegment getBezierCurrentSegment(Animatable toMove) {
        if (currentSegment < waypoints.size()) {
            Vector3f start = toMove.getWorldTranslation().clone();
            Vector3f direction = convertVectorToLocal(toMove.getForwardDirection());
            Vector3f cp1dir = direction.normalize();
            cp1dir.multLocal(0.1f);

            Vector3f cp1 = start.add(cp1dir);
            Vector3f end = waypoints.get(currentSegment);

            Vector3f lookat;
            if (currentSegment < waypoints.size() - 1) {
                lookat = waypoints.get(currentSegment + 1);
            } else {
                if (finalLookAt != null) {
                    lookat = finalLookAt;
                } else if (finalDestination != null) {
                    lookat = new Vector3f(finalDestination.getWorldTranslation());
                } else {
                    Vector3f diff = end.subtract(start);
                    lookat = end.add(diff);
                }
            }
            Vector3f diff = lookat.subtract(end);
            diff.normalizeLocal();
            diff.multLocal(0.1f);
            Vector3f cp2 = end.subtract(diff);
            return new CharacterPathSegment(start, cp1, cp2, end);
        } else if (finalDestination != null) {
            return createFinalDestination(toMove);
        } else {
            return null;
        }
    }

    public void setFinalLookAt(Vector3f lookAt) {
        this.finalLookAt = lookAt;
    }

    public void setFinalDestination(Spatial fd) {
        this.finalDestination = fd;
    }

    public PathSegment createFinalDestination(Animatable toMove) {
        finalDestination.forceRefresh(true, true, true);
        System.out.println(finalDestination.getName());
        Vector3f start = toMove.getWorldTranslation();
        Vector3f direction = convertVectorToLocal(toMove.getForwardDirection());
        Vector3f cp1dir = direction.normalize();
        cp1dir.multLocal(0.1f);

        Vector3f cp1 = start.add(cp1dir);

        Vector3f end = new Vector3f(finalDestination.getWorldTranslation());
        Vector3f lookat = new Vector3f();
        finalDestination.localToWorld(Vector3f.UNIT_Z, lookat);
        lookat.multLocal(0.1f);

        return new CharacterPathSegment(start, cp1, end.add(lookat), end);
    }

    public PathSegment createCRFinalDestination(Animatable toMove) {
        Vector3f p0, p1;
        p1 = waypoints.get(currentSegment - 1);
        if (waypoints.size() >= 2) {
            p0 = waypoints.get(currentSegment - 2);
        } else {
            Vector3f direction = convertVectorToLocal(toMove.getForwardDirection());
            Vector3f cp1dir = direction.normalize();
            cp1dir.negateLocal();
            p0 = p1.add(cp1dir);
        }


        Vector3f p2 = finalDestination.getWorldTranslation();
        Vector3f p3 = new Vector3f();
        finalDestination.localToWorld(Vector3f.UNIT_Z, p3);
        Vector3f diff = p3.subtract(p2);
        diff.negateLocal();

        return new NaturalCharacterPathSegmentXZ(toMove.getWorldTranslation(), convertVectorToLocal(toMove.getForwardDirection()), p2, diff);

    }

    public void setRestoreOriginalPosition(boolean restoreOP) {
        this.restoreOP = restoreOP;
    }

    public boolean isRestoreOriginalPosition() {
        return restoreOP;
    }

    @Override
    public int attachChild(Spatial child) {
        int index = super.attachChild(child);
        if (child instanceof Waypoint) {
            Waypoint w = (Waypoint) child;
            waypointList.add(w);
            createPathMesh();
        }
        return index;
    }

    @Override
    public int detachChild(Spatial child) {
        int index = super.detachChild(child);
        if (child instanceof Waypoint) {
            waypointList.remove((Waypoint) child);
            createPathMesh();
        }
        return index;
    }

    @Override
    public void updateLogicalState(float tpf) {
        super.updateLogicalState(tpf);

    }

    public void createPathMesh() {
        if (waypointList.isEmpty()) {
            return;
        }
        waypoints.clear();
        if (body != null) {
            // add the translation of the body as a local translation relative to this 
            // character path.
            Vector3f worldBody = body.getWorldTranslation();
            worldBody = this.worldToLocal(worldBody, null);
            waypoints.add(worldBody);
        }
        waypoints.add(new Vector3f());
        for (int i = 0; i < waypointList.size(); ++i) {
            Waypoint w = waypointList.get(i);
            Vector3f currentLoc = w.getLocalTranslation();
            waypoints.add(currentLoc);
        }
        ArrayList<Vector3f> locations = new ArrayList<Vector3f>();
        currentSegment = 0;
        for (int i = 0; i < (waypoints.size() - 1); ++i) {
            PathSegment ps = getCRCurrentSegment();
            float t = 0;
            do {
                Vector3f loc = new Vector3f();
                ps.interpolate(t, loc);
                locations.add(loc);
                t = ps.getSegment(t, 0.01f);
                //System.out.println("Loc" + loc);
            } while (t < 1.0f);
            currentSegment++;
        }

        Mesh m = new Mesh();
        m.setMode(Mesh.Mode.LineStrip);
        m.setLineWidth(5.0f);

        int[] indices = new int[locations.size()];
        for (int i = 0; i < locations.size(); ++i) {
            indices[i] = i;
        }
// Line from 0,0,0 to 0,1,0
        FloatBuffer buffer = BufferUtils.createFloatBuffer(locations.size() * 3);
        for (int i = 0; i < locations.size(); ++i) {
            Vector3f loc = locations.get(i);
            buffer.put(loc.x);
            buffer.put(loc.y+0.4f);
            buffer.put(loc.z);

        }

        m.setBuffer(VertexBuffer.Type.Position, 3, buffer);
        m.setBuffer(VertexBuffer.Type.Index, 2, indices);

        Geometry lines = new Geometry("path", m);
        lines.setMaterial(manager.loadMaterial("Materials/PathMaterial.j3m"));

        if (pathMesh != null) {
            pathMesh.removeFromParent();
        }
        lines.setCullHint(CullHint.Never);
        this.attachChild(lines);
        pathMesh = lines;

        createPathFootsteps();

    }

    public void createPathFootsteps() {
        // todo create components to define footstep behaviour in a rig.
        /*
        if (body != null) {
            // body.clearFootSteps();
            waypoints.clear();
            // add the translation of the body as a local translation relative to this 
            // character path.
            Vector3f worldBody = body.getWorldTranslation();
            worldBody = this.worldToLocal(worldBody, null);
            waypoints.add(worldBody);

            waypoints.add(new Vector3f());
            for (int i = 0; i < waypointList.size(); ++i) {
                Waypoint w = waypointList.get(i);
                Vector3f currentLoc = w.getLocalTranslation();
                waypoints.add(currentLoc);
            }

            currentSegment = 0;
            boolean right = true;
            boolean first = true;
            float remainingLength = 0;
            for (int i = 0; i < (waypoints.size() - 1); ++i) {
                PathSegment ps = getCRCurrentSegment();
                float t = ps.getSegment(0, remainingLength);
                do {
                    Vector3f loc = new Vector3f();
                    ps.interpolate(t, loc);
                    Vector3f tangent = new Vector3f();
                    ps.getTangent(t, tangent);
                    t = ps.getSegment(t, body.getFootStepLength());
                    Spatial footStep = null;

                    if (right) {
                        footStep = manager.loadModel("/Skeleton/Helpers/rightfoot.j3o");
                        loc.addLocal(-tangent.z * 0.1f, 0, tangent.x * 0.1f);
                        footStep.setUserData("type", "right");
                    } else {
                        footStep = manager.loadModel("/Skeleton/Helpers/leftfoot.j3o");
                        loc.addLocal(tangent.z * 0.1f, 0, -tangent.x * 0.1f);
                        footStep.setUserData("type", "left");
                    }

                    // locations and tangent are in character path space, we need to transform
                    // them into body space (footsteps node is child of the body.


                    this.getWorldRotation().mult(tangent, tangent);
                    Vector3f bodyRot = body.getWorldRotation().inverse().mult(tangent);
                    float yAngle = FastMath.atan2(bodyRot.x, bodyRot.z) - FastMath.HALF_PI;
                    Quaternion q = new Quaternion();
                    q.fromAngleAxis(yAngle, Vector3f.UNIT_Y);
                    footStep.setLocalRotation(q);

                    this.localToWorld(loc, loc);
                    Vector3f bodyLoc = body.worldToLocal(loc, null);
                    footStep.setLocalTranslation(bodyLoc);

                    body.addFootStep(footStep);
                    right = !right;
                    //System.out.println("Loc" + loc);
                } while (t < 1.0f);
                remainingLength = (t - 1.0f) * ps.getTotalLength();
                currentSegment++;
            }
        }
        */
    }

    public PathSegment getCRCurrentSegment() {
        //todo add component to define forward direction in rig.
        /*
        if (currentSegment < waypoints.size() - 1) {
            Vector3f p1 = waypoints.get(currentSegment);
            Vector3f p2 = waypoints.get(currentSegment + 1);

            Vector3f dir1 = null;
            if (currentSegment == 0 && body != null) {
                Vector3f bodyWorldDir = body.getForwardWorldDirection();
                dir1 = this.getWorldRotation().inverse().mult(bodyWorldDir);
            } else {
                dir1 = p2.subtract(p1).normalize();
            }

            Vector3f dir2;
            if (currentSegment < (waypoints.size() - 2)) {
                Vector3f p3 = waypoints.get(currentSegment + 2);
                dir2 = p3.subtract(p2).normalize();

            } else {
                dir2 = dir1.clone();
            }

            return new NaturalCharacterPathSegmentXZ(p1, dir1, p2, dir2);
            //return new CRCharacterPathSegment(p0, p1, p2, p3, 0.2f);
        } else {
            return null;
        }
        */
        return null;
    }

    public void setCharacter(Rig rig) {
        this.body = body;
    }

    @Override
    public void setLocalTranslation(Vector3f localTranslation) {
        super.setLocalTranslation(localTranslation); //To change body of generated methods, choose Tools | Templates.
        createPathMesh();

    }

    @Override
    public void setLocalTranslation(float x, float y, float z) {
        super.setLocalTranslation(x, y, z); //To change body of generated methods, choose Tools | Templates.
        createPathMesh();
    }

    @Override
    public void setLocalRotation(Matrix3f rotation) {
        super.setLocalRotation(rotation); //To change body of generated methods, choose Tools | Templates.
        createPathMesh();
    }

    @Override
    public void setLocalRotation(Quaternion quaternion) {
        super.setLocalRotation(quaternion); //To change body of generated methods, choose Tools | Templates.
        createPathMesh();
    }

    /**
     * Resets the path.
     */
    public void reset() {
        this.currentSegment = 0;
    }

    public void insertWaypointAfter(Waypoint first, Waypoint second) {

        int index = this.getChildIndex(first);
        if (index > -1) {
            this.attachChildAt(second, index + 1);
        }
        int wpindex = waypointList.indexOf(first);
        if ( wpindex > -1)
        {
            waypointList.add(wpindex + 1, second);
        }
    }

    /**
     * Returns the waypoint location in world space.
     * @param waypointId the waypoint location in world space.
     * @return the waypoint location in world space.
     */
    public Vector3f getWaypointLocation(int waypointId) {
        if ( waypointId == 0 ){
            return this.getWorldTranslation();
        }else{
            return waypointList.get(waypointId-1).getWorldTranslation();
        }
    }
}

