package scripting.apos;
import compatibility.apos.Script;
import java.util.Locale;

public class SleepingBag extends Script {
    private PathWalker pw;
    private PathWalker.Path walkingPath;

    public SleepingBag(String ex) {
        this.pw = new PathWalker(ex);
    }
    
    @Override
    public void init(String params) {
    	int x = 120;
    	int y = 648;
    	
        if (walkingPath == null) {
            pw.init(null);
            walkingPath = pw.calcPath(getX(), getY(), x, y);
        }
        
        pw.setPath(walkingPath);
    }
    
    @Override
    public int main() {
    	if(this.isLoggedIn() && this.getInventoryCount(1263) > 0)
    		System.exit(0);
    	
    	if(pw.walkPath()) return 100;
    	
    	System.exit(0);
    	stopScript();
    	return 0;
    }
}
