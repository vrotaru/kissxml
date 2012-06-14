package kissxml;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.easymock.EasyMock.reset;
import static org.easymock.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.InputStream;

import javax.xml.namespace.QName;
import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;

import kissxml.XmlReader.PrefixView;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class XmlReaderTest {
	
	private XMLStreamReader streamReader; // Mock
	private XmlReader xmlReader;

	@Before
	public void setUp() throws Exception {
		streamReader = EasyMock.createMock(XMLStreamReader.class);

		xmlReader = new XmlReader() {
			protected XMLStreamReader createStreamReader(java.io.InputStream inputStream)
					throws XMLStreamException, FactoryConfigurationError {
				return streamReader;
			};
		};
	}

	@After
	public void tearDown() throws Exception {		
	}

	@Test(expected = XmlReaderException.class)
	public void testRead_factory_configuration_error() throws Exception {
		XmlReader reader = new XmlReader () {
			@Override
			protected XMLStreamReader createStreamReader(InputStream inputStream) throws XMLStreamException,
					FactoryConfigurationError {
				throw new FactoryConfigurationError();
			}
		};
		
		reader.read(null);
	}
	
	@Test(expected = XMLStreamException.class)
	public void testRead_xml_stream_exception() throws Exception {
		expect(streamReader.hasNext()).andReturn(true);
		expect(streamReader.next()).andThrow(new XMLStreamException());
		
		replay(streamReader);
		xmlReader.read(null);
		verify(streamReader);
	}
	
	@Test()
	public void testRead() throws Exception {
		expect(streamReader.hasNext()).andReturn(true);
		expect(streamReader.next()).andReturn(XMLEvent.START_ELEMENT);
		expect(streamReader.getLocalName()).andReturn("baz");
		
		expect(streamReader.getAttributeCount()).andReturn(0);
		
		expect(streamReader.hasNext()).andReturn(true);
		expect(streamReader.next()).andReturn(XMLEvent.START_ELEMENT);
		expect(streamReader.getLocalName()).andReturn("quux");		
		
		expect(streamReader.getAttributeCount()).andReturn(2);
		expect(streamReader.getAttributeName(0)).andReturn(new QName("superhero"));
		expect(streamReader.getAttributeValue(0)).andReturn("Captain Debug");
		expect(streamReader.getAttributeName(1)).andReturn(new QName("villain"));
		expect(streamReader.getAttributeValue(1)).andReturn("HeisenBug");
		
		expect(streamReader.hasNext()).andReturn(true);
		expect(streamReader.next()).andReturn(XMLEvent.CHARACTERS);
		expect(streamReader.getText()).andReturn("Hello");
		
		expect(streamReader.hasNext()).andReturn(true);
		expect(streamReader.next()).andReturn(XMLEvent.END_ELEMENT);
		
		expect(streamReader.hasNext()).andReturn(true);
		expect(streamReader.next()).andReturn(XMLEvent.END_ELEMENT);
		
		expect(streamReader.hasNext()).andReturn(false);
		
		replay(streamReader);		
		xmlReader.read(null);		
		verify(streamReader);
	}

	@Test
	public void testGet() throws Exception{
		testRead();
		
		assertEquals("<>", xmlReader.get("/baz"));
		
		assertEquals("Hello", xmlReader.get("/baz/quux"));
		assertEquals("Hello", xmlReader.get("/baz,1/quux"));
		assertEquals("Hello", xmlReader.get("/baz,1/quux,1"));
		assertEquals("Hello", xmlReader.get("/baz/quux,1"));
		
		assertNull(xmlReader.get("/foo"));
		
		// Attributes
		assertEquals("Captain Debug", xmlReader.get("/baz/quux@superhero"));		
		assertEquals("HeisenBug", xmlReader.get("/baz/quux@villain"));
	}

	@Test
	public void testCount() throws Exception {
		testRead();
		
		assertEquals(1, xmlReader.count("/baz"));
		assertEquals(1, xmlReader.count("/baz,1"));
		
		assertEquals(1, xmlReader.count("/baz/quux"));
		assertEquals(1, xmlReader.count("/baz/quux,1"));
	}
	
	@Test(expected = XmlReaderException.class)
	public void testCreatePrefixView_fails() throws Exception {
		testRead();
		
		xmlReader.createPrefixView("/foo");
	}
	
	@Test
	public void testCreatePrefixView() throws Exception {
		testRead();
		
		PrefixView prefixView = xmlReader.createPrefixView("/baz");
		
		assertEquals("Hello", prefixView.get("/quux"));
		assertEquals("Hello", prefixView.get("/quux,1"));
		
		assertNull(prefixView.get("/foo"));
		
		// Attributes
		assertEquals("Captain Debug", prefixView.get("/quux@superhero"));
		assertEquals("Captain Debug", prefixView.get("/quux,1@superhero"));
		
		assertEquals("HeisenBug", prefixView.get("/quux@villain"));		
	}

	@Test
	public void testReadAttirbutes() throws Exception {
		testRead();
		
		reset(streamReader);
		expect(streamReader.getAttributeCount()).andReturn(2);
		expect(streamReader.getAttributeName(0)).andReturn(new QName("yep"));
		expect(streamReader.getAttributeValue(0)).andReturn("Hello");
		expect(streamReader.getAttributeName(1)).andReturn(new QName("nope"));
		expect(streamReader.getAttributeValue(1)).andReturn("Bye");
		
		replay(streamReader);
		xmlReader.readAttirbutes("/a/b,88/c,66/xyz", streamReader);
		verify(streamReader);
		
		assertEquals("Hello", xmlReader.get("/a/b,88/c,66/xyz@yep"));
		assertEquals("Bye", xmlReader.get("/a/b,88/c,66/xyz@nope"));
	}

	@Test
	public void testReadInnerElements() throws  Exception {
		testRead();
		
		reset(streamReader);
		
		expect(streamReader.hasNext()).andReturn(true);
		expect(streamReader.next()).andReturn(XMLEvent.START_ELEMENT);
		expect(streamReader.getLocalName()).andReturn("t");
		
		expect(streamReader.getAttributeCount()).andReturn(0);

		expect(streamReader.hasNext()).andReturn(true);
		expect(streamReader.next()).andReturn(XMLEvent.CHARACTERS);
		expect(streamReader.getText()).andReturn("elem1");
		
		expect(streamReader.hasNext()).andReturn(true);
		expect(streamReader.next()).andReturn(XMLEvent.END_ELEMENT);
		
		expect(streamReader.hasNext()).andReturn(true);
		expect(streamReader.next()).andReturn(XMLEvent.END_ELEMENT);
		
		
		expect(streamReader.hasNext()).andReturn(true);
		expect(streamReader.next()).andReturn(XMLEvent.START_ELEMENT);
		expect(streamReader.getLocalName()).andReturn("t");
		
		expect(streamReader.getAttributeCount()).andReturn(0);

		expect(streamReader.hasNext()).andReturn(true);
		expect(streamReader.next()).andReturn(XMLEvent.CHARACTERS);
		expect(streamReader.getText()).andReturn("elem2");		
		
		expect(streamReader.hasNext()).andReturn(true);
		expect(streamReader.next()).andReturn(XMLEvent.END_ELEMENT);
		
		expect(streamReader.hasNext()).andReturn(true);
		expect(streamReader.next()).andReturn(XMLEvent.END_ELEMENT);
		
		replay(streamReader);
		xmlReader.readInnerElements("/boo/boo/u", streamReader);
		xmlReader.readInnerElements("/boo/boo/u", streamReader);
		verify(streamReader);
		
		assertEquals("elem1", xmlReader.get("/boo/boo/u/t,1"));
		assertEquals("elem2", xmlReader.get("/boo/boo/u/t,2"));		
	}

}
