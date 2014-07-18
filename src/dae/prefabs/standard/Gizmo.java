/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.prefabs.standard;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.Vector3f;
import com.jme3.scene.Spatial;

/**
 *
 * @author SabreWing
 */
public class Gizmo extends MeshObject {

    protected String type;
    protected float displaceWidth;
    protected float displaceLength;
    protected float displaceHeight;
    protected float posX;
    protected float posY;
    protected float posZ;

    public Gizmo(float originPositionX, float originPositionY, float originPositionZ, float originWidth, float originLength, float originHeight, String typeXYZ) {
        super();
        type = typeXYZ;
        displaceHeight = originHeight;
        displaceWidth = originWidth;
        displaceLength = originLength;
        posX = originPositionX;
        posY = originPositionY;
        posZ = originPositionZ;
    }

    public Gizmo(float originPositionX, float originPositionY, float originPositionZ, float originRadius, float originHeight, String typeXYZ) {
        super();
        type = typeXYZ;
        displaceHeight = originHeight;
        displaceWidth = originRadius;
        displaceLength = originRadius;
        posX = originPositionX;
        posY = originPositionY;
        posZ = originPositionZ;
    }

    public Gizmo(Vector3f position, float originWidth, float originLength, float originHeight, String typeXYZ) {
        super();
        type = typeXYZ;
        displaceHeight = originHeight;
        displaceWidth = originWidth;
        displaceLength = originLength;
        posX = position.x;
        posY = position.y;
        posZ = position.z;
    }

    public Gizmo(Vector3f position, float originRadius, float originHeight, String typeXYZ) {
        super();
        type = typeXYZ;
        displaceHeight = originHeight;
        displaceWidth = originRadius;
        displaceLength = originRadius;
        posX = position.x;
        posY = position.y;
        posZ = position.z;
    }

    public void create(AssetManager manager) {

        Spatial model = null;
        Material mat = null;
        float scaleVal = 0.005f;
        if (type.equals("ScaleX")) {
            model = manager.loadModel("Objects/TransformationGizmo.obj");
            model.scale(scaleVal);
            model.move(posX, posY, posZ);
            model.setLocalTranslation(displaceWidth + 1, 0, 0);
            model.rotate(-(float) (Math.PI / 2), 0.0f, 0.0f);
            mat = manager.loadMaterial("Materials/gizmoXMaterial.j3m");
            setOriginalMaterial(mat);
            model.setMaterial(mat);
            this.attachChild(model);
        } else if (type.equals("ScaleY")) {
            model = manager.loadModel("Objects/TransformationGizmo.obj");
            model.scale(scaleVal);
            model.move(posX, posY, posZ);
            model.setLocalTranslation(0, 0, displaceHeight + 1);
            model.rotate(0.0f, -(float) (Math.PI / 2), 0.0f);
            mat = manager.loadMaterial("Materials/gizmoYMaterial.j3m");
            setOriginalMaterial(mat);
            model.setMaterial(mat);
            this.attachChild(model);
        } else if (type.equals("ScaleZ")) {
            model = manager.loadModel("Objects/TransformationGizmo.obj");
            model.scale(scaleVal);
            model.move(posX, posY, posZ);
            model.setLocalTranslation(0, displaceLength + 1, 0);
            model.rotate(0.0f, 0.0f, (float) (Math.PI / 2));
            mat = manager.loadMaterial("Materials/gizmoZMaterial.j3m");
            setOriginalMaterial(mat);
            model.setMaterial(mat);
            this.attachChild(model);
        } else if (type.equals("RotateX")) {
            model = manager.loadModel("Objects/RotationGizmo.obj");
            model.scale(scaleVal);
            model.move(posX, posY, posZ);
            model.setLocalTranslation(displaceWidth + 1, 0, 0);
            mat = manager.loadMaterial("Materials/gizmoXMaterial.j3m");
            setOriginalMaterial(mat);
            model.setMaterial(mat);
            this.attachChild(model);
        } else if (type.equals("RotateY")) {
            model = manager.loadModel("Objects/RotationGizmo.obj");
            model.scale(scaleVal);
            model.move(posX, posY, posZ);
            model.setLocalTranslation(0, 0, displaceHeight + 1);
            model.rotate(0.0f, -(float) (Math.PI / 2), 0.0f);
            mat = manager.loadMaterial("Materials/gizmoYMaterial.j3m");
            setOriginalMaterial(mat);
            model.setMaterial(mat);
            this.attachChild(model);
        } else if (type.equals("RotateZ")) {
            model = manager.loadModel("Objects/RotationGizmo.obj");
            model.scale(scaleVal);
            model.move(posX, posY, posZ);
            model.setLocalTranslation(0, displaceLength + 1, 0);
            model.rotate(0.0f, 0.0f, (float) (Math.PI / 2));
            mat = manager.loadMaterial("Materials/gizmoZMaterial.j3m");
            setOriginalMaterial(mat);
            model.setMaterial(mat);
            this.attachChild(model);
        } else if (type.equals("TranslateX")) {
            model = manager.loadModel("Objects/TranslationGizmo.obj");
            model.scale(scaleVal);
            model.move(posX, posY, posZ);
            model.setLocalTranslation(displaceWidth + 1, 0, 0);
            mat = manager.loadMaterial("Materials/gizmoXMaterial.j3m");
            setOriginalMaterial(mat);
            model.setMaterial(mat);
            this.attachChild(model);
        } else if (type.equals("TranslateY")) {
            model = manager.loadModel("Objects/TranslationGizmo.obj");
            model.scale(scaleVal);
            model.move(posX, posY, posZ);
            model.setLocalTranslation(0, 0, displaceHeight + 1);
            model.rotate(0.0f, -(float) (Math.PI / 2), 0.0f);
            mat = manager.loadMaterial("Materials/gizmoYMaterial.j3m");
            setOriginalMaterial(mat);
            model.setMaterial(mat);
            this.attachChild(model);
        } else if (type.equals("TranslateZ")) {
            model = manager.loadModel("Objects/TranslationGizmo.obj");
            model.scale(scaleVal);
            model.move(posX, posY, posZ);
            model.setLocalTranslation(0, displaceLength + 1, 0);
            model.rotate(0.0f, 0.0f, (float) (Math.PI / 2));
            mat = manager.loadMaterial("Materials/gizmoZMaterial.j3m");
            setOriginalMaterial(mat);
            model.setMaterial(mat);
            this.attachChild(model);
        }
    }

    @Override
    public void create(String name, AssetManager manager, String extraInfo) {
        Spatial model = null;
        Material mat = null;
        float scaleVal = 0.005f;
        if (type.equals("ScaleX")) {
            model = manager.loadModel("Objects/TransformationGizmo.obj");
            model.scale(scaleVal);
            model.move(posX, posY, posZ);
            model.setLocalTranslation(displaceWidth + 1, 0, 0);
            model.rotate(-(float) (Math.PI / 2), 0.0f, 0.0f);
            mat = manager.loadMaterial("Materials/gizmoXMaterial.j3m");
            setOriginalMaterial(mat);
            model.setMaterial(mat);
            this.attachChild(model);
        } else if (type.equals("ScaleY")) {
            model = manager.loadModel("Objects/TransformationGizmo.obj");
            model.scale(scaleVal);
            model.move(posX, posY, posZ);
            model.setLocalTranslation(0, 0, displaceHeight + 1);
            model.rotate(0.0f, -(float) (Math.PI / 2), 0.0f);
            mat = manager.loadMaterial("Materials/gizmoYMaterial.j3m");
            setOriginalMaterial(mat);
            model.setMaterial(mat);
            this.attachChild(model);
        } else if (type.equals("ScaleZ")) {
            model = manager.loadModel("Objects/TransformationGizmo.obj");
            model.scale(scaleVal);
            model.move(posX, posY, posZ);
            model.setLocalTranslation(0, displaceLength + 1, 0);
            model.rotate(0.0f, 0.0f, (float) (Math.PI / 2));
            mat = manager.loadMaterial("Materials/gizmoZMaterial.j3m");
            setOriginalMaterial(mat);
            model.setMaterial(mat);
            this.attachChild(model);
        } else if (type.equals("RotateX")) {
            model = manager.loadModel("Objects/RotationGizmo.obj");
            model.scale(scaleVal);
            model.move(posX, posY, posZ);
            model.setLocalTranslation(displaceWidth + 1, 0, 0);
            mat = manager.loadMaterial("Materials/gizmoXMaterial.j3m");
            setOriginalMaterial(mat);
            model.setMaterial(mat);
            this.attachChild(model);
        } else if (type.equals("RotateY")) {
            model = manager.loadModel("Objects/RotationGizmo.obj");
            model.scale(scaleVal);
            model.move(posX, posY, posZ);
            model.setLocalTranslation(0, 0, displaceHeight + 1);
            model.rotate(0.0f, -(float) (Math.PI / 2), 0.0f);
            mat = manager.loadMaterial("Materials/gizmoYMaterial.j3m");
            setOriginalMaterial(mat);
            model.setMaterial(mat);
            this.attachChild(model);
        } else if (type.equals("RotateZ")) {
            model = manager.loadModel("Objects/RotationGizmo.obj");
            model.scale(scaleVal);
            model.move(posX, posY, posZ);
            model.setLocalTranslation(0, displaceLength + 1, 0);
            model.rotate(0.0f, 0.0f, (float) (Math.PI / 2));
            mat = manager.loadMaterial("Materials/gizmoZMaterial.j3m");
            setOriginalMaterial(mat);
            model.setMaterial(mat);
            this.attachChild(model);
        } else if (type.equals("TranslateX")) {
            model = manager.loadModel("Objects/TranslationGizmo.obj");
            model.scale(scaleVal);
            model.move(posX, posY, posZ);
            model.setLocalTranslation(displaceWidth + 1, 0, 0);
            mat = manager.loadMaterial("Materials/gizmoXMaterial.j3m");
            setOriginalMaterial(mat);
            model.setMaterial(mat);
            this.attachChild(model);
        } else if (type.equals("TranslateY")) {
            model = manager.loadModel("Objects/TranslationGizmo.obj");
            model.scale(scaleVal);
            model.move(posX, posY, posZ);
            model.setLocalTranslation(0, 0, displaceHeight + 1);
            model.rotate(0.0f, -(float) (Math.PI / 2), 0.0f);
            mat = manager.loadMaterial("Materials/gizmoYMaterial.j3m");
            setOriginalMaterial(mat);
            model.setMaterial(mat);
            this.attachChild(model);
        } else if (type.equals("TranslateZ")) {
            model = manager.loadModel("Objects/TranslationGizmo.obj");
            model.scale(scaleVal);
            model.move(posX, posY, posZ);
            model.setLocalTranslation(0, displaceLength + 1, 0);
            model.rotate(0.0f, 0.0f, (float) (Math.PI / 2));
            mat = manager.loadMaterial("Materials/gizmoZMaterial.j3m");
            setOriginalMaterial(mat);
            model.setMaterial(mat);
            this.attachChild(model);
        }
    }
}
