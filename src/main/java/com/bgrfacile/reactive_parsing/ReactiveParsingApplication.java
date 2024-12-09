package com.bgrfacile.reactive_parsing;

import com.bgrfacile.reactive_parsing.service.DataIngestionService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.File;
import java.nio.file.Paths;

@SpringBootApplication
public class ReactiveParsingApplication implements CommandLineRunner {

    @Autowired
    private DataIngestionService dataIngestionService;

    public static void main(String[] args) {
        SpringApplication.run(ReactiveParsingApplication.class, args);
    }

    @Override
    public void run(String... args) {
        System.out.println("Starting JSON processing...");

        String filePath = "src/main/resources/demo.json"; // Remplacez par le chemin de votre fichier JSON

        processJsonFile(filePath)
                .subscribeOn(Schedulers.boundedElastic())
                .doOnSubscribe(subscription -> System.out.println("Démarrage du traitement..."))
                .doOnTerminate(() -> System.out.println("Traitement terminé."))
                .subscribe(
                        result -> System.out.println("Temps de traitement : " + result + " secondes"),
                        error -> System.err.println("Erreur lors du traitement du fichier JSON : " + error.getMessage())
                );


        /*String filePath = "src/main/resources/demo.json";
        dataIngestionService.processJsonFile(filePath)
                .doOnSuccess(v -> System.out.println("JSON processing completed successfully."))
                .doOnError(e -> System.err.println("Error during JSON processing: " + e.getMessage()))
                .subscribe();*/
    }

    public static Mono<Double> processJsonFile(String filePath) {
        return Mono.fromCallable(() -> {
            // Démarrer le chronomètre
            long startTime = System.nanoTime();

            // Charger le fichier JSON
            ObjectMapper objectMapper = new ObjectMapper();
            File file = Paths.get(filePath).toFile();
            JsonNode rootNode = objectMapper.readTree(file);

            // Vérifier la structure du JSON
            if (rootNode.has("info") && rootNode.has("playlists")) {
                JsonNode info = rootNode.get("info");
                JsonNode playlists = rootNode.get("playlists");

                System.out.println("\nNombre total de playlists : " + playlists.size());

                // Parcourir les playlists
                playlists.forEach(playlist -> {
                    // System.out.println("\nNom de la playlist : " + playlist.get("name").asText());
                    JsonNode tracks = playlist.get("tracks");
                    if (tracks != null) {
                        tracks.forEach(track -> {
                            // System.out.println("    - Titre : " + track.get("track_name").asText());
                            // System.out.println("    - Album : " + track.get("album_name").asText());
                            System.out.println("    - Durée : " + track.get("duration_ms").asLong() + " ms");
                        });
                    }
                });
            } else {
                System.out.println("Structure JSON inattendue.");
            }

            // Arrêter le chronomètre
            long endTime = System.nanoTime();
            return (endTime - startTime) / 1_000_000_000.0; // Temps en secondes
        });
    }
}
