import java.io.PrintWriter;

public interface Command {
    void execute(String[] args, PrintWriter writer);
}
