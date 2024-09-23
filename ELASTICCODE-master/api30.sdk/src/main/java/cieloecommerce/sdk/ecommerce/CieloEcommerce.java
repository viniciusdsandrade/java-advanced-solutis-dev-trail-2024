package cieloecommerce.sdk.ecommerce;

import java.io.IOException;

import org.apache.http.client.HttpClient;

import cieloecommerce.sdk.Merchant;
import cieloecommerce.sdk.ecommerce.request.CieloRequestException;
import cieloecommerce.sdk.ecommerce.request.CreateSaleRequest;
import cieloecommerce.sdk.ecommerce.request.QuerySaleRequest;
import cieloecommerce.sdk.ecommerce.request.UpdateSaleRequest;

/**
 * The Cielo Ecommerce SDK front-end;
 */
public class CieloEcommerce {
	private final Merchant merchant;
	private final Environment environment;
	private HttpClient httpClient;

	/**
	 * Create an instance of CieloEcommerce choosing the environment where the
	 * requests will be send
	 *
	 * @param merchant
	 *            The merchant credentials
	 * @param environment
	 *            The environment: {@link Environment#PRODUCTION} or
	 *            {@link Environment#SANDBOX}
	 */
	public CieloEcommerce(Merchant merchant, Environment environment) {
		this.merchant = merchant;
		this.environment = environment;
	}

	/**
	 * Create an instance of CieloEcommerce to work on production environment
	 *
	 * @param merchant
	 *            The merchant credentials
	 */
	public CieloEcommerce(Merchant merchant) {
		this(merchant, Environment.PRODUCTION);
	}

	public void setHttpClient(HttpClient httpClient) {
		this.httpClient = httpClient;
	}

	/**
	 * Send the Sale to be created and return the Sale with tid and the status
	 * returned by Cielo.
	 *
	 * @param sale
	 *            The preconfigured Sale
	 * @return The Sale with authorization, tid, etc. returned by Cielo.
	 * @throws IOException
	 * @throws CieloRequestException
	 *             if anything gets wrong.
	 * @see <a href=
	 *      "https://developercielo.github.io/Webservice-3.0/english.html#error-codes">Error
	 *      Codes</a>
	 */
	public Sale createSale(Sale sale) throws IOException, CieloRequestException {
		CreateSaleRequest createSaleRequest = new CreateSaleRequest(merchant, environment);

		createSaleRequest.setHttpClient(httpClient);

		sale = createSaleRequest.execute(sale);

		return sale;
	}

	/**
	 * Query a Sale on Cielo by paymentId
	 *
	 * @param paymentId
	 *            The paymentId to be queried
	 * @return The Sale with authorization, tid, etc. returned by Cielo.
	 * @throws IOException
	 * @throws CieloRequestException
	 *             if anything gets wrong.
	 * @see <a href=
	 *      "https://developercielo.github.io/Webservice-3.0/english.html#error-codes">Error
	 *      Codes</a>
	 */
	public Sale querySale(String paymentId) throws IOException, CieloRequestException {
		QuerySaleRequest querySaleRequest = new QuerySaleRequest(merchant, environment);

		querySaleRequest.setHttpClient(httpClient);

		Sale sale = querySaleRequest.execute(paymentId);

		return sale;
	}

	/**
	 * Cancel a Sale on Cielo by paymentId and speficying the amount
	 *
	 * @param paymentId
	 *            The paymentId to be queried
	 * @param amount
	 *            Order value in cents
	 * @return The Sale with authorization, tid, etc. returned by Cielo.
	 * @throws IOException
	 * @throws CieloRequestException
	 *             if anything gets wrong.
	 * @see <a href=
	 *      "https://developercielo.github.io/Webservice-3.0/english.html#error-codes">Error
	 *      Codes</a>
	 */
	public Sale cancelSale(String paymentId, Integer amount) throws IOException, CieloRequestException {
		UpdateSaleRequest updateSaleRequest = new UpdateSaleRequest("void", merchant, environment);

		updateSaleRequest.setHttpClient(httpClient);
		updateSaleRequest.setAmount(amount);

		Sale sale = updateSaleRequest.execute(paymentId);

		return sale;
	}

	/**
	 * Cancel a Sale on Cielo by paymentId
	 *
	 * @param paymentId
	 *            The paymentId to be queried
	 * @return The Sale with authorization, tid, etc. returned by Cielo.
	 * @throws IOException
	 * @throws CieloRequestException
	 *             if anything gets wrong.
	 * @see <a href=
	 *      "https://developercielo.github.io/Webservice-3.0/english.html#error-codes">Error
	 *      Codes</a>
	 */
	public Sale cancelSale(String paymentId) throws IOException, CieloRequestException {
		return cancelSale(paymentId, null);
	}

	/**
	 * Capture a Sale on Cielo by paymentId and specifying the amount and the
	 * serviceTaxAmount
	 *
	 * @param paymentId
	 *            The paymentId to be captured
	 * @param amount
	 *            Amount of the authorization to be captured
	 * @param serviceTaxAmount
	 *            Amount of the authorization should be destined for the service
	 *            charge
	 * @return The Sale with authorization, tid, etc. returned by Cielo.
	 * @throws IOException
	 * @throws CieloRequestException
	 *             if anything gets wrong.
	 * @see <a href=
	 *      "https://developercielo.github.io/Webservice-3.0/english.html#error-codes">Error
	 *      Codes</a>
	 */
	public Sale captureSale(String paymentId, Integer amount, Integer serviceTaxAmount)
			throws IOException, CieloRequestException {
		UpdateSaleRequest updateSaleRequest = new UpdateSaleRequest("capture", merchant, environment);

		updateSaleRequest.setHttpClient(httpClient);
		updateSaleRequest.setAmount(amount);
		updateSaleRequest.setServiceTaxAmount(serviceTaxAmount);

		Sale sale = updateSaleRequest.execute(paymentId);

		return sale;
	}

	/**
	 * Capture a Sale on Cielo by paymentId and specifying the amount
	 *
	 * @param paymentId
	 *            The paymentId to be captured
	 * @param amount
	 *            Amount of the authorization to be captured
	 * @return The Sale with authorization, tid, etc. returned by Cielo.
	 * @throws IOException
	 * @throws CieloRequestException
	 *             if anything gets wrong.
	 * @see <a href=
	 *      "https://developercielo.github.io/Webservice-3.0/english.html#error-codes">Error
	 *      Codes</a>
	 */
	public Sale captureSale(String paymentId, Integer amount) throws IOException, CieloRequestException {
		return captureSale(paymentId, amount, null);
	}

	/**
	 * Capture a Sale on Cielo by paymentId
	 *
	 * @param paymentId
	 *            The paymentId to be captured
	 * @return The Sale with authorization, tid, etc. returned by Cielo.
	 * @throws IOException
	 * @throws CieloRequestException
	 *             if anything gets wrong.
	 * @see <a href=
	 *      "https://developercielo.github.io/Webservice-3.0/english.html#error-codes">Error
	 *      Codes</a>
	 */
	public Sale captureSale(String paymentId) throws IOException, CieloRequestException {
		return captureSale(paymentId, null, null);
	}
}
