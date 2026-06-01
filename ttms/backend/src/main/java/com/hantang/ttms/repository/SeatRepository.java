package com.hantang.ttms.repository;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.hantang.ttms.domain.Seat;

public interface SeatRepository {
    @Select("""
        SELECT se.id, se.studio_id, se.row_no, se.col_no, se.status,
               st.name AS studio_name, st.row_count AS studio_row_count, st.col_count AS studio_col_count, st.status AS studio_status
        FROM seats se
        JOIN studios st ON st.id = se.studio_id
        WHERE se.studio_id = #{studioId}
        ORDER BY se.row_no ASC, se.col_no ASC
        """)
    @Results(id = "seatWithStudio", value = {
        @Result(property = "id", column = "id", id = true),
        @Result(property = "studio.id", column = "studio_id"),
        @Result(property = "studio.name", column = "studio_name"),
        @Result(property = "studio.rowCount", column = "studio_row_count"),
        @Result(property = "studio.colCount", column = "studio_col_count"),
        @Result(property = "studio.status", column = "studio_status"),
        @Result(property = "rowNo", column = "row_no"),
        @Result(property = "colNo", column = "col_no"),
        @Result(property = "status", column = "status")
    })
    List<Seat> findByStudioIdOrderByRowNoAscColNoAsc(Long studioId);

    @Select("""
        SELECT se.id, se.studio_id, se.row_no, se.col_no, se.status,
               st.name AS studio_name, st.row_count AS studio_row_count, st.col_count AS studio_col_count, st.status AS studio_status
        FROM seats se
        JOIN studios st ON st.id = se.studio_id
        WHERE se.id = #{id}
        """)
    @Results(id = "seatById", value = {
        @Result(property = "id", column = "id", id = true),
        @Result(property = "studio.id", column = "studio_id"),
        @Result(property = "studio.name", column = "studio_name"),
        @Result(property = "studio.rowCount", column = "studio_row_count"),
        @Result(property = "studio.colCount", column = "studio_col_count"),
        @Result(property = "studio.status", column = "studio_status"),
        @Result(property = "rowNo", column = "row_no"),
        @Result(property = "colNo", column = "col_no"),
        @Result(property = "status", column = "status")
    })
    Seat selectById(Long id);

    default Optional<Seat> findById(Long id) {
        return Optional.ofNullable(selectById(id));
    }

    @Select("SELECT COUNT(*) FROM seats WHERE studio_id = #{studioId}")
    long countByStudioId(Long studioId);

    @Insert("""
        INSERT INTO seats (studio_id, row_no, col_no, status, created_at, updated_at)
        VALUES (#{studio.id}, #{rowNo}, #{colNo}, #{status}, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
        """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Seat seat);

    @Update("""
        UPDATE seats
        SET studio_id = #{studio.id}, row_no = #{rowNo}, col_no = #{colNo}, status = #{status}, updated_at = CURRENT_TIMESTAMP
        WHERE id = #{id}
        """)
    int update(Seat seat);

    default Seat save(Seat seat) {
        if (seat.getId() == null) {
            insert(seat);
        } else {
            update(seat);
        }
        return seat;
    }
}
