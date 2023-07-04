package com.example.telegrambot1.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CurrencyModel {
//    String cur_ID;
//    Date date;
    String cur_Abbreviation;
//    Integer cur_scale;
    String cur_name;
    Double cur_OfficialRate;

}
