package com.hantang.ttms.service;

import java.util.List;

import com.hantang.ttms.domain.Play;
import com.hantang.ttms.dto.PlayRequest;

public interface PlayService {
    List<Play> search(String name);
    Play create(PlayRequest request);
    Play update(Long id, PlayRequest request);
    void disable(Long id);
}
