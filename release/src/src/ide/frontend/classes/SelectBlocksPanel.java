package ide.frontend.classes;

import ide.backend.language.LanguageModule;
import ide.backend.model.BlockModel;
import ide.backend.model.ConnectedBlocks;



import ide.backend.model.conditions.ForeverModel;
import ide.backend.model.conditions.IfBlockModel;
import ide.backend.model.conditions.IfElseModel;
import ide.backend.model.conditions.WhileModel;
import ide.backend.model.function.AccessModel;
import ide.backend.model.function.EmitModel;
import ide.backend.model.function.FunctionModel;
import ide.backend.model.function.HandlerModel;
import ide.backend.model.function.ReturnModel;
import ide.backend.model.locks.LockModel;
import ide.backend.model.locks.UnLockModel;
import ide.backend.model.math.RandomModel;
import ide.backend.model.operator.OperatorModel;
import ide.backend.model.operator.UnOperatorModel;
import ide.backend.model.other.SleepModel;
import ide.backend.model.physicModels.ChangeAppearanceModel;
import ide.backend.model.physicModels.HideModel;
import ide.backend.model.physicModels.MoveModel;
import ide.backend.model.physicModels.ShowModel;
import ide.backend.model.string.CharAtModel;
import ide.backend.model.string.ConcatModel;
import ide.backend.model.string.LengthModel;
import ide.backend.model.variables.AbstractRefVariabelModel;
import ide.backend.model.variables.MemberModel;
import ide.backend.model.variables.PrintModel;
import ide.backend.model.variables.SetModel;
import ide.backend.model.variables.ValueModel;
import ide.backend.model.variables.VariableModel;
import ide.frontend.classes.views.AbstractBlockController;
import ide.frontend.classes.views.AnchorBlock;
import ide.frontend.classes.views.BlockView;
import ide.frontend.classes.views.conditions.IfElseView;
import ide.frontend.classes.views.conditions.ForeverView;
import ide.frontend.classes.views.conditions.IfView;
import ide.frontend.classes.views.conditions.WhileView;
import ide.frontend.classes.views.functions.AccesView;
import ide.frontend.classes.views.functions.EmitView;
import ide.frontend.classes.views.functions.FunctionView;
import ide.frontend.classes.views.functions.HandlerView;
import ide.frontend.classes.views.functions.ReturnView;
import ide.frontend.classes.views.locks.LockView;
import ide.frontend.classes.views.locks.UnLockView;
import ide.frontend.classes.views.math.RandomView;
import ide.frontend.classes.views.other.SleepView;
import ide.frontend.classes.views.physicViews.ChangeAppearanceView;
import ide.frontend.classes.views.physicViews.HideView;
import ide.frontend.classes.views.physicViews.MoveView;
import ide.frontend.classes.views.physicViews.ShowView;
import ide.frontend.classes.views.string.CharAtView;
import ide.frontend.classes.views.string.ConcatView;
import ide.frontend.classes.views.string.LengthView;
import ide.frontend.classes.views.operations.BinaryOperator;
import ide.frontend.classes.views.operations.MakeVarView;
import ide.frontend.classes.views.operations.PrintView;
import ide.frontend.classes.views.operations.SetBlockView;
import ide.frontend.classes.views.operations.UnOperatorView;
import ide.frontend.classes.views.variables.ValueView;
import ide.frontend.classes.views.variables.VariableRefView;
import ide.frontend.mvcbase.Controller;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;

/**
 * This is the view where the user can select blocks to place on the class panel
 * @author axel && Matthijs
 *
 */
public class SelectBlocksPanel implements Observer {
	
	/**PANELS**/
	private JScrollPane _outer;
	private JPanel _main;
	private JPanel _container;
	private JPanel _loadedPanel;
	private ArrayList<HashMap<Class<? extends BlockView>, Class<? extends BlockModel>>> _blocks;
	
	private JComboBox<String> _dropdown;
	private JPanel _view;
	
	/**FIELDS**/
	private JTabbedPane _ide;
	private ActionListener _listener;
	private LanguageModule _lang;
	
	/**
	 * Class that holds variables needed to instantiate a new class
	 * @author axel
	 *
	 */
	public class LoadClass {
		private Class<? extends BlockView> _key;
		private Class<? extends BlockModel> _val;
		
		private boolean _isMemberVar;
		private MemberModel _memberModel;
		
		public LoadClass(Class<? extends BlockView> key, Class<? extends BlockModel> val) {
			_key = key;
			_val = val;
			_isMemberVar = false;
		}
		
		public LoadClass(MemberModel mdl) {
			_isMemberVar = true;
			_memberModel = mdl;
		}
		
		public void createBlockView(int x,int y) throws 
				InstantiationException, IllegalAccessException, NoSuchMethodException, 
				SecurityException, IllegalArgumentException, InvocationTargetException {
			if(_ide.getSelectedComponent().getName().equals("$addTab"))
				return;
			
			if (_isMemberVar) {
				createMemberBlock(x, y);
			} else {
				createOtherBlock(x, y);
			}
		}
		
		private void createMemberBlock(int x, int y) {
			AbstractRefVariabelModel ref =_memberModel.makeReference(null);
			VariableRefView view = new VariableRefView(ref, null, 
						((ClassView)_ide.getSelectedComponent()).getPanel(), _lang);
			view.set(x, y);
			((ClassView)_ide.getSelectedComponent()).getPanel().add(view, ref);
		}
		
		private void createOtherBlock(int x, int y) throws 
				NoSuchMethodException, SecurityException, InstantiationException, 
				IllegalAccessException, IllegalArgumentException, InvocationTargetException {
			BlockModel m = _val.newInstance();
			Constructor<? extends BlockView> c = _key.getConstructor(Observable.class,Controller.class, IDEPanel.class, LanguageModule.class);
			Object[] args = new Object[4];
			args[0] = m;
			args[1] = null;
			args[2] = ((ClassView)_ide.getSelectedComponent()).getPanel();
			args[3] = _lang;
			BlockView v = (BlockView) c.newInstance(args);
			
			
			BlockView v3 = v;
			BlockModel model = m;
			((ClassView)_ide.getSelectedComponent()).getPanel().addEmit(v);
			if (AbstractBlockController.needsAnchor(v)) {
				model = new ConnectedBlocks(null);
				v3 = new AnchorBlock(model, null, ((ClassView)_ide.getSelectedComponent()).getPanel(), _lang); 
				v.set(x,y);
				v3.set(x, y);
				v3.add(v);
				((ClassView)_ide.getSelectedComponent()).getPanel().add(v3, model);
			} else {
				((ClassView)_ide.getSelectedComponent()).getPanel().add(v3, model);
				v.set(x,y);
			}			
			
		}
	}
	
	private LoadClass _loaded;
	
	/**
	 * Creates a new SelectBlocksPanel
	 * @param tabs tabs panel for classes
	 * @param lang language module of the ide.
	 */
	public SelectBlocksPanel(JTabbedPane tabs, LanguageModule lang) {
		_ide = tabs;
		_lang = lang;
		_lang.addObserver(this);
		initPanels();
		initCategories();
		DrawBlocks();
	}
	
	/**
	 * Initiate the panels
	 */
	private void initPanels(){
		_dropdown = new JComboBox<>();
		_view = new JPanel(new GridLayout(0,1));
		_view.setBorder(BorderFactory.createEmptyBorder());
		_loadedPanel = new JPanel();
		_loadedPanel.setBorder(BorderFactory.createTitledBorder(_lang.getString("loadedborder")));
		
		_container = new JPanel(new BorderLayout());
		_container.setBorder(BorderFactory.createEmptyBorder());
		
		_main = new JPanel(new BorderLayout());
		_main.setBorder(BorderFactory.createTitledBorder(_lang.getString("blockborder")));
		
		_container.add(_loadedPanel, BorderLayout.NORTH);
		_main.add(_dropdown, BorderLayout.NORTH);
		
		_outer = new JScrollPane(_view);
		_outer.setBorder(BorderFactory.createEmptyBorder());
		_main.add(_outer, BorderLayout.CENTER);
		_container.add(_main, BorderLayout.CENTER);
		_listener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				DrawBlocks();
			}
			
		};
	}
	
	/**
	 * Initiate all categories
	 */
	private void initCategories() {
		_blocks = new ArrayList<>();
		initVariables();
		initConditions();
		initFunctions();
		initPhysics();
		initLocks();
		initTrivial();
		initMath();
		initString();
		
		_dropdown.setSelectedItem(_lang.getString("varCat"));
		_dropdown.addActionListener(_listener);
	}
	
	/**
	 * Initiate the variables category
	 */
	private void initVariables() {
		_dropdown.addItem(_lang.getString("varCat"));
		
		HashMap<Class<? extends BlockView>, Class<? extends BlockModel>> cat = new HashMap<>();
		_blocks.add(cat);
		
		cat.put(ValueView.class, ValueModel.class);
		cat.put(PrintView.class, PrintModel.class);
		cat.put(MakeVarView.class, VariableModel.class);
		cat.put(SetBlockView.class, SetModel.class);
	}
	
	/**
	 * Initiate the variables category
	 */
	private void initConditions() {
		_dropdown.addItem(_lang.getString("condCat"));
		
		HashMap<Class<? extends BlockView>, Class<? extends BlockModel>> cat = new HashMap<>();
		_blocks.add(cat);
		
		cat.put(IfView.class, IfBlockModel.class);
		cat.put(WhileView.class, WhileModel.class);
		cat.put(IfElseView.class, IfElseModel.class);
		cat.put(ForeverView.class, ForeverModel.class);
	}
	
	/**
	 * Initate the functions category
	 */
	private void initFunctions() {
		_dropdown.addItem(_lang.getString("funcCat"));
		
		HashMap<Class<? extends BlockView>, Class<? extends BlockModel>> cat = new HashMap<>();
		_blocks.add(cat);
		
		cat.put(HandlerView.class, HandlerModel.class);
		cat.put(EmitView.class, EmitModel.class);
		cat.put(AccesView.class, AccessModel.class);
		cat.put(FunctionView.class, FunctionModel.class);
		cat.put(ReturnView.class, ReturnModel.class);
	}
	
	/**
	 * Initiate the physics categorie
	 */
	private void initPhysics() {
		_dropdown.addItem(_lang.getString("physCat"));
		
		HashMap<Class<? extends BlockView>, Class<? extends BlockModel>> cat = new HashMap<>();
		_blocks.add(cat);
		
		cat.put(ChangeAppearanceView.class, ChangeAppearanceModel.class);
		cat.put(ShowView.class, ShowModel.class);
		cat.put(HideView.class, HideModel.class);
		cat.put(MoveView.class, MoveModel.class);
	}
	
	/**
	 * Initiate the locks categorie
	 */
	private void initLocks() {
		_dropdown.addItem(_lang.getString("locksCat"));
		
		HashMap<Class<? extends BlockView>, Class<? extends BlockModel>> cat = new HashMap<>();
		_blocks.add(cat);
		
		cat.put(LockView.class, LockModel.class);
		cat.put(UnLockView.class, UnLockModel.class);
	}
	
	private void initTrivial() {
		_dropdown.addItem(_lang.getString("trivCat"));
		
		HashMap<Class<? extends BlockView>, Class<? extends BlockModel>> cat = new HashMap<>();
		_blocks.add(cat);
		
		cat.put(SleepView.class, SleepModel.class);
	}
	
	private void initMath(){
		_dropdown.addItem(_lang.getString("mathCat"));
		HashMap<Class<? extends BlockView>, Class<? extends BlockModel>> cat = new HashMap<>();
		_blocks.add(cat);
		
		cat.put(RandomView.class, RandomModel.class);
		cat.put(BinaryOperator.class, OperatorModel.class);
		cat.put(UnOperatorView.class, UnOperatorModel.class);
	}
	
	private void initString() {
		_dropdown.addItem(_lang.getString("stringCat"));
		HashMap<Class<? extends BlockView>, Class<? extends BlockModel>> cat = new HashMap<>();
		_blocks.add(cat);
		
		cat.put(LengthView.class, LengthModel.class);
		cat.put(ConcatView.class, ConcatModel.class);
		cat.put(CharAtView.class, CharAtModel.class);
	}

	/**
	 * Draws all blocks from the selected categorie to the view
	 */
	private void DrawBlocks(){
		_view.removeAll();
		HashMap<Class<? extends BlockView>, Class<? extends BlockModel>> cat = _blocks.get(_dropdown.getSelectedIndex());
		for (HashMap.Entry<Class<? extends BlockView>, Class<? extends BlockModel>> entry : cat.entrySet()) {
		   try {
				JPanel out = (JPanel)entry.getKey().getMethod("getRepresentative", LanguageModule.class).invoke(null, _lang);
				out.setBackground(Color.black);
				JPanel dummy = new JPanel();
				dummy.add(out);
				_view.add(dummy);
				out.addMouseListener(new MouseListener() {
					
					@Override
					public void mouseReleased(MouseEvent e) {
						// Do nothing
						
					}
					
					@Override
					public void mousePressed(MouseEvent e) {
						// Do nothing
						
					}
					
					@Override
					public void mouseExited(MouseEvent e) {
						// Do nothing
						
					}
					
					@Override
					public void mouseEntered(MouseEvent e) {
						// Do nothing
						
					}
					
					@Override
					public void mouseClicked(MouseEvent e) {
						try {
							_loadedPanel.removeAll();
							JPanel out = (JPanel) entry.getKey().getMethod("getRepresentative", LanguageModule.class).invoke(null, _lang);
							out.setBackground(Color.black);
							JPanel dummy = new JPanel();
							dummy.add(out);
							_loadedPanel.add(dummy);
							_loaded = new LoadClass(entry.getKey(), entry.getValue());
							
							_loadedPanel.revalidate();
							_loadedPanel.repaint();
							_container.revalidate();
							_container.repaint();
							
						} catch (IllegalAccessException
								| IllegalArgumentException
								| InvocationTargetException
								| NoSuchMethodException | SecurityException e1) {
							e1.printStackTrace();
						}
					}
				});
				
			} catch ( IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
				e.printStackTrace();
			}
		}
		_view.revalidate();
		_view.repaint();
	}
	
	/**
	 * Deselect the loaded class
	 */
	public void deselectLoaded() {
		_loadedPanel.removeAll();
		_loaded = null;
		_loadedPanel.revalidate();
		_loadedPanel.repaint();
		_container.revalidate();
		_container.repaint();
		
	}
	
	/**
	 * Get the loaded class
	 * @return
	 */
	public LoadClass getLoaded() {
		return _loaded;
	}
	
	/**
	 * Load a member variable
	 * @param mdl
	 */
	public void load(MemberModel mdl) {
		_loadedPanel.removeAll();
		_loaded = new LoadClass(mdl);
		JPanel out = VariableRefView.getRepresentative(_lang);
		out.setBackground(Color.black);
		JPanel dummy = new JPanel();
		dummy.add(out);
		_loadedPanel.add(dummy);
		
		_loadedPanel.revalidate();
		_loadedPanel.repaint();
		_container.revalidate();
		_container.repaint();
	}

	/**
	 * Get the UI
	 * @return
	 */
	public JPanel getUI(){
		return _container;
	}
	
	/**
	 * Update the language
	 */
	private void updateLanguage() {
		_dropdown.removeActionListener(_listener);
		_dropdown.removeAllItems();
		initCategories();
		DrawBlocks();
		
		_main.setBorder(BorderFactory.createTitledBorder(_lang.getString("blockborder")));
		_main.repaint();
		
		_loadedPanel.setBorder(BorderFactory.createTitledBorder(_lang.getString("loadedborder")));
		_loadedPanel.repaint();
		deselectLoaded();
	}

	@Override
	public void update(Observable arg0, Object arg1) {
		if (arg0 instanceof LanguageModule) {
			updateLanguage();
		}
	}
}
