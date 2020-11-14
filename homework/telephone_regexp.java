import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.regex.*;

public class Main {
	public static void main(String[] args) throws Exception {

		String s = readFileAsString(args[0]);
			Pattern regex = Pattern.compile("\\(\\d{3}\\)[\\s]\\d{3}[\\s-]\\d{4}|"
					+ "\\(\\d{3}\\)[-]\\d{3}[\\s-]\\d{4}|"
					+ "^\\d{3}[\\s.-]\\d{4}|"
					+ "\\d{3}[-]\\d{3}[\\s-]\\d{4}");

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
