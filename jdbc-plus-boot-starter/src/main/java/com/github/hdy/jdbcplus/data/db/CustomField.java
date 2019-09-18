package com.github.hdy.jdbcplus.data.db;


import java.lang.reflect.Method;

public class CustomField {
    private boolean primaryKey;
    private String name;
    private String fieldName;
    private boolean automatic;
    private Method getMethod;
    private Method setMethod;
    private Object value;
    private String type;
    private boolean isTransient;
    private boolean isLike;
    private boolean isLikeLeft;
    private boolean isLikeRight;

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(boolean primaryKey) {
        this.primaryKey = primaryKey;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void setFieldName(String fieldName) {
        this.fieldName = fieldName;
    }

    public boolean isAutomatic() {
        return automatic;
    }

    public void setAutomatic(boolean automatic) {
        this.automatic = automatic;
    }

    public Method getGetMethod() {
        return getMethod;
    }

    public void setGetMethod(Method getMethod) {
        this.getMethod = getMethod;
    }

    public Method getSetMethod() {
        return setMethod;
    }

    public void setSetMethod(Method setMethod) {
        this.setMethod = setMethod;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isTransient() {
        return isTransient;
    }

    public void setTransient(boolean aTransient) {
        isTransient = aTransient;
    }

    public boolean isLike() {
        return isLike;
    }

    public void setLike(boolean like) {
        isLike = like;
    }

    public boolean isLikeLeft() {
        return isLikeLeft;
    }

    public void setLikeLeft(boolean likeLeft) {
        isLikeLeft = likeLeft;
    }

    public boolean isLikeRight() {
        return isLikeRight;
    }

    public void setLikeRight(boolean likeRight) {
        isLikeRight = likeRight;
    }
}
