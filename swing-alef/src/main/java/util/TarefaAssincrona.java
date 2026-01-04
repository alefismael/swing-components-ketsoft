package util;

import javax.swing.*;
import java.util.List;
import java.util.function.Consumer;

/**
 * Utilitário para executar tarefas assíncronas com facilidade.
 * 
 * <p>Encapsula {@link SwingWorker} para simplificar operações em background
 * mantendo a UI responsiva.</p>
 * 
 * <h3>Tarefa simples:</h3>
 * <pre>{@code
 * TarefaAssincrona.executar(
 *     () -> repository.findAll(),        // Em background
 *     lista -> tabela.setDados(lista)    // Na EDT após conclusão
 * );
 * }</pre>
 * 
 * <h3>Com tratamento de erro:</h3>
 * <pre>{@code
 * TarefaAssincrona.executar(
 *     () -> repository.save(cliente),
 *     cliente -> Toast.success(this, "Salvo!"),
 *     erro -> Toast.error(this, "Erro: " + erro.getMessage())
 * );
 * }</pre>
 * 
 * <h3>Com progresso:</h3>
 * <pre>{@code
 * TarefaAssincrona.comProgresso(
 *     publisher -> {
 *         for (int i = 0; i < 100; i++) {
 *             publisher.accept("Processando " + i);
 *             Thread.sleep(100);
 *         }
 *         return "Concluído!";
 *     },
 *     resultado -> label.setText(resultado),
 *     progresso -> statusLabel.setText(progresso)
 * );
 * }</pre>
 * 
 * @author alefi
 * @since 1.0
 */
public class TarefaAssincrona {
    
    /**
     * Interface funcional para tarefas que retornam resultado.
     * 
     * @param <T> tipo do resultado
     */
    @FunctionalInterface
    public interface Tarefa<T> {
        T executar() throws Exception;
    }
    
    /**
     * Interface funcional para tarefas com publicação de progresso.
     * 
     * @param <T> tipo do resultado
     * @param <P> tipo do progresso
     */
    @FunctionalInterface
    public interface TarefaComProgresso<T, P> {
        T executar(Consumer<P> publisher) throws Exception;
    }
    
    /**
     * Executa uma tarefa em background.
     * 
     * @param tarefa tarefa a executar
     * @param <T> tipo do resultado
     */
    public static <T> void executar(Tarefa<T> tarefa) {
        executar(tarefa, null, null);
    }
    
    /**
     * Executa uma tarefa em background com callback de sucesso.
     * 
     * @param tarefa tarefa a executar
     * @param onSucesso callback chamado com o resultado (na EDT)
     * @param <T> tipo do resultado
     */
    public static <T> void executar(Tarefa<T> tarefa, Consumer<T> onSucesso) {
        executar(tarefa, onSucesso, null);
    }
    
    /**
     * Executa uma tarefa em background com callbacks de sucesso e erro.
     * 
     * @param tarefa tarefa a executar
     * @param onSucesso callback chamado com o resultado (na EDT)
     * @param onErro callback chamado com a exceção (na EDT)
     * @param <T> tipo do resultado
     */
    public static <T> void executar(Tarefa<T> tarefa, Consumer<T> onSucesso, Consumer<Exception> onErro) {
        new SwingWorker<T, Void>() {
            private Exception erro;
            
            @Override
            protected T doInBackground() {
                try {
                    return tarefa.executar();
                } catch (Exception e) {
                    erro = e;
                    return null;
                }
            }
            
            @Override
            protected void done() {
                if (erro != null) {
                    if (onErro != null) {
                        onErro.accept(erro);
                    } else {
                        erro.printStackTrace();
                    }
                } else if (onSucesso != null) {
                    try {
                        onSucesso.accept(get());
                    } catch (Exception e) {
                        if (onErro != null) {
                            onErro.accept(e);
                        } else {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }.execute();
    }
    
    /**
     * Executa uma tarefa simples (void) em background.
     * 
     * @param tarefa tarefa a executar
     */
    public static void executarSimples(Runnable tarefa) {
        executarSimples(tarefa, null, null);
    }
    
    /**
     * Executa uma tarefa simples com callbacks.
     * 
     * @param tarefa tarefa a executar
     * @param onSucesso callback de sucesso
     * @param onErro callback de erro
     */
    public static void executarSimples(Runnable tarefa, Runnable onSucesso, Consumer<Exception> onErro) {
        executar(() -> {
            tarefa.run();
            return null;
        }, resultado -> {
            if (onSucesso != null) {
                onSucesso.run();
            }
        }, onErro);
    }
    
    /**
     * Executa uma tarefa com publicação de progresso.
     * 
     * @param tarefa tarefa com progresso
     * @param onSucesso callback de sucesso
     * @param onProgresso callback de progresso (na EDT)
     * @param <T> tipo do resultado
     * @param <P> tipo do progresso
     */
    public static <T, P> void comProgresso(TarefaComProgresso<T, P> tarefa, 
                                            Consumer<T> onSucesso,
                                            Consumer<P> onProgresso) {
        comProgresso(tarefa, onSucesso, onProgresso, null);
    }
    
    /**
     * Executa uma tarefa com publicação de progresso e tratamento de erro.
     * 
     * @param tarefa tarefa com progresso
     * @param onSucesso callback de sucesso
     * @param onProgresso callback de progresso
     * @param onErro callback de erro
     * @param <T> tipo do resultado
     * @param <P> tipo do progresso
     */
    public static <T, P> void comProgresso(TarefaComProgresso<T, P> tarefa,
                                            Consumer<T> onSucesso,
                                            Consumer<P> onProgresso,
                                            Consumer<Exception> onErro) {
        new SwingWorker<T, P>() {
            private Exception erro;
            
            @Override
            protected T doInBackground() {
                try {
                    return tarefa.executar(p -> publish(p));
                } catch (Exception e) {
                    erro = e;
                    return null;
                }
            }
            
            @Override
            protected void process(List<P> chunks) {
                if (onProgresso != null && !chunks.isEmpty()) {
                    // Processa apenas o último valor de progresso
                    onProgresso.accept(chunks.get(chunks.size() - 1));
                }
            }
            
            @Override
            protected void done() {
                if (erro != null) {
                    if (onErro != null) {
                        onErro.accept(erro);
                    } else {
                        erro.printStackTrace();
                    }
                } else if (onSucesso != null) {
                    try {
                        onSucesso.accept(get());
                    } catch (Exception e) {
                        if (onErro != null) {
                            onErro.accept(e);
                        }
                    }
                }
            }
            
            @SuppressWarnings("unchecked")
            private void publish(P chunk) {
                super.publish(chunk);
            }
        }.execute();
    }
    
    /**
     * Executa na EDT (Event Dispatch Thread).
     * 
     * <p>Se já está na EDT, executa imediatamente.
     * Caso contrário, agenda para execução na EDT.</p>
     * 
     * @param acao ação a executar
     */
    public static void naEDT(Runnable acao) {
        if (SwingUtilities.isEventDispatchThread()) {
            acao.run();
        } else {
            SwingUtilities.invokeLater(acao);
        }
    }
    
    /**
     * Executa na EDT e aguarda conclusão.
     * 
     * @param acao ação a executar
     * @throws Exception se houver erro na execução
     */
    public static void naEDTEAguardar(Runnable acao) throws Exception {
        if (SwingUtilities.isEventDispatchThread()) {
            acao.run();
        } else {
            SwingUtilities.invokeAndWait(acao);
        }
    }
}
