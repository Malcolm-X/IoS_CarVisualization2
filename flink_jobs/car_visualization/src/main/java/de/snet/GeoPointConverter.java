//package de.snet;
//
//import com.google.gson.*;
//
//import java.lang.reflect.Type;
//
//public class GeoPointConverter implements JsonSerializer<Point>,
//        JsonDeserializer<Point> {
//
//    public Gson gson;
//
//    public GeoPointConverter(){
//        this.gson = new Gson();
//    }
//
//
//    @Override
//    public JsonElement serialize(Point src, Type srcType,
//                                 JsonSerializationContext context) {
//        return gson.fromJson(src.getGeoPoint().toJson(), JsonElement.class);
//    }
//
//    @Override
//    public Point deserialize(JsonElement json, Type type,
//        JsonDeserializationContext context)
//        throws JsonParseException {
//        //not needed
////
////        final JsonArray array = json.getAsJsonArray();
////        final JsonElement lonElement = array.get(0);
////        final JsonElement latElement = array.get(1);
////        final Double lon = lonElement.getAsDouble();
////        final Double lat = latElement.getAsDouble();
////
////        return new Point((int)(lat * 1E6), (int)(lon * 1E6));
//    return null;
//    }
//}
