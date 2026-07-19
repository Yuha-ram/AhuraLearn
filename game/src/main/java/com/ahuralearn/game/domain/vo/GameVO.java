package com.ahuralearn.game.domain.vo;

import lombok.Data;

@Data
public class GameVO {

    private Long id;
    private String gameCode;
    private String title;
    private String icon;
    private String description;
}