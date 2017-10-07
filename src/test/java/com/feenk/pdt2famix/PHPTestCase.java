package com.feenk.pdt2famix;

import com.feenk.pdt2famix.Importer;
import com.feenk.pdt2famix.model.famix.Attribute;
import com.feenk.pdt2famix.model.famix.Method;

public class PHPTestCase {

	protected Importer importer;

	protected Method methodNamed(String name) {
		return importer.methods()
				.stream()
	            .filter(m -> m.getName().equals(name))
	            .findAny()
	            .get();
	}

	protected Attribute attributeNamed(String name) {
		return importer.attributes()
				.stream()
				.filter(a -> a.getName().equals(name))
				.findAny()
				.get();
	}

}
