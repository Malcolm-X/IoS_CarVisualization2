package de.snet;

import com.google.gson.Gson;
import com.mongodb.DBObject;
import com.mongodb.hadoop.io.BSONWritable;
import com.mongodb.util.JSON;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.hadoop.io.Text;

/**
 * Created by Malcolm-X on 30.05.2016.
 */
public class ToMongoMap implements org.apache.flink.api.common.functions.MapFunction<Observation, Tuple2<Text,BSONWritable>> {
//    public Gson gson;

//    public ToMongoMap(){
//
//    }

    
    @Override
    public Tuple2<Text, BSONWritable> map(Observation observation) throws Exception {
//        Gson gson = new Gson();
//        GsonBuilder builder = new GsonBuilder();
//        builder.registerTypeAdapter(Point.class, new GeoPointConverter());
//        builder.setPrettyPrinting();
//        Gson gson = builder.create();
        Gson gson = new Gson();
        Tuple2<Text,BSONWritable> result = new Tuple2<>();
        result.f0 = new Text("Device" + observation.deviceID);
        String json = gson.toJson(observation);
        DBObject bson = (DBObject) JSON.parse(json);
        result.f1 = new BSONWritable(bson);
        result.f1 = new BSONWritable();
        return result;
    }
}
