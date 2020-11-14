import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Main {
	public static void main (String [] args) throws IOException {
		String filename = "WSJ_23.pos";
		BufferedReader br = new BufferedReader(new FileReader(filename));
		String line = br.readLine();
		String [] previous = null;
		while (line != null) {
			String printout = "";
			if (line.isEmpty()) {			
				BufferedWriter writer = new BufferedWriter(new FileWriter("test.feature", true));
				writer.write(printout);
				writer.newLine();
				writer.close();
				// set the previous as null
				previous = null;
				line = br.readLine();
			} else {
				br.mark(100);
				String next = br.readLine();
				boolean noNext = false;
				String [] nextArr = null;
				if(next.isEmpty()) {
					noNext = true;
				}else {
					 nextArr = next.split("\t");
				}
				String [] arr = line.split("\t");
				if(previous!=null) {
					printout = arr[0] + "\tPOS=" + arr[1] + "\tprevious_POS=" + previous[1] + "\tprevious_word=" + previous[0] + "\tfirst_word=FALSE";
					if(!noNext) {
						printout+= "\tnext_POS=" + nextArr[1] +"\tnext_word=" + nextArr[0] + "\tlast_word=FALSE";
					} else {
						printout+= "\tlast_word=TRUE";
					}
					if(filename.equals("WSJ_02-21.pos-chunk")) {
						printout += "\t" + arr[2];
					}
					BufferedWriter writer = new BufferedWriter(new FileWriter("test.feature", true));
					writer.write(printout);
					writer.newLine();
					writer.close();
				}
				// previous was a blank line
				else {
					printout = arr[0] + "\tPOS=" + arr[1] + "\tfirst_word=TRUE";
					if(!noNext) {
						printout+= "\tnext_POS=" + nextArr[1] +"\tnext_word=" + nextArr[0] + "\tlast_word=FALSE";
					} else {
						printout+= "\tlast_word=TRUE";
					}
					if(filename.equals("WSJ_02-21.pos-chunk")) {
						printout += "\t" + arr[2];
					}
					BufferedWriter writer = new BufferedWriter(new FileWriter("test.feature", true));
					writer.write(printout);
					writer.newLine();
					writer.close();
				}
				previous = new String[]{arr[0], arr[1]};
				br.reset();
				line = br.readLine();
			}
		}
		br.close();
	}
}
