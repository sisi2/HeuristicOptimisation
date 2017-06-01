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
	private static List<Integer> containedOfeachLine = new ArrayList<Integer>(); // To
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
		//ConstructInitialSolution(false);
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
				containedOfeachLine.add(nb);
				subsets.add(new ArrayList<String>());

				for (int j = 0; j < nb; ++j) {
					int value = scanner.nextInt();
					subsets.get(i).add(String.valueOf(value));
				}
			}

			/* Info of rows that are covered by each column */

			// row = (int **) mymalloc(n*sizeof(int *));
			List<List<Integer>> elts = new ArrayList<List<Integer>>();

			// nrow = (int *) mymalloc(n*sizeof(int));
			List<Integer> nElts = new ArrayList<Integer>();
			// k = (int *) mymalloc(n*sizeof(int));
			List<Integer> nbrElts = new ArrayList<Integer>();

			for (int j = 0; j < numberOfSubsets; ++j)
				nElts.add(0);
			for (int i = 0; i < numberOfElements; ++i) {

				for (int h = 0; h < containedOfeachLine.get(i); ++h) {
					// nrow[col[i][h]]++;
					int index = Integer.valueOf(subsets.get(i).get(h));
					// System.out.println(index-1);
					nElts.set(index - 1, nElts.get(index - 1) + 1);
				}

			}
			for (int j = 0; j < numberOfSubsets; ++j) {
				// row[j] = (int *) mymalloc(nrow[j]*sizeof(int));
				// List<Integer> val = new ArrayList<Integer>(numberOfSubsets);
				elts.add(new ArrayList<Integer>());
				for (int init = 0; init < numberOfSubsets; ++init)
					elts.get(j).add(0);

				nbrElts.add(0);
			}
			for (int i = 0; i < numberOfElements; ++i) {
				for (int h = 0; h < containedOfeachLine.get(i); ++h) {
					// row[col[i][h]][k[col[i][h]]] = i
					int tmp = Integer.valueOf(subsets.get(i).get(h));
					int ind = elts.get(tmp - 1).get(nbrElts.get(tmp - 1));

					//System.out.println(nbrElts);
					//elts.get(ind).add(i);
					elts.get(tmp - 1).set(nbrElts.get(tmp - 1), i);
					int index = Integer.valueOf(subsets.get(i).get(h));
					nbrElts.set(index - 1, nElts.get(index - 1) + 1);
				}
			}

			// Weigths of each subset
			System.out.println(elts.size());
			System.out.println(containedOfeachLine);
			/*
			 * for (int value = 0; value < numberOfSubsets; ++value ) {
			 * 
			 * weightSubsets.put(subsets.get(value),
			 * Float.valueOf(costs.get(value))); }
			 */
			scanner.close();

		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		}

	}

	public static void getElements() {
		Set<String> elementsValue = new HashSet<>();

		for (List<String> sets : subsets)

			if (!elementsValue.containsAll(sets)) {

				elementsValue.addAll(sets);
			}

		for (String elt : elementsValue) {
			elements.add(elt);
		}
		System.out.println(elements);

	}

	/*
	 * public static void readDataFile2(String dataFile) {
	 * 
	 * Scanner scanner = new Scanner(new File(dataFile));
	 * 
	 * numberOfElements = scanner.nextInt(); numberOfSubsets =
	 * scanner.nextInt();
	 * 
	 * List<Integer> costs = new ArrayList<Integer>();
	 * 
	 * for (int i = 0; i < numberOfSubsets; ++i) { costs.add(scanner.nextInt());
	 * }
	 * 
	 * // Extraction of the subsets
	 * 
	 * else if (k >= 1) {
	 * 
	 * // String[] parts = line.split(" "); // subsets.add(new
	 * ArrayList<String>()); // for (int s = 1; s < parts.length; s++) { // if
	 * (parts.length > 1) { // subsets.get(k - 1).add(String.valueOf(parts[s]));
	 * // } else // subsets.get(k - 1).add(String.valueOf(parts[s])); // }
	 * valueOfElement.add(scanner.nextInt());
	 * System.out.println(valueOfElement); }
	 * 
	 * ++k; sb.append(line); sb.append(System.lineSeparator()); line =
	 * br.readLine(); } br.close(); scanner.close(); // Weigth of subsets for
	 * (int value = 0; value < numberOfElements; ++value) {
	 * 
	 * if (subsets.get(value).size() == 1) break; for (int elt = 0; elt <
	 * subsets.get(value).size(); ++elt) {
	 * elements.add(subsets.get(value).get(elt)); }
	 * 
	 * } System.out.println(elements);
	 * 
	 * // Weigths of each subset int count = 0; for (int value = 0 ; value <
	 * subsets.size(); ++value) { if( subsets.get(value).size() == 1){ ++count;
	 * weightSubsets.put(subsets.get(value),
	 * Float.valueOf(subsets.get(value).get(0)));
	 * 
	 * } //System.out.println(Float.valueOf(subsets.get(value).get(0)));
	 * 
	 * } //System.out.println(weightSubsets); //subsets.remove(0);
	 * 
	 * } catch (IOException e ) { e.printStackTrace();} }catch
	 * (FileNotFoundException e1) { e1.printStackTrace(); } }
	 */

	// Info of subsets that cover each element

	public static void setCovering() {
		getElements();

		for (String e : elements) {
			List<List<String>> covering = new ArrayList<List<String>>();
			for (List<String> set : subsets) {
				for (int i = 0; i < set.size(); ++i) {
					if (e.equals(set.get(i))) {
						covering.add(set);
					}
				}
			}
			// covering.remove(0);
			setCoverElement.put(e, covering);
		}
	}

	private static String initialSolution = "CH2";

	/* Info of elements that are covered by each subset */

	// solution construction

	private static Map ConstructInitialSolution(boolean redundancy) {
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
						// System.out.println(key);
						// System.out.println(costBased.get(key) /
						// CollectionUtils.removeAll(key, coverage).size());
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
				if (redundancy)
					redundancyElimination(coverage);
			}
			break;
		}
		return order;
	}

	private static void costBasedGreedyValues(boolean byCover, boolean redundancy) {

		coverage = new HashSet<>();
		Map<List<String>, Float> costBased = new HashMap<List<String>, Float>();
		Float greedyValue = 0F;
		// copy the subset of weights into a temporay map

		for (List<String> key : weightSubsets.keySet()) {
			costBased.put(key, weightSubsets.get(key));
		}

		if (byCover) {
			for (List<String> key : costBased.keySet()) {
				costBased.put(key, costBased.get(key) / key.size());
			}
		}

		for (int j = 0; j < numberOfSubsets; ++j) {
			// values = (List<Integer>) costBased.values();
			// System.out.println(Collections.min(costBased.values()));

			// List<Integer> test = (List<Integer>) costBased.values();
			greedyValue = Collections.min(costBased.values());

			for (List<String> key : costBased.keySet()) {

				if (costBased.get(key) == greedyValue) {
					if (!coverage.containsAll(key)) {

						order.put(j, key);

						coverage.addAll(key);
					}
					costBased.remove(key);
					break;
				}

			}

		}
		if (redundancy)
			redundancyElimination(coverage);
		System.out.println(coverage.toString());
	}

	// //coverage = new HashSet<>();
	// List<String> tempList = null;
	// Map< List<String>, Integer> solution = new HashMap< List<String>,
	// Integer>();
	//// for (Integer key : order.keySet()) {
	//// solution.put(key, order.get(key) );
	//// }
	//// System.out.println(solution);
	// float WeigthSubset = 0F;
	// int maxWeigthSubset = 0;
	//
	// for (List<String> o : order.values()) {
	// WeigthSubset = weightSubsets.get(o);
	//
	// if( maxWeigthSubset < (int)WeigthSubset )
	// {
	// maxWeigthSubset= (int)WeigthSubset;
	// tempList = o;
	// }
	// solution.put(o,(int)WeigthSubset);
	// //System.out.println(solution);
	// }
	// //System.out.println(solution);
	// for(int i = 0; i< solution.size(); ++i){
	//
	// maxWeigthSubset = Collections.max(solution.values());
	// System.out.println(maxWeigthSubset);
	// //tempList = solution.get(maxWeigthSubset);
	// solution.remove(tempList);
	// coverage.removeAll(tempList);
	// System.out.println(coverage);
	// if (!coverage.containsAll(tempList) ){
	//
	// solution.put( tempList, maxWeigthSubset);
	// //solution.remove(tempList);
	// coverage.addAll(tempList);
	// System.out.println(solution);
	// }
	// }
	// System.out.println(solution);

	public static void redundancyElimination(Set<String> coverage) {

		List<String> tempList = null;
		Map<List<String>, Integer> solution = new HashMap<List<String>, Integer>();

		float WeigthSubset = 0F;
		int max = 0;

		for (List<String> o : order.values()) {
			WeigthSubset = weightSubsets.get(o);
			solution.put(o, (int) WeigthSubset);
		}

		for (List<String> set : order.values()) {
			System.out.println(set);
			int redundantElement = solution.get(set);
			for (int i = 0; i < order.size(); ++i) {
				if (set != order.get(i)) {
					if (CollectionUtils.containsAny(order.get(i), set)) {
						System.out.println(order.get(i));
						max = Math.max(solution.get(order.get(i)), redundantElement);
						System.out.println(max);
						if (max == redundantElement) {
							System.out.println("remove set");
							tempList = set;
							solution.remove(set);
						} else {
							System.out.println("remove B");
							tempList = order.get(i);
							solution.remove(order.get(i));
						}

						if (!coverage.containsAll(tempList)) {

							solution.put(tempList, max);
							// solution.remove(tempList);
							coverage.addAll(tempList);
							System.out.println(solution);
						}
					}
				}
			}

		}
		// System.out.println(solution);
		/*
		 * for(int i = 0; i< solution.size(); ++i){
		 * 
		 * maxWeigthSubset = Collections.max(solution.values());
		 * System.out.println(maxWeigthSubset); //tempList =
		 * solution.get(maxWeigthSubset); solution.remove(tempList);
		 * coverage.removeAll(tempList); System.out.println(coverage); if
		 * (!coverage.containsAll(tempList) ){
		 * 
		 * solution.put( tempList, maxWeigthSubset);
		 * //solution.remove(tempList); coverage.addAll(tempList);
		 * System.out.println(solution); } }
		 */
		System.out.println(solution);
	}

	public static int computeCost() {
		int currentCost = 0;
		for (List<String> o : order.values()) {
			currentCost += weightSubsets.get(o);
		}

		return currentCost;

	}

}
