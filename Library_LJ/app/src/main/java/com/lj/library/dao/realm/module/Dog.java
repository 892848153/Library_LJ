package com.lj.library.dao.realm.module;

import io.realm.RealmObject;

/**
 * Created by liujie_gyh on 16/6/19.
 */
public class Dog extends RealmObject {

    private String name;
    private String color;
    private int age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
}
