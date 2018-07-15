package me.afarrukh.hashbot.data;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;

public abstract class DataManager implements DataManagerInterface {
    JSONObject jsonObject;
    File file;

    DataManager() {
        this.jsonObject = new JSONObject();
        this.file = null;
    }

    void flushData() {
        try {

            FileWriter newFile = new FileWriter(file);
            newFile.write(jsonObject.toJSONString());
            newFile.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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

    public abstract void writePresets();
}
