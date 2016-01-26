/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.prefabs.standard;

import com.jme3.asset.AssetManager;
import com.jme3.asset.AssetNotFoundException;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;
import dae.GlobalObjects;
import dae.animation.skeleton.BodyElement;
import dae.components.MeshComponent;
import dae.io.XMLUtils;
import dae.prefabs.Prefab;
import dae.prefabs.gizmos.Gizmo;
import dae.prefabs.ui.events.ErrorMessage;
import java.io.IOException;
import java.io.Writer;

/**
 *
 * @author Koen
 */
public class MeshObject extends Prefab implements BodyElement {

    private String meshName;
    private AssetManager manager;

    public MeshObject() {
        this.canHaveChildren = true;
    }

    @Override
    public void create(AssetManager manager, String extraInfo) {
        this.meshName = name;
        this.manager = manager;

    }

    @Override
    public Node clone(boolean cloneMaterials) {
        return (Node) clone();
    }

    @Override
    public Spatial clone() {
        MeshObject mo = new MeshObject();
        mo.setName(this.getName());
        mo.setType(this.getType());
        mo.setCategory(this.getCategory());
        mo.setLocalScale(this.getLocalScale().clone());
        mo.setLocalPrefabRotation(this.getLocalPrefabRotation().clone());
        mo.setLocalPrefabTranslation(this.getLocalPrefabTranslation().clone());
        mo.setPhysicsMesh(this.getPhysicsMesh());

        for (Spatial s : this.children) {
            if (!(s instanceof Gizmo)) {
                mo.attachChild(s.clone(true));
            }
        }

        return mo;
    }

    @Override
    public Prefab duplicate(AssetManager manager) {
//        MeshObject mo = new MeshObject();
//        mo.setType(this.getType());
//        mo.setCategory(this.getCategory());
//        mo.create(this.meshName, manager, meshFile);
//
//        mo.setLocalScale(this.getLocalScale().clone());
//        mo.setLocalRotation(this.getLocalRotation().clone());
//        mo.setLocalTranslation(this.getLocalTranslation().clone());
//        mo.setPhysicsMesh(this.getPhysicsMesh());
//
//        return mo;
        return (Prefab) clone();
    }

    public String getMeshName() {
        return meshName;
    }

    /**
     * Reloads the mesh, the children of the mesh are not affected.
     */
    public void reloadMesh() {
        MeshComponent mc = (MeshComponent) this.getComponent("MeshComponent");
        if (mc != null) {
            mc.deinstall();
            mc.install(this);
        }
    }

    public void attachBodyElement(BodyElement element) {
        if (element instanceof Node) {
            this.attachChild((Node) element);
        }
    }

    public void reset() {
    }

    public void write(Writer w, int depth) throws IOException {
        for (int i = 0; i < depth; ++i) {
            w.write('\t');
        }
        w.write("<prefab ");
        XMLUtils.writeAttribute(w, "class", this.getClass().getName());
        XMLUtils.writeAttribute(w, "name", this.getName());
        XMLUtils.writeAttribute(w, "category", this.getCategory());
        XMLUtils.writeAttribute(w, "type", this.getType());
        XMLUtils.writeAttribute(w, "prefix", this.getPrefix());
        XMLUtils.writeAttribute(w, "offset", this.getOffset());
        XMLUtils.writeAttribute(w, "translation", this.getLocalTranslation());
        XMLUtils.writeAttribute(w, "rotation", this.getLocalRotation());
        XMLUtils.writeAttribute(w, "scale", this.getLocalScale());
        if (hasPhysicsMesh()) {
            XMLUtils.writeAttribute(w, "physicsMesh", getPhysicsMesh());
        }
        XMLUtils.writeAttribute(w, "shadowmode", getShadowMode().toString());



        boolean hasBodyElements = false;
        for (Spatial child : this.getChildren()) {
            if (child instanceof BodyElement) {
                hasBodyElements = true;
                break;
            }
        }

        if (!hasBodyElements) {
            w.write("/>\n");
        } else {
            w.write(">\n");
            for (Spatial child : this.getChildren()) {
                if (child instanceof BodyElement) {
                    ((BodyElement) child).write(w, depth + 1);
                }
            }
            for (int i = 0; i < depth; ++i) {
                w.write('\t');
            }
            w.write("</prefab>\n");
        }
    }
}
