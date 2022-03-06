package dev.ultimaratio.messari.utils;

import dev.ultimaratio.messari.config.AppConfig;
import com.opencsv.bean.CsvToBeanBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
public class FileUtils {
    
    @Autowired
    private AppConfig config;
    
    /**
     * Parse un fichier CSV en list de POJO définit par la class passée en paramètre
     *
     * @param <T>   le type de la classe
     * @param is    le stream du fichier à parser
     * @param clazz la classe dans laquelle les lignes du CSV doivent être convertie
     * @return une liste de POJO
     */
    public static <T> List<T> read(InputStream is, Class<T> clazz) {
        return new CsvToBeanBuilder<T>(new InputStreamReader(is, StandardCharsets.UTF_8))
            .withType(clazz)
//            .withSeparator(';')
            .withQuoteChar('"')
            .build()
            .parse();
    }
}