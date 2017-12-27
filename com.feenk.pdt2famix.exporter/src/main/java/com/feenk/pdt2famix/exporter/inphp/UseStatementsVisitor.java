package com.feenk.pdt2famix.exporter.inphp;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.php.core.ast.nodes.ASTNode;
import org.eclipse.php.core.ast.nodes.NamespaceDeclaration;
import org.eclipse.php.core.ast.nodes.UseStatement;
import org.eclipse.php.core.ast.nodes.UseStatementPart;
import org.eclipse.php.core.ast.visitor.AbstractVisitor;

public class UseStatementsVisitor extends AbstractVisitor {
	
	private Map<String, String> usedStatementParts = new HashMap<String, String>();
	
	public Map<String, String> usedStatementParts() {
		return usedStatementParts;
	}
	public void resetUsedStatementParts() {
		usedStatementParts = new HashMap<String, String>();
	}
	public void addUsedStatementPart(String alias, String referencedName) {
		usedStatementParts.put(alias, referencedName);
	}
	
	public static Map<String, String> extractUseStatements(ASTNode targetNode) {
		ASTNode container = getNamespaceDeclaration(targetNode);
		if (container==null) {
			container = targetNode.getProgramRoot();
		}
		
		UseStatementsVisitor visitor = new UseStatementsVisitor();
		visitor = new UseStatementsVisitor();
		container.accept(visitor);
		
		return visitor.usedStatementParts();
	}
	
	private static NamespaceDeclaration getNamespaceDeclaration(final ASTNode node) {
		ASTNode currentNode = node;

		while (currentNode != null) {
			if (currentNode.getType() == ASTNode.NAMESPACE) {
				return (NamespaceDeclaration) currentNode;
			}
			currentNode = currentNode.getParent();
		}

		return null;
	}
	
	/**
	 * We override this as we need the used entities to propertly resolve annotations.
	 */
	@Override
	public boolean visit(UseStatement useStatement) {
		if (useStatement.getStatementType() != UseStatement.T_NONE) {
			return false;
		}
		for (UseStatementPart part : useStatement.parts()) {
			if (part.getName() == null) {
				continue;
			}
			String fullName = part.getName().getName();
			if (part.getAlias() != null) {
				addUsedStatementPart(part.getAlias().getName().toLowerCase(), fullName);
			} else {
				addUsedStatementPart(part.getName().segments().get(part.getName().segments().size()-1).getName().toLowerCase(), fullName);
			}
		}
		return false;
	}

}
