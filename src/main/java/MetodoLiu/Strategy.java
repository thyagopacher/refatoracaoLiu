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

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Modifier;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.TypeParameter;

/**
 * Strategy
 */
public class Strategy {

	private List<IfStmt> condicoesComIfElse = new ArrayList<>();
	private Leitor leitor;
	private String caminho;

	public Strategy() {

	}

	public Strategy(Leitor l) {
		this.leitor = l;
	}

	/**
	 * 
	 * @return List com Statement de ifs que tem no seu condicional algum parametro
	 *         do método
	 * @param Leitor
	 *            l - é a classe responsável por ser feito a leitura genérica da
	 *            classe table 4 - Strategy pattern directed refactoring
	 *            opportunities identification algorithm
	 */
	public Map<MethodDeclaration, List<Statement>> analisador() {
		Map<MethodDeclaration, List<Statement>> mapaMetodosAnalisados = new HashMap<>();

		List<MethodDeclaration> metodos = leitor.metodosDeclaradosJavaParser();
		for (MethodDeclaration metodo : metodos) {
			List<Statement> instrucoesIf = new ArrayList<>();
			NodeList<com.github.javaparser.ast.body.Parameter> parametrosMetodo = metodo.getParameters();
			if (parametrosMetodo == null || parametrosMetodo.isEmpty()) {
				System.out.println(" -- Método:" + metodo.getName() + " -- não tem parametros não pode ser usado strategy");
				continue;
			}

			/** if abaixo se existe if dentro do método else continue */
			/** se o tipo não for interface ele pode ter métodos */
			if (!leitor.getTipoClasse().equals("interface")) {
				/** retorno pesado */
				List<Statement> instrucoesMetodo = leitor.linhasMetodo(metodo.getName().toString());

				if (!instrucoesMetodo.isEmpty()) {
					/** para cada if faça */
					for (Statement var : instrucoesMetodo) {
						if (var instanceof IfStmt) {
							IfStmt ifStmt = (IfStmt) var;
							IfStmt condicional = leitor.temParametroNoIf1(parametrosMetodo, ifStmt);

							if (condicional != null) {
								instrucoesIf.add(condicional);
							} else {
								break;
							}
						}
					}
				}
			}
			if (instrucoesIf != null && !instrucoesIf.isEmpty()) {
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
			IfStmt ifStmt1 = instrucoesIf.get(0).asIfStmt();
			String retorno1 = ifStmt1.getThenStmt().toString().replaceAll("[0-9]", "").replaceAll("[-+=*;%$#@!{}.]", "").replace("return ", "");
			String nomeParametro = retorno1.trim();
			
			EnumSet<Modifier> modifiers = EnumSet.of(Modifier.PUBLIC);
			modifiers.add(Modifier.PUBLIC);			
			
			/** precisa criar antes a classe abstract */
			CompilationUnit cu1 = new CompilationUnit();
			ClassOrInterfaceDeclaration type1 = cu1.addClass("Strategy");
			MethodDeclaration method1 = new MethodDeclaration();
			method1.setName(metodoDeclarado.getName());
			method1.setType(metodoDeclarado.getType());
			method1.setModifiers(modifiers);
			method1.addParameter(metodoDeclarado.getType(), nomeParametro);
			type1.addMember(method1);			
			type1.setAbstract(true);
			
			FileWriter fileWriter1 = new FileWriter(this.getCaminho() + "/Strategy.java");
			fileWriter1.write(cu1.toString());
			fileWriter1.flush();
			fileWriter1.close();
			
			for (Statement statement : instrucoesIf) {
				IfStmt ifStmt = (IfStmt) statement;
				if (!this.temElse(ifStmt)) {
					/** criar um arquivo ConcreteStrategy + code com cada if */
					for (IfStmt ifMetodo : condicoesComIfElse) {
						String condicao = ifMetodo.getCondition().toString();
						String separa_condicao[] = null;
						if(condicao.contains(" == ")) {
							separa_condicao = condicao.split(" == ");
						}else if(condicao.contains(" >= ")) {
							separa_condicao = condicao.split(" >= ");
						}else if(condicao.contains(" <= ")) {
							separa_condicao = condicao.split(" <= ");
						}else if(condicao.contains(" != ")) {
							separa_condicao = condicao.split(" != ");
						}
						String nomeClasse = separa_condicao[1].replaceAll("'", "");
						String nomeClasseArquivo = "ConcreteStrategy" + (String)nomeClasse;
						
						CompilationUnit cu = new CompilationUnit();
						cu.setBlockComment("*\n* Class gerada automaticamente pelo sistema de refatoração\n* @author - Thyago Henrique Pacher\n *@since "+ hoje +"\n ");
						ClassOrInterfaceDeclaration type = cu.addClass(nomeClasseArquivo);
						
						// create a method
						BlockStmt block = new BlockStmt();
						block.addStatement(ifMetodo.getThenStmt().toString().replace("{", "").replace("}", ""));
						MethodDeclaration method = new MethodDeclaration();
						method.setName(metodoDeclarado.getName());
						method.setType(metodoDeclarado.getType());
						method.setModifiers(modifiers);
						method.addParameter(metodoDeclarado.getType(), nomeParametro);
						method.setBody(block);
						type.addMember(method);
						type.addExtendedType("Strategy");
						
						fileWriter = new FileWriter(this.getCaminho() + "/" + nomeClasseArquivo + ".java");
						fileWriter.write(cu.toString());
						fileWriter.flush();
						
					}
				}
			}
			
			//reescreve método da classe refatorada
			BlockStmt block = new BlockStmt();
			block.addStatement("return strategy." + metodoDeclarado.getName() + "(" + nomeParametro + ");");
			metodoDeclarado.setBody(block);
			metodoDeclarado.setParameter(0, new com.github.javaparser.ast.body.Parameter(new TypeParameter("Strategy"), "strategy"));
			fileWriter = new FileWriter(this.getCaminho() + "/" + leitor.getNomeClasse() + ".java");
			fileWriter.write(leitor.getCu().toString());
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
			IfStmt elseStmt = (IfStmt) ifStmt.getElseStmt().get();
			return this.temElse(elseStmt);
		}else {
			return false;
		}
	}

	public Leitor getLeitor() {
		return leitor;
	}

	public void setLeitor(Leitor l) {
		this.leitor = l;
	}

	public String getCaminho() {
		return caminho;
	}

	public void setCaminho(String caminho) {
		this.caminho = caminho;
	}
}