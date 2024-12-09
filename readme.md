# java reactive (parsing data)

- la structure du fichier json

```json
{
    "info": {
        "generated_on": "2017-12-03 08:41:42.057563", 
        "slice": "0-999", 
        "version": "v1"
    }, 
    "playlists": [
        {
            "name": "Throwbacks", 
            "collaborative": "false", 
            "pid": 0, 
            "modified_at": 1493424000, 
            "num_tracks": 52, 
            "num_albums": 47, 
            "num_followers": 1,	
   "tracks": [
                {
                    "pos": 0, 
                    "artist_name": "Missy Elliott", 
                    "track_uri": "spotify:track:0UaMYEvWZi0ZqiDOoHU3YI", 
                    "artist_uri": "spotify:artist:2wIVse2owClT7go1WT98tk", 
                    "track_name": "Lose Control (feat. Ciara & Fat Man Scoop)", 
                    "album_uri": "spotify:album:6vV5UrXcfyQD1wu4Qo2I9K", 
                    "duration_ms": 226863, 
                    "album_name": "The Cookbook"
                }, 
 ], 
            "num_edits": 6, 
            "duration_ms": 11532414, 
            "num_artists": 37
        }
]
}
```