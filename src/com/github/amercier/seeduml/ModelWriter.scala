package com.github.amercier.seeduml

import scala.collection.JavaConversions.asScalaBuffer

import org.eclipse.emf.common.util.BasicEList
import org.eclipse.emf.common.util.EList
import org.eclipse.emf.ecore.EClass
import org.eclipse.emf.ecore.EPackage

trait ModelWriter {
	
	protected def union(s1: String, s2: String): String = {
		return s1 + "\n" + s2;
	}
	
	protected def writePackage(thePackage: EPackage, writtenPackages: EList[EPackage] = new BasicEList[EPackage]): String = {
		var result = "";
		
		// 1. Write sub-packages (if not already written)
		for(subPackage <- thePackage.getESubpackages) {
			if(writtenPackages.contains(subPackage)) {
				sys.error("Warning: recursive package definition: " + subPackage.getName + " in " + thePackage.getName)
			}
			else {
				writtenPackages.add(subPackage)
				result = union(result, this.writePackage(subPackage, writtenPackages));
			}
		}
		
		// 2. Write classes
		/*
		for(childClass <- thePackage.getEClassifiers.iterator.filter(x => EClass.isInstance(x))) {
			
		}*/
		/*
		for(classifier <- thePackage.getEClassifiers) {
			if(classifier.isInstance(EClass)) {
				this.writeClass(classifier.asInstanceOf(EClass));
			}
		}
		*/
		return result;
	}
	
	protected def writeClass(theClass: EClass): String

}