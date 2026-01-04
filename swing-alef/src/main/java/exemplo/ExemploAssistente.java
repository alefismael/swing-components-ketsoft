package exemplo;

import componente.*;
import dialogo.*;

import javax.swing.*;
import java.awt.*;
import java.util.function.Supplier;

/**
 * Exemplo do Assistente/Wizard (Ciclo 11).
 */
public class ExemploAssistente extends JPanel {
    
    public ExemploAssistente() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Descrição
        JTextArea descricao = new JTextArea(
            "O PainelAssistente permite criar fluxos de cadastro passo-a-passo (wizard).\n\n" +
            "• Adicione etapas com adicionarEtapa()\n" +
            "• Cada etapa pode ter validação própria\n" +
            "• O IndicadorEtapas mostra o progresso visual\n" +
            "• Navegação com botões Anterior/Próximo/Cancelar"
        );
        descricao.setEditable(false);
        descricao.setLineWrap(true);
        descricao.setWrapStyleWord(true);
        descricao.setBackground(getBackground());
        
        add(descricao, BorderLayout.NORTH);
        
        // Botão para abrir assistente
        JButton btnAbrir = new JButton("Abrir Assistente de Cadastro");
        btnAbrir.setFont(btnAbrir.getFont().deriveFont(16f));
        btnAbrir.addActionListener(e -> abrirAssistente());
        
        JPanel centro = new JPanel(new FlowLayout(FlowLayout.CENTER));
        centro.add(btnAbrir);
        add(centro, BorderLayout.CENTER);
        
        // Indicador standalone
        JPanel painelIndicador = new JPanel(new BorderLayout());
        painelIndicador.setBorder(BorderFactory.createTitledBorder("IndicadorEtapas (standalone)"));
        
        IndicadorEtapas indicador = new IndicadorEtapas();
        indicador.setEtapas(new String[]{"Dados", "Endereço", "Pagamento", "Confirmação"});
        indicador.setEtapaAtual(1);
        indicador.setPreferredSize(new Dimension(0, 70));
        
        JSlider slider = new JSlider(0, 3, 1);
        slider.addChangeListener(ev -> indicador.setEtapaAtual(slider.getValue()));
        
        painelIndicador.add(indicador, BorderLayout.CENTER);
        painelIndicador.add(slider, BorderLayout.SOUTH);
        
        add(painelIndicador, BorderLayout.SOUTH);
    }
    
    private void abrirAssistente() {
        JDialog dialog = new JDialog(
            (Frame) SwingUtilities.getWindowAncestor(this),
            "Assistente de Cadastro",
            true
        );
        
        PainelAssistente assistente = new PainelAssistente();
        
        // Etapa 1 - Dados Pessoais
        assistente.adicionarEtapa("Dados Pessoais", () -> {
            JPanel p = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.anchor = GridBagConstraints.WEST;
            
            gbc.gridx = 0; gbc.gridy = 0;
            p.add(new JLabel("Nome:"), gbc);
            gbc.gridx = 1; gbc.weightx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
            p.add(new JTextField(20), gbc);
            
            gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
            p.add(new JLabel("Email:"), gbc);
            gbc.gridx = 1; gbc.weightx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
            p.add(new JTextField(20), gbc);
            
            gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
            p.add(new JLabel("Telefone:"), gbc);
            gbc.gridx = 1; gbc.weightx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
            p.add(new JTextField(20), gbc);
            
            return p;
        });
        
        // Etapa 2 - Endereço
        assistente.adicionarEtapa("Endereço", () -> {
            JPanel p = new JPanel(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(5, 5, 5, 5);
            gbc.anchor = GridBagConstraints.WEST;
            
            gbc.gridx = 0; gbc.gridy = 0;
            p.add(new JLabel("CEP:"), gbc);
            gbc.gridx = 1; gbc.weightx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
            p.add(new JTextField(10), gbc);
            
            gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
            p.add(new JLabel("Rua:"), gbc);
            gbc.gridx = 1; gbc.weightx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
            p.add(new JTextField(30), gbc);
            
            gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE;
            p.add(new JLabel("Cidade:"), gbc);
            gbc.gridx = 1; gbc.weightx = 1; gbc.fill = GridBagConstraints.HORIZONTAL;
            p.add(new JTextField(20), gbc);
            
            return p;
        });
        
        // Etapa 3 - Confirmação
        assistente.adicionarEtapa("Confirmação", () -> {
            JPanel p = new JPanel(new BorderLayout(10, 10));
            p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            
            JLabel lbl = new JLabel("<html><center>" +
                "<h2>✓ Tudo pronto!</h2>" +
                "<p>Revise os dados e clique em Concluir.</p>" +
                "</center></html>");
            lbl.setHorizontalAlignment(SwingConstants.CENTER);
            p.add(lbl, BorderLayout.CENTER);
            
            return p;
        });
        
        assistente.setAoConcluir(() -> {
            JOptionPane.showMessageDialog(dialog, "Cadastro concluído com sucesso!");
            dialog.dispose();
        });
        
        dialog.setContentPane(assistente);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
}
