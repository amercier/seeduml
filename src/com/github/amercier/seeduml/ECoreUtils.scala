package com.github.amercier.seeduml

import org.eclipse.emf.common.util.EList

object ECoreUtils {
	
	def eListToArray[T: ClassManifest](list: EList[T]): Array[T] = {
		
		var newArray = new Array[T](list.size)
		
		for(i <- 1 to list.size) {
			newArray(i) = list.get(i)
		}
		
		return newArray;
	}

}
