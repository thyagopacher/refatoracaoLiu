package MetodoLiu;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import java.lang.reflect.Parameter;

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
                continue;
            }

            /** if abaixo se existe if dentro do método else continue */
 
        }
    }
    
}