package com.bgrfacile.reactive_parsing.service;

import com.bgrfacile.reactive_parsing.Dto.PlaylistDto;
import com.bgrfacile.reactive_parsing.Dto.RootDto;
import com.bgrfacile.reactive_parsing.entity.Artist;
import com.bgrfacile.reactive_parsing.entity.Info;
import com.bgrfacile.reactive_parsing.entity.Playlist;
import com.bgrfacile.reactive_parsing.entity.Track;
import com.bgrfacile.reactive_parsing.repository.ArtistRepository;
import com.bgrfacile.reactive_parsing.repository.InfoRepository;
import com.bgrfacile.reactive_parsing.repository.PlaylistRepository;
import com.bgrfacile.reactive_parsing.repository.TrackRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;

@Service
public class DataIngestionService {
    private final InfoRepository infoRepository;
    private final PlaylistRepository playlistRepository;
    private final ArtistRepository artistRepository;
    private final TrackRepository trackRepository;
    private final ObjectMapper objectMapper;
    private final TransactionalOperator transactionalOperator;

    private static final int MAX_PARALLELISM = 10; // Limite de parallélisme
    private final DatabaseClient databaseClient;

    public DataIngestionService(InfoRepository infoRepository,
                                PlaylistRepository playlistRepository,
                                ArtistRepository artistRepository,
                                TrackRepository trackRepository,
                                ObjectMapper objectMapper,
                                TransactionalOperator transactionalOperator, DatabaseClient databaseClient) {
        this.infoRepository = infoRepository;
        this.playlistRepository = playlistRepository;
        this.artistRepository = artistRepository;
        this.trackRepository = trackRepository;
        this.objectMapper = objectMapper;
        this.transactionalOperator = transactionalOperator;
        this.databaseClient = databaseClient;
    }

    public Mono<Void> processJsonFile(String filePath) {
        long startTime = System.currentTimeMillis();

        return Mono.fromCallable(() -> objectMapper.readValue(new File(filePath), RootDto.class))
                .flatMap(root -> {
                    if (root.getInfo() == null || root.getPlaylists() == null) {
                        return Mono.error(new RuntimeException("Structure JSON inattendue."));
                    }

                    Info info = new Info();
                    info.setGeneratedOn(root.getInfo().getGeneratedOn());
                    info.setSlice(root.getInfo().getSlice());
                    info.setVersion(root.getInfo().getVersion());

                    return transactionalOperator.transactional(
                            infoRepository.save(info)
                                    .flatMap(savedInfo -> processPlaylists(root, savedInfo.getId()))
                    );
                })
                .doOnSuccess(v -> {
                    long endTime = System.currentTimeMillis();
                    System.out.println("Traitement terminé en " + ((endTime - startTime) / 1000.0) + " secondes.");
                })
                .doOnError(e -> System.err.println("Erreur lors du traitement du fichier JSON : " + e.getMessage()));
    }

    private Mono<Void> processPlaylists(RootDto root, Long infoId) {
        return Flux.fromIterable(root.getPlaylists())
                .flatMap(playlistDto -> {
                    Playlist playlist = new Playlist();
                    playlist.setName(playlistDto.getName());
                    playlist.setCollaborative(Boolean.TRUE.equals(playlistDto.getCollaborative()));
                    playlist.setPid(playlistDto.getPid());
                    playlist.setModifiedAt(playlistDto.getModifiedAt());
                    playlist.setNumTracks(playlistDto.getNumTracks());
                    playlist.setNumAlbums(playlistDto.getNumAlbums());
                    playlist.setNumFollowers(playlistDto.getNumFollowers());
                    playlist.setInfoId(infoId);

                    return playlistRepository.save(playlist)
                            .flatMap(savedPlaylist -> processTracks(playlistDto, savedPlaylist.getId()));
                }, MAX_PARALLELISM)
                .then();
    }

    private Mono<Void> processTracks(PlaylistDto playlistDto, Long playlistId) {
        if (playlistDto.getTracks() == null || playlistDto.getTracks().isEmpty()) {
            return Mono.empty();
        }

        return Flux.fromIterable(playlistDto.getTracks())
                .flatMap(trackDto -> getOrCreateArtist(trackDto.getArtistName(), trackDto.getArtistUri())
                        .flatMap(artistId -> {
                            Track track = new Track();
                            track.setPos(trackDto.getPos());
                            track.setTrackUri(trackDto.getTrackUri());
                            track.setTrackName(trackDto.getTrackName());
                            track.setAlbumUri(trackDto.getAlbumUri());
                            track.setDurationMs(trackDto.getDurationMs());
                            track.setAlbumName(trackDto.getAlbumName());
                            track.setPlaylistId(playlistId);
                            track.setArtistId(artistId);

                            return trackRepository.save(track);
                        }), 10) // Limite de 10 requêtes simultanées
                .then();
    }

    private Mono<Long> getOrCreateArtist(String artistName, String artistUri) {
        return artistRepository.findByArtistUri(artistUri)
                .map(Artist::getId)
                .switchIfEmpty(
                        artistRepository.save(createArtist(artistName, artistUri))
                                .map(Artist::getId)
                                .onErrorResume(org.springframework.dao.DuplicateKeyException.class, e ->
                                        artistRepository.findByArtistUri(artistUri)
                                                .map(Artist::getId)
                                )
                                .onErrorResume(e -> {
                                    System.err.println("Erreur lors de la création de l'artiste : " + e.getMessage());
                                    return Mono.error(e);
                                })
                );
    }
    private Artist createArtist(String name, String uri) {
        Artist artist = new Artist();
        artist.setArtistName(name);
        artist.setArtistUri(uri);
        return artist;
    }

    private Mono<Artist> saveArtistSafely(Artist artist) {
        return databaseClient.sql("INSERT INTO artists (artist_name, artist_uri) VALUES ($1, $2) " +
                        "ON CONFLICT (artist_uri) DO NOTHING RETURNING id, artist_name, artist_uri")
                .bind(0, artist.getArtistName())
                .bind(1, artist.getArtistUri())
                .map((row, metadata) -> {
                    Artist savedArtist = new Artist();
                    savedArtist.setId(row.get("id", Long.class));
                    savedArtist.setArtistName(row.get("artist_name", String.class));
                    savedArtist.setArtistUri(row.get("artist_uri", String.class));
                    return savedArtist;
                })
                .one()
                .switchIfEmpty(artistRepository.findByArtistUri(artist.getArtistUri())); // Récupère l'artiste si "DO NOTHING"
    }
}