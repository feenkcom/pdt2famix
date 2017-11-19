package com.feenk.pdt2famix.test.oneSample;

import static org.junit.Assert.assertEquals;

import org.eclipse.dltk.core.ModelException;
import org.junit.Test;

import com.feenk.pdt2famix.model.famix.Attribute;
import com.feenk.pdt2famix.model.famix.Comment;
import com.feenk.pdt2famix.model.famix.IndexedFileAnchor;
import com.feenk.pdt2famix.model.famix.Method;
import com.feenk.pdt2famix.test.support.OneSampleTestCase;

public class ClassWithVariousComments extends OneSampleTestCase {
	
	protected String sample() {
		return removeTestSuffix(ClassWithVariousComments.class.getSimpleName());
	}
	
	@Test
	public void testModelSize() {
		assertEquals(1, importer.namespaces().size());
		assertEquals(1, importer.types().size());
		assertEquals(6, importer.attributes().size());
		assertEquals(1, importer.methods().size());
	}

	@Test
	public void testClassComment() {
		String expectedComment = "/**\n" + 
				" * Simple comment for a class\n" + 
				" */";
		assertEquals(1, type.getComments().size());
		Comment comment =  type.getComments().stream().findFirst().get();
		
		assertEquals(type, comment.getContainer());
		assertEquals(expectedComment, getSourceFromComment(comment));
	}
	
	@Test
	public void testMethodComment() {
		String expectedComment = "/**\n" + 
				"	* Method with a comment\n" + 
				"	*\n" + 
				"	* @return string\n" + 
				"	*/";
		Method method = methodInType(type, "main1");
		assertEquals(1, method.getComments().size());
		
		Comment comment =  method.getComments().stream().findFirst().get();
		
		assertEquals(method, comment.getContainer());
		assertEquals(expectedComment, getSourceFromComment(comment));
	}
	
	@Test
	public void testField1Comment() {
		String expectedComment = "/**\n" + 
				"	* Comments for field1\n" + 
				"	*/";
		Attribute field = attributeInType(type, "$field1");
		assertEquals(1, field.getComments().size());
		
		Comment comment =  field.getComments().stream().findFirst().get();
		
		assertEquals(field, comment.getContainer());
		assertEquals(expectedComment, getSourceFromComment(comment));
	}
	
	@Test
	public void testField2Comment() {
		Attribute field = attributeInType(type, "$field2");
		assertEquals(0, field.getComments().size());
	}
	
	@Test
	public void testField3Comment() {
		String expectedComment = "// Comment field3\n";
		Attribute field = attributeInType(type, "$field3");
		assertEquals(1, field.getComments().size());
		
		Comment comment =  field.getComments().stream().findFirst().get();
		
		assertEquals(field, comment.getContainer());
		assertEquals(expectedComment, getSourceFromComment(comment));
	}
	
	@Test
	public void testField4Comment() {
		String expectedComment = "// Comment field4.3\n";
		Attribute field = attributeInType(type, "$field4");
		assertEquals(1, field.getComments().size());
		
		Comment comment =  field.getComments().stream().findFirst().get();
		
		assertEquals(field, comment.getContainer());
		assertEquals(expectedComment, getSourceFromComment(comment));
	}
	
	@Test
	public void testField5Comment() {
		String expectedComment = "// Comment field5.3\n";
		Attribute field = attributeInType(type, "$field5");
		assertEquals(1, field.getComments().size());
		
		Comment comment =  field.getComments().stream().findFirst().get();
		
		assertEquals(field, comment.getContainer());
		assertEquals(expectedComment, getSourceFromComment(comment));
	}
	
	@Test
	public void testField6Comment() {
		String expectedComment = "// Comment field6.5\n";
		Attribute field = attributeInType(type, "$field6");
		assertEquals(1, field.getComments().size());
		
		Comment comment =  field.getComments().stream().findFirst().get();
		
		assertEquals(field, comment.getContainer());
		assertEquals(expectedComment, getSourceFromComment(comment));
	}
	
	private String getSourceFromComment(Comment comment) {
		if (comment.getSourceAnchor() instanceof IndexedFileAnchor) {
			IndexedFileAnchor  fileAnchor = (IndexedFileAnchor)comment.getSourceAnchor();
			try {
				return importer.getCurrentSourceModel().getSource().substring(fileAnchor.getStartPos().intValue(), fileAnchor.getEndPos().intValue());
			} catch (ModelException e) {
				e.printStackTrace();
			}
		}
		return "";
	}
}
