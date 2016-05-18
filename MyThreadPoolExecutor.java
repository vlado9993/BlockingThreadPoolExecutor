package test;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class MyThreadPoolExecutor extends ThreadPoolExecutor {
	
	private final Semaphore semaphore;
	
	public MyThreadPoolExecutor(int poolSize) {
		super(poolSize, poolSize, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
		semaphore = new Semaphore(poolSize);
	}
	
	// Block when the thread pool is full
	public void submitTask(final Runnable command) throws InterruptedException {
		semaphore.acquire();
		try {
			execute(() -> 
				{
					try {
						command.run();
					} finally {
						semaphore.release();
					}
				}
			);
		} catch (RejectedExecutionException ree) {
			semaphore.release();
		}
	}
}
