package MetodoLiu;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.List;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
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

                            if (!condicional.isEmpty()) {
                                return condicional;
                            } else {
                                break;
                            }
                        }
                    }
                }
            }

        }
        return null;
    }

    /**
     * le um arquivo .java transforma ele e adiciona coisas ao método e por fim reescreve ele perante o arquivo
     * @param caminhoArquivoJava - url do arquivo .java
     * @exception ex não ache o arquivo
     */
    public void modificaClasse(String caminhoArquivoJava) {
        try {
            FileInputStream file = new FileInputStream(caminhoArquivoJava);
            CompilationUnit cu = JavaParser.parse(file);

            /** fazer as modificações na classe */

            /** alterando arquivo .java */
            FileWriter fileWriter = new FileWriter(caminhoArquivoJava);
            fileWriter.write(cu.toString());
            fileWriter.flush();
            fileWriter.close();

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

   
    
    /**
     *  responsável por criar o arquivo e colocar conteúdo da classe abstrata 
     *  
     * */
    public void criaClasseAbstrata(String caminho) {
        try {
            
            /**pega o modelo da classe para ser trocado o texto com replace e ser criado a nova classe Strategy*/
        	String texto = "";
    		File file = new File(getClass().getResource("ModeloStrategy.txt").getPath());
    		try (FileInputStream inputStream = new FileInputStream(file)) {
    			
    			int content;
    			while ((content = inputStream.read()) != -1) {
    				// convert to char and display it
    				texto += (char) content;
    			}
    			inputStream.close();
    			texto = texto.replace("[pacote]", "");
    			texto = texto.replace("[parametro1]", "");
    			
    		} catch (Exception ex) {
    			throw new IllegalStateException("Erro causado por: " + ex.getMessage());
    		}finally {
    			
			}
    		
    		/** com modelo pego ele escreve o arquivo*/
    		FileWriter arquivo = new FileWriter(caminho + "/Strategy.java");
            arquivo.write(texto);
            arquivo.close();
        } catch (Exception ex) {
            throw new IllegalStateException("Erro causado por: " + ex.getMessage());
        }
    }
}