package com.catify.initializr.services;

import java.io.File;
import org.junit.Assert;
import org.junit.Test;
import org.rythmengine.Rythm;

/**
 *
 * @author claus
 */
public class RythmTest {
    
    @Test
    public void testHello() {
        
        ClassLoader classLoader = getClass().getClassLoader();
	File hello = new File(classLoader.getResource("templates/hello.tmpl").getFile());
        
        String r01 = Rythm.render(hello, "Paul");
        Assert.assertEquals("Hello my friend Paul!", r01);
        
        String r02 = Rythm.render(hello, "Peter");
        Assert.assertEquals("Hello my friend Peter!", r02);
    }
    
    @Test
    public void testMany() {
        
        ClassLoader classLoader = getClass().getClassLoader();
	File hello = new File(classLoader.getResource("templates/hello.tmpl").getFile());
        
        System.out.println("started...");
        
        for (int i = 0; i < 1500; i++) {
            String r01 = Rythm.render(hello, String.format("Paul_%s", i));
            Assert.assertEquals(String.format("Hello my friend Paul_%s!", i), r01);
        }
    }
}
