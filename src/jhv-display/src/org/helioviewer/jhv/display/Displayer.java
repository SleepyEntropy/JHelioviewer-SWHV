package org.helioviewer.jhv.display;

import java.util.LinkedList;

public class Displayer{
	
    private static Displayer instance = new Displayer();
    private final LinkedList<DisplayListener> listeners = new LinkedList<DisplayListener>();
    private final LinkedList<RenderListener> renderListeners = new LinkedList<RenderListener>();
    private GL3DComponentFakeInterface gl3dcomponent;

    public void register(GL3DComponentFakeInterface gl3dcomponent){
    	this.gl3dcomponent = gl3dcomponent;
    }
	public static Displayer getSingletonInstance() {
        if (instance == null) {
            throw new NullPointerException("Displayer not initialized");
        }
        return instance;
    }
    public void addListener(final DisplayListener listener) {
        listeners.add(listener);
    }
    
    public void removeListener(final DisplayListener renderListener) {
    	synchronized(renderListener){
    		listeners.remove(renderListener);
    	}
    }
    public void addRenderListener(final RenderListener renderListener) {
    	synchronized(renderListener){
    		renderListeners.add(renderListener);
    	}
    }
    public void removeRenderListener(final RenderListener listener) {
    	synchronized(renderListeners){
    		renderListeners.remove(listener);
    	}
    }    
    public void render(){
    	synchronized(renderListeners){
	        for(final RenderListener renderListener : renderListeners) {
	        	renderListener.render();
	        }
    	}
    }

    public void display(){
        for(final DisplayListener listener : listeners) {
            listener.display();
        }
    }
    public void animate(){
    	gl3dcomponent.activate();
    }
    public void stopAnimate(){

    	gl3dcomponent.deactivate();
    }
}
