package com.youdevise.fbplugins.junit.benchmarks;

import org.junit.Assert;
import org.junit.Test;

public class OneTestWithCustomAnnotationToLookFor {

    @MyCustomAnnotation
    @Test
    public void withCustomAnnotation() throws Exception {
        Assert.assertTrue(true);
    }
    
}
