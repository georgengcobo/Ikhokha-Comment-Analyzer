package com.ikhokha.techcheck;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;

public class Main {

	private static final int MAXTHREADS = 10;

	public static void main(String[] args) {

		Map<String, Integer> totalResults = new HashMap<>();
		List<Future<Map<String, Integer>>> taskList = new ArrayList<Future<Map<String, Integer>>>();

		File docPath = new File("docs");
		File[] commentFiles = docPath.listFiles((d, n) -> n.endsWith(".txt"));

		CommentAnalyzer commentAnalyzer = new CommentAnalyzer();

		//Creating an specific sized thread pool.
		ExecutorService executor = Executors.newFixedThreadPool(MAXTHREADS);

		//Crate an new task for each file in the direcrory
		for (int i = 0; i < commentFiles.length; i++) {

			final int arg = i;
			Callable<Map<String, Integer>> worker = new Callable<Map<String, Integer>>() {

				@Override
				public Map<String, Integer> call() throws Exception {

					Map<String, Integer> fileResults = commentAnalyzer.analyze(commentFiles[arg]);
					return fileResults;
				}
			};

			Future<Map<String, Integer>> submit = executor.submit(worker);
			taskList.add(submit);

		}

		// This will make the executor accept no new threads
		// and finish all existing threads in the queue
		executor.shutdown();

		// Wait until all threads are finish doing thier stuff
		while (!executor.isTerminated()) {
		}
	
		//Tally up the results of each completed task.
		for (Future<Map<String, Integer>> future : taskList) {
			try {
				addReportResults(future.get(), totalResults);
			
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
		}

		System.out.println("RESULTS\n=======");
		totalResults.forEach((k, v) -> System.out.println(k + " : " + v));
	}

	/**
	 * This method adds the result counts from a source map to the target map
	 * 
	 * @param source the source map
	 * @param target the target map
	 */
	// could make this synchronized here
	private static void addReportResults(Map<String, Integer> source, Map<String, Integer> target) {

		for (Map.Entry<String, Integer> entry : source.entrySet()) {
			target.computeIfPresent(entry.getKey(), (key, val) -> val + entry.getValue());
			target.putIfAbsent(entry.getKey(), entry.getValue());
		}

	}

}
