package com.hantang.ttms.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Many;
import org.apache.ibatis.annotations.One;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.ResultMap;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import com.hantang.ttms.domain.Customer;
import com.hantang.ttms.domain.Employee;
import com.hantang.ttms.domain.Sale;
import com.hantang.ttms.domain.SaleItem;

/**
 * 销售记录数据访问层（MyBatis Mapper）。
 *
 * <p>负责 sales 表和 sale_items 表的 CRUD 操作。支持按时间范围、员工 ID、
 * 观众 ID 查询销售记录，并通过 MyBatis 嵌套查询自动加载关联的售票员（Employee）、
 * 观众（Customer）以及销售明细（SaleItem）列表。</p>
 *
 * <p>保存销售记录时会级联保存其销售明细项。</p>
 *
 * @author XUPT
 */
public interface SaleRepository {

    /**
     * 销售记录查询 SQL 片段（不包含 WHERE 条件）。
     */
    String SALE_SELECT = """
        SELECT id, employee_id, customer_id, sale_time, paid_amount, change_amount, sale_type, status
        FROM sales
        """;

    /**
     * 查询指定时间范围内的全部销售记录，含售票员、观众、销售明细。
     *
     * <p>通过 {@code @One} 注解嵌套查询关联的 Employee 和 Customer，
     * 通过 {@code @Many} 注解嵌套查询关联的 SaleItem 列表。</p>
     *
     * @param start 查询起始时间（含）
     * @param end   查询截止时间（不含）
     * @return 销售记录列表，按销售时间降序排列
     */
    @Select(SALE_SELECT + " WHERE sale_time >= #{start} AND sale_time < #{end} ORDER BY sale_time DESC, id DESC")
    @Results(id = "saleMap", value = {
        @Result(property = "id", column = "id", id = true),
        @Result(property = "employee", column = "employee_id", one = @One(select = "selectEmployeeById")),
        @Result(property = "customer", column = "customer_id", one = @One(select = "selectCustomerById")),
        @Result(property = "saleTime", column = "sale_time"),
        @Result(property = "paidAmount", column = "paid_amount"),
        @Result(property = "changeAmount", column = "change_amount"),
        @Result(property = "saleType", column = "sale_type"),
        @Result(property = "status", column = "status"),
        @Result(property = "items", column = "id", many = @Many(select = "findItemsBySaleId"))
    })
    List<Sale> findBySaleTimeBetween(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    /**
     * 根据销售 ID 查询单条销售记录（含关联数据）。
     *
     * @param id 销售主键 ID
     * @return 销售记录，未找到返回 null
     */
    @Select(SALE_SELECT + " WHERE id = #{id}")
    @ResultMap("saleMap")
    Sale selectById(Long id);

    /**
     * 根据销售 ID 查询销售记录，返回 Optional 包装。
     *
     * @param id 销售主键 ID
     * @return 销售记录的 Optional 对象
     */
    default Optional<Sale> findById(Long id) {
        return Optional.ofNullable(selectById(id));
    }

    /**
     * 根据销售 ID 查询销售记录（含明细），findById 的别名。
     *
     * @param id 销售主键 ID
     * @return 销售记录的 Optional 对象
     */
    default Optional<Sale> findWithItemsById(Long id) {
        return findById(id);
    }

    /**
     * 查询指定售票员在指定时间范围内的销售记录。
     *
     * @param employeeId 售票员（员工）ID
     * @param start      查询起始时间（含）
     * @param end        查询截止时间（不含）
     * @return 销售记录列表，按销售时间降序排列
     */
    @Select(SALE_SELECT + " WHERE employee_id = #{employeeId} AND sale_time >= #{start} AND sale_time < #{end} ORDER BY sale_time DESC, id DESC")
    @ResultMap("saleMap")
    List<Sale> findByEmployeeIdAndSaleTimeBetween(
        @Param("employeeId") Long employeeId,
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end
    );

    /**
     * 查询指定观众的全部销售记录。
     *
     * @param customerId 观众 ID
     * @return 销售记录列表，按销售时间降序排列
     */
    @Select(SALE_SELECT + " WHERE customer_id = #{customerId} ORDER BY sale_time DESC, id DESC")
    @ResultMap("saleMap")
    List<Sale> findByCustomerId(Long customerId);

    /**
     * 根据员工 ID 查询员工基本信息（供嵌套查询使用）。
     *
     * @param id 员工 ID
     * @return 员工实体，未找到返回 null
     */
    @Select("SELECT id, employee_no, name, position, phone, email, password_hash, status FROM employees WHERE id = #{id}")
    Employee selectEmployeeById(Long id);

    /**
     * 根据观众 ID 查询观众基本信息（供嵌套查询使用）。
     *
     * @param id 观众 ID
     * @return 观众实体，未找到返回 null
     */
    @Select("SELECT id, username, password_hash, name, phone, email, gender, payment_password, balance, status FROM customers WHERE id = #{id}")
    Customer selectCustomerById(Long id);

    /**
     * 查询指定销售记录的销售明细列表（含票务、座位、演出计划、影厅、剧目信息）。
     *
     * @param saleId 销售记录 ID
     * @return 销售明细列表，按 ID 排序
     */
    @Select("""
        SELECT si.id, si.sale_id, si.ticket_id, si.price,
               t.price AS ticket_price, t.status AS ticket_status, t.lock_time AS ticket_lock_time, t.version AS ticket_version,
               se.id AS seat_id, se.row_no AS seat_row_no, se.col_no AS seat_col_no, se.status AS seat_status,
               sc.id AS schedule_id, sc.show_time AS schedule_show_time, sc.ticket_price AS schedule_ticket_price, sc.status AS schedule_status,
               st.id AS studio_id, st.name AS studio_name,
               pl.id AS play_id, pl.name AS play_name, pl.duration_minutes AS play_duration_minutes
        FROM sale_items si
        JOIN tickets t ON t.id = si.ticket_id
        JOIN seats se ON se.id = t.seat_id
        JOIN schedules sc ON sc.id = t.schedule_id
        JOIN studios st ON st.id = sc.studio_id
        JOIN plays pl ON pl.id = sc.play_id
        WHERE si.sale_id = #{saleId}
        ORDER BY si.id
        """)
    @Results(id = "saleItemMap", value = {
        @Result(property = "id", column = "id", id = true),
        @Result(property = "sale.id", column = "sale_id"),
        @Result(property = "ticket.id", column = "ticket_id"),
        @Result(property = "ticket.price", column = "ticket_price"),
        @Result(property = "ticket.status", column = "ticket_status"),
        @Result(property = "ticket.lockTime", column = "ticket_lock_time"),
        @Result(property = "ticket.version", column = "ticket_version"),
        @Result(property = "ticket.seat.id", column = "seat_id"),
        @Result(property = "ticket.seat.rowNo", column = "seat_row_no"),
        @Result(property = "ticket.seat.colNo", column = "seat_col_no"),
        @Result(property = "ticket.seat.status", column = "seat_status"),
        @Result(property = "ticket.schedule.id", column = "schedule_id"),
        @Result(property = "ticket.schedule.showTime", column = "schedule_show_time"),
        @Result(property = "ticket.schedule.ticketPrice", column = "schedule_ticket_price"),
        @Result(property = "ticket.schedule.status", column = "schedule_status"),
        @Result(property = "ticket.schedule.studio.id", column = "studio_id"),
        @Result(property = "ticket.schedule.studio.name", column = "studio_name"),
        @Result(property = "ticket.schedule.play.id", column = "play_id"),
        @Result(property = "ticket.schedule.play.name", column = "play_name"),
        @Result(property = "ticket.schedule.play.durationMinutes", column = "play_duration_minutes"),
        @Result(property = "price", column = "price")
    })
    List<SaleItem> findItemsBySaleId(Long saleId);

    /**
     * 新增一条销售记录。
     *
     * <p>插入后通过 {@link Options#useGeneratedKeys} 自动回填主键 ID。</p>
     *
     * @param sale 销售实体
     * @return 影响行数
     */
    @Insert("""
        INSERT INTO sales (employee_id, customer_id, sale_time, paid_amount, change_amount, sale_type, status, created_at, updated_at)
        VALUES (#{employeeId}, #{customerId}, #{saleTime}, #{paidAmount}, #{changeAmount}, #{saleType}, #{status}, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
        """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Sale sale);

    /**
     * 更新一条销售记录。
     *
     * @param sale 销售实体
     * @return 影响行数
     */
    @Update("""
        UPDATE sales
        SET employee_id = #{employeeId}, customer_id = #{customerId}, sale_time = #{saleTime},
            paid_amount = #{paidAmount}, change_amount = #{changeAmount}, sale_type = #{saleType},
            status = #{status}, updated_at = CURRENT_TIMESTAMP
        WHERE id = #{id}
        """)
    int update(Sale sale);

    /**
     * 新增一条销售明细记录。
     *
     * <p>插入后通过 {@link Options#useGeneratedKeys} 自动回填主键 ID。</p>
     *
     * @param item 销售明细实体
     * @return 影响行数
     */
    @Insert("""
        INSERT INTO sale_items (sale_id, ticket_id, price, created_at, updated_at)
        VALUES (#{sale.id}, #{ticket.id}, #{price}, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
        """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertItem(SaleItem item);

    /**
     * 保存销售记录（新增或更新），并级联保存销售明细。
     *
     * <p>新增时先插入销售主记录，再逐一插入其关联的 SaleItem；
     * 更新时只更新主记录，仅插入新增的明细项。</p>
     *
     * @param sale 销售实体
     * @return 保存后的销售实体（含回填的 ID 及明细项 ID）
     */
    default Sale save(Sale sale) {
        if (sale.getId() == null) {
            insert(sale);
            for (SaleItem item : sale.getItems()) {
                item.setSale(sale);
                insertItem(item);
            }
        } else {
            update(sale);
            for (SaleItem item : sale.getItems()) {
                if (item.getId() == null) {
                    item.setSale(sale);
                    insertItem(item);
                }
            }
        }
        return sale;
    }
}
