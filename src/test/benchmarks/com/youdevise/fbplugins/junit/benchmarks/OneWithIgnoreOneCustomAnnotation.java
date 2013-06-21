package com.youdevise.fbplugins.junit.benchmarks;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class OneWithIgnoreOneCustomAnnotation {
    
    @MyCustomAnnotation
    @Test
    public void withCustomAnnotation() throws Exception {
        Assert.assertTrue(true);
    }

    @Ignore
    @Test
    public void withIgnore() throws Exception {
        Assert.assertTrue(true);
    }
}
