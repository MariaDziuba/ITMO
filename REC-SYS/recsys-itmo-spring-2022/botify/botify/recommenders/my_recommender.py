from .recommender import Recommender
from .contextual import Contextual
from .indexed import Indexed
from .random import Random
from .sticky_artist import StickyArtist


class MyRecommender(Recommender):
    def __init__(self, tracks_redis, artists_redis, recommendations_redis, catalog):
        self.tracks_redis = tracks_redis
        self.artists_redis = artists_redis
        self.catalog = catalog
        self.fallback_random = Random(tracks_redis)
        self.fallback_contextual = Contextual(tracks_redis, catalog)
        self.fallback_indexed = Indexed(tracks_redis, recommendations_redis, catalog)
        self.fallback_sticky_artist = StickyArtist(tracks_redis, artists_redis, catalog)

    def recommend_next(self, user: int, prev_track: int, prev_track_time: float) -> int:
        track_data = self.tracks_redis.get(prev_track)
        if track_data is not None:
            track = self.catalog.from_bytes(track_data)
        else:
            raise ValueError(f"Track not found: {prev_track}")

        artist_data = self.artists_redis.get(track.artist)
        if artist_data is not None:
            artist_tracks = self.catalog.from_bytes(artist_data)
        else:
            raise ValueError(f"Artist not found: {prev_track}")

        if len(artist_tracks) == 1:
            try:
                return self.fallback_contextual.recommend_next(user, prev_track, prev_track_time)
            except ValueError:
                return self.fallback_random.recommend_next(user, prev_track, prev_track_time)

        if prev_track_time >= 0.7:
            try:
                return self.fallback_sticky_artist.recommend_next(user, prev_track, prev_track_time)
            except ValueError:
                return self.fallback_random.recommend_next(user, prev_track, prev_track_time)

        try:
            return self.fallback_contextual.recommend_next(user, prev_track, prev_track_time)
        except ValueError:
            return self.fallback_random.recommend_next(user, prev_track, prev_track_time)
