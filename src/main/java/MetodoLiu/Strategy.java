package MetodoLiu;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;

/**
 * Strategy
 */
public class Strategy {

    /**
     * 
     * @return String com código refatorado
     * table 4 - Strategy pattern directed refactoring opportunities identification algorithm
     */
    public void analisador(Leitor l){
        Method[] metodos = l.metodosDeclarados();
        for (Method metodo : metodos) {

            Parameter[] parametrosMetodo = metodo.getParameters();
            if(parametrosMetodo == null || parametrosMetodo.length == 0){
                System.out.println(" -- Método:" + metodo.getName() + " -- não tem parametros não é usado strategy");
                continue;
            }

            /** if abaixo se existe if dentro do método else continue */
            List<String> instrucoesMetodo = l.instrucoesMetodo(metodo);
            int qtdInstrucao = instrucoesMetodo.size();

            List<String> instrucoesIf = l.retornaIf(instrucoesMetodo);
            if(instrucoesIf != null && instrucoesIf.size() > 0){
                for (String instrucaoIf : instrucoesIf) {
                    /** compara se dentro do if foi usado algum parametro da função. */
                    for(int i = 0; i < qtdInstrucao; i++){
                        if(instrucoesMetodo.get(i) == instrucaoIf){
                            System.out.println(instrucaoIf);
                            break;//encontrou o if então acima deve-se comparar o que foi feito dentro do if
                        }
                    }
                }
            }else{
                System.out.println(" -- Método:" + metodo.getName() + " -- não tem instruções IF não é usado strategy");
                continue;
            }
        }
    }
    
}