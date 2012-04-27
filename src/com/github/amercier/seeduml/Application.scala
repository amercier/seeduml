/**
 * 
 */
package com.github.amercier.seeduml

import java.io.File

import scala.collection.mutable.Set
import scala.collection.mutable.HashSet

/**
 * @author Alexandre Mercier
 */
object Application extends App {
	
	println("SeedUML starting");
	
	new File("F:\\Documents\\seeduml\\tests\\xsd\\resources\\vcloud\\1.5");
	
	// Add files
	var files : Set[File] = new HashSet;
	args.foreach(arg => files += new File(arg));
	
	// Generate diagram source
	try {
		var generator = new XmlSchemaClassDiagramGenerator();
		println(generator.generateClassDiagram(files));
	}
	catch {
		case e: Exception => println(e.getMessage());
	}
}