package com.taa.tshirtsatis.repository;

import com.taa.tshirtsatis.entity.Ordered;
import com.taa.tshirtsatis.enums.OrderedState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Date;
import java.util.List;

public interface OrderedRepository extends JpaRepository<Ordered, Integer> {
    
    // Tek bir kullanıcının siparişlerini almak için
    List<Ordered> findByUserId(int userId);

    // Siparişin durumuna göre listeleme
    List<Ordered> findByState(OrderedState state);
    
    // Siparişin durumuna göre sayma
    long countByState(OrderedState state);
    
    // Sipariş tarihine göre listeleme
    List<Ordered> findByDate(Date date);

    // Alternatif bir tarih sorgusu (gün, ay, yıl vb.)
    @Query("SELECT o FROM Ordered o WHERE o.date BETWEEN :startDate AND :endDate")
    List<Ordered> findByDateRange(@Param("startDate") Date startDate, @Param("endDate") Date endDate);
}
