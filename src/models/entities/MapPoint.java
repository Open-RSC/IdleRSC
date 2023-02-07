package models.entities;

public class MapPoint {
    private final int x;
    private final int y;

    public MapPoint(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public static int distance(MapPoint a, MapPoint b) {
        int dx = Math.abs(a.getX() - b.getX());
        int dy = Math.abs(a.getY() - b.getY());
        return (int) Math.round(Math.sqrt(dx * dx + dy * dy));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MapPoint mapPoint = (MapPoint) o;

        if (x != mapPoint.x) return false;
        return y == mapPoint.y;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }

    @Override
    public String toString() {
        return "MapPoint{" +
                "x=" + x +
                ", y=" + y +
                '}';
    }

    public enum BankPoint {
        EDGEVILLE(new MapPoint(215, 451)),
        AL_KHARID(new MapPoint(85, 695)),
        DRAYNOR(new MapPoint(219, 635)),
        SEERS(new MapPoint(501, 454)),
        YANILLE(new MapPoint(586, 755)),
        SHANTAY_PASS(new MapPoint(58, 738)),
        TREE_GNOME_VILLAGE(new MapPoint(713, 506)),
        SHILLO_VILLAGE(new MapPoint(401, 851)),
        ARDOUGNE_WEST(new MapPoint(580, 572)),
        ARDOUGNE_EAST(new MapPoint(551, 613)),
        FALADOR_WEST(new MapPoint(329, 553)),
        FALADOR_EAST(new MapPoint(286, 571)),
        VARROCK_WEST(new MapPoint(163, 494)),
        VARROCK_EAST(new MapPoint(102, 509));

        private final MapPoint mapPoint;

        BankPoint(MapPoint mapPoint) {
            this.mapPoint = mapPoint;
        }

        public MapPoint getMapPoint() {
            return mapPoint;
        }

    }

    public enum MiningCampPoint {
        AL_KHARID(new MapPoint(71, 588)),
        BARBARIAN_VILLAGE(new MapPoint(225, 503)),
        RIMMINGTON(new MapPoint(313, 640)),
        CRAFTING_GUILD(new MapPoint(338, 609)),
        FALADOR(new MapPoint(362, 550)),
        SEERS(new MapPoint(611, 451)),
        PORT_KHAZARD(new MapPoint(611, 451)),
        ENTRANA(new MapPoint(423, 534)),
        FELDIP_HILLS(new MapPoint(614, 842)),
        SHILLO_VILLAGE(new MapPoint(426, 823)),
        EAST_OF_THE_BATTLEFIELD(new MapPoint(615, 652)),
        WEST_OF_THE_BATTLEFIELD(new MapPoint(686, 631)),
        BRIMHAVEN_NORTH(new MapPoint(505, 676)),
        BRIMHAVEN_SOUTH(new MapPoint(503, 708)),
        VARROCK_WEST(new MapPoint(160, 539)),
        VARROCK_EAST(new MapPoint(73, 546));

        private final MapPoint mapPoint;

        MiningCampPoint(MapPoint mapPoint) {
            this.mapPoint = mapPoint;
        }

        public MapPoint getMapPoint() {
            return mapPoint;
        }
    }

}

