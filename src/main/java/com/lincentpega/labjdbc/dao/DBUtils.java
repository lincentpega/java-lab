package com.lincentpega.labjdbc.dao;

import lombok.experimental.UtilityClass;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

@UtilityClass
public class DBUtils {

    public static String readInitScript() throws IOException, URISyntaxException {
        URL resource = DBUtils.class.getClassLoader().getResource("schema.sql");
        if (resource == null) {
            throw new IOException("Unable to get schema.sql");
        }
        URI uri = resource.toURI();
        Path path = Path.of(uri);
        return Files.readString(path);
    }
}
