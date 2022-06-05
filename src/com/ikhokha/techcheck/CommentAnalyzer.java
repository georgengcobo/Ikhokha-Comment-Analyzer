package com.ikhokha.techcheck;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class CommentAnalyzer {

	public Map<String, Integer> analyze(File file) {

		Map<String, Integer> resultsMap = new HashMap<>();

		var filterEngine = new WordFilter.FilterBuilder()
				.FilterByLessThan15()
				.FilterByMoverMentions()
				.FilterByShakerMentions()
				.FilterByQuestions()
				.FilterForSpam()
				.build();

		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {

			String line = null;
			while ((line = reader.readLine()) != null) {

				filterEngine.AggregateByFilter(line, resultsMap);
			}

		} catch (FileNotFoundException e) {
			System.out.println("File not found: " + file.getAbsolutePath());
			e.printStackTrace();
		} catch (IOException e) {
			System.out.println("IO Error processing file: " + file.getAbsolutePath());
			e.printStackTrace();
		}

		return resultsMap;

	}

}
