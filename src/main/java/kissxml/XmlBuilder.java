package kissxml;

import java.io.IOException;
import java.io.OutputStream;

import javax.xml.stream.FactoryConfigurationError;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class XmlBuilder implements ElementBuilder {

	private final XMLStreamWriter xmlWriter;
	private final OutputStream outputStream;
	private ElementBuilder active;

	public XmlBuilder(String name, OutputStream outputStream) throws XmlBuilderException, XMLStreamException {
		this.outputStream = outputStream;
		this.xmlWriter = createXmlWriter(outputStream);

		xmlWriter.writeStartDocument();
		xmlWriter.writeStartElement(name);
	}

	public void attribute(String attrName, Object attrValue) throws XMLStreamException {
		backToDaddy();

		xmlWriter.writeAttribute(attrName, attrValue != null ? attrValue.toString() : "");
	}

	public void data(String dataName, Object dataValue) throws XMLStreamException {
		backToDaddy();

		xmlWriter.writeStartElement(dataName);
		xmlWriter.writeCharacters(dataValue != null ? dataValue.toString() : "");
		xmlWriter.writeEndElement();
	}

	public ElementBuilder element(String name) throws XMLStreamException {
		backToDaddy();

		active = new ElementBuilderImpl(name, xmlWriter);
		return active;
	}

	protected void backToDaddy() throws XMLStreamException {
		// Close all active elements
		while (active != null) {
			xmlWriter.writeEndElement();

			ElementBuilderImpl impl = (ElementBuilderImpl) active;
			active = impl.getActive();
		}
	}

	public void flush() throws XMLStreamException, IOException {
		xmlWriter.writeEndDocument();
		xmlWriter.flush();

		outputStream.flush();
		outputStream.close();
	}

	protected XMLStreamWriter createXmlWriter(OutputStream outputStream) throws XMLStreamException {
		try {
			return XMLOutputFactory.newFactory().createXMLStreamWriter(outputStream);
		} catch (FactoryConfigurationError e) {
			throw new XmlBuilderException("Factory configuration error", e);
		}
	}

	private static class ElementBuilderImpl implements ElementBuilder {

		private final XMLStreamWriter innerXmllWriter;
		private ElementBuilder active;

		public ElementBuilderImpl(String name, XMLStreamWriter xmlWriter)
				throws XMLStreamException {
			this.innerXmllWriter = xmlWriter;

			innerXmllWriter.writeStartElement(name);
		}

		@Override
		public void attribute(String attrName, Object attrValue) throws XMLStreamException {
			backToDaddy();
			innerXmllWriter.writeAttribute(attrName, attrValue != null ? attrValue.toString() : "");
		}

		@Override
		public void data(String dataName, Object dataValue) throws XMLStreamException {
			backToDaddy();

			innerXmllWriter.writeStartElement(dataName);
			innerXmllWriter.writeCharacters(dataValue != null ? dataValue.toString() : "");
			innerXmllWriter.writeEndElement();
		}

		@Override
		public ElementBuilder element(String name) throws XMLStreamException {
			backToDaddy();

			active = new ElementBuilderImpl(name, innerXmllWriter);
			return active;
		}

		protected void backToDaddy() throws XMLStreamException {
			// Close all active elements
			while (active != null) {
				innerXmllWriter.writeEndElement();
				ElementBuilderImpl impl = (ElementBuilderImpl) active;
				active = impl.getActive();
			}
		}

		public ElementBuilder getActive() {
			return active;
		}

	}

}
