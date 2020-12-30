package com.timmytime.predictorclientreactive.facade;

public interface IS3Facade {
    void put(String key, String json);

    void put(String bucket, String key, String csv);

    void delete(String folder);

    void archive(String prefix);
}
