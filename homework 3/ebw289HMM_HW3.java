import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Map.Entry;

public class Main {

	public static void main(String[] args) throws IOException {
		try {
		merge("WSJ_02-21.pos", "WSJ_24.pos");
		}
        catch (Exception e) { 
        } 
		
		String s = "merged.pos";

		BufferedReader br = new BufferedReader(new FileReader(s));
		String line = br.readLine();
		HashMap<String, HashMap<String, Double>> frequencyMap = new HashMap<>();
		while (line != null) {
			String[] keyValuePair = line.split("\t");
			if (keyValuePair.length >= 2) {
				String value = keyValuePair[0];
				String key = keyValuePair[1];

				if (frequencyMap.containsKey(key)) {
					// word exist
					if (frequencyMap.get(key).containsKey(value)) {
						// if word exists and POS exists for the word
						// increment the frequency of the POS of the word
						frequencyMap.get(key).replace(value, frequencyMap.get(key).get(value) + 1);
					} else {
						// word exist, POS does not exist for the word
						frequencyMap.get(key).put(value, 1.0);
					}
				} else {
					// word does not exist
					// create inner hashMap first
					HashMap<String, Double> innerMap = new HashMap<>();
					// instantiate it with the POS and 1 incrementation of it
					innerMap.put(value, 1.0);
					// put the inner hashMap in the outer hashMap
					frequencyMap.put(key, innerMap);
				}
			}
			line = br.readLine();
		}
		br.close();

		// now we want to find which POS precedes which POS
		BufferedReader br2 = new BufferedReader(new FileReader(s));
		String line3;
		ArrayList<String> splitString = new ArrayList<String>();
		while ((line3 = br2.readLine()) != null) {
			splitString.add(line3);
		}
		br2.close();

		// create a table that contains consecutive POS
		HashMap<String, HashMap<String, Double>> doubleMap = new HashMap<>();
		for (int i = 0; i < splitString.size(); i++) {
			String[] currentLine = splitString.get(i).split("\t");
			String[] nextLine;
			try {
				nextLine = splitString.get(i + 1).split("\t");
			} catch (Exception e) {
				continue;
			}
			if (currentLine.length < 2) {
				if (doubleMap.containsKey("START")) {
					if (doubleMap.get("START").containsKey(nextLine[1])) {
						doubleMap.get("START").replace(nextLine[1], doubleMap.get("START").get(nextLine[1]) + 1);
					} else {
						doubleMap.get("START").put(nextLine[1], 1.0);
					}
				} else {
					HashMap<String, Double> innerMap = new HashMap<>();
					innerMap.put(nextLine[1], 1.0);
					doubleMap.put("START", innerMap);
				}
				continue;
			} else if (nextLine.length < 2) {
				if (doubleMap.containsKey(currentLine[1])) {
					if (doubleMap.get(currentLine[1]).containsKey("END")) {
						doubleMap.get(currentLine[1]).replace("END", doubleMap.get(currentLine[1]).get("END") + 1);
					} else {
						doubleMap.get(currentLine[1]).put("END", 1.0);
					}
				} else {
					HashMap<String, Double> innerMap = new HashMap<>();
					innerMap.put("END", 1.0);
					doubleMap.put("START", innerMap);
				}
				continue;
			}

			if (doubleMap.containsKey(currentLine[1])) {
				if (doubleMap.get(currentLine[1]).containsKey(nextLine[1])) {
					doubleMap.get(currentLine[1]).replace(nextLine[1],
							doubleMap.get(currentLine[1]).get(nextLine[1]) + 1);
				} else {
					doubleMap.get(currentLine[1]).put(nextLine[1], 1.0);
				}
			} else {
				HashMap<String, Double> innerMap = new HashMap<>();
				innerMap.put(nextLine[1], 1.0);
				doubleMap.put(currentLine[1], innerMap);
			}
		}

		calculateProbability(frequencyMap);
		calculateProbability(doubleMap);
		// System.out.println(frequencyMap.toString());
		//  System.out.println(doubleMap.get("RB").toString());

		// this is the corpus we are trying to tag
		String s2 = "WSJ_23.words";
		Writer wr = new FileWriter("submission.pos");
		BufferedReader br3 = new BufferedReader(new FileReader(s2));
		ArrayList<String> allLines = new ArrayList<String>();
		while ((line = br3.readLine()) != null) {
			allLines.add(line);
		}
		br3.close();
		ArrayList<String> sentence = new ArrayList<String>();
		// cycle through all the lines
		for (int i = 0; i < allLines.size(); i++) {
			// reached the end of a sentence. Implement Viterbi Algo
			if (allLines.get(i).isBlank()) {
				// initialize last word to START
				HashMap<String, Double> lastWordPOS = new HashMap<>();
				lastWordPOS.put("START", 1.0);
				// cycle through each word in the sentence
				for (int j = 0; j < sentence.size(); j++) {
					
					// hashmap to store the possible POS per word
					HashMap<String, Double> tagProb = new HashMap<>();
					// cycle through the frequency map
					for (Entry<String, HashMap<String, Double>> entry : frequencyMap.entrySet()) {
						// find the word and its probability
						if (entry.getValue().containsKey(sentence.get(j))) {
							// store it
							tagProb.put(entry.getKey(), entry.getValue().get(sentence.get(j)));
						}
					}
//					if(tagProb.isEmpty()) {
//						System.out.println(sentence.get(j));
//						tagProb.put(oov(sentence.get(j),sentence.get(j-1)),1.0);
//					}
					// ultimate max Value and POS (what will be printed)
					String POSfinal = "";
					double maxValueFinal = 0.0;
					// cycle through the stored tag and probability
					for (Entry<String, Double> entry : tagProb.entrySet()) {

						double maxValue = 0.0;
						String POS = null;
						// cycle through the lastWordPOS sets
						for (Entry<String, Double> entryX : lastWordPOS.entrySet()) {
							// if the current matches with the stored tag
							// extract the probability from prior to current and multiply it by the stored
							// value of the word
//							if(sentence.get(j).equals("no")) {
//								System.out.println(entry);
//							}
							if (doubleMap.containsKey(entryX.getKey())
									&& doubleMap.get(entryX.getKey()).containsKey(entry.getKey())) {
								double temp = entry.getValue() * doubleMap.get(entryX.getKey()).get(entry.getKey())
										* entryX.getValue();
								if (temp > maxValue) {
									maxValue = temp;
									POS = entry.getKey();
								}
								if (temp > maxValueFinal) {
									maxValueFinal = temp;
									POSfinal = entry.getKey();
								}
								
//								System.out.println(entry2.getKey() + " -> " + entry.getKey() + ": "
//										+ entry2.getValue().get(entry.getKey()));
							} else {
								continue;
							}
						}
						lastWordPOS.put(POS, maxValue);
					}
					if(POSfinal.isEmpty() && j!=0) {
						POSfinal = oov(sentence.get(j-1),sentence.get(j));
					}
					  wr.write(sentence.get(j) + "\t" + POSfinal + "\n");
					 // System.out.println(sentence.get(j) + "\t" + POSfinal);
					// System.out.println(sentence.get(j) + " " + tagProb.toString());
					tagProb.clear();
					
				}
				sentence.clear();
				wr.write("\n");
				// add to the sentence
			} else {
				sentence.add(allLines.get(i));
			}

		}
		wr.close();

	}

	private static void calculateProbability(HashMap<String, HashMap<String, Double>> map) {
		for (String POS : map.keySet()) {
			// count the appearances of a word
			double count = 0;
			for (String key2 : map.get(POS).keySet()) {
				count += map.get(POS).get(key2);
			}
			for (String key2 : map.get(POS).keySet()) {
				double probability = map.get(POS).get(key2) / count;
				map.get(POS).replace(key2, probability);
			}

		}

	}

	public static String oov(String prior, String curr) {
		String tag = "";
		if (!prior.equals("") && curr.charAt(0) > 64 && curr.charAt(0) < 91)
			tag = "NNP";
		else if (curr.indexOf('-') > -1
				|| (curr.length() > 2 && (curr.substring(curr.length() - 3, curr.length()).equalsIgnoreCase("ble")
						|| curr.substring(curr.length() - 3, curr.length()).equalsIgnoreCase("ive")
						|| curr.substring(curr.length() - 2, curr.length()).equalsIgnoreCase("us")))
				|| prior.equalsIgnoreCase("be"))
			tag = "JJ";
		else if (curr.substring(curr.length() - 1, curr.length()).equalsIgnoreCase("s"))
			tag = "NNS";
		else if (prior.equals("would"))
			tag = "VB";
		else if (curr.length() > 2 && curr.substring(curr.length() - 3, curr.length()).equalsIgnoreCase("ing"))
			tag = "VBG";
		else if (curr.length() > 2 && curr.substring(curr.length() - 2, curr.length()).equalsIgnoreCase("ed"))
			tag = "VBD";
		else if (curr.length() > 2 && curr.substring(curr.length() - 2, curr.length()).equalsIgnoreCase("en"))
			tag = "VBN";
		else if (prior.equals("it"))
			tag = "VBZ";
		else if (curr.length() > 2 && curr.substring(curr.length() - 2, curr.length()).equalsIgnoreCase("ly"))
			tag = "RB";
		else if (curr.indexOf(".") > -1 || curr.indexOf("$") > -1)
			tag = "CD";
		else
			tag = "NN";
		return tag;
	}
	
	
    public static void merge(String a, String b){
        Scanner as = null;
        Scanner bs = null;
        Writer w = null;
        try{
            as = new Scanner(new File(a));
            bs = new Scanner(new File(b));
            w = new FileWriter("merged.pos");
            while(as.hasNextLine()){
                w.write("\n"+as.nextLine());
            }
            while(bs.hasNextLine()){
                w.write("\n"+bs.nextLine());
            }
        } catch(Exception e){}
        try{
            as.close();
            bs.close();
            w.close();
        } catch(Exception e){}
    }

}
