package ide.backend.runtime;
	
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import ide.backend.blocks.Block;
import ide.backend.blocks.conditions.IfBlock;
import ide.backend.blocks.conditions.IfElseBlock;
import ide.backend.blocks.conditions.WhileBlock;
import ide.backend.blocks.debug.DebugBlock;
import ide.backend.blocks.debug.NOPBlock;
import ide.backend.blocks.eventsAndEmits.AccesBlock;
import ide.backend.blocks.eventsAndEmits.EmitBlock;
import ide.backend.blocks.functionsAndHandlers.FunctionBlock;
import ide.backend.blocks.functionsAndHandlers.FunctionCallBlock;
import ide.backend.blocks.functionsAndHandlers.HandlerBlock;
import ide.backend.blocks.functionsAndHandlers.PrintBlock;
import ide.backend.blocks.functionsAndHandlers.ReturnBlock;
import ide.backend.blocks.locks.LockBlock;
import ide.backend.blocks.locks.UnLockBlock;
import ide.backend.blocks.math.RandomBlock;
import ide.backend.blocks.operator.ArithBlock;
import ide.backend.blocks.operator.LogicBlock;
import ide.backend.blocks.other.SleepBlock;
import ide.backend.blocks.physics.ChangeAppearanceBlock;
import ide.backend.blocks.physics.MoveBlock;
import ide.backend.blocks.physics.ShowBlock;
import ide.backend.blocks.string.CharAtBlock;
import ide.backend.blocks.string.ConcatBlock;
import ide.backend.blocks.string.LengthBlock;
import ide.backend.blocks.variablesAndTypes.GetVarBlock;
import ide.backend.blocks.variablesAndTypes.SetBlock;
import ide.backend.blocks.variablesAndTypes.ValueBlock;
import ide.backend.blocks.variablesAndTypes.VariableBlock;
import ide.backend.core.ClassContainer;
import ide.backend.core.Event;
import ide.backend.exceptions.CompileException;
import ide.backend.model.BlockModel;
import ide.backend.model.ConnectedBlocks;
import ide.backend.variables.LogicOperators;
import ide.backend.model.conditions.ForeverModel;
import ide.backend.model.classes.ClassModel;
import ide.backend.model.classes.Costume;
import ide.backend.model.classes.InstanceModel;
import ide.backend.model.classes.WireFrameModel;
import ide.backend.model.classes.WireModel;
import ide.backend.model.conditions.IfBlockModel;
import ide.backend.model.conditions.IfElseModel;
import ide.backend.model.conditions.WhileModel;
import ide.backend.model.event.EventModel;
import ide.backend.model.event.RefEventModel;
import ide.backend.model.function.AccessModel;
import ide.backend.model.function.EmitModel;
import ide.backend.model.function.FunctionCallModel;
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
import ide.backend.model.variables.RefMemberModel;
import ide.backend.model.variables.RefVariabelModel;
import ide.backend.model.variables.SetModel;
import ide.backend.model.variables.TypeModel;
import ide.backend.model.variables.ValueModel;
import ide.backend.model.variables.VariableModel;

/**
 * Interpreter Compiler implements Compiler.
 * @author Matthijs Kaminski 
 *
 */
public class InterpreterCompiler implements Compiler<Block> {

	private Runtime _runtime;
	
	/**
	 * Indicates whether to compile in debug mode
	 */
	private boolean _debug;
	
	/**
	 * Creates an new InterpreterCompiler.
	 */
	public InterpreterCompiler() {
		
	}

	@Override
	public boolean compile(Runtime runtime, ModelCollection collection) {
		return compileSetDebug(runtime, collection, false);
	}
	
	/**
	 * The function used to compile programs
	 * @param runtime, the runtime containing the program
	 */
	public boolean compileDebug(Runtime runtime, ModelCollection collection) {
		return compileSetDebug(runtime, collection, true);
	}
	
	/**
	 * Compile with given debug modus (on/off).
	 * @param runtime runtime to compile to
	 * @param collection collection being compiled
	 * @param debug debug modus on or off.
	 * @return if compiled successful true, else false.
	 */
	private boolean compileSetDebug(Runtime runtime, ModelCollection collection, boolean debug) {
		_runtime = runtime;
		_debug = debug;
		
		try {
			compileEvents(collection);
			compileClasses(collection);
			compileWireFrame(collection);
		} catch (CompileException e) {
			return false;
		}
		return true;
	}
	
	private void compileWireFrame(ModelCollection collection) throws CompileException {
		collection.getWireFrameModel().compile(this);
	}

	/**
	 * Compile all the classes in the given runtime.
	 * @throws CompileException
	 */
	private void compileClasses(ModelCollection collection) throws CompileException{
		for(ClassModel model: collection.getClassModels()) {
			model.compile(this);
		}
	}
	
	/**
	 * Compile all the events in the given runtime.
	 * @throws CompileException
	 */
	private void compileEvents(ModelCollection collection) throws CompileException{
		for(EventModel event : collection.getEventModels()){
			event.compile(this);
		}
		
	}
	
	@Override
	public Block compileBlock(ValueModel model) throws CompileException {
		return new ValueBlock(model.getContent());
	}
	
	@Override
	public Block compileBlock(PrintModel model) throws CompileException {
		if (model.getContent() == null) throw new CompileException();
		return new PrintBlock((Block) model.getContent().compile(this));
	}
	
	@Override
	public Block compileBlock(ClassModel model) throws CompileException {
		ClassContainer c = new ClassContainer(model.getName(), model.getSelectedCostume());
		
		//compile functions and add them to the compiled Class.
		for (FunctionModel func : model.getFunctions()) {
			c.addFunction(func.getName(), (Block) func.compile(this)); 
		}
		
		//compile handlers and add them to their events in the compiledClass
		for (HashMap.Entry<EventModel, ArrayList<HandlerModel>> entry : model.getHandlers().entrySet()) {
			if (entry.getKey() == null) continue;
			Event e =  ((InterpreterRuntime) _runtime).getEvent(entry.getKey().getType());
			for (HandlerModel hand : entry.getValue()) {
				c.addHandler(e, (Block) hand.compile(this));
			}
		}
		
		//compile variables and add them to the compiled Class.
		for(HashMap.Entry<String, MemberModel> entry : model.getMembers().entrySet()){
			c.addMember(entry.getKey(), entry.getValue().getType());
		}
		
		//copy the costumes (images) for the class to the classcontainer.
		for(HashMap.Entry<String, Costume> entry : model.getCostumes().entrySet()){
			c.addCostume(entry.getKey(),new Costume( entry.getValue()));
		}
			
		((InterpreterRuntime) _runtime).addClassContainer(c);
		
		return null;	
	}
	
	@Override
	public Block compileBlock(ConnectedBlocks model) throws CompileException {
		throw new CompileException();
	}
	
	@Override
	public Block compileBlock(EventModel model) throws CompileException {
		Event e = new Event(model.getType());
		for (String name : model.getMemberNames()) {
			e.addType(name, model.getVarType(name));
		}
		((InterpreterRuntime) _runtime).AddEvent(e);
		return null;
	}
	
	@Override
	public Block compileBlock(FunctionModel model) throws CompileException {
		//Fetch and compile body of the function.
		ArrayList<Block> blocks = new ArrayList<Block>();

		if (_debug) blocks.add(new DebugBlock(model, true, model.isBreak()));
		for (BlockModel block : model.getBody()) {
			if (_debug) blocks.add(new DebugBlock(block, true, block.isBreak()));
			blocks.add((Block)block.compile(this));
			if (_debug) blocks.add(new DebugBlock(block, false, false));
		}
		if (_debug) blocks.add(new DebugBlock(model, false, false));
		if (_debug) blocks.add(new NOPBlock());
		
		//Fetch and compile parameters of the function.
		ArrayList<VariableBlock> paramblocks = new ArrayList<VariableBlock>();
		for (BlockModel block : model.getParams()) {
			paramblocks.add((VariableBlock)block.compile(this));
		}
		
		return new FunctionBlock(paramblocks, blocks);	
	}
	
	@Override
	public Block compileBlock(HandlerModel model) throws CompileException {
		ArrayList<Block> blocks = new ArrayList<Block>();
		
		if (_debug) blocks.add(new DebugBlock(model, true, model.isBreak()));
		for (BlockModel block : model.getBody()) {
			if (_debug) blocks.add(new DebugBlock(block, true, block.isBreak()));
			blocks.add((Block)block.compile(this));
			if (_debug) blocks.add(new DebugBlock(block, false, false));
		}
		if (_debug) blocks.add(new DebugBlock(model, false, false));
		if (_debug) blocks.add(new NOPBlock());
		return new HandlerBlock(blocks);	
	}
	
	@Override
	public Block compileBlock(RefVariabelModel model) throws CompileException {
		return new GetVarBlock(model.getName());	
	}
	
	@Override
	public Block compileBlock(ReturnModel model) throws CompileException {
		ArrayList<Block> blocks = new ArrayList<Block>();
		for (BlockModel block : model.getReturnVars()) {
			blocks.add((Block)block.compile(this));
		}
		return new ReturnBlock(blocks);
	}
	
	@Override
	public Block compileBlock(TypeModel model) throws CompileException {
		throw new CompileException();
	}
	
	@Override
	public Block compileBlock(VariableModel model) throws CompileException {
		return new VariableBlock(model.getName(), model.getType());	
	}

	@Override
	public Block compileBlock(SetModel model) throws CompileException {
		if (model.getContent() == null) throw new CompileException();
		return new SetBlock(model.getVariable(), (Block) model.getContent().compile(this));
	}

	@Override
	public Block compileBlock(RefEventModel model) throws CompileException {
		throw new CompileException();
	}

	@Override
	public Block compileBlock(AccessModel model) throws CompileException {
		return new AccesBlock(model.getEventName(), model.getMember());
	}

	@Override
	public Block compileBlock(FunctionCallModel model) throws CompileException {
		if (model.getParamNames().contains(null)) throw new CompileException();
		if (model.getReturnNames().contains(null)) throw new CompileException();
		return new FunctionCallBlock(model.getParamNames(), model.getReturnNames(), model.getFuncName());
	}

	@Override
	public Block compileBlock(EmitModel model) throws CompileException {
		String name = model.getEventName();
		HashMap<String, Block> members = new HashMap<String, Block>();
		
		for(Entry<String, AbstractRefVariabelModel> a: model.getMembers().entrySet()) {
			members.put(a.getKey(),	a.getValue().compile(this));
		}
		
		EmitBlock b = new EmitBlock(name, members);
		
		return b;
	}

	@Override
	public Block compileBlock(InstanceModel model) throws CompileException {
		model.deleteObserver(((InterpreterRuntime)_runtime).getCatcher());
		model.addObserver(((InterpreterRuntime)_runtime).getCatcher());
		((InterpreterRuntime)_runtime).addInstance(model.getClassName(), model.getName(), model);
		return null;
	}

	@Override
	public Block compileBlock(OperatorModel model) throws CompileException {
		if (model.getLeft() == null) throw new CompileException();
		if (model.getRight() == null) throw new CompileException();
		if (LogicOperators.operators.containsKey(model.getOperator())) {
			return new LogicBlock(model.getLeft().compile(this), model.getOperator(), model.getRight().compile(this));
		}
		else {
			return new ArithBlock(model.getLeft().compile(this), model.getOperator(), model.getRight().compile(this));
		}
	}
	@Override
	public Block compileBlock(WireModel model) throws CompileException {
		((InterpreterRuntime)_runtime).addConnection(model.getFrom(), model.getTo(), model.getEvent());
		return null;
	}

	@Override
	public Block compileBlock(WireFrameModel model) throws CompileException {
		for (InstanceModel m : model.getInstances()) {
			m.compile(this);
		}
		for (WireModel m : model.getWires()) {
			m.compile(this);
		}
		return null;
	}

	@Override
	public Block compileBlock(IfBlockModel model)throws CompileException {
		if (model.getCondition() == null) throw new CompileException();
		Block condition = model.getCondition().compile(this);
		ArrayList<Block> body = new ArrayList<Block>();
		
		//if (_debug) body.add(new DebugBlock(model, true, model.isBreak()));
		if (_debug) body.add(new NOPBlock());
		for (BlockModel block : model.getBody().getBlocks()) {
			if (_debug) body.add(new DebugBlock(block, true, block.isBreak()));
			body.add(block.compile(this));
			if (_debug) body.add(new DebugBlock(block, false, false));
		}
		if (_debug) body.add(new NOPBlock());
		if (_debug) body.add(new NOPBlock());
		//if (_debug) body.add(new DebugBlock(model, false, false));
		
		
		return new IfBlock(condition, body);
	}
	
	@Override
	public Block compileBlock(IfElseModel model)throws CompileException {
		if (model.getCondition() == null) throw new CompileException();
		Block condition = model.getCondition().compile(this);
		ArrayList<Block> bodyIf = new ArrayList<Block>();
		ArrayList<Block> bodyElse = new ArrayList<Block>();
		
		// Parse the if body
		if (_debug) bodyIf.add(new NOPBlock());
		for (BlockModel block : model.getBodyIf().getBlocks()) {
			if (_debug) bodyIf.add(new DebugBlock(block, true, block.isBreak()));
			bodyIf.add(block.compile(this));
			if (_debug) bodyIf.add(new DebugBlock(block, false, false));
		}
		if (_debug) bodyIf.add(new NOPBlock());
		if (_debug) bodyIf.add(new NOPBlock());
		
		// Parse the else body
		if (_debug) bodyElse.add(new NOPBlock());
		for (BlockModel block : model.getBodyElse().getBlocks()) {
			if (_debug) bodyElse.add(new DebugBlock(block, true, block.isBreak()));
			bodyElse.add(block.compile(this));
			if (_debug) bodyElse.add(new DebugBlock(block, false, false));
		}
		if (_debug) bodyElse.add(new NOPBlock());
		if (_debug) bodyElse.add(new NOPBlock());
		
		return new IfElseBlock(condition, bodyIf, bodyElse);
	}

	@Override
	public Block compileBlock(WhileModel model) throws CompileException {
		if (model.getCondition() == null) throw new CompileException();
		Block condition = model.getCondition().compile(this);
		ArrayList<Block> body = new ArrayList<Block>();
		
		if (_debug) body.add(new NOPBlock());
		for (BlockModel block : model.getBody().getBlocks()) {
			if (_debug) body.add(new DebugBlock(block, true, block.isBreak()));
			body.add(block.compile(this));
			if (_debug) body.add(new DebugBlock(block, false, false));
		}
		if (_debug) body.add(new NOPBlock());
		if (_debug) body.add(new NOPBlock());
		
		return new WhileBlock(condition, body);
	}

	@Override
	public Block compileBlock(MemberModel memberModel) throws CompileException {
		return null;
	}

	@Override
	public Block compileBlock(RefMemberModel model) throws CompileException {
		return new GetVarBlock(model.getName());
	}

	@Override
	public Block compileBlock(ChangeAppearanceModel model) throws CompileException {
		if (model.getContent() == null) throw new CompileException();
		return new ChangeAppearanceBlock((Block) model.getContent().compile(this));
	}

	@Override
	public Block compileBlock(ForeverModel model) throws CompileException {
		Block condition = new ValueBlock("true");
		ArrayList<Block> body = new ArrayList<Block>();
		if (_debug) body.add(new NOPBlock());
		for (BlockModel block : model.getBody().getBlocks()) {
			if (_debug) body.add(new DebugBlock(block, true, block.isBreak()));
			body.add(block.compile(this));
			if (_debug) body.add(new DebugBlock(block, false, false));
		}
		if (_debug) body.add(new NOPBlock());
		if (_debug) body.add(new NOPBlock());
		return new WhileBlock(condition, body);

	}
	
	@Override
	public Block compileBlock(LockModel model) throws CompileException {
		if (model.getVariableModel() == null) throw new CompileException();
		return new LockBlock(model.getVariable());
	}

	@Override
	public Block compileBlock(UnLockModel model) throws CompileException {
		if (model.getVariableModel() == null) throw new CompileException();
		return new UnLockBlock(model.getVariable());
	}

	@Override
	public Block compileBlock(SleepModel model) throws CompileException {
		if (model.getContent() == null) throw new CompileException();
		return new SleepBlock(model.getContent().compile(this));
	}

	@Override
	public Block compileBlock(RandomModel model) throws CompileException {
		return new RandomBlock();
	}

	@Override
	public Block compileBlock(ShowModel model) throws CompileException {
		return new ShowBlock(true);
	}

	@Override
	public Block compileBlock(MoveModel model) throws CompileException {
		if(model.getX() == null || model.getY() == null)
			throw new CompileException();
		return new MoveBlock(model.getX().compile(this), model.getY().compile(this));
	}

	@Override
	public Block compileBlock(HideModel model) {
		return new ShowBlock(false);
	}

	@Override
	public Block compileBlock(ConcatModel model) throws CompileException {
		if (model.getLeft() == null || model.getRight() == null)
			throw new CompileException();
		
		return new ConcatBlock(model.getLeft().compile(this), model.getRight().compile(this));
	}

	@Override
	public Block compileBlock(LengthModel model) throws CompileException {
		if (model.getContent() == null)
			throw new CompileException();
		
		return new LengthBlock(model.getContent().compile(this));
	}

	@Override
	public Block compileBlock(CharAtModel model) throws CompileException {
		if (model.getContent() == null || model.getIndex() == null)
			throw new CompileException();
		
		return new CharAtBlock(model.getContent().compile(this), model.getIndex().compile(this));
	}

	@Override
	public Block compileBlock(UnOperatorModel model)
			throws CompileException {
		if (model.getLeft() == null) throw new CompileException();
		if (LogicOperators.operators.containsKey(model.getOperator())) {
			System.out.println(model.getLeft());
			System.out.println(model.getRight());
			return new LogicBlock(model.getLeft().compile(this), model.getOperator(), model.getRight().compile(this));
		}
		else {
			return new ArithBlock(model.getLeft().compile(this), model.getOperator(), model.getRight().compile(this));
		}
		
	}

}

