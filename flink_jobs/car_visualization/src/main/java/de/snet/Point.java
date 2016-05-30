//package de.snet;
//
//import com.mongodb.client.model.geojson.Position;
//
//import java.io.Serializable;
//
///**
// * Created by Malcolm-X on 30.05.2016.
// */
//public class Point implements Serializable {
//    public double longitude;
//    public double latitude;
//
//    public Point(){
//
//    }
//
//    public Point(Double longitude, Double latitude) {
//        this.latitude = latitude;
//        this.longitude = longitude;
//    }
//
//    public com.mongodb.client.model.geojson.Point getGeoPoint(){
//        return new com.mongodb.client.model.geojson.Point(new Position(longitude,latitude));
//    }
//}
