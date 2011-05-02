package jp.tonyu.rhiveconnect;

import java.applet.Applet;
import java.util.Vector;

import netscape.javascript.JSObject;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;

public class RCApplet extends Applet implements Runnable {
	private boolean optimizeDisabled=false;
	@Override
	public void start() {
		try {
			System.out.println("Codebase="+getCodeBase());
			Context c=Context.enter();
			root=c.initStandardObjects();
			c.evaluateString(root, "x=0;", "CMD", 1, null);		
			System.out.println("Optimization OK");
		} catch (Exception e) {
			optimizeDisabled=true;
			e.printStackTrace();
		} finally {
			Context.exit();
		}	
		new Thread(this).start();
	}
	Vector<Object[]> queue=new Vector<Object[]>();
	public void run() {
		while (true) {
			/*Object o=root.get("onTimer",root);
			if (o instanceof JSObject) {
				JSObject j = (JSObject) o;
				j.call("call", new Object[0]);
			}*/
			try {
				while (queue.size()>0) {
					//System.out.println(queue.size());
					Object[] q=queue.remove(0);
					q[1]=eval(q[0].toString());
				}
				Thread.sleep(10);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	public Object asyncEval(String s) {
		String not="NOT";
		Object[] place=new Object[]{s,not};
		queue.add(place);
		while (place[1]==not) {
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return place[1];
	}
	Scriptable root;
	public Object eval(String s) {
		try {
			System.out.println("Evaling - "+s+ " OptimizeDisabled="+optimizeDisabled+"  codebase="+getCodeBase());
			Context c=Context.enter();
			if (optimizeDisabled) c.setOptimizationLevel(-1);
			Object res= c.evaluateString(root, s, "CMD", 1, null);		
			return res;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		} finally {
			Context.exit();
		}	
	}
	public void put(String key,Object val) {
		root.put(key, root, val);
	}
}
