package MetodoLiu;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.TypeVariable;

import org.apache.bcel.classfile.LocalVariable;
import org.apache.bcel.classfile.Utility;
import org.apache.bcel.generic.Type;
import org.apache.bcel.util.ByteSequence;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.CodeSource;
import java.security.ProtectionDomain;

/**
 * Classe para testar a refatoração com métodos reflexivos
 * @author Thyyago Henrique Pacher
 */
public class App {

    private App instance;
    private String valor;

    public App() {

    }

    private App(String valor) {

    }

    private void metodo1() {
        boolean res = false;
        System.out.println(" -- verificação do método 01 --");
        instance = new App();
        if (res == true) {
            res = false;
        }
        if (instance != null) {
            instance = new App();
        }
        if (instance == null) {
            instance = new App();
        }
    }

    private boolean metodo2(boolean res) {
        return true;
    }

    public void verificarClasseExemplo() {
        try {
            Leitor l = new Leitor("App", "C:/programa-java/MetodoLiu/src/main/java/MetodoLiu/App.java");

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
                System.out.println(" - Comparator: " + metodoBcel.getComparator().toString());
                //pega linha por linha e passa as instruções nelas.
                // l.checkTable(metodoBcel.getLineNumberTable());
                // org.apache.bcel.classfile.Attribute[] atributosBcel = metodoBcel.getCode().getAttributes();
                // int qtdAtributosBcel = atributosBcel.length;
                // if (qtdAtributosBcel > 0) {
                //     System.out.println(" == Atributos Bcel == ");
                //     for (org.apache.bcel.classfile.Attribute attr : atributosBcel) {
                //         System.out.println(" - Nome: " + attr);
                //     }
                //     System.out.println(" ================ \n");
                // }

                System.out.println(" == Avaliando linha por linha do método ==\n");
                byte[] code = metodoBcel.getCode().getCode();
                ByteSequence stream = new ByteSequence(code);
                String info = "";
                while (stream.available() > 0) {
                    info += " -> " + Utility.codeToString(stream, metodoBcel.getConstantPool());
                }
                System.out.println("\n");

                org.apache.bcel.classfile.LocalVariableTable variaveisLocal2 = metodoBcel.getLocalVariableTable();
                int qtdVariavelLocal = variaveisLocal2.getLength();
                if (qtdVariavelLocal > 0) {
                    System.out.println(" == Variáveis locais ==");
                    for (int j = 0; j < qtdVariavelLocal; j++) {
                        final LocalVariable localVariable = variaveisLocal2.getLocalVariable(j);

                        if (localVariable != null && !localVariable.getName().equals("this")) {
                            Type tp = Type.getType(localVariable.getSignature());
                            System.out.println(" - Nome: " + localVariable.getName() + " - Assinatura: "
                                    + localVariable.toString() + " - tipo: " + tp.toString());
                        }
                    }
                    System.out.println(" ======================= \n");
                }

                System.out.println(" - Flags de acesso: " + metodoBcel.getAccessFlags());
                System.out.println(" - Código BCEL: " + metodoBcel.getCode().toString());
                System.out.println("\n ========================");
            }
        } catch (Exception ex) {
            System.out.println(" - Erro ao executar método exemplo: " + ex.getMessage());
        }
    }

    /** exemplo para carregar classes de outro diretório em java ele pega só arquivos .class */
    public void carregaClasseOutraURL() {
        try {
            File file = new File("c:\\other_classes\\");
            //convert the file to URL format
            URL url = file.toURI().toURL();
            URL[] urls = new URL[] { url };
            //load this folder into Class loader
            ClassLoader cl = new URLClassLoader(urls);
            //load the Address class in 'c:\\other_classes\\'
            Class cls = cl.loadClass("com.mkyong.io.Address");
            //print the location from where this class was loaded
            ProtectionDomain pDomain = cls.getProtectionDomain();
            CodeSource cSource = pDomain.getCodeSource();
            URL urlfrom = cSource.getLocation();
            System.out.println(urlfrom.getFile());

            System.out.println("Nome da classe carregada: " + cls.getSimpleName());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void verificarProjetoExemplo() {
        Refatoracao rf = new Refatoracao();
        rf.lerProjeto("D:/Java/Padr-es-de-Projeto-master");
    }

    public static void main(String[] args) {
        App app = new App();
        // app.carregaClasseOutraURL();
        //app.verificarClasseExemplo();
        app.verificarProjetoExemplo();
        
    }
}
