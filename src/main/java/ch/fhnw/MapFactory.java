package ch.fhnw;

class MapFactory {

    static Map getNewMap(int mapSize) {
        return new Map(mapSize);
    }

    static Map getOriginalMap(int mapIndex) {
        Map map = new Map(64);

        if (mapIndex == 0) {
            map.addStairsSimulationFeatures();
        }

        if (mapIndex == 1) {
            map.addTheWallMapFeatures();
        }
        return map;
    }
}

