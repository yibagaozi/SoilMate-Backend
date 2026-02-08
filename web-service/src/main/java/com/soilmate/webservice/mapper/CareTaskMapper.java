package com.soilmate.webservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.soilmate.webservice.entity.CareTask;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface CareTaskMapper extends BaseMapper<CareTask> {

    /**
     * Check if a pending task exists for user plant and care type.
     */
    @Select("SELECT COUNT(*) > 0 FROM care_task WHERE user_plant_id = #{userPlantId} AND care_type = #{careType} AND status = 'PENDING'")
    boolean existsPendingTask(@Param("userPlantId") Long userPlantId, @Param("careType") String careType);

    /**
     * Get tasks for multiple user plants within date range.
     */
    @Select("<script>" +
            "SELECT * FROM care_task WHERE user_plant_id IN " +
            "<foreach item='id' collection='userPlantIds' open='(' separator=',' close=')'> #{id} </foreach> " +
            "AND scheduled_date BETWEEN #{startDate} AND #{endDate} ORDER BY scheduled_date, scheduled_time" +
            "</script>")
    List<CareTask> selectByUserPlantIdsAndDateRange(@Param("userPlantIds") List<Long> userPlantIds,
                                                    @Param("startDate") LocalDate startDate,
                                                    @Param("endDate") LocalDate endDate);

    /**
     * Get pending tasks count by status for date range.
     */
    @Select("SELECT COUNT(*) FROM care_task " +
            "WHERE user_plant_id IN (SELECT id FROM user_plant WHERE user_id = #{userId} AND is_active = 1) " +
            "AND status = #{status} AND scheduled_date BETWEEN #{startDate} AND #{endDate}")
    int countByUserIdAndStatusAndDateRange(@Param("userId") Long userId, @Param("status") String status,
                                           @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

}
