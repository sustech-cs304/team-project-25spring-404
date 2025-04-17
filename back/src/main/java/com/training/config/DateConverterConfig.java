package com.training.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 日期转换器配置
 */
@Configuration
public class DateConverterConfig {

    /**
     * 字符串转日期
     */
    @Bean
    public Converter<String, Date> stringToDateConverter() {
        return new Converter<String, Date>() {
            @Override
            public Date convert(String source) {
                if (source == null || source.isEmpty()) {
                    return null;
                }
                
                SimpleDateFormat sdf;
                
                if (source.matches("^\\d{4}-\\d{1,2}-\\d{1,2}$")) {
                    sdf = new SimpleDateFormat("yyyy-MM-dd");
                } else if (source.matches("^\\d{4}-\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}$")) {
                    sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                } else if (source.matches("^\\d{4}-\\d{1,2}-\\d{1,2} \\d{1,2}:\\d{1,2}:\\d{1,2}$")) {
                    sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                } else {
                    return null;
                }
                
                try {
                    return sdf.parse(source);
                } catch (ParseException e) {
                    return null;
                }
            }
        };
    }
} 