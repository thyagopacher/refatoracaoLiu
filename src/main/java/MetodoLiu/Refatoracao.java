
package MetodoLiu;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ast.CompilationUnit;

/**
 * Refatoracao para ler projetos e aplicar método de refatoração
 */
public class Refatoracao {

    /**
     * le diretório do projeto ve o que é classe java e separa para ler em refatoração
     * @param caminho - para apontar onde está o projeto perante o sistema operacional
     */
    public void lerProjeto(String caminho) {
        File d = new File(caminho);
        File[] files = d.listFiles();
        if(files != null && files.length > 0){
            Strategy st = new Strategy();//para só instanciar caso ache algo no diretório
            Factory fa = new Factory();
            int indiceClasse = 0;
            for (File arquivo : files) {
                if(arquivo.isDirectory()){
                    System.out.println("Diretório: " + arquivo.getName());
                    /** usa recurso para entrar dentro das pastas - isso ajuda com pacotes de código */
                    this.lerProjeto(caminho + "/" + arquivo.getName());
                }else if(!arquivo.isDirectory() && arquivo.getName().contains(".java")
                    &&  !arquivo.getName().contains("Test")){
                    /** identificação de possíveis classe aqui */
                    System.out.println("Arquivo: " + arquivo.getName());
                    String nomeClasse[] = arquivo.getName().split(".java");
                    Leitor l = new Leitor(nomeClasse[0], arquivo.toPath().toString());

                    System.out.println(" -- Começando análise da classe --> " + l.getNomeClasse());

                    System.out.println("\n == Começando análise do Strategy ==");
                    st.analisador(l);

                    System.out.println("\n == Começando análise do Factory Method ==");
                    fa.analisador(l);  
                    indiceClasse++;
                }else{
                    continue;
                }
            }
        }
    }


    
}