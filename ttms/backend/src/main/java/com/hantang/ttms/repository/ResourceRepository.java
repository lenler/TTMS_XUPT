package com.hantang.ttms.repository;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import com.hantang.ttms.domain.Resource;

/**
 * 资源 MyBatis Mapper
 */
public interface ResourceRepository {

    @Select("SELECT id, type, name, url FROM resources ORDER BY id")
    List<Resource> findAll();

    @Select("SELECT id, type, name, url FROM resources WHERE id = #{id}")
    Resource findById(Long id);

    @Insert("INSERT INTO resources (type, name, url, created_at, updated_at) VALUES (#{type}, #{name}, #{url}, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Resource resource);
}
