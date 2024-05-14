package com.mrthinkj.integratemiddlewareapplication.payload;

import lombok.Data;

public enum UpdateInfo {
    Mongo,
    SqlServer,
    All;

    public static UpdateInfo getInfoFromInteger(Integer value){
        return switch (value){
            case 0 -> UpdateInfo.Mongo;
            case 1-> UpdateInfo.SqlServer;
            default -> UpdateInfo.All;
        };
    }
}
