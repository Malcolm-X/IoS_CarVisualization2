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
        System.out.println("gson.toJson(observation);#\n" +
                "        #\n" +
                "        #\n" +
                "        #\n" +
                "        #\n" +
                "        #\n" +
                "        #\n" +
                "        #\n" +
                "        #\n" +
                "        #\n" +
                "        ##\n" +
                "        #\n" +
                "        #\n" +
                "        #\n" +
                "        #\n" +
                "        #\n" +
                "        AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "                #\n" +
                "        #\n" +
                "        #\n" +
                "        #\n" +
                "        #\n" +
                "        #\n" +
                "        #\n" +
                "        #\n" +
                "        #\n" +
                "        #\n" +
                "        ##\n" +
                "        #");
        System.out.println(json);

        DBObject bson = (DBObject) JSON.parse(json);
        System.out.println("BObject bson = (DBObject) JSON.parse(json);#\n" +
                "        #\n" +
                "        #\n" +
                "        #\n" +
                "        #\n" +
                "        #\n" +
                "        #\n" +
                "        #\n" +
                "        #\n" +
                "        #\n" +
                "        ##\n" +
                "        #\n" +
                "        #\n" +
                "        #\n" +
                "        #\n" +
                "        #\n" +
                "        AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "                #\n" +
                "        #\n" +
                "        #\n" +
                "        #\n" +
                "        #\n" +
                "        #\n" +
                "        #\n" +
                "        #\n" +
                "        #\n" +
                "        #\n" +
                "        ##\n" +
                "        #");
        result.f1 = new BSONWritable(bson);
        System.out.println("result.f1 = new BSONWritable(bson);#\n" +
                "        #\n" +
                "        #\n" +
                "        #\n" +
                "        #\n" +
                "        #\n" +
                "        #\n" +
                "        #\n" +
                "        #\n" +
                "        #\n" +
                "        ##\n" +
                "        #\n" +
                "        #\n" +
                "        #\n" +
                "        #\n" +
                "        #\n" +
                "        AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA\n" +
                "                #\n" +
                "        #\n" +
                "        #\n" +
                "        #\n" +
                "        #\n" +
                "        #\n" +
                "        #\n" +
                "        #\n" +
                "        #\n" +
                "        #\n" +
                "        ##\n" +
                "        #");
//        result.f1 = new BSONWritable();
        return result;
    }
}
