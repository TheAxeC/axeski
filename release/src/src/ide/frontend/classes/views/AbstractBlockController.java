package ide.frontend.classes.views;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Observable;

import ide.backend.model.BlockModel;
import ide.backend.model.variables.AbstractRefVariabelModel;
import ide.frontend.classes.views.math.RandomView;
import ide.frontend.classes.views.physicViews.HideView;
import ide.frontend.classes.views.physicViews.MoveView;
import ide.frontend.classes.views.physicViews.ShowView;
import ide.frontend.classes.views.conditions.ForeverView;
import ide.frontend.classes.views.conditions.IfView;
import ide.frontend.classes.views.conditions.WhileView;
import ide.frontend.classes.views.functions.AccesView;
import ide.frontend.classes.views.functions.FunctionView;
import ide.frontend.classes.views.functions.FunctionCallView;
import ide.frontend.classes.views.functions.HandlerView;
import ide.frontend.classes.views.functions.ReturnView;
import ide.frontend.classes.views.locks.LockView;
import ide.frontend.classes.views.locks.UnLockView;
import ide.frontend.classes.views.operations.BinaryOperator;
import ide.frontend.classes.views.operations.MakeVarView;
import ide.frontend.classes.views.operations.PrintView;
import ide.frontend.classes.views.operations.SetBlockView;
import ide.frontend.classes.views.operations.UnOperatorView;
import ide.frontend.classes.views.physicViews.ChangeAppearanceView;
import ide.frontend.classes.views.string.CharAtView;
import ide.frontend.classes.views.string.ConcatView;
import ide.frontend.classes.views.string.LengthView;
import ide.frontend.classes.views.variables.ValueView;
import ide.frontend.classes.views.variables.VariableRefView;
import ide.frontend.mvcbase.AbstractController;

/**
 * Base class for all controllers
 * @author axel
 *
 */
public abstract class AbstractBlockController extends AbstractController{

	private static final ArrayList<Class <? extends BlockView>> _acceptedBlocks =  new ArrayList<>();
	private static final ArrayList<Class <? extends BlockView>> _acceptedConnectedBlocks =  new ArrayList<>();
	private static final ArrayList<Class <? extends BlockView>> _topLevelBlocks =  new ArrayList<>();
	
	static{
		//lose blocks
		_acceptedBlocks.add(ValueView.class);
		_acceptedBlocks.add(RandomView.class);
		_acceptedBlocks.add(VariableRefView.class);
		_acceptedBlocks.add(LengthView.class);
		_acceptedBlocks.add(ConcatView.class);
		_acceptedBlocks.add(CharAtView.class);
		_acceptedBlocks.add(BinaryOperator.class);
		_acceptedBlocks.add(UnOperatorView.class);
		_acceptedBlocks.add(AccesView.class);

		//connectedblocks
		_acceptedConnectedBlocks.add(PrintView.class);
		_acceptedConnectedBlocks.add(IfView.class);
		_acceptedConnectedBlocks.add(AnchorBlock.class);

		_acceptedConnectedBlocks.add(ShowView.class);
		_acceptedConnectedBlocks.add(HideView.class);
		_acceptedConnectedBlocks.add(MoveView.class);
		
		_acceptedConnectedBlocks.add(FunctionCallView.class);
		_acceptedConnectedBlocks.add(ReturnView.class);
		_acceptedConnectedBlocks.add(SetBlockView.class);
		_acceptedConnectedBlocks.add(MakeVarView.class);
		_acceptedConnectedBlocks.add(WhileView.class);
		_acceptedConnectedBlocks.add(ForeverView.class);
		_acceptedConnectedBlocks.add(ChangeAppearanceView.class);
		_acceptedConnectedBlocks.add(LockView.class);
		_acceptedConnectedBlocks.add(UnLockView.class);
		
		//toplevelBlocks

		_topLevelBlocks.add(HandlerView.class);
		_topLevelBlocks.add(FunctionView.class);
	}
	
	public AbstractBlockController(Observable model) {
		super(model);
	}
	
	/**
	 * Set the position of this block
	 * @param x the x position
	 * @param y the y position
	 */
	public void updatePosition(int x, int y) {
		((BlockModel) getModel()).setPos(new Point(x,y));
	}	
	
	/**
	 * Accept blocks that can be connected to one another
	 * @param c the block view to check
	 * @return whether it accepts the param or not
	 */
	public boolean acceptConnected(BlockView c){
		if(_acceptedConnectedBlocks.contains(c.getClass()))
			return true;
		return false;
		
	}
	
	/**
	 * Accepts a single block
	 * @param c the block view to check
	 * @return whether it accepts the param or not
	 */
	public boolean acceptSingle(BlockView c){
		if(_acceptedBlocks.contains(c.getClass()))
			return true;
		return false;
	}
	
	/**
	 * Accept only a variable
	 * @param c the block view to check
	 * @return whether it accepts the param or not
	 */
	public boolean acceptVar(BlockView c) {
		return c.getModel() instanceof AbstractRefVariabelModel;
	}
	
	/**
	 * Update the position of the model
	 * @param x the x position
	 * @param y the y position
	 */
	public void updateLocation(int x, int y){
		((BlockModel)getModel()).setPos(new Point(x, y));
	}
	
	/**
	 * Check if this model needs an anchor
	 * @param c the block view to check
	 * @return true if an anchor is needed
	 */
	public static boolean needsAnchor(BlockView v) {
		if(_acceptedBlocks.contains(v.getClass()) || _topLevelBlocks.contains(v.getClass()))
			return false;
		return true;
	}
	
	/**
	 * Set the break status of the model
	 * @param status the break status
	 */
	public void setBreakStatus(boolean status) {
		((BlockModel)getModel()).setBreak(status);
	}
	
	/**
	 * Checks if we need to break at this block
	 * @return get the break status
	 */
	public boolean getBreakStatus() {
		return ((BlockModel)getModel()).isBreak();
	}
	
	/**
	 * Checks if the model is marked at the debug state
	 * @return get the debug status
	 */
	public boolean getDebugStatus() {
		return ((BlockModel)getModel()).isDebug();
	}

}
