package kissxml.demo;

import java.io.InputStream;

import kissxml.XmlReader;
import kissxml.XmlReader.PrefixView;

public class XmlReaderDemo {
	public void run() throws Exception {
		InputStream inputStream = getClass().getResourceAsStream("refunds.xml");
		XmlReader reader = new XmlReader();
		reader.read(inputStream);

		String refunds = reader.get("/refunds");
		String refund = reader.get("/refunds/refund");

		System.out.println("  refunds/refund: " + refunds + "/" + refund);
		System.out.println();

		int refundCount = reader.count("/refunds/refund");

		System.out.println("         refund#: " + refundCount);
		System.out.println();

		String refundId = reader.get("/refunds/refund,1@id");
		String transaction = reader.get("/refunds/refund,1/transaction");
		String type = reader.get("/refunds/refund,1/transaction@type");
		String amount = reader.get("/refunds/refund,1/amount");
		String comment = reader.get("/refunds/refund,1/comment");

		System.out.println("       refund.id: " + refundId);
		System.out.println("     transaction: " + transaction);
		System.out.println("transaction.type: " + type);
		System.out.println("          amount: " + amount);
		System.out.println("         comment: " + comment);
		System.out.println();

		refundId = reader.get("/refunds/refund,2@id");
		transaction = reader.get("/refunds/refund,2/transaction");
		type = reader.get("/refunds/refund,2/transaction@type");
		amount = reader.get("/refunds/refund,2/amount");
		comment = reader.get("/refunds/refund,2/comment");

		System.out.println("       refund.id: " + refundId);
		System.out.println("     transaction: " + transaction);
		System.out.println("transaction.type: " + type);
		System.out.println("          amount: " + amount);
		System.out.println("         comment: " + comment);
		System.out.println();

		// Testing the prefix view
		PrefixView refund3 = reader.createPrefixView("/refunds/refund,3");
		System.out.println("       refund.id: " + refund3.get("@id"));
		System.out.println("     transaction: " + refund3.get("/transaction"));
		System.out.println("transaction.type: " + refund3.get("/transaction@type"));
		System.out.println("          amount: " + refund3.get("/amount"));
		System.out.println("         comment: " + refund3.get("/comment"));
		System.out.println();

		String zfa = reader.get("/refunds/zfa");
		String quux = reader.get("/refunds/quux");
		String timestamp = reader.get("/refunds@timestamp");
		System.out.println("             zfa: " + zfa);
		System.out.println("            quux: " + quux);
		System.out.println("       timestamp: " + timestamp);
	}

	public static void main(String[] args) throws Exception {
		new XmlReaderDemo().run();
	}
}
