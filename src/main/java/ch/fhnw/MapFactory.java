package ch.fhnw;

class MapFactory {

    static Map getNewMap(int mapSize) {
        return new Map(mapSize);
    }

    static Map getOriginalMap(int mapIndex) {
        Map map = null;
        System.out.println("Mapindex: " + mapIndex);
        if (mapIndex == 0) {
            map = new Map(32);
            map.addStairsSimulationFeatures();
        }

        if (mapIndex == 1) {
            map = new Map(64);
            map.addTheWallMapFeatures();
        }
        return map;
    }
}

