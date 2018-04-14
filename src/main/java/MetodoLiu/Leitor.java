package MetodoLiu;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.TypeVariable;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.bcel.Repository;
import org.apache.bcel.classfile.Code;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.LineNumber;
import org.apache.bcel.classfile.LineNumberTable;
import org.apache.bcel.classfile.Utility;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.util.ByteSequence;

/**
 * Leitor
 */
public class Leitor {

	private String pacoteClasse = "";
	private String nomeClasse;
	private Class<?> caller;
	private JavaClass classeInstanciada = null;
	private String tipoClasse;

	public Leitor(String nomeClasse, String caminho) {
		try {
			/**le e nós define o nome do pacote - essencial para Class.forName  */
			BufferedReader br = new BufferedReader(new FileReader(caminho));
			while (br.ready()) {
				String linha = br.readLine();
				if (linha.contains("package")) {
					String separa_linha[] = linha.split("package ");
					this.pacoteClasse = separa_linha[1].replace(";", "");
				}
				//para identificar o modificar da classe
				if (linha.contains("public") || linha.contains("protected") || linha.contains("private")) {
					String separa_linhaModificador[] = null;
					if (linha.contains("public")) {
						separa_linhaModificador = linha.split("public ");
					}
					if (linha.contains("protected")) {
						separa_linhaModificador = linha.split("protected ");
					}
					if (linha.contains("private")) {
						separa_linhaModificador = linha.split("private ");
					}
					String separadorTipo[] = separa_linhaModificador[1].trim().split(" ");
					this.tipoClasse = separadorTipo[0];//pegando aqui o tipo se é interface, abstract, ou class
					break;
				}
			}

			br.close();
			this.nomeClasse = nomeClasse;
			this.carregaClasseOutraURL(caminho);
		} catch (Exception ex) {
			throw new IllegalStateException("Erro causado por: " + ex.getMessage() + " no caminho: " + caminho);
		}
	}

	/**
	 *  carrega classes apontados em outro diretório
	 * @param caminho onde o arquivo se encontra em .java
	 */
	public void carregaClasseOutraURL(String caminho) {
		try {
			String separa_caminho[] = caminho.split("src");
			//começando a procura na pasta target padrão para IDE Visual Studio Code
			String caminhoClasse = separa_caminho[0] + "target\\classes\\";
			File file = new File(caminhoClasse);
			if (!file.exists()) {//caso a pasta não exista então procura na build  - padrão para IDE netbeans e eclipse
				file = new File(separa_caminho[0] + "build\\classes\\");
				if (!file.exists()) {//caso a pasta não exista então procura na bin
					file = new File(separa_caminho[0] + "bin\\");
				}
			}
			//convert the file to URL format
			URL url = file.toURI().toURL();
			URL[] urls = new URL[] { url };
			//load this folder into Class loader
			ClassLoader cl = new URLClassLoader(urls);
			//load the Address class in 'c:\\other_classes\\'
			String classeCarregada = this.pacoteClasse + "." + this.nomeClasse;
			this.caller = cl.loadClass(classeCarregada);
			//print the location from where this class was loaded
			ProtectionDomain pDomain = this.caller.getProtectionDomain();
			CodeSource cSource = pDomain.getCodeSource();
			URL urlfrom = cSource.getLocation();

			System.out.println(" -- Nome da classe carregada: " + this.caller.getSimpleName());
		} catch (Exception ex) {
			throw new IllegalStateException(
					"Erro ao carregar .class causado por: " + ex.getMessage() + " no caminho: " + caminho);
		}
	}

	public Class<?> classe() {
		return caller;
	}

	/** 
	 * retorna os campos da classe
	 */
	public Field[] campos() {
		try {
			return caller.getDeclaredFields();
		} catch (Exception ex) {
			throw new IllegalStateException("Erro causado por: " + ex.getMessage());
		}
	}

	/** 
	 * retorna os construtores da classe
	 */
	public Constructor<?>[] construtor() {
		try {
			Constructor<?>[] constructors = caller.getConstructors();
			return constructors;
		} catch (Exception ex) {
			throw new IllegalStateException("Erro causado por: " + ex.getMessage());
		}
	}

	/** 
	 * retorna os construtores da classe
	 */
	public Constructor<?>[] construtoresDeclarados() {
		try {
			Constructor<?>[] constructors = caller.getDeclaredConstructors();
			return constructors;
		} catch (Exception ex) {
			throw new IllegalStateException("Erro causado por: " + ex.getMessage());
		}
	}

	/** retorna os métodos da classe */
	public Method[] metodos() {
		try {
			return caller.getMethods();
		} catch (Exception ex) {
			throw new IllegalStateException("Erro causado por: " + ex.getMessage());
		}
	}

	/** retorna os métodos da classe */
	public Method[] metodosDeclarados() {
		try {
			return caller.getDeclaredMethods();
		} catch (Exception ex) {
			throw new IllegalStateException("Erro causado por: " + ex.getMessage());
		}
	}

	public TypeVariable<?>[] parametros() {
		try {
			return caller.getTypeParameters();
		} catch (Exception ex) {
			throw new IllegalStateException("Erro causado por: " + ex.getMessage());
		}
	}

	/**
	 * @return the nomeClasse
	 */
	public String getNomeClasse() {
		return nomeClasse;
	}

	/**
	 * @param nomeClasse the nomeClasse to set
	 */
	public void setNomeClasse(String nomeClasse) {
		this.nomeClasse = nomeClasse;
	}

	public String modificador(int qualModificador) {
		String res = "";
		if (qualModificador == Modifier.PUBLIC) {
			res = "public";
		} else if (qualModificador == Modifier.PRIVATE) {
			res = "private";
		} else if (qualModificador == Modifier.PROTECTED) {
			res = "protected";
		} else if (qualModificador == Modifier.STATIC) {
			res = "static";
		} else if (qualModificador == Modifier.VOLATILE) {
			res = "volatile";
		} else if (qualModificador == Modifier.FINAL) {
			res = "final";
		} else if (qualModificador == Modifier.NATIVE) {
			res = "native";
		}
		return res;
	}

	public org.apache.bcel.classfile.Method metodoBcel(Method metodo) {
		try {
			if (classeInstanciada == null) {
				classeInstanciada = Repository.lookupClass(caller);
			}
			return classeInstanciada.getMethod(metodo);
		} catch (ClassNotFoundException ex) {
			throw new IllegalStateException("Erro causado por: " + ex.getMessage());
		}
	}

	/**
	 * retorna as instruções perante o método
	 */
	public List<String> instrucoesMetodo(Method metodo) {
		return this.instrucoesMetodo(this.metodoBcel(metodo));
	}

	public List<String> instrucoesMetodo(org.apache.bcel.classfile.Method metodoBcel) {
		try {
			List<String> instrucoes = new ArrayList<String>();
			if (metodoBcel.getCode() != null) {
				byte[] code = metodoBcel.getCode().getCode();
				ByteSequence stream = new ByteSequence(code);
				while (stream.available() > 0) {
					instrucoes.add(Utility.codeToString(stream, metodoBcel.getConstantPool()));
				}
			}
			return instrucoes;
		} catch (Exception ex) {
			throw new IllegalStateException(
					"Erro causado por: " + ex.getMessage() + " - método: " + metodoBcel.getName());
		}
	}

	public List<String> retornaIf(Method metodo) {
		org.apache.bcel.classfile.Method metodoBcel = this.metodoBcel(metodo);
		List<String> instrucoes = this.instrucoesMetodo(metodoBcel).stream()
				.filter(line -> line.equals("ifnonnull") || line.equals("ifeq") || line.equals("ifnull"))
				.collect(Collectors.toList());
		return instrucoes;
	}

	/**
	 * pesquisa se tem algum if nas instruções
	 */
	public List<String> retornaIf(List<String> instrucoes) {
		List<String> resultado = instrucoes.stream()
				.filter(line -> line.equals("ifnonnull") || line.equals("ifeq") || line.equals("ifnull"))
				.collect(Collectors.toList());
		return resultado;
	}

	public void checkTable(LineNumberTable table) {
		System.out.println("line number table has length " + table.getTableLength());
		LineNumber[] entries = table.getLineNumberTable();
		int lastBytecode = -1;
		for (int i = 0; i < entries.length; ++i) {
			LineNumber ln = entries[i];
			System.out.println("Entry " + i + ": pc=" + ln.getStartPC() + ", line=" + ln.getLineNumber());
			int pc = ln.getStartPC();
			if (pc <= lastBytecode) {
				throw new IllegalStateException("LineNumberTable is not sorted");
			}
		}
	}

	public void analyze(org.apache.bcel.classfile.Method method) {
		Code code = method.getCode();
		if (code != null) {
			byte[] instructionList = code.getCode();
			System.out.println(" - Método de análise");
			final InstructionList list = new InstructionList(instructionList);
			System.out.println(list.getInstructionPositions());
		}
	}

	/**
	 * @return the tipoClasse
	 */
	public String getTipoClasse() {
		return tipoClasse;
	}

	/**
	 * @param tipoClasse the tipoClasse to set
	 */
	public void setTipoClasse(String tipoClasse) {
		this.tipoClasse = tipoClasse;
	}
}