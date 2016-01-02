package krut.KRUT_GUI.tray;


public class TrayEvent {

	private TrayEventType type; 
	private Object source;
	private String message;
	public TrayEvent( Object object, TrayEventType type) {
		super();
		this.type = type;
		this.source = object;
	}

	public TrayEvent(Object source2, TrayEventType type,
			String string) {
		this(source2, type); 
		this.message= string; 
		// TODO Auto-generated constructor stub
	}

	public TrayEventType getType() {
		return type;
	}
	public Object getSource() {
		return source;
	}

	public String getMessage() {
		return message;
	} 
	

}
