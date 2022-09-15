package com.example.food_drugs.rest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.Trigger;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.config.TriggerTask;
import org.springframework.scheduling.support.CronTrigger;
import com.example.examplequerydslspringdatajpamaven.repository.ScheduledRepository;
import com.example.examplequerydslspringdatajpamaven.service.DeviceServiceImpl;
import com.example.food_drugs.service.impl.ScheduledServiceImplSFDA;

/**
 * Handling of schedule timing
 * @author fuinco
 *
 */
@Configuration
public class ScheduledTasksRestControllerSFDA implements SchedulingConfigurer,DisposableBean{
	private static final Log logger = LogFactory.getLog(DeviceServiceImpl.class);
	public static ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

	public static ArrayList<String> cronsExpressions=new ArrayList<String>();
	public static ScheduledTaskRegistrar scheduledTask;
	
	@Autowired
	private ScheduledServiceImplSFDA scheduledServiceImplSFDA;
	
	@Autowired
	private ScheduledRepository scheduledRepository;
	
	public String tryTime(String exp) {
		logger.info("************************ @Scheduled SFDA STARTED ***************************");
		logger.info("Expression in now : "+exp);
	    scheduledServiceImplSFDA.accessExpressionSFDA(exp);
	    
		logger.info("************************ @Scheduled SFDA ENDED ***************************");
		
		return LocalDateTime.now().toString();
	}
	    
	@Override
	public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
	
		cronsExpressions = scheduledRepository.getDistinctExp();
		this.scheduledTask = scheduledTaskRegistrar;
		registeCrons();
		

	}
	
	public void registeCrons() {
		logger.info("All Expressions SFDA: "+cronsExpressions);

		for(String cronExpression: cronsExpressions) {

			Runnable runnable = () -> tryTime(cronExpression);
			
	        Trigger trigger = new Trigger() {
	        	
	            @Override
	            public Date nextExecutionTime(TriggerContext triggerContext) {

	                CronTrigger crontrigger = new CronTrigger(cronExpression);
	
	                return crontrigger.nextExecutionTime(triggerContext);
	
	            }
	
	        };
			
		    this.scheduledTask.addTriggerTask(runnable, trigger);
		}
	}
	@Override
	public  void destroy() throws Exception {
		
   	   scheduledTask.setTriggerTasksList(new ArrayList<TriggerTask>());

	   registeCrons();

       scheduledTask.destroy(); 

       scheduledTask.setScheduler(executor);

       scheduledTask.afterPropertiesSet(); 
		
        /*if (executor != null) {
            executor.shutdownNow();
        }*/

    }

}
