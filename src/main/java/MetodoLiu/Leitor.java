
package MetodoLiu;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.TypeVariable;
import java.util.Scanner;

import org.apache.bcel.Repository;
import org.apache.bcel.classfile.JavaClass;

/**
 * Leitor
 */
public class Leitor {

	private String nomeClasse;
	private Class<?> caller;

	public Leitor(String nomeClasse) {
		try {
			this.nomeClasse = nomeClasse;
			caller = Class.forName(this.nomeClasse);
		} catch (ClassNotFoundException ex) {
			System.out.println("-> Não pode continuar pois a Classe não foi encontrada");
			System.exit(1);
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
			System.out.println("Não conseguiu achar o construtor");
			return null;
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
			System.out.println("Não conseguiu achar o construtor");
			return null;
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
			System.out.println("Não conseguiu achar o construtor");
			return null;
		}
	}

	/** retorna os métodos da classe */
	public Method[] metodos() {
		try {
			return caller.getMethods();
		} catch (Exception ex) {
			return null;
		}
	}

	/** retorna os métodos da classe */
	public Method[] metodosDeclarados() {
		try {
			return caller.getDeclaredMethods();
		} catch (Exception ex) {
			return null;
		}
	}

	public TypeVariable<?>[] parametros() {
		try {
			return caller.getTypeParameters();
		} catch (Exception ex) {
			return null;
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


	public  org.apache.bcel.classfile.Method metodoBcel(Method metodo){
		try{
			JavaClass classeInstanciada = Repository.lookupClass(this.nomeClasse);
			return classeInstanciada.getMethod(metodo);
			
		}catch(ClassNotFoundException ex){
			return null;
		}
	}
}