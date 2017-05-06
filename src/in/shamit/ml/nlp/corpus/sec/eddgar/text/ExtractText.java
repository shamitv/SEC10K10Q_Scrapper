package in.shamit.ml.nlp.corpus.sec.eddgar.text;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;

import in.shamit.ml.nlp.corpus.sec.eddgar.Config;

public class ExtractText {
	static Logger log = Logger.getLogger("EdgarIndexData");
	static Pattern emptyLinePtrn = Pattern.compile("^\\s*?$");
	static Pattern nonSentenceLinePtrn = Pattern.compile("^\\s*?$|^[\\d\\-\\s]*?$|^[^a-zA-Z0-9]*?$");
	static Pattern pageNumberPtrn = Pattern.compile("^[\\d\\-\\s]*?$");
	static Pattern docPtrn = Pattern.compile("<DOCUMENT>.*?<\\/DOCUMENT>", Pattern.DOTALL|Pattern.CASE_INSENSITIVE);
	static Pattern pdfPtrn = Pattern.compile("<PDF>.*?<\\/PDF>", Pattern.DOTALL|Pattern.CASE_INSENSITIVE);
	static Pattern tblPtrn = Pattern.compile("<TABLE>.*?<\\/TABLE>", Pattern.DOTALL|Pattern.CASE_INSENSITIVE);
	static Pattern tagPtrn = Pattern.compile("<.*?>");
	static Pattern ccyPtrn = Pattern.compile("\\s[\\$\\d,\\(\\)\\.]+\\s");
	static Pattern punctuationPtrn = Pattern.compile("[\\(\\)\\[\\],.!?;:'\"]");
	static Pattern dashPtrn = Pattern.compile("[=\\-_]{3,}");
	static Pattern htmlEntityPtrn = Pattern.compile("&#[\\d]{1,}");

	static AtomicLong fcount=new AtomicLong(0);
	
	static void cleanFile(File f){
		try {
			if(fcount.incrementAndGet()%1000==0){
				log.info(fcount+"\t\t"+f.getAbsolutePath());
			}
			String text=new String(Files.readAllBytes(f.toPath()),Charset.forName("UTF-8"));
			String plainText=getPlainText(text);

			synchronized(ExtractText.class){
			File destFile=getDestinationFile(f);
			destFile.getParentFile().mkdirs();
			Files.write(destFile.toPath(), plainText.getBytes(Charset.forName("UTF-8")),StandardOpenOption.APPEND);
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	private static String getPlainText(String text) {
		List<String> docs = getDocs(text);
		String ret="";
		for(String d:docs){
			String docText=removeTables(d);
			docText=removePDF(d);
			docText=removeTags(docText);
			docText=replaceCCY(docText);
			docText=processLines(docText);
			docText=removeDashes(docText);
			docText=removeHtmlEntities(docText);
			ret += "\r\n"+docText;
		}
		return ret;
	}

	private static String removeHtmlEntities(String docText) {
		Matcher m=htmlEntityPtrn.matcher(docText);
		return m.replaceAll(" ");
	}

	private static String removeDashes(String docText) {
		Matcher m=dashPtrn.matcher(docText);
		return m.replaceAll(" ");
	}

	private static String removePDF(String docText) {
		Matcher m=pdfPtrn.matcher(docText);
		return m.replaceAll(" ");
	}

	private static String replaceCCY(String docText) {
		Matcher m=ccyPtrn.matcher(docText);
		return m.replaceAll(" NUM ");
	}

	private static String removePageNumbers(String docText) {
		Matcher m=pageNumberPtrn.matcher(docText);
		return m.replaceAll(" ");
	}

	private static String processLines(String docText) {
		String lines[]=docText.split("\\R");
		StringBuffer ret=new StringBuffer();
		int lcount=0;
		int ignoreLines=5;
		for(String l:lines){
			lcount++;
			if(lcount>=ignoreLines){
				l=l.trim();
				Matcher mEmpty=nonSentenceLinePtrn.matcher(l);
				if(!(mEmpty.matches())){
					l=postProcessLine(l);
					if(!(ret.length()==0)){
						ret.append("\r\n");
					}				
					ret.append(l);
				}				
			}
		}
		return ret.toString().trim();
	}

	private static String postProcessLine(String l) {
		Matcher m=punctuationPtrn.matcher(l);
		String ret=m.replaceAll(" $0 ");
		ret=ret.replaceAll("\\s+", " ");
		ret=ret.toLowerCase();
		return ret;
	}

	private static String removeTables(String d) {
		Matcher m=tblPtrn.matcher(d);
		return m.replaceAll(" ");
	}

	private static String removeTags(String d) {
		Matcher m=tagPtrn.matcher(d);
		return m.replaceAll(" ");		
	}

	private static List<String> getDocs(String text) {
		List<String> docs=new ArrayList<>();
		Matcher m = docPtrn.matcher(text);
		while(m.find()){
			docs.add(m.group());
		}
		return docs;
	}

	private static File getDestinationFile(File f) {
		String path=f.getAbsolutePath();
		//String pathNew=path.replace("sec_edgar"+File.separatorChar+"data"+File.separatorChar, "sec_edgar"+File.separatorChar+"data_text"+File.separatorChar);
		return new File(Config.baseDir,"combined_output.txt");
		//return new File(pathNew);
	}

	public static void main(String[] args) {
		try {
			List<File> files= Files.walk(Config.dataDir.toPath())
				.filter(p -> p.toFile().isFile())
				.filter((p -> p.toString().endsWith(".txt") || p.toString().endsWith(".text")))
				.map(p->p.toFile())
				.collect(Collectors.toList());
			System.out.println(files.size()+" files to clean.");
			files.stream()
				.parallel()
				.forEach(f->{cleanFile(f);});
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}
	
	public static void main_test(String[] args) throws IOException{
		File f=new File("L:\\work\\mldata\\sec_edgar\\data\\1997\\QTR_4","0000001988-97-000008.txt");
		String text=new String(Files.readAllBytes(f.toPath()),Charset.forName("UTF-8"));
		String textClean=getPlainText(text);
		System.out.println(textClean);
	}

}
