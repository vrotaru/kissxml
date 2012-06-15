package kissxml.demo;

import java.io.FileOutputStream;

import kissxml.ElementBuilder;
import kissxml.XmlBuilder;

public class XmlBuilderDemo {

	public static void main(String[] args) throws Exception {
		XmlBuilder refunds = new XmlBuilder("refunds", new FileOutputStream("out.xml"));
		refunds.attr("timestamp", "83028392");

		// first refund
		ElementBuilder refund = refunds.element("refund");

		refund.attr("id", 1111);

		ElementBuilder transaction = refund.element("transaction");
		transaction.attr("type", "aaaa");
		transaction.content("803-820832-93289-7832");

		refund.value("amount", 90000.23);
		refund.value("comment", "   Some comment");

		// second refund
		refund = refunds.element("refund");
		refund.attr("id", 2222);

		transaction = refund.element("transaction");
		transaction.attr("type", "zzzz");
		transaction.content("78392-738927-7382");

		refund.value("amount", 4444.12);
		refund.value("comment", "Other comment");

		// third refund
		refund = refunds.element("refund");
		refund.attr("id", 3333);

		transaction = refund.element("transaction");
		transaction.attr("type", "jjjj");
		transaction.content("048304-849384-8492");

		refund.value("amount", 30238.773);
		refund.value("comment", "Quite another comment");

		// other attributes
		refunds.value("zfa", "8392-8392923-7839293-3923");
		refunds.value("quux", "939920030238");

		refunds.flush();
	}

}
