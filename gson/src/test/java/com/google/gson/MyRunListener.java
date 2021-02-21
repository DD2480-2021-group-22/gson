package com.google.gson;

import com.google.gson.DYI.DYIStructureSingleton;
import org.junit.runner.Result;
import org.junit.runner.notification.RunListener;


/**
 * Extends junit Runlistener, which is a class that acts as a listener for tests.
 * Documentation see: https://junit.org/junit4/javadoc/4.12/org/junit/runner/notification/RunListener.html
 * There has multiple neat feature, but the purpose of implementing a coverage tool,
 * we will use testRunFinished() which runs when all the tests are done -
 * and the constructor which is ran before all the tests starts.
 * The RunListener is the hooked in the internal pom.xml configuration of Maven and is run through Maven.
 */
public class MyRunListener extends RunListener {

    /**
     * Output text that notifies that the Listener has been hooked.
     * Initialize and creates the only instance of the singleton object DYIStructureSingleton.
     */
    public MyRunListener() {
        System.out.println("Creation of Run Listener...");
        DYIStructureSingleton dyiStructureSingleton = DYIStructureSingleton.getInstance();
    }

    /**
     * Fetches the only singleton object of DYIStructureSingleton.
     * Loops through the list of boolean flags and counts number of flags tagged as true.
     * If a flag was not reached. output the Flag ID, notifying which branch that was not reached by tests.
     * At the end it will output the number of true flags compared to the total as a representation of the -
     * total branch coverage for the function.
     * tearDown of singleton object at the end.
     * @param result Input is the result of the tests, by default construction.
     */
    public void testRunFinished(Result result) {
        DYIStructureSingleton dyiStructureSingleton = DYIStructureSingleton.getInstance();
        float trueCount = 0;
        float total= dyiStructureSingleton.flag.length;
        for (int i = 0; i < dyiStructureSingleton.flag.length; i++) {
            if (dyiStructureSingleton.flag[i]==true) {
                trueCount++;
            }
            if (!dyiStructureSingleton.flag[i]) {
                System.out.println("Flag ID: " + trueCount + " Was not reached.");
            }
        }
        System.out.println("Total branch-coverage: " + trueCount/total*100 + "%");
        dyiStructureSingleton.tearDown();
        }

    }
