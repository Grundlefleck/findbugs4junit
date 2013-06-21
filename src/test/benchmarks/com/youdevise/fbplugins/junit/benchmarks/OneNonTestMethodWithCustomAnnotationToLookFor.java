package com.youdevise.fbplugins.junit.benchmarks;

import org.junit.Assert;

public class OneNonTestMethodWithCustomAnnotationToLookFor {

    @MyCustomAnnotation
    public void withCustomAnnotation() throws Exception {
        Assert.assertTrue(true);
    }
    
}
