package in.shamit.ml.nlp.corpus.sec.eddgar.fs;

import java.io.File;

import in.shamit.ml.nlp.corpus.sec.eddgar.Config;
import in.shamit.ml.nlp.corpus.sec.eddgar.time.TimePeriod;

public class DirManager {
	public File getIndexFile(TimePeriod p, boolean compressed){
		String extension=".txt";
		if(compressed){
			extension=".zip";
		}
		File f = new File(Config.indexDir,"sec_edgar_index_"+p.getYear()+"_QTR_"+p.getQuarter()+extension);
		return f;
	}
	public File getFile(TimePeriod p, String secUri){
		String urilParts[]=secUri.split("/");
		String fname=urilParts[urilParts.length-1];
		File yearDir=new File(Config.dataDir,""+p.getYear());
		File qtrDir=new File(yearDir,"QTR_"+p.getQuarter());
		File f=new File(qtrDir,fname);
		return f;
	}
}
