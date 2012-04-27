package com.github.amercier.seeduml

import java.io.File
import java.io.FilenameFilter

trait ClassDiagramGenerator {
	
	protected def generateClassDiagram(sourceFiles: File): String

	def generateClassDiagram(sourceFiles: Iterable[File]): String = {
		
		var result = ""
		
		sourceFiles.foreach(file => {
			if(file.isDirectory() && !file.isHidden()) {
				println("Entering " + file.getName)
				def filter(directory:File, fileName:String):Boolean = { 
					fileName.toLowerCase.endsWith(".xsd")
				}
				result += generateClassDiagram(file.listFiles(new FilenameFilter {
					override def accept(dir:File, name:String) : Boolean = {
						return name.toLowerCase.endsWith(".xsd");
					}
				})) + "\n"
			}
			else {
				println("Reading " + file.getName);
				result += generateClassDiagram(file) + "\n"
			}
		});
		
		return result
	}
		
}