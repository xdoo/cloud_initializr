package com.catify.initializr.services;

import com.google.common.base.CaseFormat;
import com.google.common.collect.ImmutableList;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.stereotype.Service;

/**
 *
 * @author claus
 */
@Service
public class Util {

    private static final Logger LOG = Logger.getLogger(Util.class.getName());

    public static String cleanPackageName(String packageName) {
        return packageName.replaceAll("\\.", "/");
    }

    public static String createServiceName(String name) {
        return name
                .replaceAll("-", "") // minus
                .replaceAll("_", "") // underscore
                .replaceAll("\\.", "") // dot
                .replaceAll(" ", "") // space
                .toLowerCase();
    }

    public static String createServiceId(String name) {
        return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, name.replaceAll("-", "_"));
    }

    public static void writeToFile(String content, Path path) {
        try {
            Files.write(path, ImmutableList.of(content), StandardCharsets.UTF_8);
        } catch (IOException ex) {
            Logger.getLogger(ServiceGeneratorService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void createDirectory(Path path) {
        try {
            Files.createDirectories(path);
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }

    public static String convertStreamToString(java.io.InputStream is) {
        java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
        return s.hasNext() ? s.next() : "";
    }

    public String readTemplateContent(String path) {
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream(path);
        String result = Util.convertStreamToString(stream);
        LOG.log(Level.INFO, "created template from path '{0}'.", path);
        return result;
    }

}
