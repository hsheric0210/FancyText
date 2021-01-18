package fancytext.utils;

import java.util.Collection;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public final class MultiThreading
{
	@SuppressWarnings("WeakerAccess")
	static final ExecutorService defaultWorkers;
	static final ForkJoinPool defaultForkJoinPool;

	static
	{
		// This part is important
		final ThreadFactory workerFactory = new ThreadFactory()
		{
			private final ThreadFactory defaultFactory = Executors.defaultThreadFactory();

			public Thread newThread(final Runnable r)
			{
				final Thread thread = defaultFactory.newThread(r);
				thread.setName("FancyTextWorker-" + thread.getName());
				thread.setDaemon(true); // This part is important
				return thread;
			}
		};

		final int availableThreads = Runtime.getRuntime().availableProcessors();
		defaultWorkers = new ThreadPoolExecutor(availableThreads, availableThreads, 10L, TimeUnit.MINUTES, new LinkedBlockingQueue<>(), workerFactory);
		defaultForkJoinPool = new ForkJoinPool(availableThreads);
	}

	private MultiThreading()
	{

	}

	public static void shutdownAll()
	{
		defaultWorkers.shutdownNow();
	}

	public static ExecutorService getDefaultWorkers()
	{
		return defaultWorkers;
	}

	public static ForkJoinPool getDefaultForkJoinPool()
	{
		return defaultForkJoinPool;
	}

//	public static <T> Collection<Future<T>> submitCallables(final Collection<? extends Callable<T>> tasks)
//	{
//		return tasks.stream().map(workers::submit).collect(Collectors.toList());
//	}

	/**
	 * Submit multiple tasks to workers
	 * 
	 * @param  tasks
	 *               Tasks to execute
	 * @return       Futures representing pending completions of tasks
	 */
	@SuppressWarnings("UnusedReturnValue")
	public static Collection<Future<?>> submitRunnables(final Collection<? extends Runnable> tasks)
	{
		return tasks.stream().map(defaultWorkers::submit).collect(Collectors.toList());
	}

//
//	/**
//	 * DON'T OVERUSE IT. Perform "lengthy GUI-interaction tasks" in a background thread. Use getDefaultWorkers().submit() instead of non-GUI-interaction tasks.
//	 * 
//	 * @param  work
//	 *                Task to execute
//	 * @param  ifDone
//	 *                Task to execute after the work is done
//	 * @return        a Future representing pending completion of the task
//	 */
//	@SuppressWarnings("unchecked")
//	public static <T> Future<T> submitSwingWorker(final Callable<? extends T> work, final Runnable ifDone)
//	{
//		final Runnable worker = new SwingWorker<T, Object>()
//		{
//			@Override
//			protected T doInBackground() throws Exception
//			{
//				return work.call();
//			}
//
//			@Override
//			protected void done()
//			{
//				Optional.ofNullable(ifDone).ifPresent(Runnable::run);
//			}
//		};
//
//		Future<T> future = null;
//
//		try
//		{
//			final Field futureField = SwingWorker.class.getDeclaredField("future");
//			futureField.setAccessible(true);
//			future = (Future<T>) futureField.get(worker);
//		}
//		catch (final NoSuchFieldException | IllegalAccessException ignored)
//		{
//
//		}
//
//		defaultWorkers.submit(worker);
//
//		return future;
//	}
}
