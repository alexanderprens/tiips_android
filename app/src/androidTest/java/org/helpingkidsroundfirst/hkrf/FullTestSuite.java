package org.helpingkidsroundfirst.hkrf;

import android.test.suitebuilder.TestSuiteBuilder;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * Created by alexa on 1/31/2017.
 */
public class FullTestSuite extends TestSuite {
    public static Test suite() {
        return new TestSuiteBuilder(FullTestSuite.class)
                .build();
    }

    public FullTestSuite() {
        super();
    }
}
