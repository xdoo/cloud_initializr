package com.catify.initializr.services;

import com.catify.initializr.domain.Domain;
import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Map;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author claus
 */
public class MavenStructureServiceTest {

    public MavenStructureServiceTest() {
    }

    /**
     * Test of createEmptyMavenProject method, of class MavenStructureService.
     * @throws java.io.IOException
     */
    @Test
    public void testCreateEmptyMavenProject() throws IOException {
        System.out.println("createEmptyMavenProject");
        MavenStructureService instance = new MavenStructureService();
        FileSystem fs = Jimfs.newFileSystem(Configuration.unix());
        Domain domain = new Domain("My-Domain", "com.foo.bar");
        Map<String,Path> result01 = instance.createEmptyMavenProject(fs, domain , "service-a");
        assertNotNull(result01);
        
        Map<String,Path> result02 = instance.createEmptyMavenProject(fs, domain, "service-b");
        assertNotNull(result02);

        Iterable<Path> root = fs.getRootDirectories();
        for (Path path : root) {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>()
            {

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    System.out.println(dir.toString());
                    return FileVisitResult.CONTINUE;
                }
                
            });
        }
    }

    /**
     * Test of createMavenPaths method, of class MavenStructureService.
     */
    @Test
    public void testCreateMavenPaths() {
        System.out.println("createMavenPaths");
        FileSystem fs = Jimfs.newFileSystem(Configuration.unix());
        MavenStructureService instance = new MavenStructureService();
        Domain domain = new Domain("My-Domain", "com.foo.bar");
        Map<String,Path> result = instance.createMavenPaths(fs, domain, "service-a");
        assertEquals(6, result.size());
        assertTrue(result.containsKey(MavenStructureService.MAIN_DOCKER));
        assertTrue(result.containsKey(MavenStructureService.MAIN_JAVA));
        assertTrue(result.containsKey(MavenStructureService.MAIN_RESOURCES));
        assertTrue(result.containsKey(MavenStructureService.TEST_JAVA));
        assertTrue(result.containsKey(MavenStructureService.TEST_RESOURCES));
        
        System.out.println("path --> " + result.get(MavenStructureService.BASE).toString());
    }

}
