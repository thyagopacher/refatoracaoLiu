package MetodoLiu;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.nio.file.DirectoryStream.Filter;
import java.util.List;
import java.util.stream.Stream;

import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;

/**
 * Factory
 */
public class Factory {

    /**
     * 
     * @return String com código refatorado
     * table 2 - Factory method patterndirected refactoring opportunities identification algorithm
     */
    public void analisador(Leitor l) {
        Method[] metodos = l.metodosDeclarados();
        for (Method metodo : metodos) {
            String modificador = Modifier.toString(metodo.getModifiers());
            if (modificador.equals("void")) {
                System.out.println(" -- Método:" + metodo.getName() + " -- é void não é usado Factory");
                continue;
            }

            Parameter[] parametrosMetodo = metodo.getParameters();
            if (parametrosMetodo == null || parametrosMetodo.length == 0) {
                System.out.println(" -- Método:" + metodo.getName() + " -- não tem parametros não é usado Factory");
                continue;
            }

            boolean flag = false;

            /** if abaixo se existe if dentro do método */
            List<String> instrucoesIf = l.retornaIf(metodo);
            if (instrucoesIf != null && instrucoesIf.size() > 0) {
                List<Statement> instrucoes = l.linhasMetodo(metodo.getName());
                int qtdInstrucoes = instrucoes.size();
                for (int i = 0; i < qtdInstrucoes; i++) {
                    if (instrucoes.get(i) instanceof IfStmt) {
                        flag = false;
                        IfStmt ifStmt = (IfStmt) instrucoes.get(i);
                       
                        System.out.println(" -- instruções IF no método: " + metodo.getName() + " - " + ifStmt.getCondition());
                    }
                }
            }
        }
    }

}