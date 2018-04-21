package MetodoLiu;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;

import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;

/**
 * Strategy
 */
public class Strategy {

    /**
     * 
     * @return String com código refatorado
     * table 4 - Strategy pattern directed refactoring opportunities identification algorithm
     */
    public String analisador(Leitor l) {
        Method[] metodos = l.metodosDeclarados();
        for (Method metodo : metodos) {

            Parameter[] parametrosMetodo = metodo.getParameters();
            if (parametrosMetodo == null || parametrosMetodo.length == 0) {
                System.out.println(" -- Método:" + metodo.getName() + " -- não tem parametros não é usado strategy");
                continue;
            }

            /** if abaixo se existe if dentro do método else continue */
            /** se o tipo não for interface ele pode ter métodos */
            if (!l.getTipoClasse().equals("interface")) {
                List<Statement> instrucoesMetodo = l.linhasMetodo(metodo.getName().toString());
                int qtdInstrucao = instrucoesMetodo.size();
                if (qtdInstrucao > 0) {
                    /** para cada if faça */
                    for (Statement var : instrucoesMetodo) {
                        if (var instanceof IfStmt) {
                            IfStmt ifStmt = (IfStmt) var;
                            String condicional = l.temParametroNoIf(parametrosMetodo, ifStmt.getCondition().toString());
                            
                            if(!condicional.isEmpty()){
                                return condicional;
                            }else{
                                break;
                            }    
                        }
                    }
                }
            }

        }
        return null;
    }


}