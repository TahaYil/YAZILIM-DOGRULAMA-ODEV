package com.taa.tshirtsatis.dto;

import com.taa.tshirtsatis.entity.Ordered;
import com.taa.tshirtsatis.enums.OrderedState;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderedDto {

    private int id;

    @NotNull(message = "Sipariş ID'si boş olamaz.")
    @Min(value = 1, message = "Sipariş ID'si 1'den küçük olamaz.")
    private int orderId;

    @NotNull(message = "Kullanıcı ID'si boş olamaz.")
    @Min(value = 1, message = "Kullanıcı ID'si 1'den küçük olamaz.")
    private int userId;

    @NotNull(message = "Tarih boş olamaz.")
    @PastOrPresent(message = "Tarih geçmiş veya bugün olmalıdır.")
    private Date date;

    @NotNull(message = "Durum boş olamaz.")
    private OrderedState state;

    public OrderedDto(Ordered ordered) {
        this.id = ordered.getId();
        this.orderId = ordered.getOrder().getId();
        this.userId = ordered.getUser().getId(); 
        this.date = ordered.getDate();
        this.state = ordered.getState();
    }
}
