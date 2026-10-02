package uk.gov.hmcts.reform.pt.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.kagkarlsson.scheduler.Scheduler;
import com.github.kagkarlsson.scheduler.SchedulerBuilder;
import com.github.kagkarlsson.scheduler.SchedulerClient;
import com.github.kagkarlsson.scheduler.event.ExecutionInterceptor;
import com.github.kagkarlsson.scheduler.event.SchedulerListener;
import com.github.kagkarlsson.scheduler.serializer.JacksonSerializer;
import com.github.kagkarlsson.scheduler.task.Task;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.time.Duration;
import java.util.List;

@Configuration
@Slf4j
@Getter
public class SchedulingConfiguration {
    @Bean
    @Primary
    public SchedulerClient schedulerClient(DataSource dataSource, ObjectMapper objectMapper) {
        return SchedulerClient.Builder
            .create(dataSource)
            .serializer(new JacksonSerializer(objectMapper))
            .build();
    }

    @Bean(initMethod = "start", destroyMethod = "stop")
    @ConditionalOnProperty(prefix = "db-scheduler", name = "executor-enabled", havingValue = "true")
    @DependsOn("schedulerClient")
    public Scheduler startupTasksScheduler(DataSource dataSource,
                                           ObjectMapper objectMapper,
                                           @Value("${db-scheduler.threads}") int threadCount,
                                           @Value("${db-scheduler.polling-interval-seconds}") long interval,
                                           List<Task<?>> tasks,
                                           List<SchedulerListener> schedulerListeners,
                                           List<ExecutionInterceptor> executionInterceptors) {
        log.info("Starting scheduler");

        SchedulerBuilder builder = Scheduler.create(dataSource, tasks)
            .threads(threadCount)
            .pollingInterval(Duration.ofSeconds(interval))
            .serializer(new JacksonSerializer(objectMapper))
            .registerShutdownHook();

        schedulerListeners.forEach(builder::addSchedulerListener);
        executionInterceptors.forEach(builder::addExecutionInterceptor);

        Scheduler scheduler = builder.build();
        scheduler.start();
        return scheduler;
    }
}
