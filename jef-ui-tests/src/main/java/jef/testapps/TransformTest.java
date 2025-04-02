package jef.testapps;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Transform;
import org.eclipse.swt.widgets.Shell;

public class TransformTest
{

	public static void main(String [] args)
	{
		Shell shell = new Shell();
		shell.setMaximized(true);
		shell.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_BLACK));
		shell.addMouseListener(new MouseAdapter() 
		{

			@Override
			public void mouseUp(MouseEvent e)
			{
				// TODO Auto-generated method stub
				super.mouseUp(e);
				shell.redraw();
			}
			
		});
		
		shell.addPaintListener(event -> 
		{
			GC gc = event.gc;
			
			Transform t = new Transform(shell.getDisplay());

			// Put transformations here
			t.scale(1.0f, -1.0f);
			t.translate(0, -1280);
			
			gc.setTransform(t);
			
			// Put drawing here
			
			gc.setBackground(shell.getDisplay().getSystemColor(SWT.COLOR_BLUE));
			gc.fillRectangle(700, 400, 500, 250);

			gc.setForeground(shell.getDisplay().getSystemColor(SWT.COLOR_YELLOW));
			gc.drawText("This is some text.", 800, 450);
			t.dispose();
		});

		shell.open();

		while (!shell.isDisposed())
		{
			try
			{
				if (!shell.getDisplay().readAndDispatch())
				{
					shell.getDisplay().sleep();
				}
			}
			catch (final Throwable t)
			{
				t.printStackTrace();
			}
		}
	
	}

}
