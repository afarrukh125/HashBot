package me.afarrukh.hashbot.data;

public interface IDataManager {

    /**
     * Load any presets for the data manager. This can range from the associated user data, if the implementing interface
     * involves a <code>User</code>.
     */
    void load();

    /**
     * A preset method that assumes a key-value format between the object to be updated. For example, if we are updating
     * user experience points, then the key could be the "experience" (actually "score" in the database), and it will
     * return the corresponding experience value for this field. This is particularly easy to implement if the
     * class that is implementing this interface already knows what the ID of the object is.
     *
     * @param key The key that is to be used to obtain the corresponding value
     * @return The value for the provided key
     * @see SQLUserDataManager#getValue(Object)
     */
    Object getValue(Object key);

    /**
     * Another preset method that also assumes a key-value format. As above, but this time it behaves as a mutator method.
     *
     * @param key   The key to be provided, for which we will update the value
     * @param value The value to be updated
     */
    void updateValue(Object key, Object value);
}
