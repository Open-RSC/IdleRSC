package controller;

import orsc.enumerations.MessageType;

/**
 * This is a helper class for comparing messages from RSC. The equals operator has been overloaded.
 * 
 * @author Dvorak
 *
 */
public class ORSCMessage {

	String color;
	int crownId;
	String message;
	String sender;
	int timeout;
	MessageType type;
	
	public ORSCMessage(String _color, int _crownId, String _message, String _sender, int _timeout, MessageType _type) {
		color = _color;
		crownId = _crownId;
		message = _message;
		sender = _sender;
		timeout = _timeout;
		type = _type;
	}

	public ORSCMessage() {
		// TODO Auto-generated constructor stub
	}

	public String getColor() {
		return color;
	}

	public int getCrownId() {
		return crownId;
	}

	public String getMessage() {
		return message;
	}

	public String getSender() {
		return sender;
	}

	public int getTimeout() {
		return timeout;
	}

	public MessageType getType() {
		return type;
	}	
	
	@Override
	public boolean equals(Object o) {
		
		if(!(o instanceof ORSCMessage))
			return false;
		
		if(o == this)
			return true;
		
		ORSCMessage m = (ORSCMessage) o;
		
		if(m.getMessage() == null || this.getMessage() == null)
			return false;
		
		if(m.getSender() != null && this.getSender() != null) {
			return m.getMessage().equals(this.getMessage()) && m.getSender().equals(this.getSender());
		} else {
			return m.getMessage().equals(this.getMessage());
		}
		
	}
	
}
