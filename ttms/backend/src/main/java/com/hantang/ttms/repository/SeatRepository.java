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

/**
 * 座位数据访问层（MyBatis Mapper）。
 *
 * <p>负责 seat 表的 CRUD 操作，支持按影厅 ID 查询座位列表（含影厅信息联查）、
 * 按座位 ID 查询单个座位详情，以及座位数量统计。座位按行列升序排列。</p>
 *
 * @author XUPT
 */
public interface SeatRepository {

    /**
     * 根据影厅 ID 查询该影厅的全部座位，含影厅基本信息，按行列升序排列。
     *
     * @param studioId 影厅 ID
     * @return 座位列表（含关联 Studio 信息）
     */
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

    /**
     * 根据座位 ID 查询单个座位详情（含影厅信息）。
     *
     * @param id 座位主键 ID
     * @return 座位详情对象，未找到返回 null
     */
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

    /**
     * 根据座位 ID 查询单个座位详情，返回 Optional 包装。
     *
     * @param id 座位主键 ID
     * @return 座位详情的 Optional 对象
     */
    default Optional<Seat> findById(Long id) {
        return Optional.ofNullable(selectById(id));
    }

    /**
     * 统计指定影厅的座位总数。
     *
     * @param studioId 影厅 ID
     * @return 座位总数
     */
    @Select("SELECT COUNT(*) FROM seats WHERE studio_id = #{studioId}")
    long countByStudioId(Long studioId);

    /**
     * 新增一条座位记录。
     *
     * <p>插入后通过 {@link Options#useGeneratedKeys} 自动回填主键 ID。</p>
     *
     * @param seat 座位实体
     * @return 影响行数
     */
    @Insert("""
        INSERT INTO seats (studio_id, row_no, col_no, status, created_at, updated_at)
        VALUES (#{studio.id}, #{rowNo}, #{colNo}, #{status}, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
        """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Seat seat);

    /**
     * 更新一条座位记录。
     *
     * @param seat 座位实体
     * @return 影响行数
     */
    @Update("""
        UPDATE seats
        SET studio_id = #{studio.id}, row_no = #{rowNo}, col_no = #{colNo}, status = #{status}, updated_at = CURRENT_TIMESTAMP
        WHERE id = #{id}
        """)
    int update(Seat seat);

    /**
     * 保存座位记录（新增或更新）。
     *
     * <p>ID 为空时执行 insert，否则执行 update。</p>
     *
     * @param seat 座位实体
     * @return 保存后的座位实体（含回填的 ID）
     */
    default Seat save(Seat seat) {
        if (seat.getId() == null) {
            insert(seat);
        } else {
            update(seat);
        }
        return seat;
    }
}
