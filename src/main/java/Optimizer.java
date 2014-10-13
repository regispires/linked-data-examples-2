import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.query.ARQ;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.sparql.algebra.Algebra;
import com.hp.hpl.jena.sparql.algebra.Op;
import com.hp.hpl.jena.sparql.algebra.OpAsQuery;
import com.hp.hpl.jena.sparql.core.Substitute;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.engine.QueryIterator;
import com.hp.hpl.jena.sparql.engine.binding.Binding;
import com.hp.hpl.jena.sparql.engine.binding.BindingFactory;
import com.hp.hpl.jena.sparql.mgt.Explain;

public class Optimizer {
    public static String readFile(String path) throws IOException {
        FileInputStream stream = new FileInputStream(new File(path));
        try {
            FileChannel fc = stream.getChannel();
            MappedByteBuffer bb = fc.map(FileChannel.MapMode.READ_ONLY, 0, fc.size());
            return Charset.defaultCharset().decode(bb).toString();
        }
        finally {
            stream.close();
        }
    }

    public static void main(String[] args) {
        try {
            ARQ.setExecutionLogging(Explain.InfoLevel.ALL) ;
            //String modelFileNameURL = "file:" + Optimizer.class.getResource("/").getPath() + "blank_node.ttl";
            Model model = ModelFactory.createDefaultModel();
            //model.read(modelFileNameURL, "TTL");
            
            String sparqlFileName = Optimizer.class.getResource("/").getPath() + "q-drugs-03.spql";
            String str = readFile(sparqlFileName);
            System.out.println("Query:");
            System.out.println(str);
            Query query = QueryFactory.create(str.toString());
            Op op = Algebra.compile(query);
            System.out.println("Algebra:");
            System.out.println(op);

            System.out.println("Algebra (binding - substitute):");
            Binding binding = BindingFactory.binding(Var.alloc("?o"), Node.createLiteral("aaa"));
            Substitute.substitute(op, binding);
            System.out.println(op);

            op = Algebra.optimize(op);
            System.out.println("Optimized Algebra:");
            System.out.println(op);
            System.out.println("As SPARQL Query:");
            System.out.println(OpAsQuery.asQuery(op));
            QueryIterator it = Algebra.exec(op, model);
            while (it.hasNext()) {
                Binding b = it.next();
                System.out.println(b);
            }
            it.close() ;        

//            QueryExecution qExec = QueryExecutionFactory.create(str) ;
//            qExec.getContext().set(ARQ.symLogExec, Explain.InfoLevel.ALL) ;
//            ResultSet rs = qExec.execSelect();
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
