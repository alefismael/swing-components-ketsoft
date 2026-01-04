package exemplo;

import componente.BotaoCarregamento;
import componente.Toast;
import dialogo.DialogoCarregamento;
import dialogo.DialogoProgresso;
import util.TarefaAssincrona;

import javax.swing.*;
import java.awt.*;

/**
 * Painel de exemplo dos componentes de carregamento e progresso.
 * 
 * Demonstra:
 * - DialogoCarregamento (spinner)
 * - DialogoProgresso (barra de progresso)
 * - BotaoCarregamento (botão com loading)
 * - TarefaAssincrona (SwingWorker simplificado)
 */
public class ExemploCarregamento extends JPanel {
    
    public ExemploCarregamento() {
        super(new BorderLayout());
        
        JPanel panel = new JPanel(new GridLayout(0, 1, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Título
        JLabel titulo = new JLabel("Componentes de Carregamento e Progresso");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 18f));
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        titulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // === 1. DialogoCarregamento ===
        JButton btnCarregamento = new JButton("DialogoCarregamento (3 segundos)");
        btnCarregamento.addActionListener(e -> {
            Window parent = SwingUtilities.getWindowAncestor(this);
            DialogoCarregamento.executar(parent, "Carregando dados...", () -> {
                dormir(3000);
            }, () -> {
                Toast.success(parent, "Carregamento concluído!");
            }, erro -> {
                Toast.error(parent, "Erro: " + erro.getMessage());
            });
        });
        panel.add(btnCarregamento);
        
        // === 2. DialogoProgresso (determinado) ===
        JButton btnProgresso = new JButton("DialogoProgresso (0-100%)");
        btnProgresso.addActionListener(e -> {
            Window parent = SwingUtilities.getWindowAncestor(this);
            DialogoProgresso progresso = new DialogoProgresso(parent, "Importando dados...");
            progresso.setMaximo(100);
            progresso.setCancelavel(true);
            
            TarefaAssincrona.executarSimples(() -> {
                for (int i = 0; i <= 100 && !progresso.isCancelado(); i++) {
                    final int valor = i;
                    SwingUtilities.invokeLater(() -> {
                        progresso.setProgresso(valor);
                        progresso.setStatus("Processando item " + valor + " de 100");
                    });
                    dormir(50);
                }
            }, () -> {
                progresso.fechar();
                if (progresso.isCancelado()) {
                    Toast.warning(parent, "Operação cancelada!");
                } else {
                    Toast.success(parent, "Importação concluída!");
                }
            }, erro -> {
                progresso.fechar();
                Toast.error(parent, "Erro: " + erro.getMessage());
            });
            
            progresso.mostrar();
        });
        panel.add(btnProgresso);
        
        // === 3. DialogoProgresso (indeterminado) ===
        JButton btnIndeterminado = new JButton("DialogoProgresso (indeterminado)");
        btnIndeterminado.addActionListener(e -> {
            Window parent = SwingUtilities.getWindowAncestor(this);
            DialogoProgresso progresso = new DialogoProgresso(parent, "Sincronizando...", true);
            
            TarefaAssincrona.executarSimples(() -> {
                dormir(3000);
            }, () -> {
                progresso.fechar();
                Toast.success(parent, "Sincronização concluída!");
            }, null);
            
            progresso.mostrar();
        });
        panel.add(btnIndeterminado);
        
        // === 4. BotaoCarregamento (auto-loading) ===
        BotaoCarregamento btnAutoLoading = new BotaoCarregamento("BotaoCarregamento (auto)");
        btnAutoLoading.addActionListener(e -> {
            dormir(2000);
            Window parent = SwingUtilities.getWindowAncestor(this);
            SwingUtilities.invokeLater(() -> Toast.success(parent, "Salvo com sucesso!"));
        });
        panel.add(btnAutoLoading);
        
        // === 5. BotaoCarregamento (manual) ===
        BotaoCarregamento btnManual = new BotaoCarregamento("BotaoCarregamento (manual)");
        btnManual.setAutoLoading(false);
        btnManual.addActionListenerDireto(e -> {
            btnManual.iniciarCarregamento("Processando...");
            Window parent = SwingUtilities.getWindowAncestor(this);
            
            TarefaAssincrona.executarSimples(() -> {
                dormir(2000);
            }, () -> {
                btnManual.finalizarCarregamento();
                Toast.success(parent, "Processo finalizado!");
            }, null);
        });
        panel.add(btnManual);
        
        // === 6. TarefaAssincrona com progresso ===
        JButton btnTarefa = new JButton("TarefaAssincrona com progresso");
        JLabel lblProgresso = new JLabel("Status: Aguardando...");
        btnTarefa.addActionListener(e -> {
            Window parent = SwingUtilities.getWindowAncestor(this);
            TarefaAssincrona.comProgresso(
                publisher -> {
                    for (int i = 1; i <= 5; i++) {
                        publisher.accept("Etapa " + i + " de 5...");
                        dormir(800);
                    }
                    return "Concluído com sucesso!";
                },
                resultado -> {
                    lblProgresso.setText("Status: " + resultado);
                    Toast.success(parent, resultado);
                },
                progresso -> lblProgresso.setText("Status: " + progresso),
                erro -> {
                    lblProgresso.setText("Status: Erro!");
                    Toast.error(parent, erro.getMessage());
                }
            );
        });
        
        JPanel tarefaPanel = new JPanel(new BorderLayout(5, 5));
        tarefaPanel.add(btnTarefa, BorderLayout.NORTH);
        tarefaPanel.add(lblProgresso, BorderLayout.CENTER);
        panel.add(tarefaPanel);
        
        // Scroll
        JScrollPane scroll = new JScrollPane(panel);
        scroll.setBorder(null);
        
        add(titulo, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
    }
    
    /** Utilitário para evitar try-catch repetido */
    private static void dormir(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
