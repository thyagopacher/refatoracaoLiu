package MetodoLiu;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.TypeVariable;

import org.apache.bcel.classfile.Code;
import org.apache.bcel.classfile.LocalVariable;
import org.apache.bcel.generic.Type;
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

    private void metodo1() {
        boolean res2 = false;
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
                System.out.println(
                        " - Nome: " + campo.getName() + " - modificador:" + Modifier.toString(campo.getModifiers()));
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
                            System.out.print(
                                    "Nome: " + parametroMetodo.getName() + " - tipo: " + parametroMetodo.getType());
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
                System.out.println(" - Assinatura do método: " + var.toString());
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
                org.apache.bcel.classfile.Method metodoBcel = l.metodoBcel(var);
                org.apache.bcel.classfile.Attribute[] atributosBcel = metodoBcel.getCode().getAttributes();
                int qtdAtributosBcel = atributosBcel.length;
                if (qtdAtributosBcel > 0) {
                    System.out.println(" == Atributos Bcel == ");
                    for (org.apache.bcel.classfile.Attribute attr : atributosBcel) {
                        System.out.println(" - Nome: " + attr);
                    }
                    System.out.println(" ================ \n");
                }

                org.apache.bcel.classfile.LocalVariableTable variaveisLocal2 = metodoBcel.getLocalVariableTable();
                int qtdVariavelLocal = variaveisLocal2.getLength();
                if (qtdVariavelLocal > 0) {
                    System.out.println(" == Variáveis locais ==");
                    for (int j = 0; j < qtdVariavelLocal; j++) {
                        final LocalVariable localVariable = variaveisLocal2.getLocalVariable(j);
                        if (localVariable != null && !localVariable.getName().equals("this")) {
                            Type tp = Type.getType(localVariable.getSignature());
                            System.out.println(" - Nome: " + localVariable.getName() + " - Assinatura: " + localVariable.toString() + " - tipo: "+ tp.toString());
                        }
                    }
                    System.out.println(" ======================= \n");
                }
                System.out.println("\n ========================");
            }
        } catch (Exception ex) {
            System.out.println(" - Erro: " + ex.getMessage());
        }
    }
}
