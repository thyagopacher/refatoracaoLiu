package MetodoLiu;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.TypeVariable;

/**
 * Hello world!
 *
 */
public class App {

    private String valor;

    public App() {

    }

    private App(String valor) {

    }

    private final static boolean metodo1(boolean res) {
        return res;
    }

    public static void main(String[] args) {
        try {
            Leitor l = new Leitor("MetodoLiu.App");
            Class<?> caller = l.classe();
            System.out.println(" == Informações primárias == ");
            System.out.println(" - Nome da classe com pacote: " + caller.getName());
            System.out.println(" - Classe chamada de: " + caller.getSimpleName());
            
            System.out.println("\n == Campos da classe == ");
            Field[] campos = l.campos();
            for (Field campo : campos) {
                System.out.println(" - Nome: " + campo.getName() + " - modificador:" + Modifier.toString(campo.getModifiers()));
            }
            System.out.println(" ==================\n");

            Constructor<?>[] construtores = l.construtoresDeclarados();
            if (construtores != null && construtores.length > 0) {
                System.out.println(" == Analisando construtores ==");
                for (Constructor<?> construtor : construtores) {
                    System.out.println(" - " + Modifier.toString(construtor.getModifiers()));
                    Parameter[] parametrosMetodo = construtor.getParameters();
                    if (parametrosMetodo != null && parametrosMetodo.length > 0) {
                        System.out.println(" - Parametros encontrados no construtor: ");
                        for (Parameter parametroMetodo : parametrosMetodo) {
                            System.out
                                    .print("Nome: " + parametroMetodo.getName() + " - tipo: " + parametroMetodo.getType());
                        }
                        System.out.println();
                    }                   
                }
                
            }

            System.out.println(" ====================== \n");
            TypeVariable<?>[] parametros = l.parametros();
            if (parametros != null && parametros.length > 0) {
                for (TypeVariable<?> t : parametros) {
                    System.out.print(t.getName() + ",");
                }
            } else {
                System.out.println(" == Nenhum parametro encontrado == ");
            }

            Method[] metodos = l.metodosDeclarados();
            for (Method var : metodos) {
                System.out.println(" - Metodo: " + var.getName() + " - tipo de retorno: " + var.getReturnType()
                        + " - qtd de parametros: " + var.getParameterCount());

                System.out.println(" - Modificador: " + Modifier.toString(var.getModifiers()));
                Parameter[] parametrosMetodo = var.getParameters();
                if (parametrosMetodo != null && parametrosMetodo.length > 0) {
                    System.out.println(" - Parametros encontrados no método: ");
                    for (Parameter parametroMetodo : parametrosMetodo) {
                        System.out
                                .print("Nome: " + parametroMetodo.getName() + " - tipo: " + parametroMetodo.getType());
                    }
                    System.out.println();
                }
                System.out.println("\n ========================");
            }
        } catch (Exception ex) {
            System.out.println(" - Erro: " + ex.getMessage());
        }
    }
}
