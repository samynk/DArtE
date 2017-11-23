package dae.animation.trajectory;

import com.jme3.asset.AssetManager;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import dae.GlobalObjects;
import dae.animation.rig.Rig;


/**
 *
 * @author Koen
 */
public class RayCastFootSteps {
    // the body to do the raycasting for.

    private Rig body;
    // the first foot to start with.
    private String type = "right";
    // the node to do the ray casting from
    private Node rootNode;
    private FootStep current;
    private CollisionResults results = new CollisionResults();
    public Material leftFootMaterial;
    public Material rightFootMaterial;

    public RayCastFootSteps(Rig body) {
        this.body = body;
        rootNode = body.getParent();
        
        
        AssetManager assetInfo = GlobalObjects.getInstance().getAssetManager();
        leftFootMaterial = new Material(assetInfo,
                "Common/MatDefs/Misc/Unshaded.j3md");
        leftFootMaterial.setColor("Color", ColorRGBA.White);
        leftFootMaterial.setTexture("ColorMap", assetInfo.loadTexture("Textures/lfoot.png"));

        rightFootMaterial = new Material(assetInfo,
                "Common/MatDefs/Misc/Unshaded.j3md");
        rightFootMaterial.setColor("Color", ColorRGBA.White);
        rightFootMaterial.setTexture("ColorMap", assetInfo.loadTexture("Textures/rfoot.png"));
                //calculateNextFootStep();
    }

    public FootStep getCurrentFootStep() {
        return current;
    }

    private void calculateNextFootStep() {
        Vector3f hipLocation = body.getWorldTranslation();

        Vector3f moveDirection = new Vector3f(0, 0, -1);
        moveDirection = body.getWorldRotation().mult(moveDirection);
        moveDirection.y = 0;
        moveDirection.normalizeLocal();

        // TODO: query up axis from application
        Vector3f upVector = new Vector3f(0, 1, 0);
        Vector3f sagVector = moveDirection.cross(upVector);
        sagVector.normalizeLocal();

        // start ray casting from current support foot
        Vector3f currentSupport = null;
        Material material = null;
        if ("right".equals(type)) {
            currentSupport = body.getChild("leftFootJointX").getWorldTranslation();
            material = rightFootMaterial;
        } else {
            currentSupport = body.getChild("rightFootJointX").getWorldTranslation();
            material = leftFootMaterial;
        }

        Vector3f currentDir = currentSupport.subtract(hipLocation);
        float dist = currentDir.dot(sagVector);
        currentDir.addLocal(-dist * sagVector.x, -dist * sagVector.y, -dist * sagVector.z);
        currentDir.normalizeLocal();
        float alpha = FastMath.acos(currentDir.dot(moveDirection)) * FastMath.RAD_TO_DEG;
        //System.out.println("alpha : " + alpha * FastMath.RAD_TO_DEG);

        if (alpha > 70) {
            alpha = 70;
        }

        boolean found = false;
        do {
            Vector3f rayDirection = moveDirection.mult(FastMath.cos(alpha * FastMath.DEG_TO_RAD)).add(upVector.mult(-FastMath.sin(alpha * FastMath.DEG_TO_RAD)));
            Ray ray = new Ray(hipLocation, rayDirection);
            results.clear();
            // Collect intersections between ray and all nodes in results list.
            body.getParent().collideWith(ray, results);
            CollisionResult closest = results.getClosestCollision();
            if (closest == null) {
                alpha -= 1.0f;
                continue;
            }
            if (!body.hasChild(closest.getGeometry())) {
                Vector3f normal = closest.getContactNormal();
                if (normal.y > .6f) {
                    found = true;

                    current = new FootStep(material, closest.getContactPoint());
                    body.getParent().attachChild(current);
                }
            }

            alpha -= 5.0f;
        } while (!found && alpha > 30);

    }

    public void next() {
        calculateNextFootStep();
    }
}
