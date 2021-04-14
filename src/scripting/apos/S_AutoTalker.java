package scripting.apos;
import compatibility.apos.Script;
public final class S_AutoTalker extends Script {

    private String message;
    private int delay1;
    private int delay2;

    public S_AutoTalker(String ex) {
//        super(ex);
    }

    @Override
    public void init(String params) {
        try {
            String[] split = params.split(",");
            message = split[0];
            delay1 = Integer.parseInt(split[1]);
            delay2 = Integer.parseInt(split[2]);
            setTypeLine(message);
        } catch (Throwable t) {
            System.out.println(t);
            System.out.println("Parameters: message,secondsMin,secondsMax");
        }
    }

    @Override
    public int main() {
        if (next()) {
            return random(delay1, delay2) * 1000;
        }
        return 0;
    }
}