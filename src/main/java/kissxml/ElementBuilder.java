package kissxml;

import javax.xml.stream.XMLStreamException;

public interface ElementBuilder {

	void attribute (String attrName, Object attrValue) throws XMLStreamException;
	void data(String dataName, Object dataValue) throws XMLStreamException;
	ElementBuilder element(String name) throws XMLStreamException;
}
