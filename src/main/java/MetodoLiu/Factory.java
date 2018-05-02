package MetodoLiu;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.BodyDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.TypeParameter;

/**
 * Factory
 */
public class Factory {

	private List<IfStmt> condicoesComIfElse = new ArrayList<>();
	private Leitor leitor;
	private String caminho;
  
    /**
     * 
     * @return List com Statement de ifs que tem no seu condicional algum parametro do método
     * @param Leitor l - é a classe responsável por ser feito a leitura genérica da classe
     * table 4 - Strategy pattern directed refactoring opportunities identification algorithm
     */
    public Map<MethodDeclaration, List<Statement>> analisador() {
    	Map<MethodDeclaration, List<Statement>> mapaMetodosAnalisados = new HashMap<>(); 
        List<MethodDeclaration> metodos = leitor.metodosDeclaradosJavaParser();
        for (MethodDeclaration metodo : metodos) {
        	List<Statement> instrucoesIf = new ArrayList<>();
            NodeList<com.github.javaparser.ast.body.Parameter> parametrosMetodo = metodo.getParameters();
            if (parametrosMetodo == null || parametrosMetodo.isEmpty()) {
                System.out.println(" -- Método:" + metodo.getName() + " -- não tem parametros não é usado factory");
                continue;
            }

            /** if abaixo se existe if dentro do método else continue */
            /** se o tipo não for interface ele pode ter métodos */
            if (!leitor.getTipoClasse().equals("interface")) {
            	/**retorno pesado*/
                List<Statement> instrucoesMetodo = leitor.linhasMetodo(metodo.getName().toString());
                
                if (!instrucoesMetodo.isEmpty()) {
                    /** para cada if faça */
                    for (Statement var : instrucoesMetodo) {
                        if (var instanceof IfStmt) {
                            IfStmt ifStmt = (IfStmt) var;
                            IfStmt condicional = leitor.temParametroNoIf2(parametrosMetodo, ifStmt);
                   
                            if (condicional != null) {
                            	instrucoesIf.add(condicional);
                            } else {
                                break;
                            }
                        }
                    }
                }
            }
            if(instrucoesIf != null && !instrucoesIf.isEmpty()) {
            	mapaMetodosAnalisados.put(metodo, instrucoesIf);
            }
        }
        return mapaMetodosAnalisados;
    }
    

	/**
	 * le um arquivo .java transforma ele e adiciona coisas ao método e por fim
	 * reescreve ele perante o arquivo
	 */
	public void modificaClasse(MethodDeclaration metodoDeclarado, List<Statement> instrucoesIf) throws IOException {
		DateFormat dat = DateFormat.getDateInstance(DateFormat.LONG, new Locale("pt","BR"));
		String hoje = dat.format(new Date());
		FileWriter fileWriter = null;
		try {
			String classeAvaliada = leitor.getNomeClasse();
			IfStmt ifStmt1 = instrucoesIf.get(0).asIfStmt();
			String retorno1 = ifStmt1.getThenStmt().toString().replaceAll("[0-9]", "").replaceAll("[-+=*;%$#@!{}.]", "").replace("return ", "");
			String nomeParametro = retorno1.trim();
			
			EnumSet<Modifier> modifiers = EnumSet.of(Modifier.PUBLIC);
			modifiers.add(Modifier.PUBLIC);			

			for (Statement statement : instrucoesIf) {
				IfStmt ifStmt = (IfStmt) statement;
				if (!this.temElse(ifStmt)) {
					/** criar um arquivo ConcreteStrategy + code com cada if */
					for (IfStmt ifMetodo : condicoesComIfElse) {
						String ifThen = ifMetodo.getThenStmt().getChildNodes().toString();
						String separa_then[] = ifThen.split("new ");
						String nomeClasse = separa_then[1].replaceAll("'", "").replace("(", "").replace(");]", "");
						String nomeClasseArquivo = nomeClasse + "Factory";
						
						CompilationUnit cu = new CompilationUnit();
						cu.setBlockComment("*\n* Class gerada automaticamente pelo sistema de refatoração - Factory \n* @author - Thyago Henrique Pacher\n *@since "+ hoje +"\n ");
						ClassOrInterfaceDeclaration type = cu.addClass(nomeClasseArquivo);
						
						// create a method
						BlockStmt block = new BlockStmt();
						block.addStatement("return new " + nomeClasse + "();");
						
						MethodDeclaration method = new MethodDeclaration();
						method.setName(metodoDeclarado.getName());
						method.setType(metodoDeclarado.getType());
						method.setModifiers(modifiers);
						method.setBody(block);
						type.addMember(method);
						type.addExtendedType(classeAvaliada);
						
						fileWriter = new FileWriter(this.getCaminho() + "/" + nomeClasseArquivo + ".java");
						fileWriter.write(cu.toString());
						fileWriter.flush();
//						
					}
				}
			}
			
			//reescreve método da classe refatorada
			CompilationUnit cu = JavaParser.parse(leitor.getArquivoClasse());
			ClassOrInterfaceDeclaration classeOrigem = (ClassOrInterfaceDeclaration) cu.getType(0);
            NodeList<BodyDeclaration<?>> members = classeOrigem.getMembers();
            for (BodyDeclaration<?> member : members) {
                if (member instanceof MethodDeclaration) {
                    MethodDeclaration method = (MethodDeclaration) member;
                    if(method.getName().equals(metodoDeclarado.getName())) {
                    	metodoDeclarado = method;
                    	break;
                    }
                }
            }			
			classeOrigem.setAbstract(true);//seta classe factory pai para abstract
			metodoDeclarado.setBody(new BlockStmt());
			metodoDeclarado.getParameter(0).remove();
			fileWriter = new FileWriter(this.getCaminho() + "/" + classeAvaliada + ".java");
			fileWriter.write(cu.toString());
			fileWriter.flush();			
		
		} catch (Exception ex) {
			throw new IllegalStateException("Erro causado por: " + ex.getMessage());
		} finally {
			fileWriter.close();
		}
	}    
    

	public boolean temElse(IfStmt ifStmt) {
		this.condicoesComIfElse.add(ifStmt);
		if (ifStmt.getElseStmt().isPresent()) {
			if(ifStmt.getElseStmt() != null && ifStmt.getElseStmt().get().isIfStmt()) {
				IfStmt elseStmt = (IfStmt) ifStmt.getElseStmt().get();
				return this.temElse(elseStmt);				
			}else {
				return false;
			}
		}else {
			return false;
		}
	}	
	
    /**
     *  responsável por criar o arquivo e colocar conteúdo da classe abstrata 
     *  @exception ex retorna caso de problema de escrita no FileWriter
     * */
    public void criaClasseConcreta(String caminho) {
        try {
            /**pega o modelo da classe para ser trocado o texto com replace e ser criado a nova classe Strategy*/
        	StringBuilder sb = new StringBuilder();
    		File file = new File(getClass().getResource("ModeloFactory.txt").getPath());
    		try (FileInputStream inputStream = new FileInputStream(file)) {		
    			int content;
    			while ((content = inputStream.read()) != -1) {
    				sb.append((char) content);
    			}
    			inputStream.close();    			
    		} catch (Exception ex) {
    			throw new IllegalStateException("Erro causado por: " + ex.getMessage());
    		}
    		
    		/** com modelo pego ele escreve o arquivo*/
    		FileWriter arquivo = new FileWriter(caminho + "/Factory.java");
            arquivo.write(sb.toString().replace("[pacote]", "").replace("[parametro1]", ""));
            arquivo.close();
        } catch (Exception ex) {
            throw new IllegalStateException("Erro causado por: " + ex.getMessage());
        }
    }    
	
    public Leitor getLeitor() {
		return leitor;
	}


	public void setLeitor(Leitor leitor) {
		this.leitor = leitor;
	}


	public String getCaminho() {
		return caminho;
	}


	public void setCaminho(String caminho) {
		this.caminho = caminho;
	}
}