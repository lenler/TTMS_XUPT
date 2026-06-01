package com.hantang.ttms.service;

import java.util.List;

import com.hantang.ttms.domain.Studio;
import com.hantang.ttms.dto.StudioRequest;

public interface StudioService {
    List<Studio> search(String name);
    Studio create(StudioRequest request);
    Studio update(Long id, StudioRequest request);
    void disable(Long id);
    void rebuildSeats(Long studioId);
}
