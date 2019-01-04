import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.filechooser.FileSystemView;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class MyFrame extends JFrame {
	private static final int WIDTH = 800;
	private static final int HEIGHT = 450;

	private static boolean jarLoaded;

	private static DefaultListModel<Class<?>> loadedClasses;
	private static JList<Class<?>> classList;
	private static Container container;

	private static JTextField methodDescription;
	private static JLabel arg1Label;
	private static JLabel arg2Label;
	private static JTextField arg1Field;
	private static JTextField arg2Field;
	private static JLabel resultLabel;
	private static JTextField resultField;
	private static JButton runBtn;

	public MyFrame() {
		container = new Container();
		loadedClasses = new DefaultListModel<>();

		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setBounds(200, 100, WIDTH, HEIGHT);
		container.setPreferredSize(new Dimension(WIDTH, HEIGHT));

		setMenu();
		setClassList();
		setComponentsLayout();

		setVisible(true);
		setContentPane(container);
	}

	private void setMenu() {
		JMenuBar menuBar = new JMenuBar();
		JMenu menu = new JMenu("Load file");
		JMenuItem menuItem = new JMenuItem("Jar");
		menuBar.add(menu);
		menu.add(menuItem);
		menuItem.addActionListener(e -> loadClasses());
		menuBar.setBounds(0, 0, WIDTH, 20);
		container.add(menuBar);
	}

	private void loadClasses() {
		JFileChooser jFileChooser = new JFileChooser(FileSystemView.getFileSystemView().getHomeDirectory());
		jFileChooser.setDialogTitle("Select jar file");
		jFileChooser.setAcceptAllFileFilterUsed(false);

		FileNameExtensionFilter filter = new FileNameExtensionFilter("Jar files", "jar");

		jFileChooser.addChoosableFileFilter(filter);

		jFileChooser.showOpenDialog(null);

		String path;
		JarFile jarFile = null;

		try {
			path = jFileChooser.getSelectedFile().getAbsolutePath();
			jarFile = new JarFile(path);
			Enumeration<JarEntry> entry = jarFile.entries();

			URL[] urls = { new URL("jar:file:" + path + "!/") };
			URLClassLoader loader = URLClassLoader.newInstance(urls);

			loadedClasses.clear();

			while (entry.hasMoreElements()) {
				JarEntry jarEntry = entry.nextElement();
				if (jarEntry.isDirectory() || !jarEntry.getName().endsWith(".class")) {
					continue;
				}

				String className = jarEntry.getName().substring(0, jarEntry.getName().length() - 6);
				className = className.replace('/', ',');

				try {
					Class<?> cl = loader.loadClass(className);

					if (cl.isAnnotationPresent(Description.class)) {
						Description description = (Description) cl.getAnnotation(Description.class);

						if (ICallable.class.isAssignableFrom(cl)) {
							ICallable callable = (ICallable) cl.newInstance();

							if (callable != null) {
								loadedClasses.addElement(cl);
							}
						}
					}
				} catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			jarLoaded = true;
			if (jarFile != null) {
				try {
					jarFile.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void setClassList() {
		classList = new JList<>(loadedClasses);
		classList.setBounds(20, 50, 200, 300);
		classList.setBackground(Color.lightGray);

		classList.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (classList.getSelectedValue() != null) {
					String description = classList.getSelectedValue().getAnnotation(Description.class).description();
					methodDescription.setText(description);
				}
			}
		});
		container.add(classList);
	}

	private void setComponentsLayout() {
		methodDescription = new JTextField();
		methodDescription.setBounds(250, 50, 500, 50);
		methodDescription.setHorizontalAlignment(JTextField.CENTER);
		container.add(methodDescription);

		arg1Field = new JTextField();
		arg1Field.setBounds(300, 180, 100, 25);
		container.add(arg1Field);

		arg2Field = new JTextField();
		arg2Field.setBounds(300, 230, 100, 25);
		container.add(arg2Field);

		arg1Label = new JLabel("1. parametr:");
		arg1Label.setBounds(300, 160, 100, 25);
		container.add(arg1Label);

		arg2Label = new JLabel("2. parametr:");
		arg2Label.setBounds(300, 210, 100, 25);
		container.add(arg2Label);

		resultLabel = new JLabel("Wynik:");
		resultLabel.setBounds(450, 300, 100, 25);
		container.add(resultLabel);

		resultField = new JTextField();
		resultField.setBounds(450, 325, 230, 25);
		container.add(resultField);

		runBtn = new JButton("Wykonaj");
		runBtn.setBounds(300, 300, 100, 50);

		runBtn.addActionListener(e -> {
			if (jarLoaded) {
				ICallable callable;
				String methodresultField;

				try {
					callable = (ICallable) classList.getSelectedValue().newInstance();

					methodresultField = callable.call(arg1Field.getText(), arg2Field.getText());

					resultField.setText(methodresultField);
				} catch (InstantiationException | IllegalAccessException e1) {
					e1.printStackTrace();
				}
			} else {
				resultField.setText("A moze by tak zaladowac jara najpierw?");
			}
		});
		container.add(runBtn);
	}

}