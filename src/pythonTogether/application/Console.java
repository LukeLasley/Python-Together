package pythonTogether.application;

import java.io.IOException;
import java.io.OutputStream;
import javafx.scene.control.TextArea;

public class Console extends OutputStream {

    private TextArea output;
    private UserViewController control;

    public Console(TextArea textArea, UserViewController control) {
        this.output = textArea;
        this.control = control;
    }

    @Override
    public void write(int i) throws IOException {
    	control.updateConsole(String.valueOf((char) i),output);
    }

}