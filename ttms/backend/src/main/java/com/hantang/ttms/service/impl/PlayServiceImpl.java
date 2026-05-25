package com.hantang.ttms.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hantang.ttms.common.BusinessException;
import com.hantang.ttms.domain.Play;
import com.hantang.ttms.domain.Status;
import com.hantang.ttms.dto.PlayRequest;
import com.hantang.ttms.repository.PlayRepository;
import com.hantang.ttms.service.PlayService;

@Service
public class PlayServiceImpl implements PlayService {
    private final PlayRepository playRepository;

    public PlayServiceImpl(PlayRepository playRepository) {
        this.playRepository = playRepository;
    }

    @Override
    public List<Play> search(String name) {
        if (name == null || name.isBlank()) {
            return playRepository.findAll();
        }
        return playRepository.findByNameContaining(name);
    }

    @Override
    @Transactional
    public Play create(PlayRequest request) {
        Play play = new Play();
        apply(play, request);
        return playRepository.save(play);
    }

    @Override
    @Transactional
    public Play update(Long id, PlayRequest request) {
        Play play = playRepository.findById(id).orElseThrow(() -> new BusinessException("剧目不存在"));
        apply(play, request);
        return playRepository.save(play);
    }

    @Override
    @Transactional
    public void disable(Long id) {
        Play play = playRepository.findById(id).orElseThrow(() -> new BusinessException("剧目不存在"));
        play.setStatus(Status.DISABLED);
        playRepository.save(play);
    }

    private void apply(Play play, PlayRequest request) {
        play.setType(request.type());
        play.setLanguage(request.language());
        play.setName(request.name());
        play.setIntroduction(request.introduction());
        play.setPosterUrl(request.posterUrl());
        play.setTrailerUrl(request.trailerUrl());
        play.setDurationMinutes(request.durationMinutes());
        play.setBasePrice(request.basePrice());
    }
}
