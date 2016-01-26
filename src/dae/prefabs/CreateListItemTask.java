package dae.prefabs;

import com.jme3.asset.AssetManager;
import dae.GlobalObjects;
import dae.prefabs.parameters.ListParameter;
import dae.prefabs.types.ObjectType;

/**
 * This task is executed when a new list item has to be created.
 *
 * @author Koen Samyn
 */
public class CreateListItemTask implements Runnable {
    private Prefab prefab;
    private ListParameter lp;
    private ObjectType objectType;

    public CreateListItemTask(Prefab prefab, ListParameter lp, ObjectType toCreate) {
        this.lp = lp;
        this.objectType = toCreate;
        this.prefab = prefab;
    }

    public void run() {
        int size = lp.getListSize(prefab);
        String name = objectType.getLabel() + (size + 1);
        AssetManager am = GlobalObjects.getInstance().getAssetManager();
        Object object = objectType.create(am,name);
        lp.addListItem(prefab,object);
        lp.notifyChangeInValue(prefab);
    }
}
