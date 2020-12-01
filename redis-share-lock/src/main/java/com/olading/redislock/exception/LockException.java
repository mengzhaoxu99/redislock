package com.olading.redislock.exception;

/**
 * 分布式锁异常
 */
public class LockException extends RuntimeException {

    private static final long serialVersionUID = -2671363806685361626L;

    public LockException(String message) {
        super(message);
    }
}
