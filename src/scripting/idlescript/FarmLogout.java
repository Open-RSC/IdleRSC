package scripting.idlescript;

public class FarmLogout extends IdleScript {
	
	public int start(String[] parameters) {
		while(!controller.isLoggedIn()) return 100;
		
		controller.logout();
		controller.stop();
		System.exit(0);
		return 100;
	}
}