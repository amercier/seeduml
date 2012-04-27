package com.github.amercier.seeduml

import java.io.File

import scala.collection.mutable.HashMap
import scala.collection.mutable.Map

import org.xml.sax.helpers.DefaultHandler
import org.xml.sax.Attributes

import javax.xml.parsers.SAXParser
import javax.xml.parsers.SAXParserFactory

class XmlSchemaClassDiagramGenerator extends ClassDiagramGenerator {

	override def generateClassDiagram(sourceFile: File): String = {
		
		var result = ""

		var types : Map[String, String] = new HashMap
		var isInsideSequence : Boolean = false

		var factory  : SAXParserFactory = SAXParserFactory.newInstance()
		var saxParser: SAXParser        = factory.newSAXParser()
		var handler  : DefaultHandler   = new DefaultHandler() {
			
			override def startElement(uri:String, localName:String, qName:String, attributes:Attributes) {
				
				var NamespaceSeparator = """^(.*:)?(.*)$""".r
				val NamespaceSeparator(ns:String, name:String) = qName
				
				if(name == "element") {
					if(isInsideSequence) {
						result += "    - " + attributes.getValue("name") + ": " + attributes.getValue("type") + "\n"
					} else {
						types += attributes.getValue("type") -> attributes.getValue("name")
					}
				} else if (name == "complexType") {
					result += "   class " + types(attributes.getValue("name")) + " {\n"
				} else if (name == "sequence") {
					isInsideSequence = true
				}
			}
			
			override def endElement(uri:String, localName:String, qName:String) {
				
				var NamespaceSeparator = """^(.*:)?(.*)$""".r
				val NamespaceSeparator(ns:String, name:String) = qName
				
				if(name == "complexType") {
					result += "   }\n"
				}
				else if(name == "sequence") {
					isInsideSequence = false
				}
			}
			/*
			def characters(ch:Char[], Start:Int, length:Int) {
				System.out.println("    - " + new String(ch, start, length));
			}
			*/
		}
		
		result += "==== " + sourceFile.getName() + " ====\n";
		try {
			saxParser.parse(sourceFile, handler);
		}
		catch {
			case e:Exception => result += "Error: " + e.getMessage() + "\n";
		}
		
		result += "================================\n";
		
		return result;
	}

}