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

public interface SaleRepository {
    String SALE_SELECT = """
        SELECT id, employee_id, customer_id, sale_time, paid_amount, change_amount, sale_type, status
        FROM sales
        """;

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

    @Select(SALE_SELECT + " WHERE id = #{id}")
    @ResultMap("saleMap")
    Sale selectById(Long id);

    default Optional<Sale> findById(Long id) {
        return Optional.ofNullable(selectById(id));
    }

    default Optional<Sale> findWithItemsById(Long id) {
        return findById(id);
    }

    @Select(SALE_SELECT + " WHERE employee_id = #{employeeId} AND sale_time >= #{start} AND sale_time < #{end} ORDER BY sale_time DESC, id DESC")
    @ResultMap("saleMap")
    List<Sale> findByEmployeeIdAndSaleTimeBetween(
        @Param("employeeId") Long employeeId,
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end
    );

    @Select(SALE_SELECT + " WHERE customer_id = #{customerId} ORDER BY sale_time DESC, id DESC")
    @ResultMap("saleMap")
    List<Sale> findByCustomerId(Long customerId);

    @Select("SELECT id, employee_no, name, position, phone, email, password_hash, status FROM employees WHERE id = #{id}")
    Employee selectEmployeeById(Long id);

    @Select("SELECT id, username, password_hash, name, phone, email, gender, payment_password, balance, status FROM customers WHERE id = #{id}")
    Customer selectCustomerById(Long id);

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

    @Insert("""
        INSERT INTO sales (employee_id, customer_id, sale_time, paid_amount, change_amount, sale_type, status, created_at, updated_at)
        VALUES (#{employeeId}, #{customerId}, #{saleTime}, #{paidAmount}, #{changeAmount}, #{saleType}, #{status}, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
        """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Sale sale);

    @Update("""
        UPDATE sales
        SET employee_id = #{employeeId}, customer_id = #{customerId}, sale_time = #{saleTime},
            paid_amount = #{paidAmount}, change_amount = #{changeAmount}, sale_type = #{saleType},
            status = #{status}, updated_at = CURRENT_TIMESTAMP
        WHERE id = #{id}
        """)
    int update(Sale sale);

    @Insert("""
        INSERT INTO sale_items (sale_id, ticket_id, price, created_at, updated_at)
        VALUES (#{sale.id}, #{ticket.id}, #{price}, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
        """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insertItem(SaleItem item);

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
