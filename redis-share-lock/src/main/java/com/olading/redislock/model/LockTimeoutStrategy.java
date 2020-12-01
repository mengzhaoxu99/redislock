package com.olading.redislock.model;

import com.olading.redislock.exception.LockException;
import com.olading.redislock.lock.LockTimeoutHandler;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public enum LockTimeoutStrategy implements LockTimeoutHandler {

    /**
     * 继续执行业务逻辑，不做任何处理
     */
    NO_OPERATION() {
        @Override
        public void handle(String lockKey) {
            log.info("do nothing");
            // do nothing
        }
    },

    /**
     * 快速失败
     */
    FAIL_FAST() {
        @Override
        public void handle(String lockKey) {
            String errorMsg = String.format("Failed to acquire Lock(%s) with timeout", lockKey);
            throw new LockException(errorMsg);
        }
    },

    /**
     * 一直阻塞，直到获得锁，在太多的尝试后，仍会报错
     */
//    KEEP_ACQUIRE()
}