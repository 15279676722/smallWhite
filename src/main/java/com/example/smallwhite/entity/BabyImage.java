package com.example.smallwhite.entity;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.sql.Timestamp;


/**
 *  图片
 * @author 杨强
 * @date 2020-08-23
 */
@Data
@AllArgsConstructor
public class BabyImage implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    public String id;

    /**
     * url
     */
    public String url;

    /**
     * name
     */
    public String name;

    /**
     * ts
     */
    public Timestamp ts;
    /**
     * babyid
     */
    public String babyid;

    public BabyImage() {
    }
    public String getTableName(){
        return "babyimage";
    }
}

