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

/**
 * 票务数据访问层（MyBatis Mapper）。
 *
 * <p>负责 ticket 表的 CRUD 操作，支持多表联查（座位 Seat、演出计划 Schedule、
 * 影厅 Studio、剧目 Play），提供按演出计划 ID、票状态、票 ID 集合等维度
 * 查询票务详情的功能，以及票务统计（计数、金额汇总）。</p>
 *
 * <p>使用乐观锁（version 字段）保证并发更新安全。</p>
 *
 * @author XUPT
 */
public interface TicketRepository {

    /**
     * 票务详情联查 SQL 片段。
     *
     * <p>LEFT JOIN 座位（seats）、演出计划（schedules）、影厅（studios）、
     * 剧目（plays）四张表，一次查询返回完整的票务关联数据。</p>
     */
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

    /**
     * 根据票务 ID 查询完整的票务详情（含座位、演出计划、影厅、剧目信息）。
     *
     * @param id 票务主键 ID
     * @return 票务详情对象（含关联实体），未找到返回 null
     */
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

    /**
     * 根据票务 ID 查询完整的票务详情（含关联实体），返回 Optional 包装。
     *
     * @param id 票务主键 ID
     * @return 票务详情的 Optional 对象
     */
    default Optional<Ticket> findDetailedById(Long id) {
        return Optional.ofNullable(selectDetailedById(id));
    }

    /**
     * 根据票务 ID 查询票务详情，findDetailedById 的便捷别名。
     *
     * @param id 票务主键 ID
     * @return 票务详情的 Optional 对象
     */
    default Optional<Ticket> findById(Long id) {
        return findDetailedById(id);
    }

    /**
     * 查询指定演出计划的全部票务，按座位行列排序。
     *
     * @param scheduleId 演出计划 ID
     * @return 票务列表（含关联实体详情）
     */
    @Select(DETAIL_SELECT + " WHERE t.schedule_id = #{scheduleId} ORDER BY se.row_no, se.col_no")
    @ResultMap("ticketMap")
    List<Ticket> findByScheduleId(Long scheduleId);

    /**
     * 根据票务 ID 集合批量查询票务详情。
     *
     * @param ids 票务 ID 集合
     * @return 票务列表（含关联实体详情），按 ID 排序
     */
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

    /**
     * 统计指定演出计划处于指定状态的票务数量。
     *
     * @param scheduleId 演出计划 ID
     * @param status     票务状态
     * @return 符合条件的票务数量
     */
    @Select("SELECT COUNT(*) FROM tickets WHERE schedule_id = #{scheduleId} AND status = #{status}")
    long countByScheduleIdAndStatus(@Param("scheduleId") Long scheduleId, @Param("status") TicketStatus status);

    /**
     * 统计指定演出计划的全部票务数量（不论状态）。
     *
     * @param scheduleId 演出计划 ID
     * @return 票务总数
     */
    @Select("SELECT COUNT(*) FROM tickets WHERE schedule_id = #{scheduleId}")
    long countByScheduleId(Long scheduleId);

    /**
     * 统计处于指定状态的票务总数。
     *
     * @param status 票务状态
     * @return 该状态的票务总数
     */
    @Select("SELECT COUNT(*) FROM tickets WHERE status = #{status}")
    long countByStatus(TicketStatus status);

    /**
     * 统计票务表总记录数。
     *
     * @return 票务总数
     */
    @Select("SELECT COUNT(*) FROM tickets")
    long count();

    /**
     * 汇总指定演出计划中处于指定状态的票务金额总和。
     *
     * @param scheduleId 演出计划 ID
     * @param status     票务状态
     * @return 金额总和（没有记录时返回 0）
     */
    @Select("SELECT COALESCE(SUM(price), 0) FROM tickets WHERE schedule_id = #{scheduleId} AND status = #{status}")
    java.math.BigDecimal sumPriceByScheduleIdAndStatus(@Param("scheduleId") Long scheduleId, @Param("status") TicketStatus status);

    /**
     * 新增一条票务记录。
     *
     * <p>插入后通过 {@link Options#useGeneratedKeys} 自动回填主键 ID。</p>
     *
     * @param ticket 票务实体
     * @return 影响行数
     */
    @Insert("""
        INSERT INTO tickets (seat_id, schedule_id, price, status, lock_time, version, created_at, updated_at)
        VALUES (#{seat.id}, #{schedule.id}, #{price}, #{status}, #{lockTime}, 0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
        """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Ticket ticket);

    /**
     * 更新一条票务记录。
     *
     * <p>每次更新自增 version 字段，实现乐观锁并发控制。</p>
     *
     * @param ticket 票务实体
     * @return 影响行数
     */
    @Update("""
        UPDATE tickets
        SET seat_id = #{seat.id}, schedule_id = #{schedule.id}, price = #{price}, status = #{status},
            lock_time = #{lockTime}, version = COALESCE(version, 0) + 1, updated_at = CURRENT_TIMESTAMP
        WHERE id = #{id}
        """)
    int update(Ticket ticket);

    /**
     * 保存票务记录（新增或更新）。
     *
     * <p>ID 为空时执行 insert，否则执行 update。</p>
     *
     * @param ticket 票务实体
     * @return 保存后的票务实体（含回填的 ID）
     */
    default Ticket save(Ticket ticket) {
        if (ticket.getId() == null) {
            insert(ticket);
        } else {
            update(ticket);
        }
        return ticket;
    }
}
