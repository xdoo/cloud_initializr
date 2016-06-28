package com.catify.initializr.services;

import com.catify.initializr.domain.Domain;
import com.google.common.jimfs.Configuration;
import com.google.common.jimfs.Jimfs;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 *
 * @author claus
 */
@Service
public class ZipGeneratorService {

    private static final Logger LOG= Logger.getLogger( MavenStructureService.class.getName() );
    
    private DomainGeneratorService domainGen;

    @Autowired
    public ZipGeneratorService(DomainGeneratorService domainGen) {
        this.domainGen = domainGen;
    }
    
    public byte[] zipFileSystem(Domain domain) {
        
        byte[] result = null;
        
        // create virtual file system
        try (FileSystem fs = Jimfs.newFileSystem(Configuration.unix())) {
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
                
                this.domainGen.createDomain(domain, zipfs);
                
                // finally close file system
                zipfs.close();

            } catch (IOException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
            
            result = Files.readAllBytes(zipPath);
            
            // finally close file system
            fs.close();

        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
        
        return result;
    }

}
