import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

public class Main {
	public static void main(String[] args) throws IOException {

		// takes the stop words
		String[] stop_words = { "", "a", "the", "an", "and", "or", "but", "about", "above", "after", "along", "amid",
				"among", "as", "at", "by", "for", "from", "in", "into", "like", "minus", "near", "of", "off", "on",
				"onto", "out", "over", "past", "per", "plus", "since", "till", "to", "under", "until", "up", "via",
				"vs", "with", "that", "can", "cannot", "could", "may", "might", "must", "need", "ought", "shall",
				"should", "will", "would", "have", "had", "has", "having", "be", "is", "am", "are", "was", "were",
				"being", "been", "get", "gets", "got", "gotten", "getting", "seem", "seeming", "seems", "seemed",
				"enough", "both", "all", "those", "this", "these", "their", "the", "that", "some", "our", "no",
				"neither", "my", "its", "his", "her", "every", "either", "each", "any", "another", "an", "a", "just",
				"mere", "such", "merely", "right", "no", "not", "only", "sheer", "even", "especially", "namely", "as",
				"more", "most", "less", "least", "so", "enough", "too", "pretty", "quite", "rather", "somewhat",
				"sufficiently", "same", "different", "such", "when", "why", "where", "how", "what", "who", "whom",
				"which", "whether", "why", "whose", "if", "anybody", "anyone", "anyplace", "anything", "anytime",
				"anywhere", "everybody", "everyday", "everyone", "everyplace", "everything", "everywhere", "whatever",
				"whenever", "whereever", "whichever", "whoever", "whomever", "he", "him", "she", "it", "they", "them",
				"its", "their", "theirs", "you", "your", "yours", "me", "my", "mine", "I", "we", "us", "much",
				"and/or" };

		// Arraylist with all the queries
		ArrayList<Query> queryList = new ArrayList<>();
		HashMap<String, Integer> frequencyMap = new HashMap<>();
		// first we have to read the cran.qry file
		String filename = "cran.qry";
		BufferedReader br = new BufferedReader(new FileReader(filename));
		String line = br.readLine();
		while (line != null) {
			int id = -1;
			String query = "";
			if (line.contains(".I")) {
				// gets the ID
				String[] splitID = line.split(" ");
				id = Integer.parseInt(splitID[1]);
				// reads the ".W" line
				line = br.readLine();
				// reads the next line
				line = br.readLine();

				while (!line.contains(".I")) {
					query = query + " " + line;
					line = br.readLine();
					if (line == null) {
						break;
					}
				}
			}
			// remove all punctuation
			query = query.replaceAll("\\p{Punct}", "");
			// remove all digits
			query = query.replaceAll("\\d", "");

			ArrayList<String> splitQuery = new ArrayList<String>(Arrays.asList(query.split(" ")));
			for (int i = 0; i < stop_words.length; i++) {
				for (int j = 0; j < splitQuery.size(); j++) {
					if (stop_words[i].equals(splitQuery.get(j))) {
						splitQuery.remove(j);
					}
				}
			}
			query = String.join(" ", splitQuery);
//			System.out.println(query);

			Query q = new Query(id, query);

			queryList.add(q);

		}
		br.close();

		// now we want to tokenize each query
		for (int i = 0; i < queryList.size(); i++) {
			queryList.get(i).tokenizedQuery = queryList.get(i).getQuery().split(" ");
			// input into hashSet to remove duplicates
			HashSet<String> hashSet = new HashSet<>(Arrays.asList(queryList.get(i).tokenizedQuery));
			// after duplicates are removed to put it back into the tokenizedQuery
			queryList.get(i).tokenizedQuery = hashSet.toArray(new String[] {});
			for (int j = 0; j < queryList.get(i).tokenizedQuery.length; j++) {
				String key = queryList.get(i).tokenizedQuery[j];
				// if it contains key increment count
				if (frequencyMap.containsKey(key)) {
					frequencyMap.replace(key, frequencyMap.get(key) + 1);
				} // otherwise add the key with a count of 1
				else {
					frequencyMap.put(key, 1);
				}
			}
		}
		// find the IDF score of each word in the queryList
		HashMap<String, Double> idfMap = new HashMap<>();
		for (Map.Entry<String, Integer> entry : frequencyMap.entrySet()) {
			String word = entry.getKey();
			double idfScore = Math.log((double) (queryList.size()) / entry.getValue());
			idfMap.put(word, idfScore);
		}
//		System.out.println(idfMap.toString());

		// count the number of each non-stopWord in each query
		for (int i = 0; i < queryList.size(); i++) {
			queryList.get(i).tokenizedQuery = queryList.get(i).getQuery().split(" ");
			// create a hashMap for each query
			HashMap<String, Double> hashMap = new HashMap<>();
			for (int j = 0; j < queryList.get(i).tokenizedQuery.length; j++) {
				String key = queryList.get(i).tokenizedQuery[j];
				// if it contains key increment count
				if (hashMap.containsKey(key)) {
					hashMap.replace(key, hashMap.get(key) + 1.0);
				} // otherwise add the key with a count of 1
				else {
					hashMap.put(key, 1.0);
				}
			}
			for (Map.Entry<String, Double> entry : hashMap.entrySet()) {
				// calculate the tfIDF scores
				hashMap.replace(entry.getKey(), entry.getValue() * idfMap.get(entry.getKey()));
			}
//			System.out.println(hashMap.toString());
			// put tfIDF scores into the query
			queryList.get(i).hm = hashMap;
		}

		// Arraylist with all the queries
		ArrayList<Abstract> abstractList = new ArrayList<>();
		HashMap<String, Integer> frequencyMap2 = new HashMap<>();
		// first we have to read the cran.qry file
		String filename2 = "cran.all.1400";
		BufferedReader br2 = new BufferedReader(new FileReader(filename2));
		String line2 = br2.readLine();
		while (line2 != null) {
			int id = -1;
			String query = "";
			if (line2.contains(".I")) {
				// gets the ID
				String[] splitID = line2.split(" ");
				id = Integer.parseInt(splitID[1]);
				while (!line2.contains(".W")) {
					line2 = br2.readLine();
				}
				line2 = br2.readLine();
				while (!line2.contains(".I")) {
					query = query + " " + line2;
					line2 = br2.readLine();
					if (line2 == null) {
						break;
					}
				}
			}
			// remove all punctuation
			query = query.replaceAll("\\p{Punct}", "");
			// remove all digits
			query = query.replaceAll("\\d", "");

			ArrayList<String> splitQuery = new ArrayList<String>(Arrays.asList(query.split(" ")));
			for (int i = 0; i < stop_words.length; i++) {
				for (int j = 0; j < splitQuery.size(); j++) {
					if (stop_words[i].equals(splitQuery.get(j))) {
						splitQuery.remove(j);
					}
				}
			}
			query = String.join(" ", splitQuery);
//			System.out.println(query);

			Abstract a = new Abstract(id, query);

			abstractList.add(a);

		}
		br2.close();

		// now we want to tokenize each abstract
		for (int i = 0; i < abstractList.size(); i++) {
//			System.out.println(abstractList.get(i).getQuery());
			abstractList.get(i).tokenizedQuery = abstractList.get(i).getQuery().split(" ");
			// input into hashSet to remove duplicates
			HashSet<String> hashSet = new HashSet<>(Arrays.asList(abstractList.get(i).tokenizedQuery));
			// after duplicates are removed to put it back into the tokenizedQuery
			abstractList.get(i).tokenizedQuery = hashSet.toArray(new String[] {});
			for (int j = 0; j < abstractList.get(i).tokenizedQuery.length; j++) {
				String key = abstractList.get(i).tokenizedQuery[j];
				// if it contains key increment count
				if (frequencyMap2.containsKey(key)) {
					frequencyMap2.replace(key, frequencyMap2.get(key) + 1);
				} // otherwise add the key with a count of 1
				else {
					frequencyMap2.put(key, 1);
				}
			}
		}
		frequencyMap2.remove("");
//		System.out.println(frequencyMap2);

		// find the IDF score of each word in the queryList
		HashMap<String, Double> idfMap2 = new HashMap<>();
		for (Map.Entry<String, Integer> entry : frequencyMap2.entrySet()) {
			String word = entry.getKey();
			double idfScore = Math.log((double) (abstractList.size()) / entry.getValue());
			idfMap2.put(word, idfScore);
		}
//		System.out.println(idfMap2.toString());

		for (int i = 0; i < abstractList.size(); i++) {
			abstractList.get(i).tokenizedQuery = abstractList.get(i).getQuery().split(" ");
			// create a hashMap for each query
			HashMap<String, Double> hashMap = new HashMap<>();
			for (int j = 0; j < abstractList.get(i).tokenizedQuery.length; j++) {
				String key = abstractList.get(i).tokenizedQuery[j];
				// if it contains key increment count
				if (hashMap.containsKey(key)) {
					hashMap.replace(key, hashMap.get(key) + 1.0);
				} // otherwise add the key with a count of 1
				else {
					hashMap.put(key, 1.0);
				}
			}
			hashMap.remove("");
			abstractList.get(i).hm = hashMap;

//			System.out.println(hashMap.toString());
		}

		// create an arraylist to store hashmap containing the abstract ID and cosineSimilarity
		ArrayList<HashMap<Integer, Double>> cosineSimilarityList = new ArrayList<>();
		// cycle through the queries
		for (int i = 0; i < queryList.size(); i++) {
			// create an ArrayList to hold the tfIDFScores vectors of the abstracts
			ArrayList<HashMap<String, Double>> tfIdfScores = new ArrayList<>();
			// cycle through the abstracts
			for (int j = 0; j < abstractList.size(); j++) {
				// create an abstract vector
				HashMap<String, Double> abstractVector = new HashMap<>();
				// cycle through the words that we need for the query
				for (Entry<String, Double> e : queryList.get(i).hm.entrySet()) {
					// if the term in the query exists in the abstractVector
					if (abstractList.get(j).hm.containsKey(e.getKey())) {
						// calculate its idfTf score using TF * IDF
						double score = idfMap2.get(e.getKey()) * abstractList.get(j).hm.get(e.getKey());
						// add it to the vector
						abstractVector.put(e.getKey(), score);
					}
				}
//				for(Entry <String, Double> e: abstractList.get(j).hm.entrySet()) {
//					double score = idfMap2.get(e.getKey()) * e.getValue();
//					abstractVector.put(e.getKey(), score);
//				}
				// add the vector to the list of vectors
				tfIdfScores.add(abstractVector);
			}
			// calculate cosine similarity between queryList and each abstractList
			HashMap<Integer, Double> documentSimilarity = new HashMap<>();
			// cycle through the idfTFscore list of the abstracts
			for (int j = 0; j < tfIdfScores.size(); j++) {
				// calculate cosineSimlilarity using function below give arguments
				// of tfIDFscores from query and vectors
				Double cosineSimilarity = cosineSimilarity(queryList.get(i).hm, tfIdfScores.get(j));
				// remove NaN cosineSimilarity Values
				if (!cosineSimilarity.isNaN()) {
					// add it to the the documentList
//					System.out.println(j + " " + cosineSimilarity);
					documentSimilarity.put(j, cosineSimilarity);
				}
			
			}
			// each value in this should be another query 
			// each containing a hashMap with <abstractID, cosineSimilarity>
			cosineSimilarityList.add(documentSimilarity);
		}
		
		// Cycle through the list of queries
		for (int i = 0; i < cosineSimilarityList.size(); i++) {
			// create a temp array for sorting
			ArrayList<Entry<Integer, Double>> tempArray = new ArrayList<>();
			// get every element and add it to the temp array
			for (Entry<Integer, Double> e : cosineSimilarityList.get(i).entrySet()) {
				tempArray.add(e);
			}
			// comparator
			Comparator<Entry<Integer, Double>> valueComparator = new Comparator<Entry<Integer, Double>>() {
				@Override
				public int compare(Entry<Integer, Double> e1, Entry<Integer, Double> e2) {
					Double v1 = e1.getValue();
					Double v2 = e2.getValue();
					return v2.compareTo(v1);
				}
			};
			// sort using comparator
			Collections.sort(tempArray, valueComparator);
			//print to file
			for (Entry<Integer, Double> e : tempArray) {
				// get the queryID, abstractID, and cosineSimilarity
				String textToAppend = (i + 1) + " " + (e.getKey() + 1) + " " + e.getValue();
				BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt", true));
				writer.write(textToAppend);
				writer.newLine();
				writer.close();
			}
		}
	}

	// given 2 vectors calculate the tfIDF scores 
	public static double cosineSimilarity(HashMap<String, Double> queryVector, HashMap<String, Double> abstractVector) {
//		System.out.println(queryVector.toString());
//		System.out.println(abstractVector.toString());
		// initialize numerator
		double numerator = 0.0;
		// numerator == dot product of qVector and aVector
		// cycle through the queryVector
		for (Entry<String, Double> e : queryVector.entrySet()) {
			// if aVector contains entry in qVector
			if (abstractVector.containsKey(e.getKey())) {
				// add it to the numerator
				numerator += (e.getValue() * abstractVector.get(e.getKey()));
			}
		}
		// create a collection containing the values of the query vector
		Collection<Double> queryVectorValues = queryVector.values();
		// initialize a sum
		double queryVectorSum = 0.0;
		// cycle through the collection
		for (Iterator<Double> i = queryVectorValues.iterator(); i.hasNext();) {
			// add the square to the sum
			queryVectorSum += (Math.pow(i.next(), 2));
		}
		// create a collection containing the values of the abstract vector
		Collection<Double> abstractVectorValues = abstractVector.values();
		// initialize a sum
		double abstractVectorSum = 0.0;
		// cycle through the collection
		for (Iterator<Double> i = abstractVectorValues.iterator(); i.hasNext();) {
			// add the square to the sum
			abstractVectorSum += (Math.pow(i.next(), 2));
		}
		// get the denominator which is the squareRoot of product of squares
		double denominator = Math.sqrt(queryVectorSum * abstractVectorSum);
		
//		System.out.println(denominator);
		// return the consine Similarity
		return (numerator / denominator);

	}

}
