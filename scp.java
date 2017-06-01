/**
 * 
 */
package main;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.Character.Subset;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

import org.apache.commons.collections4.CollectionUtils;

/**
 * @author Dansy
 *
 */

public class Scp_main {

	private static Integer numberOfElements;
	private static Integer numberOfSubsets;
	private static Map<List<String>, Float> weightSubsets = new HashMap<List<String>, Float>();
	private static List<List<String>> subsets;// = new
												// ArrayList<List<String>>();
	private static List<String> elements = new ArrayList<String>(); // To modify
	private static List<List<String>> realSubsets = new ArrayList<List<String>>();
	// private static List<Integer> containedOfeachLine = new
	// ArrayList<Integer>(); // To
	// modify
	private static Map<String, List<List<String>>> setCoverElement = new HashMap<String, List<List<String>>>();
	private static Map<Integer, List<String>> order;
	static List<Integer> costs = new ArrayList<Integer>();
	private static Set<String> coverage;
	private static List<List<String>> order2;

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		getInstances("instance");

		for (String file : getInstances("instance")) {
			readDataFile(file);
		}
		/*
		 * List<String> files = getInstances("instance");
		 * 
		 * for (String file : files)readDataFile(file);
		 */
		// readDataFile("instance/example.txt");

		setCovering();
		ConstructInitialSolution(true);
		// redundancyElimination();
		System.out.println(order);
	}

	private static List<String> getInstances(String dir) {
		File folder = new File(dir);

		File[] listOfFiles = folder.listFiles();
		List<String> files = new ArrayList<String>();

		for (int i = 0; i < listOfFiles.length; i++) {
			if (listOfFiles[i].isFile()) {
				files.add(dir + "/" + listOfFiles[i].getName());
			}
		}

		return files;
	}

	public static void readDataFile(String dataFile) {
		try {
			Scanner scanner = new Scanner(new File(dataFile));

			numberOfElements = scanner.nextInt();
			numberOfSubsets = scanner.nextInt();

			subsets = new ArrayList<List<String>>();

			for (int i = 0; i < numberOfSubsets; ++i) {
				costs.add(scanner.nextInt());
			}

			for (int i = 0; i < numberOfElements; ++i) {
				int nb = scanner.nextInt();
				// containedOfeachLine.add(nb);
				subsets.add(new ArrayList<String>());

				for (int j = 0; j < nb; ++j) {
					int value = scanner.nextInt();
					subsets.get(i).add(String.valueOf(value));
				}
			}

			/* Info of rows that are covered by each column */

			List<List<Integer>> elts = new ArrayList<List<Integer>>();

			for (int j = 0; j < numberOfSubsets; ++j) {
				elts.add(new ArrayList<Integer>());

				for (int i = 0; i < numberOfElements; ++i) {
					elts.get(j).add(0);
				}
			}
			int covered = 0;
			for (List<String> line : subsets) {

				for (int columnCoverLine = 0; columnCoverLine < line.size(); ++columnCoverLine) {
					int column = Integer.valueOf(line.get(columnCoverLine)) - 1;
					elts.get(column).set(covered, 1);
				}
				++covered;
			}

			covered = 0;
			for (List<Integer> subset : elts) {
				realSubsets.add(new ArrayList<String>());
				for (int elementInsubset = 0; elementInsubset < subset.size(); ++elementInsubset) {
					if (subset.get(elementInsubset) == 1) {
						String elementCover = String.valueOf(elementInsubset + 1);
						realSubsets.get(covered).add(elementCover);
					}
				}
				++covered;
			}

			// Weigths of each subset

			for (int value = 0; value < numberOfSubsets; ++value) {

				weightSubsets.put(realSubsets.get(value), Float.valueOf(costs.get(value)));
			}

			scanner.close();

		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}
	}

	public static void getElements() {
		Set<String> elementsValue = new HashSet<>();

		for (List<String> sets : realSubsets)

			if (!elementsValue.containsAll(sets)) {

				elementsValue.addAll(sets);
			}

		for (String elt : elementsValue) {
			elements.add(elt);
		}
	}

	// Info of subsets that cover each element

	public static void setCovering() {
		getElements();

		for (String elementInSet : elements) {
			List<List<String>> covering = new ArrayList<List<String>>();
			for (List<String> set : realSubsets) {
				for (int i = 0; i < set.size(); ++i) {
					if (elementInSet.equals(set.get(i))) {
						covering.add(set);
					}
				}
			}
			setCoverElement.put(elementInSet, covering);
		}
	}

	private static String initialSolution = "CH4";

	// solution construction

	private static Map<Integer, List<String>> ConstructInitialSolution(boolean redundancy) {
		order = new HashMap<Integer, List<String>>();
		switch (initialSolution) {
		case "CH1": // Random Solution construction
			String currentValue;
			coverage = new HashSet<>();
			List<List<String>> subsetList = null;
			int pos, set = 0;
			List<String> possibleElement = new ArrayList<String>(numberOfElements);
			Map<String, List<List<String>>> setCoverElementCP = new HashMap<String, List<List<String>>>();

			for (String key : setCoverElement.keySet()) {
				setCoverElementCP.put(key, setCoverElement.get(key));
			}
			for (int j = 0; j < numberOfElements; ++j) {
				possibleElement.add(elements.get(j)); // copy the list element
			}

			for (int j = 0; j < numberOfElements; ++j) {
				pos = new Random().nextInt(possibleElement.size());
				currentValue = possibleElement.get(pos);

				possibleElement.remove(currentValue);
				for (String s : setCoverElement.keySet()) {
					if (s.equals(currentValue)) {
						subsetList = setCoverElement.get(currentValue);
						set = new Random().nextInt(subsetList.size());
						if (!coverage.containsAll(subsetList.get(set))) {

							order.put(j, subsetList.get(set));

							coverage.addAll(subsetList.get(set));
						}
						setCoverElementCP.remove(set);
					}
				}
				if (redundancy)
					redundancyElimination(coverage);

			}

			break;
		case "CH2": // Static cost based greedy value
			costBasedGreedyValues(false, redundancy);
			break;
		case "CH3": // Static cover cost based greedy value
			costBasedGreedyValues(true, redundancy);
			break;
		case "CH4": // Adaptative cover cost based greedy value
			order2 = new ArrayList<List<String>>();
			coverage = new HashSet<>();
			Map<List<String>, Float> costBased = new HashMap<List<String>, Float>();
			Float greedyValue = 0F;
			// copy the subset of weights into a temporary map

			for (List<String> key : weightSubsets.keySet()) {
				costBased.put(key, weightSubsets.get(key));
			}
			// coverage.add("a");
			Map<List<String>, Float> tempSet = null;
			for (int j = 0; j < numberOfSubsets; ++j) {
				tempSet = new HashMap<List<String>, Float>();
				for (List<String> key : costBased.keySet()) {
					if (CollectionUtils.removeAll(key, coverage).size() > 0) {
						tempSet.put(key, costBased.get(key) / CollectionUtils.removeAll(key, coverage).size());
					}
				}
				if (tempSet.size() == 0) {
					break;
				}
				greedyValue = Collections.min(tempSet.values());

				for (List<String> key : tempSet.keySet()) {

					if (tempSet.get(key) == greedyValue) {

						// order2.add(key);
						order.put(j, key);

						coverage.addAll(key);

						costBased.remove(key);
						break;
					}
				}
			}
			if (redundancy)
				redundancyElimination(coverage);
			break;
		}
		System.out.println("SSSSSSS" + order);
		return order;
	}

	private static void costBasedGreedyValues(boolean byCover, boolean redundancy) {

		coverage = new HashSet<>();
		Map<List<String>, Float> costBased = new HashMap<List<String>, Float>();
		Float greedyValue = 0F;
		// copy the subset of weights into a temporary map

		for (List<String> key : weightSubsets.keySet()) {
			costBased.put(key, weightSubsets.get(key));
		}

		if (byCover) {
			for (List<String> key : costBased.keySet()) {
				costBased.put(key, costBased.get(key) / key.size());
			}
		}

		for (int j = 0; j < numberOfSubsets; ++j) {

			greedyValue = Collections.min(costBased.values());

			for (List<String> key : costBased.keySet()) {

				if (costBased.get(key) == greedyValue) {
					if (!coverage.containsAll(key)) {

						order.put(j, key);

						coverage.addAll(key);

						costBased.remove(key);
						break;
					}
				}
			}

		}
		if (redundancy)
			redundancyElimination(coverage);
		System.out.println(coverage.toString());
	}

	public static void redundancyElimination(Set<String> coverage) {

		List<String> tempList = null;
		Map<List<String>, Integer> solution = new HashMap<List<String>, Integer>();
		Map<List<String>, Integer> copyOfsolution = new HashMap<List<String>, Integer>();

		float WeigthSubset = 0F;
		int max = 0;
		System.out.println("ORDER????ZZZ" + order);

		// copy of the obtained solution "order" into a local Map solution
		for (List<String> o : order.values()) {
			WeigthSubset = weightSubsets.get(o);
			solution.put(o, (int) WeigthSubset);
			copyOfsolution.put(o, (int) WeigthSubset);
		}

		System.out.println("BEFORE????ZZZ" + copyOfsolution);

		for (List<String> set : order.values()) {
			System.out.println(set);
			int redundantElement = solution.get(set);
			for (int i = 0; i < order.size(); ++i) {
				if (set != order.get(i)) {
					if (CollectionUtils.containsAny(set, order.get(i))) {
						System.out.println(order.get(i));
						max = Math.max(solution.get(order.get(i)), redundantElement);
						System.out.println(max);
						if (max == redundantElement) {
							System.out.println("remove set");
							tempList = set;
							copyOfsolution.remove(set);

						} else {
							System.out.println("remove B");
							tempList = order.get(i);
							copyOfsolution.remove(order.get(i));

						}
						List<String> union = new ArrayList<>();
					
						for (List<String> redundantSet : copyOfsolution.keySet()) {
							union = (List<String>) CollectionUtils.union(union, redundantSet);
							
						}
						System.out.println("????ZZZ" + copyOfsolution);
						System.out.println("+++++++" + union);

						if(union.size() != numberOfElements){ // if the suppression of the set create a lack in the coverage
							copyOfsolution.put(tempList, max);
						}
						/*
						 * if (!coverage.containsAll(tempList)) {
						 * 
						 * copyOfsolution.put(tempList, max); //
						 * solution.remove(tempList); coverage.addAll(tempList);
						 * System.out.println("???"+copyOfsolution);
						 * 
						 * }
						 */

					}
				}
			}

		}
		
		System.out.println("ZZZ" + copyOfsolution);
	}

	public static int computeCost() {
		int currentCost = 0;
		for (List<String> o : order.values()) {
			currentCost += weightSubsets.get(o);
		}

		return currentCost;

	}

}
