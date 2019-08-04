package com.example.demo.dto;

/**
 * Demo class
 *
 * @author huangjing
 * @date 2019-08-04
 */
public class User {
    private Long id;
    private String name;
    private Integer age;

    public Long getId() {
        return this.id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(final String name) {
        this.name = name;
    }

    public Integer getAge() {
        return this.age;
    }

    public void setAge(final Integer age) {
        this.age = age;
    }
}
