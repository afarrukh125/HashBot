package me.afarrukh.hashbot.data;


/**
 * @author Abdullah
 * Created on 13/09/2019 at 22:56
 *
 * An interface that should be implemented by any classes that intend to be used to access any kind of data.
 */
public interface IDataManager {

    /**
     * Load any presets for the data manager. This can range from the associated user data, if the implementing interface
     * involves a <code>User</code>.
     */
    void load();

    /**
     * Assign any preset values. For example, if obtaining values for this data manager requires them to already be present,
     * then any presets that are to be written should be set in this method. One such example is when dealing with
     * user data. When a user object is created, it has preset values of 0 experience, and 0 credit (actually, the
     * value for credit has changed over time), among other values that are set to defaults.
     * This is particularly useful in preventing <code>FileNotFoundException</code> type behaviour, if a simple
     * file based database is being used, or preventing <code>SQLException</code> or <code>NullPointerException</code>
     * if SQl is being used
     */
    void writePresets();

    /**
     * A preset method that assumes a key-value format between the object to be updated. For example, if we are updating
     * user experience points, then the key could be the "experience" (actually "score" in the database), and it will
     * return the corresponding experience value for this field. This is particularly easy to implement if the
     * class that is implementing this interface already knows what the ID of the object is.
     *
     * @see SQLUserDataManager#getValue(Object)
     * @param key The key that is to be used to obtain the corresponding value
     * @return The value for the provided key
     */
    Object getValue(Object key);

    /**
     * Another preset method that also assumes a key-value format. As above, but this time it behaves as a mutator method.
     * @param key The key to be provided, for which we will update the value
     * @param value The value to be updated
     */
    void updateValue(Object key, Object value);
}
