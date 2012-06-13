package kissxml;

public class XmlBuilder {

	public static final String XML_VERSION_1_0_ENCODING_UTF_8 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
	private StringBuilder builder;
	private String name;

	private boolean innerElements = false;

	/**
	 * @param name
	 */
	public XmlBuilder(String name) {
		this(name, new StringBuilder());
	}
	
	private XmlBuilder(String name, StringBuilder builder) {
		this.name = name;
		this.builder = builder;
		
		initialize();
	}

	/**
	 * @param attributeName
	 * @param attributeValue
	 * @throws XmlBuilderException
	 */
	public void attribute(String attributeName, String attributeValue) throws XmlBuilderException {
		if (innerElements) {
			throw new XmlBuilderException("Cannot add attributes when the starting tag is closed");
		}
		builder.append(" ").append(attributeName).append("=\"").append(attributeValue).append("\"");
	}

	/**
	 * @param elementName
	 * @param value
	 */
	public void value(String elementName, Object value) {
		if (!innerElements) {
			innerElements = true;
			builder.append(">");
		}

		if (value != null) {
			builder.append("<").append(elementName).append(">").append(value).append("</").append(elementName)
					.append(">");
		} else {
			builder.append("<").append(elementName).append(" />");

		}
	}

	/**
	 * @param elementName
	 * @return
	 */
	public XmlBuilder element(String elementName) {
		if (!innerElements) {
			builder.append(">");
		}
		innerElements = true;
		
		builder.append("<").append(elementName);

		return new XmlBuilder(elementName, builder);
	}

	/**
	 * 
	 */
	public void close() {
		if (innerElements) {
			builder.append("</").append(name).append(">");
		} else {
			builder.append(" />");
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return builder.toString();
	}

	private void initialize() {
		builder.append(XML_VERSION_1_0_ENCODING_UTF_8).append("<").append(name);
	}

}
