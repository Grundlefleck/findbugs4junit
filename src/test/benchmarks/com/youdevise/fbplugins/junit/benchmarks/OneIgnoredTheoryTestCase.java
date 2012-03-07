package com.youdevise.fbplugins.junit.benchmarks;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.experimental.theories.DataPoint;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.runner.RunWith;

@RunWith(Theories.class)
public class OneIgnoredTheoryTestCase {

    @DataPoint public static final int dataPoint = 42;
    
    @Ignore
    @Theory
    public void ignoredTheory(int dataPoint) throws Exception {
        Assert.assertTrue(true);
    }
}
