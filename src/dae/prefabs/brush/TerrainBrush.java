package dae.prefabs.brush;

import com.jme3.asset.AssetManager;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import dae.GlobalObjects;
import dae.components.ComponentType;
import dae.components.PrefabComponent;
import dae.components.TransformComponent;
import dae.prefabs.AxisEnum;
import dae.prefabs.Prefab;
import dae.prefabs.standard.MeshObject;
import dae.prefabs.types.ObjectType;
import dae.project.Level;
import dae.util.MathUtil;
import java.util.ArrayList;
import java.util.Random;

/**
 * Defines a terrain brush. The terrain brush has simple settings like radius,
 * falloff etc.
 *
 * @author Koen Samyn
 */
public class TerrainBrush extends Brush {
    private CastMethod castMethod = CastMethod.RAYCASTING;
    private Vector3f brushAxis = Vector3f.UNIT_Y.negate();
    private float brushDistance = 1;
    private Random r = new Random();
    private CollisionResults results = new CollisionResults();
    private AssetManager assetManager;
    private ObjectType toCreate;
    /**
     * The spatials that will be used as a brush.
     */
    private ArrayList<Stroke> strokes =
            new ArrayList<Stroke>();

    /**
     * Creates a new terrain brush.
     */
    public TerrainBrush() {
        r.setSeed(System.currentTimeMillis());
        this.canHaveChildren = true;
        
    }

    @Override
    public void initialize(AssetManager manager, ObjectType type, String extraInfo) {
        this.objectType = type;
        attachChild(manager.loadModel("Entities/M_Sound.j3o"));
        assetManager = manager;
        toCreate = GlobalObjects.getInstance().getObjectsTypeCategory().getObjectType("Standard", "Mesh");
    }

    /**
     * Perform the brush on the terrain.
     *
     * @param scene the scene to perform the brush stroke on.
     * @param parent the node that will be the parent of all the strokes.
     * @param location the location of the brush stroke.
     * @param normal the normal of the collsion.
     */
    @Override
    public void doBrush(Level scene, Node parent, Vector3f location, Vector3f normal) {
        // select random radius between 0 and this radius and random angle.
        float randomR = (float) (r.nextGaussian() * getRadius());
        float randomAngle = r.nextFloat() * FastMath.TWO_PI;
        float x = randomR * FastMath.cos(randomAngle);
        float z = randomR * FastMath.sin(randomAngle);
        Vector3f loc = location.add(x, brushDistance, z);

        int weightSum = 0;
        for (Stroke s : this.strokes) {
            weightSum += s.getWeight();
        }

        float selection = r.nextFloat() * weightSum;
        int runningSelection = 0;
        Stroke selected = null;
        for (Stroke s : this.strokes) {
            runningSelection += s.getWeight();
            if (selection < runningSelection) {
                selected = s;
                break;
            }
        }

        if (selected != null) {
            float angles[] = new float[3];
            if (selected.getRandomizeXRot()) {
                angles[0] = r.nextFloat() * FastMath.TWO_PI;
            }
            if (selected.getRandomizeYRot()) {
                angles[1] = r.nextFloat() * FastMath.TWO_PI;
            }
            if (selected.getRandomizeZRot()) {
                angles[2] = r.nextFloat() * FastMath.TWO_PI;
            }
            Vector2f scale = selected.getScaleValues();
            float diff = Math.abs(scale.y - scale.x);
            float rScale = 1;
            if (diff > FastMath.FLT_EPSILON) {
                rScale = scale.x < scale.y ? scale.x + r.nextFloat() * diff : scale.y + r.nextFloat() * diff;
            }

            Quaternion rotation = new Quaternion();
            rotation.fromAngles(angles);


            if (castMethod == CastMethod.RAYCASTING) {
                doRayCast(loc, scene, selected, rotation, parent, rScale);
            } else if (castMethod == CastMethod.PHYSICS) {
                doPhysicsCast(loc, scene, selected, rotation, parent, rScale);
            }

        }
    }

    /**
     * Implementation of the list interface. Adds an brushHair to the list of
     * brushhair objects.
     *
     * @param brushHair the BrushHair object to add.
     */
    public void addStroke(Stroke stroke) {
        strokes.add(stroke);
        int index = strokes.indexOf(stroke);
        stroke.setLocalTranslation(this.brushAxis.mult(index * brushDistance));
        this.attachChild(stroke);
        
        
    }

    /**
     * Implementation of the list interface. Removes a brushHair from the list
     * of brushhair objects.
     *
     * @param brushHair the BrushHair object to add.
     */
    public void removeStroke(Stroke stroke) {
        strokes.remove(stroke);
        this.detachChild(stroke);
    }

    /**
     * Returns the size of the BrushHair list.
     *
     * @return the size of the BrushHair list.
     */
    public int getStrokeListSize() {
        return strokes.size();
    }
    
    /**
     * Returns an iterator with the strokes
     */
    public Iterable<Stroke> getStrokes(){
        return strokes;
    }

    /**
     * Returns the BrushHair item at the given index.
     *
     * @param index the index of the BrushHair.
     * @return the BrushHair object at the given index.
     */
    public Stroke getStrokeAt(int index) {
        return strokes.get(index);
    }

    /**
     * @return the castMethod
     */
    public CastMethod getCastMethod() {
        return castMethod;
    }

    /**
     * @param castMethod the castMethod to set
     */
    public void setCastMethod(CastMethod castMethod) {
        this.castMethod = castMethod;
    }

    private void doRayCast(Vector3f loc, Level scene, Stroke selected, Quaternion rotation, Node parent, float rScale) {
        // cast a ray on the scene.
        results.clear();
        Ray ray = new Ray(loc, this.brushAxis);
        // Collect intersections between ray and all nodes in results list.
        scene.collideWith(ray, results);
        if (results.size() > 0) {
            int selectedResult = -1;
            for (int i = 0; i < results.size(); ++i) {

                CollisionResult result = results.getCollision(i);
                Geometry hit = result.getGeometry();
                Node hitParent = hit.getParent();
                while (hitParent != null) {
                    if (hitParent.getUserData("useForRaycast") != null) {
                        boolean useForRayCast = hitParent.getUserData("useForRaycast");
                        System.out.println("useForRaycast is : " + useForRayCast);
                        if (useForRayCast) {
                            selectedResult = i;
                            break;
                        }
                    } else {
                        selectedResult = i;
                        break;
                    }
                    hitParent = hitParent.getParent();
                }
            }
            if (selectedResult < 0) {
                return;
            }
            CollisionResult result = results.getCollision(selectedResult);
            Vector3f strokeLoc = result.getContactPoint();
            // System.out.println("Raycasting on: " + strokeLoc);
            if (selected.getUseContactNormal()) {
                Vector3f strokeNormal = result.getContactNormal();
                Quaternion q = MathUtil.createRotationFromNormal(strokeNormal, AxisEnum.Y);
                rotation = q.mult(rotation);
            }
            toCreate.setExtraInfo(selected.getMesh());
            Prefab dup = toCreate.create(assetManager, "brush" + r.nextInt());
            dup.setUserData("useForRaycast", selected.getUseForRaycast());
            // location is in world space
            parent.worldToLocal(strokeLoc, strokeLoc);
            TransformComponent tc = (TransformComponent) dup.getComponent("TransformComponent");
            tc.setTranslation(strokeLoc);
            tc.setRotation(rotation);
            tc.setScale(new Vector3f(rScale, rScale, rScale));
            parent.attachChild(dup);
            dup.updateGeometricState();
        }
    }

    private void doPhysicsCast(Vector3f loc, Level scene, Stroke selected, Quaternion rotation, Node parent, float rScale) {
        if ( selected.getMesh() == null || selected.getMesh().length() == 0 ){
            return;
        }
        toCreate.setExtraInfo(selected.getMesh());
        Prefab dup = toCreate.create(assetManager, "brush" + r.nextInt());
        dup.setUserData("useForRaycast", selected.getUseForRaycast());
        // location is in world space
        parent.worldToLocal(loc,loc);
        TransformComponent tc = (TransformComponent) dup.getComponent("TransformComponent");
        tc.setTranslation(loc);
        tc.setRotation(rotation);
        tc.setScale(new Vector3f(rScale, rScale, rScale));
        parent.attachChild(dup);
        
        // adds physics component
        /*
        ComponentType physics = GlobalObjects.getInstance().getObjectsTypeCategory().getComponent("PhysicsConvexComponent");
        if ( physics != null){
            PrefabComponent pc = physics.create();
            dup.addPrefabComponent(pc);
        }
        */
        // better way, copy the components of the stroke, that way the stroke acts as a real prefab.
        for (PrefabComponent pc : selected.getComponents())
        {
            ComponentType ct = GlobalObjects.getInstance().getObjectsTypeCategory().getComponent(pc.getId());
            if ( !dup.hasPrefabComponent(pc.getId())){
                PrefabComponent dupPC = ct.create();
                dup.addPrefabComponent(dupPC, false);
                ct.copy(selected, dup);
                dupPC.install(dup);
            }
        }
        
        dup.updateGeometricState();
    }
}
