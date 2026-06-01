package com.hantang.ttms.repository;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.hantang.ttms.domain.Studio;

public interface StudioRepository {
    @Select("SELECT id, name, row_count, col_count, introduction, status FROM studios ORDER BY id")
    List<Studio> findAll();

    @Select("SELECT id, name, row_count, col_count, introduction, status FROM studios WHERE id = #{id}")
    Studio selectById(Long id);

    default Optional<Studio> findById(Long id) {
        return Optional.ofNullable(selectById(id));
    }

    @Select("SELECT id, name, row_count, col_count, introduction, status FROM studios WHERE name LIKE CONCAT('%', #{name}, '%') ORDER BY id")
    List<Studio> findByNameContaining(String name);

    @Insert("""
        INSERT INTO studios (name, row_count, col_count, introduction, status, created_at, updated_at)
        VALUES (#{name}, #{rowCount}, #{colCount}, #{introduction}, #{status}, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
        """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Studio studio);

    @Update("""
        UPDATE studios
        SET name = #{name}, row_count = #{rowCount}, col_count = #{colCount}, introduction = #{introduction},
            status = #{status}, updated_at = CURRENT_TIMESTAMP
        WHERE id = #{id}
        """)
    int update(Studio studio);

    default Studio save(Studio studio) {
        if (studio.getId() == null) {
            insert(studio);
        } else {
            update(studio);
        }
        return studio;
    }
}
