package com.litb.aims.push;

import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.BufferedReader;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import com.litb.aims.service.push.PushService;

@Component
public class Push {

	private static Logger logger = Logger.getLogger(Push.class);
	
	@Autowired
	private PushService pushService;
	
	private ApplicationContext context;
	
	public Push(){
	}

	public void setApplicationContext(ApplicationContext applicationContext)
	    throws BeansException{
		this.context = applicationContext;
	}
	public void run(List<String> merchantList) throws Exception{
		try{
			logger.info("start push aims db products to npu db...");
			logger.info("==================================================================================");
			pushService.pushToNpuAndRecord(merchantList);
			logger.info("end.\n\n");
		}catch(Exception e){    //Throwable e无法执行throw e;操作
			logger.error(e);
			throw e;
		}
	}
	
	public static void main(String[] args){
		ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		Push push = context.getBean(Push.class);
		
		List<String> merchantIdList = new ArrayList<String>();
 		if (args.length==0){
 			try{
 				String encoding="UTF-8";
 				File file = new File("/home/catchtop/push2npu/merchants.txt");
 				if(file.isFile() && file.exists()){
 					InputStreamReader reader = new InputStreamReader(new FileInputStream(file), encoding);
 				    BufferedReader bufferedReader = new BufferedReader(reader);
 				    String lineTxt = null;
 				    while((lineTxt=bufferedReader.readLine()) != null){
 				    	merchantIdList.add(lineTxt.toString().trim());
 				    }
 				    reader.close();
 				}else{
 					System.out.println("找不到指定文件!");
 					logger.error("Cannot find file: /home/catchtop/push2npu/merchants.txt");
 					System.exit(1);
 				}
 			}catch(Exception e){
 				System.out.println("读取文件内容出错!");
 				e.printStackTrace();
 				System.exit(1);
 			}
			
		}else{
			for(int i=0; i<args.length; i++){
				merchantIdList.add(args[i]);
			}
		}
		try{
			push.run(merchantIdList);
		}catch(Throwable e){
			logger.error("error", e);
			System.exit(1);
		}
	}

}
