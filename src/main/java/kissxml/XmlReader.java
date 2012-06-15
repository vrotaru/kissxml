package kissxml;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;

public class XmlReader {
	//
	// Can be anything, actually. The value chosen has a nice mnemonic ring to
	// it. At least, I think so.
	//
	public static final String MARKER = "<>";

	private Map<String, String> pathMap;
	private Map<String, Integer> countMap;

	public void read(InputStream inputStream) throws XmlReaderException, XMLStreamException {
		pathMap = new HashMap<String, String>();
		countMap = new HashMap<String, Integer>();

		try {
			XMLStreamReader reader = createStreamReader(inputStream);

			while (reader.hasNext()) {
				int event = reader.next();
				switch (event) {
				case XMLEvent.START_ELEMENT:
					String tag = reader.getLocalName();

					// Would be always one for well formed XML documents,
					// so here it is called mostly for its side effects
					int innerCount = getInnerCount("", tag);					
					String xpath = createExtendedPath("", tag, innerCount);
					
					readAttirbutes(xpath, reader);
					readInnerElements(xpath, reader);
					break;
				case XMLEvent.END_ELEMENT:
					// Should never happen.
					throw new XmlReaderException("Mismatched tag");

				default:
					break;
				}
			}
		} catch (FactoryConfigurationError e) {
			throw new XmlReaderException("Factory configuration error", e);
		}
	}

	public String get(String path) {
		path = normalize(path);

		return pathMap.get(path);
	}

	public int count(String path) {
		path = normalize(path);

		Integer pathCount = countMap.get(path);
		if (pathCount == null) {
			return 0;
		}
		return pathCount.intValue();
	}
	
	public PrefixView createPrefixView(String prefix) {
		String value = get(prefix);
		if (MARKER.equals(value)) {
			return new PrefixView(this, prefix);			
		}
		else { 
			throw new XmlReaderException("Not a valid prefix");
		}
	}

	/*
	 * For testing
	 */
	protected XMLStreamReader createStreamReader(InputStream inputStream) throws XMLStreamException,
			FactoryConfigurationError {
		XMLStreamReader streamReader = XMLInputFactory.newFactory().createXMLStreamReader(inputStream);

		return streamReader;
	}

	protected void readAttirbutes(String path, XMLStreamReader reader) {
		int attributeCount = reader.getAttributeCount();
		for (int i = 0; i < attributeCount; i++) {
			String attributeName = reader.getAttributeName(i).getLocalPart();
			String value = reader.getAttributeValue(i);

			String attributePath = path + "@" + attributeName;
			putInternal(attributePath, value);
		}
	}

	protected void readInnerElements(String path, XMLStreamReader reader)
			throws XMLStreamException {

		while (reader.hasNext()) {
			int event = reader.next();
			switch (event) {
			case XMLEvent.START_ELEMENT: {
				String innerTag = reader.getLocalName();
				putInternal(path, MARKER);

				int innerCount = getInnerCount(path, innerTag);

				String xpath = createExtendedPath(path, innerTag, innerCount);
				readAttirbutes(xpath,  reader);
				readInnerElements(xpath,  reader);
				break;
			}
			case XMLEvent.END_ELEMENT: {
				return;
			}
			case XMLEvent.CHARACTERS:
			case XMLEvent.CDATA:
				String value = reader.getText();
				String oldValue = getInternal(path);
				if (!MARKER.equals(oldValue)) {
					putInternal(path, value);
				}
				break;
			default:
				break;
			}
		}
		// Should never happen
		throw new XmlReaderException("Mismatched Tag");
	}

	private String createExtendedPath(String path, String tag, int count) {		
		if (count == 1) {
			return path + "/"  + tag;
		}
		else {
			return path + "/" + tag + "," + count;
		}
	}

	private Integer getInnerCount(String path, String innerTag) {
		String xpath = path + "/" + innerTag;

		Integer innerCount = countMap.get(xpath);
		if (innerCount != null) {
			innerCount = innerCount + 1;
		} else {
			innerCount = 1;
		}
		countMap.put(xpath, innerCount);

		return innerCount;
	}

	private void putInternal(String path, String value) {
		pathMap.put(path, value);
	}
	private String getInternal(String path) {
		return pathMap.get(path);
	}
	
	private String normalize(String path) {
		return path.replace(",1", "");
	}
	
	//
	// XmlView
	//
	public static class PrefixView {
		private XmlReader reader;
		private String prefix;
		
		private PrefixView(XmlReader reader, String prefix) {
			this.reader = reader;
			this.prefix = prefix;
		}
		
		public String get(String path) {
			return reader.get(prefix + path);
		}
		
		public int count(String path) {
			return reader.count(prefix + path);
		}
	}
}
