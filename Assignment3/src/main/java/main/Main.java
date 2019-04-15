package main;

import business.controller.Controller;

public class Main {

	public static void main(String[] args) {
		
		@SuppressWarnings("unused") // We actually use it, because instantiating it starts it up
		Controller testController = new Controller();
		
	}

}
