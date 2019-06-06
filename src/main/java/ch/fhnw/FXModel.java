package ch.fhnw;

class FXModel {


    Map createNewMap(int mapSize) {
        return MapFactory.getNewMap(mapSize);
    }

    Map createOriginalMap(int mapIndex) {
        return MapFactory.getOriginalMap(mapIndex);
    }
}



