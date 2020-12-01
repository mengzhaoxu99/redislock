package com.olading.redislock.lock;

/**
 * 获取锁超时的处理逻辑接口
 **/
public interface LockTimeoutHandler {

    void handle(String lockKey);
}
