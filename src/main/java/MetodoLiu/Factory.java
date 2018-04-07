package MetodoLiu;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;

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
                continue;
            }

            Parameter[] parametrosMetodo = metodo.getParameters();
            if(parametrosMetodo == null || parametrosMetodo.length == 0){
                continue;
            }

            boolean flag = false;
            /** if abaixo se existe if dentro do método */
        }
    }
    
}