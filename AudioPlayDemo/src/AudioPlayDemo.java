import javax.media.*; 
import javax.swing.JFrame;

import java.awt.*; 
import java.awt.event.*; 


class AudioPlayDemo extends Frame implements ActionListener, 
ControllerListener, ItemListener 
{ 
Player player; 
Component vc, cc; 
boolean first = true, loop = false; 
String currentDirectory; 
MediaPlayer (String title) 
{ 
super (title); 
addWindowListener 
(new WindowAdapter () 
{ 
public void windowClosing (WindowEvent e)
{ 
// 用户点击窗口系统菜单的关闭按钮 
// 调用dispose以执行windowClosed 
dispose (); 
}
public void windowClosed (WindowEvent e)
{ 
if (player != null) player.close (); 
System.exit (0); 
} 
}); 
Menu m = new Menu ("文件"); 
MenuItem mi = new MenuItem ("打开"); 
mi.addActionListener (this); 
m.add (mi); 
m.addSeparator (); 
CheckboxMenuItem cbmi = new CheckboxMenuItem ("循环", false); 
cbmi.addItemListener (this); 
m.add (cbmi); 
m.addSeparator (); 
mi = new MenuItem ("退出"); 
mi.addActionListener (this); 
m.add (mi); 
MenuBar mb = new MenuBar (); 
mb.add (m); 
setMenuBar (mb); 
setSize (200, 200); 
setVisible (true); 
} 
public void actionPerformed (ActionEvent e) 
{ 
if (e.getActionCommand ().equals ("退出")) 
{ 
// 调用dispose以便执行windowClosed 
dispose (); 
return; 
} 
FileDialog fd = new FileDialog (this, "打开媒体文件", 
FileDialog.LOAD); 
fd.setDirectory (currentDirectory); 
fd.show (); 
// 如果用户放弃选择文件，则返回 
if (fd.getFile () == null) return; 
currentDirectory = fd.getDirectory (); 
if (player != null) 
player.close (); 
try 
{ 
player = Manager.createPlayer (new MediaLocator ("file:" + fd.getDirectory () + fd.getFile ())); 
} 
catch (java.io.IOException e2) 
{ 
System.out.println (e2); 
return; 
} 
catch (NoPlayerException e2) 
{ 
System.out.println ("不能找到播放器."); 
return; 
} 
if (player == null) 
{ 
System.out.println ("无法创建播放器."); 
return; 
} 
first = false; 
setTitle (fd.getFile ()); 
player.addControllerListener (this); 
player.prefetch (); 
} 
public void controllerUpdate (ControllerEvent e) 
{ 
// 调用player.close()时ControllerClosedEvent事件出现。 
// 如果存在视觉部件，则该部件应该拆除（为一致起见， 
// 我们对控制面板部件也执行同样的操作） 
if (e instanceof ControllerClosedEvent) 
{ 
if (vc != null) 
{ 
remove (vc); 
vc = null; 
} 
if (cc != null) 
{ 
remove (cc); 
cc = null; 
} 
return; 
} 
if (e instanceof EndOfMediaEvent) 
{ 
if (loop) 
{ 
player.setMediaTime (new Time (0)); 
player.start (); 
} 
return; 
} 
if (e instanceof PrefetchCompleteEvent) 
{ 
player.start (); 
return; 
} 
if (e instanceof RealizeCompleteEvent) 
{ 
vc = player.getVisualComponent (); 
if (vc != null) 
add (vc); 
cc = player.getControlPanelComponent (); 
if (cc != null) 
add (cc, BorderLayout.SOUTH); 
pack (); 
} 
} 
public void itemStateChanged (ItemEvent e) 
{ 
loop = !loop; 
} 
public void paint (Graphics g) 
{ 
if (first) 
{ 
int w = getSize ().width; 
int h = getSize ().height; 
g.setColor (Color.blue); 
g.fillRect (0, 0, w, h); 
Font f = new Font ("DialogInput", Font.BOLD, 16); 
g.setFont (f); 
FontMetrics fm = g.getFontMetrics (); 
int swidth = fm.stringWidth ("*** 欢迎 ***"); 
g.setColor (Color.white); 
g.drawString ("*** 欢迎 ***", 
(w - swidth) / 2, 
(h + getInsets ().top) / 2); 
} 
// 调用超类Frame的paint()方法，该paint()方法将调用Frame包含的各个容器 
// 和部件（包括控制面板部件）的paint()方法。 
super.paint (g); 
} 
// 不执行背景清除操作，以免控制面板部件闪烁 
public void update (Graphics g) 
{ 
paint (g); 
} 
public static void main (String [] args)
{ 
new MediaPlayer ("媒体播放器1.0"); 
}
} 



