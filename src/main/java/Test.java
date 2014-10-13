import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Node_Literal;
import com.hp.hpl.jena.graph.impl.LiteralLabelFactory;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;


public class Test {

	public static void main(String[] args) {
		Model model = ModelFactory.createDefaultModel();
		Literal literal = model.createTypedLiteral("2", XSDDatatype.XSDinteger);
		System.out.println("Lexical Form: " + literal.getLexicalForm());
		System.out.println(literal);

		literal = model.createLiteral("aaa");
		System.out.println("Lexical Form: " + literal.getLexicalForm());
		System.out.println(literal.toString());
		
		System.out.println(Node.createLiteral("5", "en", XSDDatatype.XSDinteger));
	}

}
