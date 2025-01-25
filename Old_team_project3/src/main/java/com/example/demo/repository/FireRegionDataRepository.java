package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.example.demo.entity.FireRegionData;

@Repository
public interface FireRegionDataRepository extends JpaRepository<FireRegionData, Integer> {
    @Query("SELECT r FROM FireRegionData r WHERE r.regionProvince = :regionProvince "
            + "AND r.regionCity = :regionCity")
    Optional<FireRegionData> findByDetails(
            String regionProvince,
            String regionCity);

}
