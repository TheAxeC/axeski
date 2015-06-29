package ide.frontend.classes;

import java.util.ArrayList;
import java.util.HashMap;

import ide.backend.language.LanguageModule;
import ide.backend.model.BlockModel;
import ide.backend.model.ConnectedBlocks;
import ide.backend.model.classes.ClassModel;
import ide.backend.model.conditions.ForeverModel;
import ide.backend.model.conditions.IfBlockModel;
import ide.backend.model.conditions.IfElseModel;
import ide.backend.model.conditions.WhileModel;
import ide.backend.model.event.EventModel;
import ide.backend.model.function.AccessModel;
import ide.backend.model.function.EmitModel;
import ide.backend.model.function.FunctionModel;
import ide.backend.model.function.FunctionCallModel;
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
import ide.backend.model.variables.PrintModel;
import ide.backend.model.variables.RefMemberModel;
import ide.backend.model.variables.RefVariabelModel;
import ide.backend.model.variables.SetModel;
import ide.backend.model.variables.ValueModel;
import ide.backend.model.variables.VariableModel;
import ide.frontend.classes.views.AnchorBlock;
import ide.frontend.classes.views.BlockView;
import ide.frontend.classes.views.conditions.IfElseView;
import ide.frontend.classes.views.conditions.ForeverView;
import ide.frontend.classes.views.conditions.IfView;
import ide.frontend.classes.views.conditions.WhileView;
import ide.frontend.classes.views.functions.AccesView;
import ide.frontend.classes.views.functions.EmitView;
import ide.frontend.classes.views.functions.FunctionView;
import ide.frontend.classes.views.functions.FunctionCallView;
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

/**
 * This class loads a classes blocks view from its models. 
 * And places them on the idepanel.
 * @author Matthijs Kaminski
 *
 */
public class LoadClassViewFromModel {
	
	/**
	 * Fields
	 */
	//idepanel on which the blocks are placed.
	private IDEPanel _panel;
	// class whoms views are being created.
	private ClassModel _class;
	//language model for the created views.
	private LanguageModule _lang;
	
	
	/**
	 * Loads a classes blocks view from its models. 
	 * @param c classmodel whoms blockviews are being created from its models.
	 * @param p panel where the blocks a being placed on.
	 * @param lang language model for the views.
	 */
	public LoadClassViewFromModel(ClassModel c, IDEPanel p, LanguageModule lang) {
		_class = c;
		_panel = p;
		_lang = lang;
		//load all the handlers.
		makeHandlerViews();
		//load all the floating blocks.
		makeFloatingBlocks();
		//load all the functions
		makeFunctionViews();
	}
	
	/**
	 * Creates the view for floating blocks.
	 */
	private void makeFloatingBlocks() {
		ArrayList<BlockModel> floats = _class.getFloaters();
		for(BlockModel f: floats) {
			_panel.addView(makeViewFromBlock(f));
		}
	}
	
	/**
	 * Create views for all the functions.
	 */
	private void makeFunctionViews() {
		ArrayList<FunctionModel> hands = _class.getFunctions();
		for (FunctionModel entry : hands) {
			_panel.addView(makeFunctionView(entry));
		}
	}
	
	/**
	 * Creates a view for a given handler form its given model [hand]
	 * Ands creates the connection between the handler and an input event if the given 
	 * input event is not null.
	 * @param hand handler whoms view is created.
	 * @param e eventmodel which has been assigned to the given handler.
	 * @return the created handlerview.
	 */
	private FunctionView makeFunctionView(FunctionModel func) {
		func.clearVariables();
		ArrayList<BlockModel> blocks = func.getBody();
		FunctionView v = new FunctionView(func, null, _panel,_lang);
		v.setLoad(func.getPos().x, func.getPos().y);
		
		// Load the parameters
		v.prepareFunctionLoad(func.getParamModels());
		
		// Load the body
		for (BlockModel blockModel : blocks) {
			BlockView b = makeViewFromBlock(blockModel);
			v.addView(b);
		}
		v.resetPositions();

		return v;
	}
	
	/**
	 * Create views for all the handlers.
	 */
	private void makeHandlerViews() {
		HashMap<EventModel, ArrayList<HandlerModel>> hands = _class.getHandlers();
		for (HashMap.Entry<EventModel, ArrayList<HandlerModel>> entry : hands.entrySet()) {
			for (HandlerModel hand : entry.getValue()) {
				_panel.addView(makeHandlerView(hand, entry.getKey()));
			}
		}
	}
	
	/**
	 * Creates a view for a given handler form its given model [hand]
	 * Ands creates the connection between the handler and an input event if the given 
	 * input event is not null.
	 * @param hand handler whoms view is created.
	 * @param e eventmodel which has been assigned to the given handler.
	 * @return the created handlerview.
	 */
	private HandlerView makeHandlerView(HandlerModel hand, EventModel e) {
		hand.clearVariables();
		ArrayList<BlockModel> blocks = hand.getBody();
		HandlerView v = new HandlerView(hand, null, _panel,_lang);
		v.set(hand.getPos().x, hand.getPos().y);
		for (BlockModel blockModel : blocks) {
			BlockView b = makeViewFromBlock(blockModel);
			v.addView(b);
		}
		v.resetPositions();
		if (e != null)
			_panel.addConnectionHandler(e, v.getEventButton(), hand);
		
		return v;
	}
	

	/**
	 * Casts the model to its right class so the proper function 
	 * for creating its view is called.
	 * @param model model being cast for creating its view. (type here is Blockmodel).
	 * @return created block view.
	 */
	private BlockView makeViewFromBlock(BlockModel model) {
		Class<? extends BlockModel> c  = model.getClass();
		if(c == PrintModel.class)
			return makeViewFromBlock((PrintModel)model);
		else if(c == ValueModel.class)
			return makeViewFromBlock((ValueModel)model);
		else if(c == IfBlockModel.class)
			return makeViewFromBlock((IfBlockModel)model);
		else if(c == WhileModel.class)
			return makeViewFromBlock((WhileModel)model);
		else if(c == VariableModel.class)
			return makeViewFromBlock((VariableModel)model);
		else if(c == RefVariabelModel.class)
			return makeViewFromBlock((AbstractRefVariabelModel)model);
		else if(c == RefMemberModel.class)
			return makeViewFromBlock((AbstractRefVariabelModel)model);
		else if(c == SetModel.class)
			return makeViewFromBlock((SetModel)model);
		else if(c == EmitModel.class)
			return makeViewFromBlock((EmitModel)model);
		else if (c == ConnectedBlocks.class)
			return makeViewFromBlock((ConnectedBlocks)model);
		else if (c == ChangeAppearanceModel.class)
			return makeViewFromBlock((ChangeAppearanceModel) model);
		else if (c == LockModel.class)
			return makeViewFromBlock((LockModel) model);
		else if (c == UnLockModel.class)
			return makeViewFromBlock((UnLockModel) model);
		else if (c == SleepModel.class)
			return makeViewFromBlock((SleepModel) model);
		else if (c == RandomModel.class)
			return makeViewFromBlock((RandomModel) model);
		else if (c == ShowModel.class)
			return makeViewFromBlock((ShowModel) model);
		else if (c == HideModel.class)
			return makeViewFromBlock((HideModel) model);
		else if (c == LengthModel.class)
			return makeViewFromBlock((LengthModel) model);
		else if (c == ConcatModel.class)
			return makeViewFromBlock((ConcatModel) model);
		else if (c == CharAtModel.class)
			return makeViewFromBlock((CharAtModel) model);
		else if (c == IfElseModel.class)
			return makeViewFromBlock((IfElseModel) model);
		else if (c == MoveModel.class)
			return makeViewFromBlock((MoveModel) model);
		else if (c == ForeverModel.class)
			return makeViewFromBlock((ForeverModel) model);
		else if (c == OperatorModel.class)
			return makeViewFromBlock((OperatorModel) model);
		else if (c == UnOperatorModel.class)
			return makeViewFromBlock((UnOperatorModel) model);
		else if (c == AccessModel.class)
			return makeViewFromBlock((AccessModel) model);
		else if (c == FunctionCallModel.class)
			return makeViewFromBlock((FunctionCallModel)model);
		else if (c == ReturnModel.class)
			return makeViewFromBlock((ReturnModel)model);
		System.err.println("Not implemented yet");
		return null;
	}
	
	/**
	 * Creates a AnchorBlock form a connectedBlockmodel
	 * @param model connectedBlockmodel whoms view is created
	 * @return created AnchorBlock
	 */
	private BlockView makeViewFromBlock(ConnectedBlocks model) {
		AnchorBlock anchor = new AnchorBlock(model, null, _panel,_lang);
		anchor.set(model.getPos().x, model.getPos().y);
		
		ArrayList<BlockModel> blocks = model.getBlocks();
		for(BlockModel b: blocks) {
			anchor.addView(makeViewFromBlock(b));
		}
		anchor.resetPositions();
		return anchor;
	}
	
	/**
	 * Creates a PrintView form a PrintModel
	 * @param model PrintModel whoms view is created
	 * @return created PrintView
	 */
	private BlockView makeViewFromBlock(PrintModel model) {
		PrintView v = new PrintView(model, null, _panel,_lang);
		v.set(model.getPos().x, model.getPos().y);
		if (model.getContent() != null)
			v.addView(makeViewFromBlock(model.getContent()));
		v.resetPositions();
		return v;
	}
	
	/**
	 * Creates a ValueView form a ValueModel
	 * @param model ValueModel whoms view is created
	 * @return created ValueView
	 */
	private BlockView makeViewFromBlock(ValueModel model) {
		ValueView v = new ValueView(model, null, _panel,_lang);
		v.set(model.getPos().x, model.getPos().y);
		return v;
	}
	
	/**
	 * Creates a IfView form a IfBlockModel
	 * @param model IfView whoms view is created
	 * @return created PrintView
	 */
	private BlockView makeViewFromBlock(IfBlockModel model) {
		IfView v = new IfView(model, null, _panel,_lang);
		v.set(model.getPos().x, model.getPos().y);
		if (model.getCondition() != null)
			v.addConditionView(makeViewFromBlock(model.getCondition()));
		for (BlockModel block : model.getBody().getBlocks()) {
			v.addView(makeViewFromBlock(block));
		}
		v.resetPositions();
		return v;
	}
	

	private BlockView makeViewFromBlock(IfElseModel model) {
		IfElseView v = new IfElseView(model, null, _panel,_lang);
		v.set(model.getPos().x, model.getPos().y);
		if (model.getCondition() != null)
			v.addConditionView(makeViewFromBlock(model.getCondition()));
		for (BlockModel block : model.getBodyIf().getBlocks()) {
			v.addView(makeViewFromBlock(block));
		}
		v.resetPositions();
		for (BlockModel block : model.getBodyElse().getBlocks()) {
			v.addViewElse(makeViewFromBlock(block));
		}
		v.resetPositions();
		return v;
	}
	
	/**
	 * Creates a WhileView form a WhileModel
	 * @param model WhileModel whoms view is created
	 * @return created PrintView
	 */
	private BlockView makeViewFromBlock(WhileModel model) {
		WhileView v = new WhileView(model, null, _panel,_lang);
		v.set(model.getPos().x, model.getPos().y);
		if (model.getCondition() != null)
			v.addConditionView(makeViewFromBlock(model.getCondition()));
		for (BlockModel block : model.getBody().getBlocks()) {
			v.addView(makeViewFromBlock(block));
		}
		v.resetPositions();
		return v;
	}
	
	/**
	 * Creates a EmitView form a EmitModel
	 * @param model EmitModel whoms view is created
	 * @return created EmitView
	 */
	private BlockView makeViewFromBlock(EmitModel model) {
		EmitView v = new EmitView(model, null, _panel,_lang);
		v.set(model.getPos().x, model.getPos().y);
		v.setEvent(model.getEventName());

		for(HashMap.Entry<String, AbstractRefVariabelModel> entry: model.getMembers().entrySet()) {
			v.addView(entry.getKey(), makeViewFromBlock(entry.getValue()));
		}
		_panel.addEmit(v);
		v.resetPositions();
		return v;
	}
	
	/**
	 * Creates a MakeVarView form a VariableModel
	 * @param model VariableModel whoms view is created
	 * @return created MakeVarView
	 */
	private BlockView makeViewFromBlock(VariableModel model) {
		MakeVarView v = new MakeVarView(model, null, _panel,_lang);
		v.set(model.getPos().x, model.getPos().y);
		
		return v;
	}
	
	/**
	 * Creates a VariableRefView form a AbstractRefVariabelModel (VariableRefView)
	 * @param model AbstractRefVariabelModel whoms view is created
	 * @return created VariableRefView
	 */
	private BlockView makeViewFromBlock(AbstractRefVariabelModel model) {
		VariableRefView v = new VariableRefView(model, null, _panel,_lang);
		v.set(model.getPos().x, model.getPos().y);
		return v;
	}
	
	/**
	 * Creates a SetBlockView form a SetModel
	 * @param model SetModel whoms view is created
	 * @return created SetBlockView
	 */
	private BlockView makeViewFromBlock(SetModel model) {
		SetBlockView v = new SetBlockView(model, null, _panel,_lang);
		v.set(model.getPos().x, model.getPos().y);
		if (model.getVariableModel() != null)
			v.addViewRef(makeViewFromBlock(model.getVariableModel()));
		if (model.getContent() != null)
			v.addView(makeViewFromBlock(model.getContent()));
		v.resetPositions();
		return v;
	}
	
	/**
	 * Creates a ChangeAppearanceView form a ChangeAppearanceModel
	 * @param model ChangeAppearanceModel whoms view is created
	 * @return created ChangeAppearanceView
	 */
	private BlockView makeViewFromBlock(ChangeAppearanceModel model) {
		ChangeAppearanceView v = new ChangeAppearanceView(model, null, _panel,_lang);
		v.set(model.getPos().x, model.getPos().y);
		if (model.getContent() != null)
			v.addView(makeViewFromBlock(model.getContent()));
		v.resetPositions();
		return v;
	}
	
	/**
	 * Creates a LockView form a LockModel
	 * @param model LockModel whoms view is created
	 * @return created LockView
	 */
	private BlockView makeViewFromBlock(LockModel model) {
		LockView v = new LockView(model, null, _panel,_lang);
		v.set(model.getPos().x, model.getPos().y);
		if (model.getVariableModel() != null)
			v.addView(makeViewFromBlock(model.getVariableModel()));
		v.resetPositions();
		return v;
	}

	/**
	 * Creates a UnLockView form a UnLockModel
	 * @param model UnLockModel whoms view is created
	 * @return created UnLockView
	 */
	private BlockView makeViewFromBlock(UnLockModel model) {
		UnLockView v = new UnLockView(model, null, _panel,_lang);
		v.set(model.getPos().x, model.getPos().y);
		if (model.getVariableModel() != null)
			v.addView(makeViewFromBlock(model.getVariableModel()));
		v.resetPositions();
		return v;
	}
	
	/**
	 * Creates a SleepView form a SleepModel
	 * @param model SleepModel whoms view is created
	 * @return created SleepView
	 */
	private BlockView makeViewFromBlock(SleepModel model) {
		SleepView v = new SleepView(model, null, _panel,_lang);
		v.set(model.getPos().x, model.getPos().y);
		if (model.getContent() != null)
			v.addView(makeViewFromBlock(model.getContent()));
		v.resetPositions();
		return v;
	}
	
	/**
	 * Creates a RandomView form a RandomModel
	 * @param model RandomModel whoms view is created
	 * @return created RandomView
	 */
	private BlockView makeViewFromBlock(RandomModel model) {
		RandomView v = new RandomView(model, null, _panel,_lang);
		v.set(model.getPos().x, model.getPos().y);
		v.resetPositions();
		return v;
	}
	
	/**
	 * Creates a ShowView form a ShowModel
	 * @param model ShowModel whoms view is created
	 * @return created ShowView
	 */
	private BlockView makeViewFromBlock(ShowModel model) {
		ShowView v = new ShowView(model, null, _panel,_lang);
		v.set(model.getPos().x, model.getPos().y);
		v.resetPositions();
		return v;
	}
	
	/**
	 * Creates a HideView form a HideModel
	 * @param model HideModel whoms view is created
	 * @return created HideView
	 */
	private BlockView makeViewFromBlock(HideModel model) {
		HideView v = new HideView(model, null, _panel,_lang);
		v.set(model.getPos().x, model.getPos().y);
		v.resetPositions();
		return v;
	}
	
	/**
	 * Creates a LengthView form a LengthModel
	 * @param model LengthModel whoms view is created
	 * @return created LengthView
	 */
	private BlockView makeViewFromBlock(LengthModel model) {
		LengthView v = new LengthView(model, null, _panel,_lang);
		v.set(model.getPos().x, model.getPos().y);
		
		if (model.getContent() != null)
			v.addView(makeViewFromBlock(model.getContent()));
		v.resetPositions();
		return v;
	}
	
	/**
	 * Creates a ConcatView form a ConcatModel
	 * @param model ConcatModel whoms view is created
	 * @return created ConcatView
	 */
	private BlockView makeViewFromBlock(ConcatModel model) {
		ConcatView v = new ConcatView(model, null, _panel, _lang);
		v.set(model.getPos().x, model.getPos().y);
		
		if (model.getLeft() != null)
			v.addView(makeViewFromBlock(model.getLeft()));
		if (model.getRight() != null)
			v.addViewRight(makeViewFromBlock(model.getRight()));
		v.resetPositions();
		return v;
	}
	
	/**
	 * Creates a CharAtView form a CharAtModel
	 * @param model CharAtModel whoms view is created
	 * @return created CharAtView
	 */
	private BlockView makeViewFromBlock(CharAtModel model) {
		CharAtView v = new CharAtView(model, null, _panel, _lang);
		v.set(model.getPos().x, model.getPos().y);
		
		if (model.getContent() != null)
			v.addView(makeViewFromBlock(model.getContent()));
		if (model.getIndex() != null)
			v.addViewRight(makeViewFromBlock(model.getIndex()));
		v.resetPositions();
		return v;
	}
	
	/**
	 * Creates a MoveView form a MoveModel
	 * @param model MoveModel whoms view is created
	 * @return created MoveView
	 */
	private BlockView makeViewFromBlock(MoveModel model) {
		MoveView v = new MoveView(model, null, _panel, _lang);
		v.set(model.getPos().x, model.getPos().y);
		if (model.getX() != null)
			v.addView(makeViewFromBlock(model.getX()));
		if (model.getY() != null)
			v.addViewRight(makeViewFromBlock(model.getY()));
		v.resetPositions();
		return v;
	}
	
	/**
	 * Creates a ForeverView form a ForeverModel
	 * @param model ForeverModel whoms view is created
	 * @return created ForeverView
	 */
	private BlockView makeViewFromBlock(ForeverModel model) {
		ForeverView v = new ForeverView(model, null, _panel,_lang);
		v.set(model.getPos().x, model.getPos().y);
		for (BlockModel block : model.getBody().getBlocks()) {
			v.addView(makeViewFromBlock(block));
		}
		v.resetPositions();
		return v;
	}
	
	/**
	 * Creates a BinaryOperator form a MoveModel
	 * @param model OperatorModel whoms view is created
	 * @return created BinaryOperator
	 */
	private BlockView makeViewFromBlock(OperatorModel model) {
		BinaryOperator v = new BinaryOperator(model, null, _panel, _lang);
		v.set(model.getPos().x, model.getPos().y);
		if (model.getLeft() != null)
			v.addView(makeViewFromBlock(model.getLeft()));
		if (model.getRight() != null)
			v.addViewRight(makeViewFromBlock(model.getRight()));
		v.resetPositions();
		return v;
	}
	
	/**
	 * Creates a UnOperatorView form a UnOperatorModel
	 * @param model UnOperatorModel whoms view is created
	 * @return created UnOperatorView
	 */
	private BlockView makeViewFromBlock(UnOperatorModel model) {
		UnOperatorView v = new UnOperatorView(model, null, _panel, _lang);
		v.set(model.getPos().x, model.getPos().y);
		if (model.getLeft() != null)
			v.addView(makeViewFromBlock(model.getLeft()));
		v.resetPositions();
		return v;
	}
	
	/**
	 * Creates a AccesView form a AccessModel
	 * @param model AccessModel whoms view is created
	 * @return created AccesView
	 */
	private BlockView makeViewFromBlock(AccessModel model) {
		AccesView v = new AccesView(model, null, _panel, _lang);
		v.set(model.getPos().x, model.getPos().y);
		v.resetPositions();
		return v;
	}
	

	/**
	 * Creates a FunctionCallView form a FunctionCallModel
	 * @param model FunctionCallModel whom view is created
	 * @return created FunctionCallView
	 */
	private BlockView makeViewFromBlock(FunctionCallModel model) {
		FunctionCallView v = new FunctionCallView(model, null, _panel, _lang);
		v.set(model.getPos().x, model.getPos().y);
		for(AbstractRefVariabelModel para : model.getParams()){
			if (para != null)
				v.addView(makeViewFromBlock(para));
		}
		for(AbstractRefVariabelModel ret : model.getReturns()){
			if (ret != null)
				v.addView(makeViewFromBlock(ret));
		}
		v.resetPositions();
		return v;
	}
	
	/**
	 * Creates a ReturnView form a ReturnModel
	 * @param model ReturnModel whom view is created
	 * @return created ReturnView
	 */
	private BlockView makeViewFromBlock(ReturnModel model) {
		ReturnView v = new ReturnView(model, null, _panel,_lang);
		v.set(model.getPos().x, model.getPos().y);
		if (model.getReturnVars().size() > 0)
			v.addView(makeViewFromBlock(model.getReturnVars().get(0)));
		v.resetPositions();
		return v;
	}
	
	
}
