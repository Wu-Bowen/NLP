To run my program, we just compile the java file and run the compiled file.

First, ensure everything (including the two POS files - development and training corpora) are all in the same folder.

Go to the location of the folder file and follow these steps:

step1: javac ebw289HMM_HW3.java
step2: java ebw289HMM_HW3

After step1, you'll see a newly class file.

After setep2, you'll see that 2 files will come out Merged.POS, which will be the merged file of the development and training copora, and Submission.POS, which will be the end product. 


How I handled Out of Vocab words: 
made up some hard coded probabilities based on features like capitalization and endings of words. 

You can see in my code that I wrote a method named: oov

It is shown below and basically it takes the current word and the previous word to determine possible tags for the oov words. 


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