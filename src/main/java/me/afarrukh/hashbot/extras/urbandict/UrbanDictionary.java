package me.afarrukh.hashbot.extras.urbandict;

import me.afarrukh.hashbot.utils.APIUtils;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class UrbanDictionary {

    public static String getDefinition(String input) {
        String response = APIUtils.getResponseFromURL("http://api.urbandictionary.com/v0/define?term=" + input);

        JSONParser parser = new JSONParser();
        JSONObject jsonObject;
        try {
            jsonObject = (JSONObject) parser.parse(response);
        } catch (ParseException e) {
            return "Undefined";
        }

        JSONArray listOfDefinitions = (JSONArray) jsonObject.get("list");

        if (listOfDefinitions.isEmpty()) return "No Results Found";

        //JSONObject highestThumbs = resolveHighest(listOfDefinitions);
        JSONObject highestThumbs = (JSONObject) listOfDefinitions.get(0);

        StringBuilder builder = new StringBuilder();
        String definition = (String) highestThumbs.get("definition");
        definition = definition.replace("[", "").replace("]", "");

        builder.append(definition);

        if (highestThumbs.get("example") != null) {
            String example = (String) highestThumbs.get("example");
            example = example.replace("[", "").replace("]", "");
            builder.append("\n<>").append(example);
        }

        return builder.toString();
    }

    private static JSONObject resolveHighest(JSONArray listOfDefinitions) {
        JSONObject highestThumbs = (JSONObject) listOfDefinitions.get(0);

        for (Object o : listOfDefinitions) {
            JSONObject result = (JSONObject) o;

            if (computeThumbsRatio(result) > computeThumbsRatio(highestThumbs)) {
                highestThumbs = result;
            }
        }
        return highestThumbs;
    }

    private static Long computeThumbsRatio(JSONObject result) {
        Long thumbsUp = (Long) result.get("thumbs_up");
        Long thumbsDown = (Long) result.get("thumbs_down");

        if (thumbsDown == 0)
            thumbsDown++;

        return thumbsUp / thumbsDown;
    }
}
