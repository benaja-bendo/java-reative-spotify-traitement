DO
$$
    DECLARE
        table_name RECORD;
    BEGIN
        -- Parcourt toutes les tables du schéma public
        FOR table_name IN
            SELECT tablename
            FROM pg_tables
            WHERE schemaname = 'public'
            LOOP
                EXECUTE 'DROP TABLE IF EXISTS ' || quote_ident(table_name.tablename) || ' CASCADE';
            END LOOP;
    END
$$;


-- Table info (métadonnées générales)
CREATE TABLE info
(
    id           SERIAL PRIMARY KEY, -- Utilisation de SERIAL pour une clé primaire auto-incrémentée
    generated_on TEXT,
    slice        TEXT,
    version      TEXT
);

-- Table playlists
CREATE TABLE playlists
(
    id            SERIAL PRIMARY KEY, -- Utilisation de SERIAL pour une clé primaire auto-incrémentée
    name          TEXT,
    collaborative BOOLEAN,            -- BOOLEAN au lieu de INTEGER pour représenter TRUE/FALSE
    pid           INTEGER,
    modified_at   INTEGER,
    num_tracks    INTEGER,
    num_albums    INTEGER,
    num_followers INTEGER,
    info_id       INTEGER,
    FOREIGN KEY (info_id) REFERENCES info (id)
);

-- Table artists (pour éviter la redondance des informations artiste)
CREATE TABLE artists
(
    id          SERIAL PRIMARY KEY, -- Utilisation de SERIAL pour une clé primaire auto-incrémentée
    artist_name TEXT,
    artist_uri  TEXT UNIQUE         -- Unique constraint pour éviter les doublons
);

-- Table tracks (chaque piste est associée à une playlist et un artiste)
CREATE TABLE tracks
(
    id          SERIAL PRIMARY KEY, -- Utilisation de SERIAL pour une clé primaire auto-incrémentée
    pos         INTEGER,
    track_uri   TEXT,
    track_name  TEXT,
    album_uri   TEXT,
    duration_ms INTEGER,
    album_name  TEXT,
    playlist_id INTEGER,
    artist_id   INTEGER,
    FOREIGN KEY (playlist_id) REFERENCES playlists (id),
    FOREIGN KEY (artist_id) REFERENCES artists (id)
);