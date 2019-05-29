package ch.fhnw;

class FXModel {


    SimulationMap createNewMap(int mapSize) {
        return MapFactory.getNewMap(mapSize);
    }

    SimulationMap createOriginalMap(int mapIndex) {
        return MapFactory.getOriginalMap(mapIndex);
    }
}



