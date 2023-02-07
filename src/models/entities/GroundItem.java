package models.entities;

public class GroundItem {
    private MapPoint point;
    private ItemId id;

    public GroundItem(int x, int y, ItemId id) {
        this.point = new MapPoint(x, y);
        this.id = id;
    }

    public MapPoint getPoint() {
        return point;
    }

    public void setPoint(MapPoint point) {
        this.point = point;
    }

    public ItemId getId() {
        return id;
    }

    public void setId(ItemId id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GroundItem that = (GroundItem) o;

        if (!point.equals(that.point)) return false;
        return id == that.id;
    }

    @Override
    public int hashCode() {
        int result = point.hashCode();
        result = 31 * result + id.hashCode();
        return result;
    }

}
