package com.bgrfacile.reactive_parsing.component;

import com.bgrfacile.reactive_parsing.service.DataIngestionService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class DataLoaderRunner implements CommandLineRunner {

    private final DataIngestionService service;

    public DataLoaderRunner(DataIngestionService service) {
        this.service = service;
    }

    @Override
    public void run(String... args) throws Exception {
        // Chargement du fichier depuis le dossier resources
        /*File file = new ClassPathResource("demo.json").getFile();
        String filePath = file.getAbsolutePath();*/

        // Appeler le service avec le chemin du fichier
//        service.processJsonFile(filePath).subscribe();
    }
}