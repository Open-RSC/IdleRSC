package scripting.apos;
import compatibility.apos.Script;
import java.util.Locale;

public final class BronzePickaxe extends Script {
    
    private PathWalker pw;
    private PathWalker.Path walkingPath;

    public BronzePickaxe(String ex) {
        this.pw = new PathWalker(ex);
    }
    
    @Override
    public void init(String params) {
   	
        if (walkingPath == null) {
            pw.init(null);
            walkingPath = pw.calcPath(getX(), getY(), 231, 509);
        }
        
        pw.setPath(walkingPath);
    }
    
    @Override
    public int main() {
    	if(pw.walkPath()) return 100;
    	
    	while(this.getInventoryCount(156) < 1) {
    		this.pickupItem(156, 231, 508);
    		return 1000;
    	}
    	
    	stopScript();
    	System.exit(0);
    	return 0;
    }
}
