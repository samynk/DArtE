/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dae.prefabs.ui.events;

import dae.prefabs.types.ObjectType;

/**
 *
 * @author Koen
 */
public class CreateObjectEvent {

    public String objectToCreate;
    private String extraInfo;
    private ObjectType objectType;

    public CreateObjectEvent(String className, String extraInfo, ObjectType ot) {
        objectToCreate = className;
        this.extraInfo = extraInfo;
        this.objectType = ot;
    }

    public String getClassName() {
        return objectToCreate;
    }

    public String getExtraInfo() {
        return extraInfo;
    }

    public ObjectType getObjectType() {
        return objectType;
    }
}
