package com.olading.controller;

import com.olading.entity.UserEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.olading.service.SharedLockTestService;

@RestController
@RequestMapping("test")
@Slf4j
public class SharedLockTestController {

    @Autowired
    private SharedLockTestService sharedLockTestService;

    @RequestMapping("1")
    public String testLock(String lockParam) {
        try {
            UserEntity test = UserEntity.builder().id(1L).userName("test").sex("nan").build();
            sharedLockTestService.lock(test);
            return "success";
        } catch (Exception e) {
            log.error(e.getMessage());
            return "error:" + lockParam + "has been locked";
        }

    }



}
