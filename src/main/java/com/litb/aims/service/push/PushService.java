package com.litb.aims.service.push;


import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


import com.litb.aims.dao.push.PushDao;

import com.litb.npu.api.NPUAPI;
import com.litb.npu.api.entity.aims.AIMSItem;

import com.litb.aims.api.AimsAPI;

@Service
public class PushService {
	
	private static Logger logger = Logger.getLogger(PushService.class);
	
	@Autowired
	private PushDao pushDao;
	
	@Autowired
	private NPUAPI npuApi;
	
	@Autowired
	private AimsAPI aimsApi;

	public PushService(){
		
	}

	@Transactional(readOnly = false, 
			propagation = Propagation.REQUIRED, 
			rollbackFor=Throwable.class)
	public void pushToNpuAndRecord(List<String> merchantIdList) throws Exception{
		//遍历每个商户, 以便打出log
		for (String merchantId : merchantIdList){
			//导入NPU
			List<String> oneMerchantList = new ArrayList<String>();
			oneMerchantList.add(merchantId); //m中只有一个元素
			List<AIMSItem> items = pushDao.getProductsToPushFromAimsDb(oneMerchantList);
			if (items.size() == 0){
				logger.info("<" + merchantId + "> to push product id count are 0, pass it.");
				continue;
			}
			//每个商户每次最多倒入500个商品
			if (items.size() > 500){
				int times =  new Double(java.lang.Math.ceil(items.size() / 500.0)).intValue();
				logger.info("<" + merchantId + "> to push count over 500, split " + times + " times to push..." );
				for(int i=0; i<times; i++){
					List<AIMSItem> sub;
					if (i==(times-1)){
						sub = items.subList((times-1)*500, items.size());
					}else{
					    sub = items.subList(i*500, (i+1)*500);
					}
					List<Integer> values = new ArrayList<Integer>();
					for(AIMSItem obj:sub){
						values.add(obj.getProductId());
					}
					logger.info("times " + (i+1) + "(" + sub.size() + ")" + ", " + "ids are: ");
					logger.info(values);
					npuApi.importCTItemByAIMS(sub);
				}
			}else{
			    npuApi.importCTItemByAIMS(items);
			}
			//在本地product_events中记录 更改状态 #NPU段已实现
			List<Integer> aimsProductIdList = new ArrayList<Integer>();
			for(AIMSItem item:items){
				aimsProductIdList.add(item.getProductId());
			}
			logger.info("<" + merchantId + "> to push product id are as follow(totally "+ aimsProductIdList.size() + "): \n" + aimsProductIdList);
//			aimsApi.notifyEvaluationStarted(aimsProductIdList);
		}
		
	}

}
