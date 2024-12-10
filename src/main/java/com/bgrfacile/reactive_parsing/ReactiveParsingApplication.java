package com.bgrfacile.reactive_parsing;

import com.bgrfacile.reactive_parsing.entity.*;
import com.bgrfacile.reactive_parsing.repository.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootApplication
public class ReactiveParsingApplication implements CommandLineRunner {

    @Autowired
    private InfoRepository infoRepository;

    @Autowired
    private PlaylistRepository playlistRepository;

    @Autowired
    private TrackRepository trackRepository;

    @Autowired
    private ArtistRepository artistRepository;

    public static void main(String[] args) {
        SpringApplication.run(ReactiveParsingApplication.class, args);
    }

    @Override
    public void run(String... args) {
        System.out.println("Starting JSON processing...");

        String filePath = "src/main/resources/demo.json";

        processJsonFile(filePath)
                .subscribeOn(Schedulers.boundedElastic())
                .doOnSubscribe(subscription -> System.out.println("Démarrage du traitement..."))
                .doOnTerminate(() -> System.out.println("Traitement terminé."))
                .subscribe(
                        result -> System.out.println("Temps de traitement : " + result + " secondes"),
                        error -> System.err.println("Erreur lors du traitement du fichier JSON : " + error.getMessage())
                );
    }

    public Mono<Double> processJsonFile(String filePath) {
        return (Mono<Double>) Mono.fromCallable(() -> {
            // Démarrer le chronomètre
            long startTime = System.nanoTime();

            // Charger le fichier JSON
            ObjectMapper objectMapper = new ObjectMapper();
            File file = Paths.get(filePath).toFile();
            JsonNode rootNode = objectMapper.readTree(file);

            if (rootNode.has("info") && rootNode.has("playlists")) {
                JsonNode infoNode = rootNode.get("info");
                JsonNode playlistsNode = rootNode.get("playlists");

                // Insérer Info
                Info info = new Info();
                info.setGeneratedOn(infoNode.get("generated_on").asText());
                info.setSlice(infoNode.get("slice").asText());
                info.setVersion(infoNode.get("version").asText());

                return Mono.zip(
                        infoRepository.save(info),
                        artistRepository.findAll().collectMap(Artist::getArtistName)
                ).flatMap(tuple -> {
                    Info savedInfo = tuple.getT1();
                    Map<String, Artist> artistCache = tuple.getT2();

                    // Préparer les playlists et les tracks
                    List<Playlist> playlists = new ArrayList<>();
                    List<Track> tracks = new ArrayList<>();

                    playlistsNode.forEach(playlistNode -> {
                        Playlist playlist = new Playlist();
                        playlist.setName(playlistNode.get("name").asText());
                        playlist.setCollaborative(playlistNode.get("collaborative").asBoolean());
                        playlist.setPid(playlistNode.get("pid").asInt());
                        playlist.setModifiedAt(playlistNode.get("modified_at").asInt());
                        playlist.setNumTracks(playlistNode.get("num_tracks").asInt());
                        playlist.setNumAlbums(playlistNode.get("num_albums").asInt());
                        playlist.setNumFollowers(playlistNode.get("num_followers").asInt());
                        playlist.setInfoId(savedInfo.getId());
                        playlists.add(playlist);

                        JsonNode tracksNode = playlistNode.get("tracks");
                        if (tracksNode != null) {
                            tracksNode.forEach(trackNode -> {
                                String artistName = trackNode.get("artist_name").asText();
                                Artist artist = artistCache.computeIfAbsent(artistName, name -> {
                                    Artist newArtist = new Artist();
                                    newArtist.setArtistName(name);
                                    newArtist.setArtistUri(trackNode.get("artist_uri").asText());
                                    artistRepository.save(newArtist).subscribe();
                                    return newArtist;
                                });

                                Track track = new Track();
                                track.setPos(trackNode.get("pos").asInt());
                                track.setTrackUri(trackNode.get("track_uri").asText());
                                track.setTrackName(trackNode.get("track_name").asText());
                                track.setAlbumUri(trackNode.get("album_uri").asText());
                                track.setDurationMs(trackNode.get("duration_ms").asInt());
                                track.setAlbumName(trackNode.get("album_name").asText());
                                track.setPlaylistId(playlist.getId());
                                track.setArtistId(artist.getId());
                                tracks.add(track);
                            });
                        }
                    });

                    return playlistRepository.saveAll(playlists)
                            .thenMany(trackRepository.saveAll(tracks))
                            .then(Mono.just((System.nanoTime() - startTime) / 1_000_000_000.0));
                });
            } else {
                return Mono.error(new IllegalArgumentException("Structure JSON inattendue."));
            }
        }).flatMap(mono -> mono); // Aplatir le Mono<Mono<Double>> en Mono<Double>
    }


}
