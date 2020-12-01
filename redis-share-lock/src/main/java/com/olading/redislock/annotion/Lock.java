package com.olading.redislock.annotion;

import com.olading.redislock.model.LockTimeoutStrategy;
import com.olading.redislock.model.LockType;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;



@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Lock {
    /**
     * 锁的key,支持spel
     */
    String key();
    /**
     * 获取锁的最大尝试时间(单位 {@code unit})
     */
    long waitTime() default 30;
    /**
     * 加锁的时间(单位 {@code unit})，超过这个时间后锁便自动解锁；
     * 如果leaseTime为-1，则保持锁定直到显式解锁
     */
    long leaseTime() default 30;
    /**
     * 参数的时间单位
     */
    TimeUnit unit() default TimeUnit.SECONDS;
    /**
     * 默认为可重入锁，
     */
    LockType lockType() default LockType.Reentrant;
    /**
     * 加锁超时的处理策略,默认快速失败
     * @return lockTimeoutStrategy
     */
    LockTimeoutStrategy lockTimeoutStrategy() default LockTimeoutStrategy.FAIL_FAST;
}
