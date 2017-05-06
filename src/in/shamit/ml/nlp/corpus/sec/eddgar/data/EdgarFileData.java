package in.shamit.ml.nlp.corpus.sec.eddgar.data;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.io.IOUtils;

import in.shamit.ml.nlp.corpus.sec.eddgar.Config;
import in.shamit.ml.nlp.corpus.sec.eddgar.time.TimePeriod;

public class EdgarFileData {
	Logger log = Logger.getLogger("EdgarIndexData");
	Map<TimePeriod,List<String>> data=new ConcurrentHashMap<>();
	public void load() {
		try {
			List<File> files= Files.walk(Config.indexDir.toPath())
				.filter(p -> p.toFile().isFile())
				.filter((p -> p.toString().endsWith(".idx") || p.toString().endsWith(".zip")))
				.map(p->p.toFile())
				.collect(Collectors.toList());
			files.stream()
				.parallel()
				.forEach(f->{loadFile(f);});
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}
	
	void loadFile(File f){
		String text=getFileText(f);
		TimePeriod p = getTimePeriodsFromFilename(f.getName());
		String lines[]=text.split("\\R");
		List<String> statements=new ArrayList<>();
		// File format is : 
		// CIK|Company Name|Form Type|Date Filed|Filename
		for(String l:lines){
			String parts[]=l.split("\\|");
			if(parts.length>=5){
				if("CIK".equals(parts[0])){
					//This is header row, ignore it
				}else{
					//If Form Type is 10-Q or 10-K
					if("10-Q".equals(parts[2])||"10-K".equals(parts[2])){
						statements.add(parts[4]);
					}
				}
			}
		}
		log.info("Loaded "+statements.size()+" lines for "+p+" from "+f.getName());
		data.put(p, statements);
	}
	Pattern yearPtrn=Pattern.compile("_\\d\\d\\d\\d_");
	Pattern qtrPtrn=Pattern.compile("_\\d[^\\d]");
	Pattern underscorePtrn=Pattern.compile("_");
	private TimePeriod getTimePeriodsFromFilename(String name) {
		String yrStr= getPatternMatch(yearPtrn,name);
		String qtrStr= getPatternMatch(qtrPtrn,name);
		int year=getNumberFromUnderscoreString(yrStr);
		int quarter=getNumberFromUnderscoreString(qtrStr);
		return new TimePeriod(year, quarter);
	}
	private int getNumberFromUnderscoreString(String str) {
		return Integer.parseInt(str.replaceAll("_", "").replaceAll("\\.", ""));
	}
	private String getPatternMatch(Pattern ptrn, String str) {
		Matcher matcher=ptrn.matcher(str);
		if(matcher.find()){
			return matcher.group();	
		}else{
			return null;
		}
		
	}
	String getFileText(File f){
		if(isZipFile(f)){
			return readZipFile(f);
		}else{
			return readTextFile(f);
		}
	}
	
	
	private String readTextFile(File f) {
		try {
			String text=new String(Files.readAllBytes(f.toPath()),Charset.forName("UTF-8"));
			return text;
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}
	private String readZipFile(File f) {
		log.info("Reading Zip File "+f.getAbsolutePath());
		try (ZipFile zf = new ZipFile(f);) {
			ZipArchiveEntry master=zf.getEntry("master.idx");
			if(master!=null){
				InputStream inp = zf.getInputStream(master);
				String text = IOUtils.toString(inp, StandardCharsets.UTF_8.name());
				return text;
			}else{
				throw new IllegalStateException("master.idx not found in Zip");
			}
			//Read only first entry
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}
	boolean isZipFile(File f) {
		if(f.getName().toLowerCase().endsWith(".zip")){
			return true;
		}
		return false;
	}
	public Set<TimePeriod> getQuarters(){
		return data.keySet();
	}
	public List<String> getStatements(TimePeriod p){
		return data.get(p);
	}
}
