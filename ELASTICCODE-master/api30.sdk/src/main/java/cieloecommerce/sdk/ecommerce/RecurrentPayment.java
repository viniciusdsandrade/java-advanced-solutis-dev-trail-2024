package cieloecommerce.sdk.ecommerce;

import java.io.Serializable;

public class RecurrentPayment implements Serializable {
	
	private static final long serialVersionUID = -7954225571614702432L;
	private boolean authorizeNow;
	private String endDate;
	private Interval interval;

	public RecurrentPayment() {}
	
	public RecurrentPayment(boolean authorizeNow) {
		this.authorizeNow = authorizeNow;
	}

	public boolean isAuthorizeNow() {
		return authorizeNow;
	}

	public RecurrentPayment setAuthorizeNow(boolean authorizeNow) {
		this.authorizeNow = authorizeNow;
		return this;
	}

	public String getEndDate() {
		return endDate;
	}

	public RecurrentPayment setEndDate(String endDate) {
		this.endDate = endDate;
		return this;
	}

	public Interval getInterval() {
		return interval;
	}

	public RecurrentPayment setInterval(Interval interval) {
		this.interval = interval;
		return this;
	}

	public enum Interval {
		Monthly, Bimonthly, Quarterly, SemiAnnual, Annual
	}
}