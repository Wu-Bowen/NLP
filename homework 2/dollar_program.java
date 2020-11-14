import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.*;

public class Main {
	public static void main(String[] args) throws Exception {

		String s = readFileAsString(args[0]);
			Pattern regex = Pattern.compile("[\\$][\\d{1,3}],\\d{1,3}\\,\\d{1,3}|"
					+ "[\\$][\\d{1,5}]\\.\\d{1,2}|"
					+ "[\\$](\\d+\\.\\d{1,2})|"
					+ "[\\$][\\d{1,3}]\\,\\d{1,3}\\.\\d{1,2}|"
					+ "[\\$][\\d{1,3}]\\,\\d{1,3}|"
					+ "[\\$][\\d]+million|"
					+ "[\\$][\\d]+ million|"
					+ "[\\$][\\d]+billion|"
					+ "[\\$][\\d]+ billion|"
					+ "[\\$][\\d]+trillion|"
					+ "[\\$][\\d]+ trillion|"
					+ "[\\d]+[a-z]+ billion dollars|"
					+ "[a-z]+ billion dollars|"
					+ "[\\d]+ billion dollars|"
					+ "[\\d]+[a-z]+ million dollars|"
					+ "[a-z]+ million dollars|"
					+ "[\\d]+ million dollars|"
					+ "[\\d]+[a-z]+ thousand dollars|"
					+ "[a-z]+ thousand dollars|"
					+ "[\\d]+ thousand dollars|"
					+ "[a-z]+ hundred dollars|"
					+ "[\\d]+ hundred dollars|"
					+ "[\\$][\\d]+|"
					+ "[A-Z]{1}[a-z]+ dollars|"
					+ "[a-z]+ dollars|"
					+ "[\\d]+ dollars? and [\\d]+ cents|"
					+ "[a-z]+ dollars? and [a-z] cents|"
					+ "[\\d]+ cents|"
					+ "[a-z]+ cents");

			Matcher regexMatcher = regex.matcher(s);
			 while (regexMatcher.find()) {
				int start = regexMatcher.start(0);
				int end = regexMatcher.end(0);
				System.out.println(s.substring(start, end));
			}
	}

	// converting text file into a string (makes it easier to extract regular
	// expressions
	public static String readFileAsString(String fileName) {
		// initializes a string
		String text = "";
		try {
			// read the file and puts it into the string
			text = new String(Files.readAllBytes(Paths.get(fileName)));
		} catch (IOException e) {
			e.printStackTrace();
		}

		return text;
	}

}
