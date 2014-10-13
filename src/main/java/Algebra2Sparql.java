import com.hp.hpl.jena.sparql.algebra.Algebra;
import com.hp.hpl.jena.sparql.algebra.Op;
import com.hp.hpl.jena.sparql.algebra.OpAsQuery;

public class Algebra2Sparql {

    public static void main(String[] args) {
        String algebraFileName = Algebra2Sparql.class.getResource("/").getPath() + "a-02.algb";
        
        Op op = Algebra.read(algebraFileName);
        System.out.println(op);
        System.out.println(OpAsQuery.asQuery(op));
        
    }
}
