package exemplo;

import com.formdev.flatlaf.FlatLightLaf;
import componente.PainelTemas;
import componente.Toast;
import dialogo.DialogoUtil;
import janela.JanelaAbas;
import janela.PainelAbas;

import javax.swing.*;
import java.awt.*;

/**
 * Exemplo completo de aplicação usando a nova arquitetura Swing Alef.
 * 
 * Demonstra:
 * - JanelaAbas (frame com F11 fullscreen)
 * - PainelAbas (abas fecháveis)
 * - Integração com Toast e DialogoUtil
 * - PainelTemas para trocar temas
 * - Barra de navegação com campo de busca
 * 
 * @author alefi
 */
public class ExemploAplicativoCompleto {
    
    private static JanelaAbas janela;
    private static PainelAbas painelAbas;
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            System.err.println("Erro ao inicializar FlatLaf: " + e.getMessage());
        }
        
        SwingUtilities.invokeLater(() -> {
            janela = new JanelaAbas("Sistema Exemplo - Swing Alef");
            painelAbas = janela.getPainelAbas();
            
            // Registrar opções de abas para busca
            registrarOpcoesAbas();
            
            configurarToolbar();
            
            // Aba inicial fixa
            painelAbas.adicionarAbaFixa("🏠 Principal", null, criarPainelBemVindo());
            
            janela.setVisible(true);
        });
    }
    
    private static void registrarOpcoesAbas() {
        // Registra as opções de abas disponíveis
        painelAbas.registrarOpcao("Clientes", "👥", () -> new ExemploPainelClientes());
        painelAbas.registrarOpcao("Campos", "📝", () -> new ExemploCamposFormulario());
        painelAbas.registrarOpcao("Busca", "🔍", () -> new ExemploCampoBusca());
        painelAbas.registrarOpcao("Carregamento", "⏳", () -> new ExemploCarregamento());
        painelAbas.registrarOpcao("Mídia", "🖼️", () -> new ExemploMidia());
        painelAbas.registrarOpcao("Assistente", "🧙", () -> new ExemploAssistente());
        painelAbas.registrarOpcao("Dashboard", "📊", () -> new ExemploDashboard());
        painelAbas.registrarOpcao("Gráficos", "📈", () -> new ExemploGraficos());
        painelAbas.registrarOpcao("Relatórios", "📄", () -> new ExemploRelatorio());
        painelAbas.registrarOpcao("Diálogos", "💬", () -> new ExemploDialogos());
        painelAbas.registrarOpcao("Calendário", "📅", () -> new ExemploCalendario());
        painelAbas.registrarOpcao("Tabelas", "📋", () -> new ExemploTabelasAvancadas());
        painelAbas.registrarOpcao("Temas", "🎨", () -> new PainelTemas(janela));
    }
    
    private static void configurarToolbar() {
        JPanel painelHeader = new JPanel(new BorderLayout(10, 0));
        painelHeader.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        
        // Campo de busca à esquerda (20% maior)
        JPanel painelBusca = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        JTextField txtBusca = new JTextField(18);
        txtBusca.putClientProperty("JTextField.placeholderText", "🔎 Buscar tela...");
        txtBusca.setPreferredSize(new Dimension(180, 28));
        
        // Popup de sugestões
        JPopupMenu popupBusca = new JPopupMenu();
        
        txtBusca.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            private void atualizarSugestoes() {
                String texto = txtBusca.getText().trim();
                popupBusca.removeAll();
                
                if (texto.isEmpty()) {
                    popupBusca.setVisible(false);
                    return;
                }
                
                var opcoes = painelAbas.buscarOpcoes(texto);
                if (opcoes.isEmpty()) {
                    popupBusca.setVisible(false);
                    return;
                }
                
                for (var opcao : opcoes) {
                    JMenuItem item = new JMenuItem(opcao.toString());
                    item.addActionListener(ev -> {
                        painelAbas.abrirOpcao(opcao);
                        txtBusca.setText("");
                        popupBusca.setVisible(false);
                    });
                    popupBusca.add(item);
                }
                
                popupBusca.show(txtBusca, 0, txtBusca.getHeight());
            }
            @Override public void insertUpdate(javax.swing.event.DocumentEvent e) { atualizarSugestoes(); }
            @Override public void removeUpdate(javax.swing.event.DocumentEvent e) { atualizarSugestoes(); }
            @Override public void changedUpdate(javax.swing.event.DocumentEvent e) { atualizarSugestoes(); }
        });
        
        painelBusca.add(txtBusca);
        
        // Toolbar com botões à direita
        JToolBar toolbar = new JToolBar();
        toolbar.setFloatable(false);
        toolbar.setBorder(BorderFactory.createEmptyBorder());
        
        // Botões de menu
        JButton btnClientes = new JButton("👥 Clientes");
        btnClientes.addActionListener(e -> abrirAba("Clientes", new ExemploPainelClientes()));
        
        JButton btnCampos = new JButton("📝 Campos");
        btnCampos.addActionListener(e -> abrirAba("Campos", new ExemploCamposFormulario()));
        
        JButton btnBusca = new JButton("🔍 Busca");
        btnBusca.addActionListener(e -> abrirAba("Busca", new ExemploCampoBusca()));
        
        JButton btnCarregamento = new JButton("⏳ Carregamento");
        btnCarregamento.addActionListener(e -> abrirAba("Carregamento", new ExemploCarregamento()));
        
        JButton btnMidia = new JButton("🖼️ Mídia");
        btnMidia.addActionListener(e -> abrirAba("Mídia", new ExemploMidia()));
        
        // Menu para novos ciclos (dropdown)
        JPopupMenu menuMais = new JPopupMenu();
        menuMais.add(criarMenuItem("🧙 Assistente", () -> abrirAba("Assistente", new ExemploAssistente())));
        menuMais.add(criarMenuItem("📊 Dashboard", () -> abrirAba("Dashboard", new ExemploDashboard())));
        menuMais.add(criarMenuItem("📈 Gráficos", () -> abrirAba("Gráficos", new ExemploGraficos())));
        menuMais.add(criarMenuItem("📄 Relatórios", () -> abrirAba("Relatórios", new ExemploRelatorio())));
        menuMais.add(criarMenuItem("💬 Diálogos", () -> abrirAba("Diálogos", new ExemploDialogos())));
        menuMais.add(criarMenuItem("📅 Calendário", () -> abrirAba("Calendário", new ExemploCalendario())));
        menuMais.add(criarMenuItem("📋 Tabelas", () -> abrirAba("Tabelas", new ExemploTabelasAvancadas())));
        
        JButton btnMais = new JButton("⋯ Mais");
        btnMais.addActionListener(e -> menuMais.show(btnMais, 0, btnMais.getHeight()));
        
        JButton btnTemas = new JButton("🎨 Temas");
        btnTemas.addActionListener(e -> abrirAba("Temas", new PainelTemas(janela)));
        
        JButton btnSobre = new JButton("ℹ️ Sobre");
        btnSobre.addActionListener(e -> mostrarSobre());
        
        toolbar.add(btnClientes);
        toolbar.add(btnCampos);
        toolbar.add(btnBusca);
        toolbar.add(btnCarregamento);
        toolbar.add(btnMidia);
        toolbar.add(btnMais);
        toolbar.addSeparator();
        toolbar.add(btnTemas);
        toolbar.add(btnSobre);
        
        painelHeader.add(painelBusca, BorderLayout.WEST);
        painelHeader.add(toolbar, BorderLayout.CENTER);
        
        janela.add(painelHeader, BorderLayout.NORTH);
    }
    
    private static void abrirAba(String titulo, JPanel conteudo) {
        painelAbas.adicionarAba(titulo, null, conteudo);
    }
    
    private static JPanel criarPainelBemVindo() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(UIManager.getColor("Panel.background"));
        
        JLabel label = new JLabel("<html><center>" +
            "<h1>🏠 Bem-vindo ao Sistema</h1>" +
            "<p style='font-size:14px'>Use os botões na barra de ferramentas para navegar.</p>" +
            "<br>" +
            "<p style='font-size:12px; color:gray'>Dica: Pressione F11 para tela cheia.</p>" +
            "<p style='font-size:12px; color:gray'>Clique no X das abas para fechá-las.</p>" +
            "<br><br>" +
            "<p style='font-size:12px'><b>Exemplos disponíveis:</b></p>" +
            "<p style='font-size:11px'>• <b>Clientes</b> - PainelCrud com CRUD completo</p>" +
            "<p style='font-size:11px'>• <b>Campos</b> - Todos os campos de formulário</p>" +
            "<p style='font-size:11px'>• <b>Busca</b> - CampoBusca com autocomplete e Toast</p>" +
            "<p style='font-size:11px'>• <b>Carregamento</b> - Diálogos de loading e progresso</p>" +
            "<p style='font-size:11px'>• <b>Mídia</b> - Imagens, arquivos e avatares</p>" +
            "<p style='font-size:11px'>• <b>Assistente</b> - Wizard passo-a-passo</p>" +
            "<p style='font-size:11px'>• <b>Dashboard</b> - Cartões e KPIs</p>" +
            "<p style='font-size:11px'>• <b>Gráficos</b> - Barras, linhas e pizza</p>" +
            "<p style='font-size:11px'>• <b>Relatórios</b> - Construtor e preview</p>" +
            "<p style='font-size:11px'>• <b>Diálogos</b> - Login, Splash, Sobre, Config</p>" +
            "<p style='font-size:11px'>• <b>Calendário</b> - Seletores de data/período</p>" +
            "<p style='font-size:11px'>• <b>Tabelas</b> - Paginada, editável, agrupada</p>" +
            "<p style='font-size:11px'>• <b>Temas</b> - Trocar tema visual</p>" +
            "</center></html>");
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setBorder(BorderFactory.createEmptyBorder(40, 20, 20, 20));
        panel.add(label, BorderLayout.CENTER);
        
        return panel;
    }
    
    private static JMenuItem criarMenuItem(String texto, Runnable acao) {
        JMenuItem item = new JMenuItem(texto);
        item.addActionListener(e -> acao.run());
        return item;
    }
    
    private static void mostrarSobre() {
        DialogoUtil.info(janela, 
            "Swing Alef v2.0\n\n" +
            "Biblioteca de componentes Swing otimizada\n" +
            "para NetBeans GUI Builder.\n\n" +
            "Pacotes disponíveis:\n" +
            "• campo/ - Campos de formulário\n" +
            "• componente/ - Toast, PainelTemas, SobreposicaoCarregamento\n" +
            "• dialogo/ - DialogoUtil, DialogoFormulario\n" +
            "• janela/ - JanelaAbas, PainelAbas\n" +
            "• nucleo/ - Interfaces (Validavel, FabricaDialogo)\n" +
            "• painel/ - PainelCrud\n" +
            "• tabela/ - TabelaBase, TabelaScrollPane\n" +
            "• util/ - GerenciadorAtalhos, ImagemUtil\n\n" +
            "Autor: alefi",
            "Sobre o Sistema");
    }
}
