package org.springframework.samples.petclinic.system;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class AsyncConfigTest {

	@Test
	void visitSummaryExecutorHasCorePoolSizeTwo() {
		AsyncConfig config = new AsyncConfig();
		ThreadPoolTaskExecutor executor = config.visitSummaryExecutor();
		executor.initialize();
		assertThat(executor.getCorePoolSize()).isEqualTo(2);
	}

	@Test
	void visitSummaryExecutorHasMaxPoolSizeFive() {
		AsyncConfig config = new AsyncConfig();
		ThreadPoolTaskExecutor executor = config.visitSummaryExecutor();
		executor.initialize();
		assertThat(executor.getMaxPoolSize()).isEqualTo(5);
	}

	@Test
	void visitSummaryExecutorHasQueueCapacityTwentyFive() {
		AsyncConfig config = new AsyncConfig();
		ThreadPoolTaskExecutor executor = config.visitSummaryExecutor();
		executor.initialize();
		assertThat(executor.getQueueCapacity()).isEqualTo(25);
	}

	@Test
	void visitSummaryExecutorHasCallerRunsPolicyRejectionHandler() {
		AsyncConfig config = new AsyncConfig();
		ThreadPoolTaskExecutor executor = config.visitSummaryExecutor();
		executor.initialize();
		assertThat(((ThreadPoolExecutor) executor.getThreadPoolExecutor()).getRejectedExecutionHandler())
			.isInstanceOf(ThreadPoolExecutor.CallerRunsPolicy.class);
	}

	@Test
	void visitSummaryExecutorHasCorrectThreadNamePrefix() {
		AsyncConfig config = new AsyncConfig();
		ThreadPoolTaskExecutor executor = config.visitSummaryExecutor();
		executor.initialize();
		assertThat(executor.getThreadNamePrefix()).isEqualTo("visitSummary-");
	}

}
