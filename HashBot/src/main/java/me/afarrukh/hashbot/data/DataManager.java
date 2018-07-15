package me.afarrukh.hashbot.data;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;

/**
 * This is a generic data manager class which implements a JSON method of saving data.
 * In future, if a new method is chosen, the methods in this class would simply need to be rewritten
 * All method calls to this class will remain unchanged even if the implementation is changed.
 */
public abstract class DataManager implements DataManagerInterface {
    JSONObject jsonObject;
    File file;

    DataManager() {
        this.jsonObject = new JSONObject();
        this.file = null;
    }

    /**
     * Writes the data to the json object, basically updates the file with the current state of the JSON object
     */
    void flushData() {
        try {

            FileWriter newFile = new FileWriter(file);
            newFile.write(jsonObject.toJSONString());
            newFile.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads the data into the file, basically gets wrapped by the load() method in the current inheriting classes classes
     */
    void initialiseData() {
        final JSONParser jsonParser = new JSONParser();
        Object obj;

        try {
            obj = jsonParser.parse(new FileReader(file));
            this.jsonObject = (JSONObject) obj;
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            writePresets();
            load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Defines the preset values to be written into the JSON files
     */
    public abstract void writePresets();
}
