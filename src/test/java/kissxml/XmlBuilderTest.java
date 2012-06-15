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

	final static String PREFIX = "<?xml version=\"1.0\" ?>";
	
	private ByteArrayOutputStream outputStream;

	@Before
	public void setUp() {
		outputStream = new ByteArrayOutputStream();
	}

	@Test
	public void testA() throws Exception {
		XmlBuilder builder = new XmlBuilder("testA", outputStream);
		builder.flush();

		assertEquals(PREFIX + "<testA></testA>", outputStream.toString());
	}

	@Test
	public void testB() throws Exception {
		XmlBuilder builder = new XmlBuilder("testB", outputStream);
		
		builder.attr("opa", "better later");		
		builder.element("b")
				  .element("xxx")
				     .element("bunny")
				         .attr("wow", "999");
		builder.element("c")
				   .element("opa")
				       .element("kitty")
				           .value("mimi", "Me");
		
		ElementBuilder aaaa = builder.element("AAAA");
		aaaa.value("x", "999");
		aaaa.value("y", "-500");
		aaaa.value("z", "300");
		
		builder.value("France", "Cooking");
		builder.value("Germany", "Football");

		builder.flush();

		String output = outputStream.toString();
		System.out.println(output);
	}
	
	@Test(expected = XMLStreamException.class)
	public void testC() throws Exception {
		XmlBuilder testC = new XmlBuilder("testC", outputStream);
		
		testC.value("color", "mauve");
		testC.attr("mood", 999);
		
		testC.flush();
		System.out.println(outputStream.toString());
	}

}
