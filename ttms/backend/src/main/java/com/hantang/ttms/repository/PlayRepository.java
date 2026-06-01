package com.hantang.ttms.repository;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.hantang.ttms.domain.Play;

public interface PlayRepository {
    @Select("""
        SELECT id, type, language, name, introduction, poster_url, trailer_url, duration_minutes, base_price, status
        FROM plays
        ORDER BY id
        """)
    List<Play> findAll();

    @Select("""
        SELECT id, type, language, name, introduction, poster_url, trailer_url, duration_minutes, base_price, status
        FROM plays
        WHERE id = #{id}
        """)
    Play selectById(Long id);

    default Optional<Play> findById(Long id) {
        return Optional.ofNullable(selectById(id));
    }

    @Select("""
        SELECT id, type, language, name, introduction, poster_url, trailer_url, duration_minutes, base_price, status
        FROM plays
        WHERE name LIKE CONCAT('%', #{name}, '%')
        ORDER BY id
        """)
    List<Play> findByNameContaining(String name);

    @Insert("""
        INSERT INTO plays (type, language, name, introduction, poster_url, trailer_url, duration_minutes, base_price, status, created_at, updated_at)
        VALUES (#{type}, #{language}, #{name}, #{introduction}, #{posterUrl}, #{trailerUrl}, #{durationMinutes}, #{basePrice}, #{status}, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
        """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Play play);

    @Update("""
        UPDATE plays
        SET type = #{type}, language = #{language}, name = #{name}, introduction = #{introduction},
            poster_url = #{posterUrl}, trailer_url = #{trailerUrl}, duration_minutes = #{durationMinutes},
            base_price = #{basePrice}, status = #{status}, updated_at = CURRENT_TIMESTAMP
        WHERE id = #{id}
        """)
    int update(Play play);

    default Play save(Play play) {
        if (play.getId() == null) {
            insert(play);
        } else {
            update(play);
        }
        return play;
    }
}
