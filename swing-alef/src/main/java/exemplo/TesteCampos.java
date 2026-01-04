package exemplo;

import campo.*;
import nucleo.Validavel;
import com.formdev.flatlaf.FlatLightLaf;
import javax.swing.*;
import java.awt.*;

public class TesteCampos {
    
    // Simula CampoForm exatamente
    static abstract class SimulaCampoForm<T> extends JPanel implements Validavel {
        protected JLabel label;
        
        public SimulaCampoForm(String titulo) {
            super();
            setLayout(new BorderLayout(0, 2));
            label = new JLabel(titulo);
            add(label, BorderLayout.NORTH);
        }
        
        public abstract T getValue();
        public abstract void setValue(T value);
        public boolean validar() { return true; }
        public String getMensagemErro() { return null; }
        public void mostrarErro() {}
        public void limparErro() {}
    }
    
    // Simula CampoTexto
    static class SimulaCampoTexto extends SimulaCampoForm<String> {
        private JTextField field;
        
        public SimulaCampoTexto(String titulo) {
            super(titulo);
            field = new JTextField(20);
            add(field, BorderLayout.CENTER);
        }
        
        public String getValue() { return field.getText(); }
        public void setValue(String v) { field.setText(v); }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(new FlatLightLaf());
            } catch (Exception e) {}
            
            JFrame frame = new JFrame("Teste Debug Campos");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(500, 400);
            
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            // TESTE 1: SimulaCampoTexto (nossa simulação)
            SimulaCampoTexto simula = new SimulaCampoTexto("1. SimulaCampoTexto");
            simula.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));
            panel.add(simula);
            panel.add(Box.createVerticalStrut(10));
            
            // TESTE 2: CampoTexto real
            CampoTexto texto = new CampoTexto("2. CampoTexto Real");
            texto.setMaximumSize(new Dimension(Integer.MAX_VALUE, 55));
            panel.add(texto);
            
            panel.add(Box.createVerticalGlue());
            
            frame.add(new JScrollPane(panel));
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
