package com.example.productmgr.repository;

import com.example.productmgr.model.SystemSetting;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SystemSettingRepository extends CrudRepository<SystemSetting, Long> {
    
    Optional<SystemSetting> findByKey(String key);
    
    @Query("SELECT EXISTS(SELECT 1 FROM system_settings WHERE key = :key)")
    boolean existsByKey(@Param("key") String key);
}