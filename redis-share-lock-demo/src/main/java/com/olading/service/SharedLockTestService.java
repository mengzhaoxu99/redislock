package com.olading.service;


import com.olading.entity.UserEntity;
import com.olading.redislock.annotion.Lock;
import com.olading.redislock.model.LockTimeoutStrategy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SharedLockTestService {


    @Lock(key = "#userEntity.userName+#userEntity.sex",waitTime = 2,lockTimeoutStrategy = LockTimeoutStrategy.NO_OPERATION)
    public void lock(UserEntity userEntity){
        log.info("lock方法开始,lockparams="+userEntity.getUserName()+userEntity.getSex());
        try {
            Thread.sleep(30000);
        } catch (InterruptedException e) {
            log.error(e.getMessage());
        }
        log.info("lock方法结束,lockparams="+userEntity.getUserName()+userEntity.getSex());
    }

}
