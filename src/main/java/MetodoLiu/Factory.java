package MetodoLiu;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.List;

/**
 * Factory
 */
public class Factory {


    /**
     * 
     * @return String com código refatorado
     * table 2 - Factory method patterndirected refactoring opportunities identification algorithm
     */
    public void analisador(Leitor l){
        Method[] metodos = l.metodosDeclarados();
        for (Method metodo : metodos) {
            String modificador = Modifier.toString(metodo.getModifiers());
            if(modificador.equals("void")){
                System.out.println(" -- Método:" + metodo.getName() + " -- é void não é usado Factory");
                continue;
            }

            Parameter[] parametrosMetodo = metodo.getParameters();
            if(parametrosMetodo == null || parametrosMetodo.length == 0){
                System.out.println(" -- Método:" + metodo.getName() + " -- não tem parametros não é usado Factory");
                continue;
            }

            boolean flag = false;

            /** if abaixo se existe if dentro do método */
            List<String> instrucoesIf = l.retornaIf(metodo);
            if(instrucoesIf != null && instrucoesIf.size() > 0){
                for (String instrucaoIf : instrucoesIf) {
                    flag = false;
                    System.out.println(" -- instruções IF no método: " + metodo.getName() + " - " + instrucaoIf);
                }
            }
        }
    }
    
}