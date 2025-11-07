package com.userprocessor.service;

import com.userprocessor.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class StatisticsService {

    private final UserRepository userRepository;

    @Autowired
    public StatisticsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Map<String, Object> getGeneralStatistics() {
        Map<String, Object> stats = new HashMap<>();
        
        long totalUsers = userRepository.count();
        stats.put("totalUsers", totalUsers);
        
        Map<String, Long> sourceStats = getSourceStatistics();
        stats.put("usersBySource", sourceStats);
        
        if (totalUsers > 0) {
            Map<String, Double> sourcePercentages = new HashMap<>();
            for (Map.Entry<String, Long> entry : sourceStats.entrySet()) {
                double percentage = (entry.getValue() * 100.0) / totalUsers;
                sourcePercentages.put(entry.getKey(), Math.round(percentage * 100.0) / 100.0);
            }
            stats.put("sourcePercentages", sourcePercentages);
        }
        
        return stats;
    }

    public Map<String, Long> getSourceStatistics() {
        Map<String, Long> sourceStats = new HashMap<>();
        
        List<Object[]> results = userRepository.countUsersBySource();
        for (Object[] result : results) {
            String source = (String) result[0];
            Long count = (Long) result[1];
            sourceStats.put(source, count);
        }
        
        return sourceStats;
    }

    public long getUserCountBySource(String source) {
        return userRepository.countBySource(source);
    }

    public Map<String, Object> getDetailedStatistics() {
        Map<String, Object> stats = getGeneralStatistics();
        
        stats.put("csvUsers", getUserCountBySource("csv"));
        stats.put("jsonUsers", getUserCountBySource("json"));
        stats.put("xmlUsers", getUserCountBySource("xml"));
        
        return stats;
    }
}
