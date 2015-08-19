import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import spark.Request;

public class Util {

    static JsonParser jsonParser = new JsonParser();

    public static JsonObject toJson(Request request) {
        return jsonParser.parse(request.body()).getAsJsonObject();
    }

}
