package models.entities;

import com.openrsc.client.entityhandling.defs.ItemDef;
import com.openrsc.client.entityhandling.instances.Item;

public class GroundItemDef extends Item {
    private int x = 0;
    private int z = 0;
    private int distance = 0;

    public GroundItemDef() {
        super();
    }

    public GroundItemDef(ItemDef itemDef) {
            super();
            this.setItemDef(itemDef);
    }

    public int getID() {
        if(this.getItemDef() != null) {
            return this.getItemDef().id;
        }

        return -1;
    }

    public String getName() {
        if(this.getItemDef() != null) {
            return this.getItemDef().name;
        }

        return "";
    }

    public int getX() {
        return x;
    }

    public int getZ() {
        return z;
    }

    public int getDistance() {
        return distance;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }
}
