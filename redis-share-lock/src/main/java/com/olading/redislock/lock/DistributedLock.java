package com.olading.redislock.lock;

import com.olading.redislock.model.LockType;

import java.util.concurrent.TimeUnit;

/**
 * 分布式锁顶级接口
 *
 */
public interface DistributedLock {
    /**
     * 获取锁，如果获取不成功则一直等待直到lock被获取
     * @param key 锁的key
     * @param leaseTime 加锁的时间，超过这个时间后锁便自动解锁；
     *                  如果leaseTime为-1，则保持锁定直到显式解锁
     * @param unit {@code leaseTime} 参数的时间单位
     * @param lockType 锁类型
     * @return 锁对象
     */
    OladingLock lock(String key, long leaseTime, TimeUnit unit, LockType lockType) throws Exception;

    default OladingLock lock(String key, long leaseTime, TimeUnit unit) throws Exception {
        return this.lock(key, leaseTime, unit, LockType.Reentrant);
    }
    default OladingLock lock(String key, LockType lockType) throws Exception {
        return this.lock(key, -1, null, LockType.Reentrant);
    }
    default OladingLock lock(String key) throws Exception {
        return this.lock(key, -1, null, LockType.Reentrant);
    }

    /**
     * 尝试获取锁，如果锁不可用则等待最多waitTime时间后放弃
     * @param key 锁的key
     * @param waitTime 获取锁的最大尝试时间(单位 {@code unit})
     * @param leaseTime 加锁的时间，超过这个时间后锁便自动解锁；
     *                  如果leaseTime为-1，则保持锁定直到显式解锁
     * @param unit {@code waitTime} 和 {@code leaseTime} 参数的时间单位
     * @return 锁对象，如果获取锁失败则为null
     */
    OladingLock tryLock(String key, long waitTime, long leaseTime, TimeUnit unit, LockType lockType) throws Exception;

    default OladingLock tryLock(String key, long waitTime, long leaseTime, TimeUnit unit) throws Exception {
        return this.tryLock(key, waitTime, leaseTime, unit, LockType.Reentrant);
    }
    default OladingLock tryLock(String key, long waitTime, TimeUnit unit, LockType lockType) throws Exception {
        return this.tryLock(key, waitTime, -1, unit, lockType);
    }
    default OladingLock tryLock(String key, long waitTime, TimeUnit unit) throws Exception {
        return this.tryLock(key, waitTime, -1, unit, LockType.Reentrant);
    }

    /**
     * 释放锁
     * @param lock 锁对象
     */
    void unlock(Object lock) throws Exception;

    /**
     * 释放锁
     * @param oladingLock 锁抽象对象
     */
    default void unlock(OladingLock oladingLock) throws Exception {
        if (oladingLock != null) {
            this.unlock(oladingLock.getLock());
        }
    }
}
