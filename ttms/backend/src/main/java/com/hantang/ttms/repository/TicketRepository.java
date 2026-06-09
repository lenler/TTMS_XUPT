package com.hantang.ttms.repository;

import java.util.Collection;
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

import com.hantang.ttms.domain.Ticket;
import com.hantang.ttms.domain.TicketStatus;

public interface TicketRepository {
    String DETAIL_SELECT = """
        SELECT t.id, t.seat_id, t.schedule_id, t.price, t.status, t.lock_time, t.version,
               se.row_no AS seat_row_no, se.col_no AS seat_col_no, se.status AS seat_status,
               sc.show_time AS schedule_show_time, sc.ticket_price AS schedule_ticket_price, sc.status AS schedule_status,
               st.id AS studio_id, st.name AS studio_name, st.row_count AS studio_row_count, st.col_count AS studio_col_count, st.status AS studio_status,
               pl.id AS play_id, pl.type AS play_type, pl.language AS play_language, pl.name AS play_name,
               pl.duration_minutes AS play_duration_minutes, pl.base_price AS play_base_price, pl.status AS play_status
        FROM tickets t
        JOIN seats se ON se.id = t.seat_id
        JOIN schedules sc ON sc.id = t.schedule_id
        JOIN studios st ON st.id = sc.studio_id
        JOIN plays pl ON pl.id = sc.play_id
        """;

    @Select(DETAIL_SELECT + " WHERE t.id = #{id}")
    @Results(id = "ticketMap", value = {
        @Result(property = "id", column = "id", id = true),
        @Result(property = "seat.id", column = "seat_id"),
        @Result(property = "seat.rowNo", column = "seat_row_no"),
        @Result(property = "seat.colNo", column = "seat_col_no"),
        @Result(property = "seat.status", column = "seat_status"),
        @Result(property = "schedule.id", column = "schedule_id"),
        @Result(property = "schedule.showTime", column = "schedule_show_time"),
        @Result(property = "schedule.ticketPrice", column = "schedule_ticket_price"),
        @Result(property = "schedule.status", column = "schedule_status"),
        @Result(property = "schedule.studio.id", column = "studio_id"),
        @Result(property = "schedule.studio.name", column = "studio_name"),
        @Result(property = "schedule.studio.rowCount", column = "studio_row_count"),
        @Result(property = "schedule.studio.colCount", column = "studio_col_count"),
        @Result(property = "schedule.studio.status", column = "studio_status"),
        @Result(property = "schedule.play.id", column = "play_id"),
        @Result(property = "schedule.play.type", column = "play_type"),
        @Result(property = "schedule.play.language", column = "play_language"),
        @Result(property = "schedule.play.name", column = "play_name"),
        @Result(property = "schedule.play.durationMinutes", column = "play_duration_minutes"),
        @Result(property = "schedule.play.basePrice", column = "play_base_price"),
        @Result(property = "schedule.play.status", column = "play_status"),
        @Result(property = "price", column = "price"),
        @Result(property = "status", column = "status"),
        @Result(property = "lockTime", column = "lock_time"),
        @Result(property = "version", column = "version")
    })
    Ticket selectDetailedById(Long id);

    default Optional<Ticket> findDetailedById(Long id) {
        return Optional.ofNullable(selectDetailedById(id));
    }

    default Optional<Ticket> findById(Long id) {
        return findDetailedById(id);
    }

    @Select(DETAIL_SELECT + " WHERE t.schedule_id = #{scheduleId} ORDER BY se.row_no, se.col_no")
    @ResultMap("ticketMap")
    List<Ticket> findByScheduleId(Long scheduleId);

    @Select({
        "<script>",
        DETAIL_SELECT,
        "WHERE t.id IN",
        "<foreach collection='ids' item='id' open='(' separator=',' close=')'>#{id}</foreach>",
        "ORDER BY t.id",
        "</script>"
    })
    @ResultMap("ticketMap")
    List<Ticket> findByIdIn(@Param("ids") Collection<Long> ids);

    @Select("SELECT COUNT(*) FROM tickets WHERE schedule_id = #{scheduleId} AND status = #{status}")
    long countByScheduleIdAndStatus(@Param("scheduleId") Long scheduleId, @Param("status") TicketStatus status);

    @Select("SELECT COUNT(*) FROM tickets WHERE schedule_id = #{scheduleId}")
    long countByScheduleId(Long scheduleId);

    @Select("SELECT COUNT(*) FROM tickets WHERE status = #{status}")
    long countByStatus(TicketStatus status);

    @Select("SELECT COUNT(*) FROM tickets")
    long count();

    @Select("SELECT COALESCE(SUM(price), 0) FROM tickets WHERE schedule_id = #{scheduleId} AND status = #{status}")
    java.math.BigDecimal sumPriceByScheduleIdAndStatus(@Param("scheduleId") Long scheduleId, @Param("status") TicketStatus status);

    @Insert("""
        INSERT INTO tickets (seat_id, schedule_id, price, status, lock_time, version, created_at, updated_at)
        VALUES (#{seat.id}, #{schedule.id}, #{price}, #{status}, #{lockTime}, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
        """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Ticket ticket);

    @Update("""
        UPDATE tickets
        SET seat_id = #{seat.id}, schedule_id = #{schedule.id}, price = #{price}, status = #{status},
            lock_time = #{lockTime}, version = COALESCE(version, 0) + 1, updated_at = CURRENT_TIMESTAMP
        WHERE id = #{id}
        """)
    int update(Ticket ticket);

    default Ticket save(Ticket ticket) {
        if (ticket.getId() == null) {
            insert(ticket);
        } else {
            update(ticket);
        }
        return ticket;
    }
}
