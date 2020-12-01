package com.olading.entity;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class UserEntity {
    private Long id;
    private String userName;
    private String sex;

}
