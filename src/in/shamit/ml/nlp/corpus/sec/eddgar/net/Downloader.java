package in.shamit.ml.nlp.corpus.sec.eddgar.net;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

public class Downloader {
	CloseableHttpClient httpClient=null;
	PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
	public Downloader() {
		super();
		httpClient = HttpClientBuilder.create().setConnectionManager(cm).build();
	}
	
	public File download(URL url, File dstFile) {
		try {
			HttpGet get = new HttpGet(url.toURI()); // we're using GET but it could be via POST as well
			File downloaded = httpClient.execute(get, new FileDownloadResponseHandler(dstFile));
			return downloaded;
		} catch (Exception e) {
			throw new IllegalStateException(e);
		} 
	}
	
	static class FileDownloadResponseHandler implements ResponseHandler<File> {

		private final File target;

		public FileDownloadResponseHandler(File target) {
			this.target = target;
		}

		@Override
		public File handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
			InputStream source = response.getEntity().getContent();
			FileUtils.copyInputStreamToFile(source, this.target);
			return this.target;
		}
		
}

	public void download(String url, File destFile) {
		try {
			URL httpUrl = new URL(url);
			download(httpUrl, destFile);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
		
	}
}
