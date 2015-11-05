package com.litb.aims.dao.push;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.litb.npu.api.entity.aims.AIMSItem;


public interface PushDao {
	
	List<AIMSItem> getProductsToPushFromAimsDb(
			@Param("merchantIdList") List<String> merchantIdList);

}
