package kissxml;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.ByteArrayOutputStream;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class XmlBuilderTest {

	final static String PREFIX = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";

	final String testA = PREFIX + "<a></a>";
	final String testB = PREFIX + "<a><b></b></a>";
	final String testC = PREFIX + "<a><b>xyz</b></a>";
	final String testD = PREFIX + "<a w=\"t\"><b>xyz</b></a>";

	private ByteArrayOutputStream outputStream;

	@Before
	public void setUp() {
		outputStream = new ByteArrayOutputStream();
	}

	@Test
	public void testA() throws Exception {
		XmlBuilder builder = new XmlBuilder("testA", outputStream);
		builder.flush();

		System.out.println(outputStream.toString());
	}

	@Test
	public void testB() throws Exception {
		XmlBuilder builder = new XmlBuilder("testB", outputStream);
		builder.element("b")
				  .element("xxx")
				     .element("bunny")
				         .attribute("wow", "999");
		builder.element("c")
				   .element("opa")
				       .element("kitty")
				           .data("mimi", "Me");
		
		ElementBuilder aaaa = builder.element("AAAA");
		aaaa.data("x", "999");
		aaaa.data("y", "-500");
		aaaa.data("z", "300");

		builder.flush();

		System.out.println(outputStream.toString());
	}

}
