package kissxml;

import javax.xml.stream.XMLStreamException;

public interface ElementBuilder {

	void attr(String name, Object value) throws XMLStreamException;

	void value(String name, Object value) throws XMLStreamException;

	void content(String text) throws XMLStreamException;

	ElementBuilder element(String name) throws XMLStreamException;
}
