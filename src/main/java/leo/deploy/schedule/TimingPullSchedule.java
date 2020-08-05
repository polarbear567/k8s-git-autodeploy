package leo.deploy.schedule;

import java.util.concurrent.TimeUnit;

import leo.deploy.condition.EnableTiming;
import leo.deploy.config.DeployQueue;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Conditional;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.stereotype.Component;

/**
 * @author Leo Li
 */
@Component
@EnableScheduling
@Conditional(EnableTiming.class)
public class TimingPullSchedule implements SchedulingConfigurer {

	private static final String TIME_INTERVAL_OF_PULL = System.getenv("TIME_INTERVAL_OF_PULL");

	@Autowired
	private DeployQueue deployQueue;

	@Override
	public void configureTasks(ScheduledTaskRegistrar scheduledTaskRegistrar) {
		scheduledTaskRegistrar.addTriggerTask(() -> {
			System.out.println("timing pull start");
			deployQueue.put("schedule pull");
		}, triggerContext -> {
			PeriodicTrigger trigger = new PeriodicTrigger(Long.valueOf(TIME_INTERVAL_OF_PULL), TimeUnit.SECONDS);
			return trigger.nextExecutionTime(triggerContext);
		});
	}
}
