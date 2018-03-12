package org.aatm;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.time.Month;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ReadPDF implements RequestHandler<Map<String,Object>, String> {

	private static final ObjectMapper MAPPER = new ObjectMapper();

	private static final String URL = "http://www.wrightstreetmosque.com/wp-content/uploads/2018/02/MarchTT-2018.pdf";

	@Override
	public String handleRequest(Map<String,Object> input, Context context) {
		context.getLogger().log("Input: " + input);
		try {
			Instant startTime = Instant.now();
			String result = this.getPrayerTableHTML();
			System.out.println("Time => " + Duration.between(startTime, Instant.now()).toMillis());
			return result;
		} catch(Exception ex) {
			throw new RuntimeException(ex);
		}
	}
	
	public static void main(String[] args) throws Exception {
		ReadPDF test = new ReadPDF();
		Instant startTime = Instant.now();
		System.out.println(test.getPrayerTableHTML());
		System.out.println("Time => " + Duration.between(startTime, Instant.now()).toMillis());
	}

	private String getPrayerTableHTML() throws Exception {

		Instant startTime = Instant.now();
		try (PDDocument document = PDDocument.load(readPDF())) {
			PDFTextStripper tStripper = new PDFTextStripper();

			String pdfFileInText = tStripper.getText(document).replaceAll("Day ", "Day" + System.lineSeparator());
			String lines[] = pdfFileInText.split("\\r?\\n");
			StringBuffer hijriMonth = new StringBuffer();
			String hijriMonthFinalResult = "";
			Month month = null;
			Map<String, String> headerContext = new HashMap<>();
			boolean collectHijri = false;
			List<Map<Column, String>> timeTable = new ArrayList<>();
			System.out.println("Time to read and convert PDF in text => " + Duration.between(startTime, Instant.now()).toMillis());
			for (String line : lines) {
				line = line.trim();

				if (line.equalsIgnoreCase("Day")) {
					collectHijri = true;
				} else if (collectHijri) {
					hijriMonth.append(line);
				}

				if (month == null && extractMonth(line).isPresent()) {
					collectHijri = false;
					month = extractMonth(line).get();
					hijriMonthFinalResult = extractHijriMonth(hijriMonth.toString(), month.name());
				}

				if (isTimeTableRow(line)) {
					collectHijri = false;
					timeTable.add(getPrayerTime(line));
				}
			}
			headerContext.put("hijriMonth", hijriMonthFinalResult);
			headerContext.put("gregorianMonth", month.name());
			System.out.println("month = " + month);
			System.out.println("hijri = " + hijriMonthFinalResult);
			System.out.println("time table = " + timeTable);
			
			PrayerTable results = new PrayerTable(hijriMonthFinalResult, month.name(), timeTable);
			String jsonReults = MAPPER.writeValueAsString(results);
			
			AmazonS3 s3client = AmazonS3ClientBuilder.defaultClient();        
			s3client.putObject(new PutObjectRequest("aatmtestbucket", "test.json", new ByteArrayInputStream(jsonReults.getBytes()), new ObjectMetadata()));
			
			return "done";
		}
	}
	
	private static Map<Column, String> getPrayerTime(String data) {
		final String[] rowsplitted = splitData(data);

		if (rowsplitted.length != Column.values().length) {
			throw new RuntimeException("The number of column does not correspond for the records: " + data);
		}
		Map<Column, String> prayerTime = new HashMap<>();
		IntStream.range(0, rowsplitted.length)
				.forEach(index -> prayerTime.put(Column.values()[index], rowsplitted[index]));
		return prayerTime;
	}

	private static String[] splitData(String data) {
		String[] rowsplitted = data.replaceAll("  ", " Unknow ").split(" ");
		if (rowsplitted.length > Column.values().length) {
			rowsplitted = data.replaceAll("  ", " ").split(" ");
		}
		return rowsplitted;
	}

	private static String extractHijriMonth(String data, String firstNonHijriString) {
		int firstIndexNonHijriMonth = data.toLowerCase().indexOf(firstNonHijriString.toLowerCase());
		StringBuilder sb = new StringBuilder(data);
		sb.delete(firstIndexNonHijriMonth, data.length());
		return sb.toString().trim().replaceAll("’", "'");
	}

	private static Optional<Month> extractMonth(String line) {
		return Arrays.asList(Month.values()).stream()
				.filter(month -> line.toLowerCase().contains(month.name().toLowerCase())).findFirst();
	}

	private static boolean isTimeTableRow(String line) {
		if (line.length() < 4)
			return false;
		String first4Characters = line.substring(0, 4);
		first4Characters = first4Characters.replace(" ", "").replace("*", "");
		return DayOfWeek.exists(first4Characters);
	}
	
	private static InputStream readPDF() throws Exception {
	 
	    URL urlObject = new URL(URL);
	    URLConnection urlConnection = urlObject.openConnection();
	    return urlConnection.getInputStream();
	}
	
	private static String getTemplate() throws Exception {
		return new String(Files.readAllBytes(Paths.get(ReadPDF.class.getClassLoader().getResource("prayertable.template").toURI())));
	}
}
