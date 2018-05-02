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
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.TypeDeclaration;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.TypeParameter;
import com.github.javaparser.ast.visitor.GenericVisitor;
import com.github.javaparser.ast.visitor.VoidVisitor;
import com.github.javaparser.resolution.types.ResolvedType;

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
        rf.lerProjeto("C:\\programa-java\\exemplo-cavado-Liu-factory");
    }

    /**
     *  le um arquivo .java transforma ele e adiciona coisas ao método e por fim reescreve ele perante o arquivo
     * @exception ex não ache o arquivo
     */
    public void modificaClasse() {
        try {
            String camArquivo = "C:\\other_classes\\com\\mkyong\\io\\Address.java";
            FileInputStream file = new FileInputStream(camArquivo);
            CompilationUnit cu = JavaParser.parse(file);
            // change the methods names and parameters
            changeMethods(cu);
            
            // prints the changed compilation unit
            System.out.println(cu.toString());
            FileWriter fileWriter = new FileWriter(camArquivo);
            fileWriter.write(cu.toString());
            fileWriter.flush();
            fileWriter.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private static void changeMethods(CompilationUnit cu) {
        // Go through all the types in the file
        NodeList<TypeDeclaration<?>> types = cu.getTypes();
        for (TypeDeclaration<?> type : types) {
            // Go through all fields, methods, etc. in this type
            NodeList<BodyDeclaration<?>> members = type.getMembers();
            for (BodyDeclaration<?> member : members) {
                if (member instanceof MethodDeclaration) {
                    MethodDeclaration method = (MethodDeclaration) member;
                    changeMethod(method);
                }
            }
        }
    }

    private static void changeMethod(MethodDeclaration n) {
        // change the name of the method to upper case
        n.setName(n.getNameAsString().toLowerCase());

        /** cria comentários */
        n.setBlockComment("Método refatorado automaticamente");

        BlockStmt block = n.getBody().get();
        System.out.println("Tem corpo: " + block.toBlockStmt().isPresent());
        // List linhas = new Leitor().linhasMetodo(n.getName(), "Address");
        // for(int i = 0; i < linhas.size(); i++){
        //     if(linhas.get(i) instanceof IfStmt){
        //         IfStmt ifStmt = (IfStmt) linhas.get(i);
        //         System.out.println("É um IF - com condição: " + ifStmt.getCondition() + " - corpo do IF:" + ifStmt.getThenStmt() + " tem else:  " + ifStmt.hasElseBlock());
        //     }
        //     if(linhas.get(i) instanceof ForStmt){
        //         ForStmt forStmt = (ForStmt) linhas.get(i);
                
        //         System.out.println("É um FOR: inicializador - " + forStmt.getInitialization() + " - Comparador: " + forStmt.getCompare() + " - Incrementador: " + forStmt.getUpdate());
        //     }            
        //     System.out.println(linhas.get(i));
        // }
        // System.out.println(block.toIfStmt().isPresent());
        //block.addAndGetStatement("String teste");
        n.setBody(block);

        //só pode setar um parametro caso ele exista, senão ignora aqui.
        if (n.getParameters().size() > 0) {
            n.setParameter(0, new com.github.javaparser.ast.body.Parameter(new TypeParameter("int"), "valor1"));
        }else{
            //caso não tenha parametros ele adiciona um do tipo int 
            n.addParameter(int.class, "valor1");
        }

        // troca tipo para final
        n.setFinal(true);
    }

    public static void main(String[] args) {
        App app = new App();
        //app.modificaClasse();
        // app.carregaClasseOutraURL();
        //app.verificarClasseExemplo();
        app.verificarProjetoExemplo();
        
    }
}
