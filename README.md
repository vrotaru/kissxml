Keep It Simple XML
==================

Mostly a wrapper around the StAX XML APIs bundled with JDK since 1.6 version.

The use case for this project is a web-service which consumes and produces data wrapped in XML. It is not for everyone and will not work 
if XML tags/attributes contains some of the special symbols ('/', '@', ',') used in *Reader.

Yes, I know about xstream, jdom, and many other options, but often enough I do 
not need the whole object but just the values of a few fields.