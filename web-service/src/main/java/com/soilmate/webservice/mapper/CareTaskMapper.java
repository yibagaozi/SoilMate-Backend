package com.soilmate.webservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.soilmate.webservice.entity.CareTask;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

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

    @Select("SELECT * FROM care_task WHERE user_plant_id = #{userPlantId} AND care_type = #{careType} " +
            "AND status = 'PENDING' ORDER BY scheduled_date ASC LIMIT 1")
    CareTask selectNextPendingTask(@Param("userPlantId") Long userPlantId,
                                         @Param("careType") String careType);

}
