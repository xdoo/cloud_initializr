package com.catify.initializr.services;

import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipOutputStream;
import org.junit.Test;

/**
 *
 * @author claus
 */
public class ZipTest {

    @Test
    public void testZipOnVirtualNio() throws IOException {

        // create virtual file system
        FileSystem fs = Jimfs.newFileSystem(Configuration.unix());
        Path zipPath = fs.getPath("/my.zip");

        // create empty zip file
        // this is a workarond, because I found no way to
        // create the zip file system on top of the virtual
        // file system.
        ByteArrayOutputStream baos1 = new ByteArrayOutputStream();
        ZipOutputStream emptyZip = new ZipOutputStream(baos1);
        emptyZip.close();
        Files.write(zipPath, baos1.toByteArray());

        // create zip file system
        URI uri = URI.create("jar:" + zipPath.toUri());
        Map<String, String> env = new HashMap<>();
        env.put("create", "true");
        try (FileSystem zipfs = FileSystems.newFileSystem(uri, env)) {
            Files.write(zipfs.getPath("/foo.txt"), "This is my foo text document".getBytes());
            Path folder = zipfs.getPath("/folder");
            Files.createDirectory(folder);
            Files.write(folder.resolve("bar.txt"), "This is my bar text document".getBytes());
            
            // show zip
            System.out.println("\nzip ------------------ ");
            this.walker(zipfs);
            
            // show parent fs
            System.out.println("\nparent --------------- ");
            this.walker(fs);

        } catch (IOException ex) {
            Logger.getLogger(ZipTest.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // write zip file to hdd
        Path hhd = Paths.get("my-downloaded.zip");
        Files.write(hhd, Files.readAllBytes(zipPath));
        
        fs.close();

    }

    private void walker(FileSystem fs) throws IOException {
        Iterable<Path> root = fs.getRootDirectories();
        for (Path path : root) {
            Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    System.out.println(file.toString());
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    System.out.println(dir.toString());
                    return FileVisitResult.CONTINUE;
                }

            });
        }
    }

}
