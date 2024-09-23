package cieloecommerce.sdk.ecommerce.request;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.UUID;
import java.util.zip.GZIPInputStream;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;

import com.google.gson.Gson;

import cieloecommerce.sdk.Environment;
import cieloecommerce.sdk.Merchant;
import cieloecommerce.sdk.ecommerce.Sale;

/**
 * Abstraction to reuse most of the code that send and receive the HTTP
 * messages.
 *
 * @param <T>
 *            the AsyncTask expects 3 params and we can only anticipate 2 of
 *            them.
 */
public abstract class AbstractSaleRequest<T> {
	final Environment environment;
	private final Merchant merchant;
	private HttpClient httpClient;

	AbstractSaleRequest(Merchant merchant, Environment environment) {
		this.merchant = merchant;
		this.environment = environment;
	}

	public abstract Sale execute(T param) throws IOException, CieloRequestException;

	public void setHttpClient(HttpClient httpClient) {
		this.httpClient = httpClient;
	}

	/**
	 * Send the HTTP request to Cielo with the mandatory HTTP Headers set
	 *
	 * @param request
	 *            The POST, PUT, GET request and its content is defined by the
	 *            derivations
	 * @return the HTTP response returned by Cielo
	 * @throws IOException
	 *             yeah, deal with it
	 */
	HttpResponse sendRequest(HttpUriRequest request) throws IOException {
		if (httpClient == null) {
			httpClient = HttpClientBuilder.create().build();
		}

		request.addHeader("Accept", "application/json");
		request.addHeader("Accept-Encoding", "gzip");
		request.addHeader("Content-Type", "application/json");
		request.addHeader("User-Agent", "CieloEcommerce/3.0 Android SDK");
		request.addHeader("MerchantId", merchant.getId());
		request.addHeader("MerchantKey", merchant.getKey());
		request.addHeader("RequestId", UUID.randomUUID().toString());

		return httpClient.execute(request);
	}

	/**
	 * Read the response body sent by Cielo
	 *
	 * @param response
	 *            HttpResponse by Cielo, with headers, status code, etc.
	 * @return An instance of Sale with the response entity sent by Cielo.
	 * @throws IOException
	 *             yeah, deal with it
	 * @throws CieloRequestException
	 */
	Sale readResponse(HttpResponse response) throws IOException, CieloRequestException {
		HttpEntity responseEntity = response.getEntity();
		InputStream responseEntityContent = responseEntity.getContent();

		Header contentEncoding = response.getFirstHeader("Content-Encoding");

		if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
			responseEntityContent = new GZIPInputStream(responseEntityContent);
		}

		BufferedReader responseReader = new BufferedReader(new InputStreamReader(responseEntityContent));
		StringBuilder responseBuilder = new StringBuilder();
		String line;

		while ((line = responseReader.readLine()) != null) {
			responseBuilder.append(line);
		}

		return parseResponse(response.getStatusLine().getStatusCode(), responseBuilder.toString());
	}

	/**
	 * Just decode the JSON into a Sale or create the exception chain to be
	 * thrown
	 *
	 * @param statusCode
	 *            The status code of response
	 * @param responseBody
	 *            The response sent by Cielo
	 * @return An instance of Sale or null
	 * @throws CieloRequestException
	 */
	private Sale parseResponse(int statusCode, String responseBody) throws CieloRequestException {
		Sale sale = null;
		Gson gson = new Gson();

		switch (statusCode) {
		case 200:
		case 201:
			sale = gson.fromJson(responseBody, Sale.class);
			break;
		case 400:
			CieloRequestException exception = null;
			CieloError[] errors = gson.fromJson(responseBody, CieloError[].class);

			for (CieloError error : errors) {
				System.out.printf("%s: %s", "Cielo Error [" + error.getCode() + "]", error.getMessage());

				exception = new CieloRequestException(error.getMessage(), error, exception);
			}

			throw exception;
		case 404:
			throw new CieloRequestException("Not found", new CieloError(404, "Not found"), null);
		default:
			System.out.printf("%s: %s", "Cielo", "Unknown status: " + statusCode);
		}

		return sale;
	}
}
