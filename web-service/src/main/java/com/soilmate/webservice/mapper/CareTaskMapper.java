package com.soilmate.webservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.soilmate.webservice.entity.CareTask;
import org.apache.ibatis.annotations.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Mapper
public interface CareTaskMapper extends BaseMapper<CareTask> {

    @Select("<script>" +
            "SELECT * FROM care_task WHERE user_plant_id IN " +
            "<foreach item='id' collection='userPlantIds' open='(' separator=',' close=')'>#{id}" +
            "</foreach> " +
            "AND status = 'PENDING' ORDER BY scheduled_date ASC" +
            "</script>")
    List<CareTask> selectPendingTasksByUserPlantIds(@Param("userPlantIds") List<Long> userPlantIds);

    @Select("SELECT EXISTS(SELECT 1 FROM care_task WHERE user_plant_id = #{userPlantId} AND care_type = #{careType} " +
            "AND status = 'PENDING')")
    boolean existsPendingTask(@Param("userPlantId") Long userPlantId,
                              @Param("careType") String careType);

    /**
     * Query next task date for a single user plant
     */
    @Select("SELECT MIN(scheduled_date) FROM care_task " +
            "WHERE user_plant_id = #{userPlantId} " +
            "AND care_type = #{careType} " +
            "AND status = 'PENDING'")
    LocalDate selectNextTaskDate(@Param("userPlantId") Long userPlantId,
                                  @Param("careType") String careType);

    @Select("SELECT * FROM care_task WHERE user_plant_id = #{userPlantId} AND care_type = #{careType} " +
            "AND status = 'PENDING' ORDER BY scheduled_date ASC LIMIT 1")
    CareTask selectNextPendingTask(@Param("userPlantId") Long userPlantId,
                                         @Param("careType") String careType);

    /**
     * Batch query next task dates for multiple user plants
     */
    @MapKey("userPlantId")
    @Select("<script>" +
            "SELECT user_plant_id AS userPlantId, MIN(scheduled_date) AS scheduledDate " +
            "FROM care_task " +
            "WHERE user_plant_id IN " +
            "<foreach collection='userPlantIds' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach> " +
            "AND care_type = #{careType} " +
            "AND status = 'PENDING' " +
            "GROUP BY user_plant_id" +
            "</script>")
    Map<Long, Map<String, Object>> selectNextTaskDatesRaw(@Param("userPlantIds") List<Long> userPlantIds,
                                                          @Param("careType") String careType);

    /**
     * Query tasks by date range
     */
    @Select("SELECT * FROM care_task " +
            "WHERE user_plant_id IN (SELECT id FROM user_plant WHERE user_id = #{userId} AND is_active = true) " +
            "AND scheduled_date BETWEEN #{startDate} AND #{endDate} " +
            "AND status = 'PENDING' " +
            "ORDER BY scheduled_date ASC")
    List<CareTask> selectTasksByDateRange(@Param("userId") Long userId, @Param("startDate") LocalDate startDate,
                                                @Param("endDate") LocalDate endDate);

    /**
     * Query today's tasks (including overdue)
     */
    @Select("SELECT * FROM care_task " +
            "WHERE user_plant_id IN (SELECT id FROM user_plant WHERE user_id = #{userId} AND is_active = true) " +
            "AND scheduled_date <= #{today} " +
            "AND status = 'PENDING' " +
            "ORDER BY scheduled_date ASC")
    List<CareTask> selectTodayTasks(@Param("userId") Long userId,
                                           @Param("today") LocalDate today);

    /**
     * Delete pending tasks by user plant id
     */
    @Delete("DELETE FROM care_task WHERE user_plant_id = #{userPlantId} AND status = 'PENDING'")
    int deletePendingTasksByUserPlantId(@Param("userPlantId") Long userPlantId);

}
