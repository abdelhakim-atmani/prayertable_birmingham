package org.aatm;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.fasterxml.jackson.databind.ObjectMapper;

public class GenerateHTML implements RequestHandler<Map<String, Object>, String> {

	private static final String HEADER_CELL_TEMPLATE = "<th>%s</th>";
	private static final String TIME_CELL_TEMPLATE = "<td>%s</td>";

	private static final ObjectMapper MAPPER = new ObjectMapper();

	@Override
	public String handleRequest(Map<String, Object> input, Context context) {
		context.getLogger().log("Input: " + input);
		try {
			Instant startTime = Instant.now();
			String result = this.getPrayerTableHTML();
			System.out.println("Time => " + Duration.between(startTime, Instant.now()).toMillis());
			return result;
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	public static void main(String[] args) throws Exception {
		GenerateHTML test = new GenerateHTML();
		Instant startTime = Instant.now();
		System.out.println(test.getPrayerTableHTML());
		System.out.println("Time => " + Duration.between(startTime, Instant.now()).toMillis());
	}

	private String getPrayerTableHTML() throws Exception {

		Instant startTime = Instant.now();

		AmazonS3 s3client = AmazonS3ClientBuilder.defaultClient();
		S3Object object = s3client.getObject(new GetObjectRequest("aatmtestbucket", "test.json"));
		System.out.println("Time to download json file => " + Duration.between(startTime, Instant.now()).toMillis());
		PrayerTable prayerTable = MAPPER.readValue(object.getObjectContent(), PrayerTable.class);

		Map<String, String> headerContext = new HashMap<>();
		headerContext.put("hijriMonth", prayerTable.getHijriMonth());
		headerContext.put("gregorianMonth", prayerTable.getGregorianMonth());

		StringBuffer prayerTableHTML = new StringBuffer("<table id='prayertable'>");
		prayerTableHTML.append("<thead>");
		prayerTableHTML.append(generateTableHeader(headerContext));
		prayerTableHTML.append("</thead><tbody>");
		prayerTableHTML.append(generateTimeRows(prayerTable.getPrayerTimeList()));
		prayerTableHTML.append("</tbody>");
		prayerTableHTML.append("</table>");
		return getTemplate().replaceAll("%content%", prayerTableHTML.toString());

	}

	private static String generateTableHeader(Map<String, String> headerContext) {
		StringBuffer headerColumns = new StringBuffer("<tr>");
		Arrays.asList(Column.values()).forEach(
				column -> headerColumns.append(String.format(HEADER_CELL_TEMPLATE, column.getValue(headerContext))));
		headerColumns.append("</tr>");
		return headerColumns.toString();
	}

	private static String generateTimeRows(List<Map<Column, String>> prayerTimeValues) {
		StringBuffer timeRows = new StringBuffer();
		prayerTimeValues.forEach(values -> timeRows.append(generateRow(values)));
		return timeRows.toString();
	}

	private static String generateRow(Map<Column, String> valuesPerColumn) {
		String classCss = "";
		String todayDay = LocalDate.now().getDayOfMonth() + "";
		if (todayDay.equals(valuesPerColumn.get(Column.MONTH))) {
			classCss = "active";
		}
		StringBuffer row = new StringBuffer(
				"<tr id='" + valuesPerColumn.get(Column.MONTH) + "' class='" + classCss + "'>");
		Arrays.asList(Column.values())
				.forEach(column -> row.append(String.format(TIME_CELL_TEMPLATE, valuesPerColumn.get(column))));
		row.append("</tr>");
		return row.toString();
	}

	private static String getTemplate() throws Exception {
		return new String(Files.readAllBytes(
				Paths.get(GenerateHTML.class.getClassLoader().getResource("prayertable.template").toURI())));
	}
}
