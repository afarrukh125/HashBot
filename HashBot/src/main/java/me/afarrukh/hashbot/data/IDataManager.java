package me.afarrukh.hashbot.data;


/**
 * This interface is so that in future, if a different means of storing data other than JSON (initially used) is used
 * it will be easy to modify the code underneath without changing the interactions and calls to them.
 * Consider SQLite or simply MySQL in future.
 */
public interface IDataManager {

    void load();
    void writePresets();
    Object getValue(Object key);
    void updateValue(Object key, Object value);
}
