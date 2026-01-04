package nucleo;

import dialogo.DialogoFormulario;
import javax.swing.JFrame;

/**
 * Fábrica de diálogos para operações CRUD.
 * 
 * <p>Esta interface define como criar diálogos de formulário para
 * operações de criação e edição de registros.</p>
 * 
 * <h3>Exemplo de uso:</h3>
 * <pre>{@code
 * public class FabricaClienteDialogo implements FabricaDialogo {
 *     
 *     @Override
 *     public DialogoFormulario criarDialogoNovo(JFrame owner) {
 *         DialogoFormulario dialogo = new DialogoFormulario(owner, "Novo Cliente");
 *         dialogo.setConteudo(new FormularioCliente());
 *         dialogo.definirTamanho(500, 400);
 *         return dialogo;
 *     }
 *     
 *     @Override
 *     public DialogoFormulario criarDialogoEditar(JFrame owner, int linha, Object[] dados) {
 *         DialogoFormulario dialogo = new DialogoFormulario(owner, "Editar Cliente");
 *         FormularioCliente form = new FormularioCliente();
 *         form.preencherCampos(dados);
 *         dialogo.setConteudo(form);
 *         dialogo.definirTamanho(500, 400);
 *         return dialogo;
 *     }
 * }
 * }</pre>
 * 
 * @author alefi
 * @since 2.0
 * @see DialogoFormulario
 * @see painel.PainelCrud
 */
public interface FabricaDialogo {
    
    /**
     * Cria um diálogo para novo registro.
     * @param owner janela pai
     * @return diálogo configurado para novo registro
     */
    DialogoFormulario criarDialogoNovo(JFrame owner);
    
    /**
     * Cria um diálogo para edição de registro.
     * @param owner janela pai
     * @param linha índice da linha no modelo
     * @param dadosLinha dados atuais da linha
     * @return diálogo configurado para edição
     */
    DialogoFormulario criarDialogoEditar(JFrame owner, int linha, Object[] dadosLinha);
}
