package com.google.gson.DYI;
/**
 * Singleton class of DYIStructure that can only be created once via the method getInstance(),
 * further calls to the method returns the one object that is already created.
 * The class functions as a structure class that holds a boolean list with default flags set to false.
 * These flags are then switched on depending on if the branch has been reached or not.
 */
public class DYIStructureSingleton {
    // static variable single_instance of type Singleton
    private static DYIStructureSingleton single_instance = null;

    // boolean list with length of number of points.
    public boolean[] flag = new boolean[29];

    /**
     * Private constructor for singleton object. (see: https://www.geeksforgeeks.org/singleton-class-java/)
     */
    private DYIStructureSingleton()
    {
    }

    /**
     * Static public method for creating a instance, if the instance is already created, return the pointer to the object.
     * @return DYIStructureSingleton object.
     */
    public static DYIStructureSingleton getInstance()
    {
        if (single_instance == null)
            single_instance = new DYIStructureSingleton();

        return single_instance;
    }

    /**
     * Teardown and resets the singleton object.
     */
    public void tearDown(){
        for (int i = 0; i < this.flag.length ; i++) {
            this.flag[i] =false;
        }
    }
}
