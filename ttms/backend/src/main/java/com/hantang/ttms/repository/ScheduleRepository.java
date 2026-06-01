package com.hantang.ttms.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.hantang.ttms.domain.Schedule;
import com.hantang.ttms.domain.Status;

public interface ScheduleRepository {
    String BASE_SELECT = """
        SELECT sc.id, sc.studio_id, sc.play_id, sc.show_time, sc.ticket_price, sc.status,
               st.name AS studio_name, st.row_count AS studio_row_count, st.col_count AS studio_col_count, st.status AS studio_status,
               pl.type AS play_type, pl.language AS play_language, pl.name AS play_name, pl.introduction AS play_introduction,
               pl.poster_url AS play_poster_url, pl.trailer_url AS play_trailer_url,
               pl.duration_minutes AS play_duration_minutes, pl.base_price AS play_base_price, pl.status AS play_status
        FROM schedules sc
        JOIN studios st ON st.id = sc.studio_id
        JOIN plays pl ON pl.id = sc.play_id
        """;

    @Select(BASE_SELECT + " ORDER BY sc.show_time DESC, sc.id DESC")
    @Results(id = "scheduleMap", value = {
        @Result(property = "id", column = "id", id = true),
        @Result(property = "studio.id", column = "studio_id"),
        @Result(property = "studio.name", column = "studio_name"),
        @Result(property = "studio.rowCount", column = "studio_row_count"),
        @Result(property = "studio.colCount", column = "studio_col_count"),
        @Result(property = "studio.status", column = "studio_status"),
        @Result(property = "play.id", column = "play_id"),
        @Result(property = "play.type", column = "play_type"),
        @Result(property = "play.language", column = "play_language"),
        @Result(property = "play.name", column = "play_name"),
        @Result(property = "play.introduction", column = "play_introduction"),
        @Result(property = "play.posterUrl", column = "play_poster_url"),
        @Result(property = "play.trailerUrl", column = "play_trailer_url"),
        @Result(property = "play.durationMinutes", column = "play_duration_minutes"),
        @Result(property = "play.basePrice", column = "play_base_price"),
        @Result(property = "play.status", column = "play_status"),
        @Result(property = "showTime", column = "show_time"),
        @Result(property = "ticketPrice", column = "ticket_price"),
        @Result(property = "status", column = "status")
    })
    List<Schedule> findAll();

    @Select(BASE_SELECT + " WHERE sc.id = #{id}")
    @ResultMap("scheduleMap")
    Schedule selectById(Long id);

    default Optional<Schedule> findById(Long id) {
        return Optional.ofNullable(selectById(id));
    }

    @Select("SELECT COUNT(*) FROM schedules WHERE id = #{id}")
    long countById(Long id);

    default boolean existsById(Long id) {
        return countById(id) > 0;
    }

    @Select(BASE_SELECT + " WHERE sc.play_id = #{playId} ORDER BY sc.show_time DESC, sc.id DESC")
    @ResultMap("scheduleMap")
    List<Schedule> findByPlayId(Long playId);

    @Select(BASE_SELECT + " WHERE sc.studio_id = #{studioId} AND sc.status = #{status} ORDER BY sc.show_time")
    @ResultMap("scheduleMap")
    List<Schedule> findByStudioIdAndStatus(@Param("studioId") Long studioId, @Param("status") Status status);

    @Select(BASE_SELECT + " WHERE sc.status = #{status} AND sc.show_time >= #{start} AND sc.show_time < #{end} ORDER BY sc.show_time")
    @ResultMap("scheduleMap")
    List<Schedule> findByStatusAndShowTimeBetween(@Param("status") Status status, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Select(BASE_SELECT + " WHERE sc.status = #{status} ORDER BY sc.show_time")
    @ResultMap("scheduleMap")
    List<Schedule> findByStatus(Status status);

    @Select("SELECT COUNT(*) FROM schedules WHERE studio_id = #{studioId} AND show_time >= #{start} AND show_time < #{end}")
    long countByStudioIdAndShowTimeBetween(@Param("studioId") Long studioId, @Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    default boolean existsByStudioIdAndShowTimeBetween(Long studioId, LocalDateTime start, LocalDateTime end) {
        return countByStudioIdAndShowTimeBetween(studioId, start, end) > 0;
    }

    @Insert("""
        INSERT INTO schedules (studio_id, play_id, show_time, ticket_price, status, created_at, updated_at)
        VALUES (#{studio.id}, #{play.id}, #{showTime}, #{ticketPrice}, #{status}, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
        """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Schedule schedule);

    @Update("""
        UPDATE schedules
        SET studio_id = #{studio.id}, play_id = #{play.id}, show_time = #{showTime},
            ticket_price = #{ticketPrice}, status = #{status}, updated_at = CURRENT_TIMESTAMP
        WHERE id = #{id}
        """)
    int update(Schedule schedule);

    default Schedule save(Schedule schedule) {
        if (schedule.getId() == null) {
            insert(schedule);
        } else {
            update(schedule);
        }
        return schedule;
    }
}
