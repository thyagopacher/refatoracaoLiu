package MetodoLiu;

import java.io.File;
import java.io.FileInputStream;

import junit.framework.TestCase;

/**
 * Unit test for simple App.
 */
public class StrategyTest extends TestCase {

	Strategy st = new Strategy();

	/**
	 * verifica se a classe Strategy foi criada e sem tem conteúdo perante ela.
	 */
	public void testcriaClasseAbstrata() {
		
		String cam = "C:\\programa-java";
		String arquivoURL = cam + "\\Strategy.java";
		st.criaClasseAbstrata(cam);
		File file = new File(arquivoURL);
		
		try (FileInputStream inputStream = new FileInputStream(file)) {
			String texto = "";
			int content;
			while ((content = inputStream.read()) != -1) {
				// convert to char and display it
				texto += (char) content;
			}
			if(!texto.contains("package")) {
				assertTrue("Arquivo criado, porém não contém conteúdo correto para uma classe", false);
			}
		} catch (Exception ex) {
			throw new IllegalStateException("Erro causado por: " + ex.getMessage());
		}

		assertTrue("Arquivo não foi criado de classe abstrata", file.exists());
	}
}
