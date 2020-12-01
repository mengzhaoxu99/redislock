package com.olading.redislock.lock;

import com.olading.redislock.exception.LockException;
import com.olading.redislock.model.LockType;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * redisson分布式锁实现，基本锁功能的抽象实现
 */
@Slf4j
@Component
@ConditionalOnClass(RedissonClient.class)
public class RedissonDistributedLock implements DistributedLock {


    @Autowired
    private RedissonClient redisson;

    private OladingLock getLock(String key, LockType lockType) {
        RLock lock;
        switch (lockType) {
            case Fair:
                lock = redisson.getFairLock(key);
                break;
            case Read:
                lock = redisson.getReadWriteLock(key).readLock();
                break;
            case Write:
                lock = redisson.getReadWriteLock(key).writeLock();
                break;
            default:
                lock = redisson.getLock(key);
                break;
        }
        return new OladingLock(lock, this);
    }

    @Override
    public OladingLock lock(String key, long leaseTime, TimeUnit unit, LockType lockType) {
        OladingLock oladingLock = getLock(key, lockType);
        RLock lock = (RLock) oladingLock.getLock();
        lock.lock(leaseTime, unit);
        return oladingLock;
    }

    @Override
    public OladingLock tryLock(String key, long waitTime, long leaseTime, TimeUnit unit, LockType lockType) throws InterruptedException {
        OladingLock oladingLock = getLock(key, lockType);
        RLock lock = (RLock) oladingLock.getLock();
        boolean locked = lock.tryLock(waitTime, leaseTime, unit);
        if (locked){
            return oladingLock;
        }
        return null;
    }

    @Override
    public void unlock(Object lock) {
        if (lock != null) {
            if (lock instanceof RLock) {
                RLock rLock = (RLock)lock;
                if (rLock.isLocked() && rLock.isHeldByCurrentThread()) {
                    rLock.unlock();
                }
            } else {
                throw new LockException("requires RLock type");
            }
        }
    }
}
