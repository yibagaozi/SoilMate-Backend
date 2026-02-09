package com.soilmate.webservice.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.soilmate.webservice.entity.Barcode;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * Mapper for Barcode entity.
 *
 * @author MA, Ruize
 * @since 1.0.0
 */
@Mapper
public interface BarcodeMapper extends BaseMapper<Barcode> {

    @Select("SELECT plant_id FROM barcode WHERE barcode_value = #{barcodeValue}")
    Long selectPlantIdByBarcodeValue(@Param("barcodeValue") String barcodeValue);
}
