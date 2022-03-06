package dev.ultimaratio.messari.utils;

import org.springframework.stereotype.Component;

@Component
public class ResourceUtils {

    public String getResourcePath(String path) {
        return getClass().getResource(path).getPath();
    }

}
